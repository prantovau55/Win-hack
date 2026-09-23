package com.example.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.InetAddress
import java.util.concurrent.TimeUnit

data class WinGoDrawItem(
    val issueNumber: String,
    val number: Int,
    val color: String
)

enum class WinGoGameType(
    val pathSegment: String,
    val typeCode: String,
    val cycleSeconds: Int,
    val label: String
) {
    WINGO_30S("WinGo_30S", "10005", 30, "WinGo 30S"),
    WINGO_1M("WinGo_1M", "10001", 60, "WinGo 1Min"),
    WINGO_3M("WinGo_3M", "10002", 180, "WinGo 3Min"),
    WINGO_5M("WinGo_5M", "10003", 300, "WinGo 5Min")
}

/**
 * Custom DNS with fallback to known Cloudflare IPs for ar-lottery01.com
 * to guarantee resolution across all Android network configurations and sandboxes.
 */
class WinGoDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        return try {
            Dns.SYSTEM.lookup(hostname)
        } catch (e: Exception) {
            if (hostname.contains("ar-lottery01.com")) {
                listOf(
                    InetAddress.getByAddress(hostname, byteArrayOf(104.toByte(), 18.toByte(), 13.toByte(), 110.toByte())),
                    InetAddress.getByAddress(hostname, byteArrayOf(104.toByte(), 18.toByte(), 12.toByte(), 110.toByte()))
                )
            } else {
                throw e
            }
        }
    }
}

class WinGoApiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .dns(WinGoDns())
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        const val BASE_URL = "https://draw.ar-lottery01.com/WinGo"
        private const val TAG = "WinGoApiService"

        /**
         * Calculates the mathematically exact real-time period based on UTC server time
         * matching Bangladesh and international WinGo platforms.
         */
        fun calculateLiveActivePeriod(gameType: WinGoGameType = WinGoGameType.WINGO_30S): String {
            return try {
                val nowUtc = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
                val dateStr = nowUtc.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                val secsSinceMidnight = nowUtc.hour * 3600 + nowUtc.minute * 60 + nowUtc.second
                val roundIndex = (secsSinceMidnight / gameType.cycleSeconds) + 1
                String.format(java.util.Locale.US, "%s%s%04d", dateStr, gameType.typeCode, roundIndex)
            } catch (e: Exception) {
                val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                "${todayStr}${gameType.typeCode}1000"
            }
        }
    }

    suspend fun fetchLatestDrawHistory(
        gameType: WinGoGameType = WinGoGameType.WINGO_30S
    ): Result<List<WinGoDrawItem>> = withContext(Dispatchers.IO) {
        try {
            val ts = System.currentTimeMillis()
            val url = "$BASE_URL/${gameType.pathSegment}/GetHistoryIssuePage.json?ts=$ts"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36")
                .header("Accept", "application/json, text/plain, */*")
                .header("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .header("Referer", "https://draw.ar-lottery01.com/")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP error code: ${response.code}"))
                }

                val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
                val json = JSONObject(body)
                val dataObj = json.optJSONObject("data") ?: return@withContext Result.failure(Exception("Missing 'data' object in response"))
                val listArr = dataObj.optJSONArray("list") ?: return@withContext Result.failure(Exception("Missing 'list' array in data"))

                val results = mutableListOf<WinGoDrawItem>()
                for (i in 0 until listArr.length()) {
                    val item = listArr.optJSONObject(i) ?: continue
                    val issueNumber = item.optString("issueNumber", "")
                    val numberStr = item.optString("number", "0")
                    val colorStr = item.optString("color", "")
                    val num = numberStr.toIntOrNull() ?: 0

                    if (issueNumber.isNotBlank()) {
                        results.add(
                            WinGoDrawItem(
                                issueNumber = issueNumber,
                                number = num,
                                color = colorStr
                            )
                        )
                    }
                }

                Log.d(TAG, "Successfully fetched ${results.size} ${gameType.label} draws, latest: ${results.firstOrNull()?.issueNumber}")
                Result.success(results)
            }
        } catch (e: Exception) {
            Log.d(TAG, "WinGo ${gameType.label} fetch notice: ${e.message ?: "network unreachable"}")
            Result.failure(e)
        }
    }
}
