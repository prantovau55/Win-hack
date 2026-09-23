package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.BetSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class FloatingSignalService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingRootView: View? = null
    private var params: WindowManager.LayoutParams? = null

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var tvPeriod: TextView? = null
    private var tvSignal: TextView? = null
    private var tvTimer: TextView? = null
    private var tvAccuracy: TextView? = null
    private var tvConfidence: TextView? = null
    private var signalBadgeLayout: LinearLayout? = null

    companion object {
        const val CHANNEL_ID = "floating_signal_channel"
        const val NOTIFICATION_ID = 9021
        const val ACTION_STOP = "com.example.STOP_FLOATING"

        fun start(context: Context) {
            val intent = Intent(context, FloatingSignalService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, FloatingSignalService::class.java)
            context.stopService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startInForeground()
        initFloatingView()
        observeSignalState()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WinGo 30S Floating Signal Mod",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows real-time AI signal overlay over Chrome"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startInForeground() {
        val appIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            appIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WinGo 30S AI Signal Floating Mod")
            .setContentText("Signal floating active over Chrome & apps")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun initFloatingView() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 80
            y = 200
        }

        // Programmatic lightweight UI
        val density = resources.displayMetrics.density
        fun dp(value: Float): Int = (value * density).toInt()

        val rootCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10f), dp(8f), dp(10f), dp(8f))

            background = GradientDrawable().apply {
                setColor(Color.parseColor("#EE0B0E14"))
                cornerRadius = dp(14f).toFloat()
                setStroke(dp(1.5f), Color.parseColor("#00E5FF"))
            }
            elevation = dp(12f).toFloat()
        }

        // Header Row: Drag Handle + Title + Chrome Icon + Close
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val tvTitle = TextView(this).apply {
            text = "AI MOD 30S"
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#00E5FF"))
            letterSpacing = 0.05f
        }
        headerRow.addView(tvTitle, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        // Open Chrome Button
        val btnChrome = TextView(this).apply {
            text = "🌐 CHROME"
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFD700"))
            setPadding(dp(6f), dp(2f), dp(6f), dp(2f))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#33FFD700"))
                cornerRadius = dp(6f).toFloat()
                setStroke(dp(0.5f), Color.parseColor("#FFD700"))
            }
            setOnClickListener {
                launchChromeOrBrowser()
            }
        }
        headerRow.addView(btnChrome)

        val spacerClose = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(8f), 1)
        }
        headerRow.addView(spacerClose)

        // Close Button
        val btnClose = TextView(this).apply {
            text = "✕"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#8E8EA0"))
            setPadding(dp(6f), dp(2f), dp(6f), dp(2f))
            setOnClickListener {
                stopSelf()
            }
        }
        headerRow.addView(btnClose)

        rootCard.addView(headerRow)

        // Period text with quick -1 / +1 match adjustment
        val periodRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(2f), 0, dp(2f))
        }

        tvPeriod = TextView(this).apply {
            text = "PERIOD: #20260923..."
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#A0AEC0"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }
        periodRow.addView(tvPeriod)

        val btnPeriodMinus = TextView(this).apply {
            text = " -1 "
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#E2E8F0"))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2D3748"))
                cornerRadius = dp(4f).toFloat()
            }
            setPadding(dp(6f), dp(2f), dp(6f), dp(2f))
            setOnClickListener {
                FloatingSignalStateManager.adjustPeriod(-1)
            }
        }
        periodRow.addView(btnPeriodMinus)

        val spacerPeriod = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(4f), 1)
        }
        periodRow.addView(spacerPeriod)

        val btnPeriodPlus = TextView(this).apply {
            text = " +1 "
            textSize = 9f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#E2E8F0"))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2D3748"))
                cornerRadius = dp(4f).toFloat()
            }
            setPadding(dp(6f), dp(2f), dp(6f), dp(2f))
            setOnClickListener {
                FloatingSignalStateManager.adjustPeriod(1)
            }
        }
        periodRow.addView(btnPeriodPlus)

        rootCard.addView(periodRow)

        // Big / Small + Timer row
        val signalRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(2f), 0, dp(2f))
        }

        signalBadgeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(12f), dp(4f), dp(12f), dp(4f))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#33FFB300"))
                cornerRadius = dp(8f).toFloat()
                setStroke(dp(1.5f), Color.parseColor("#FFB300"))
            }
        }

        tvSignal = TextView(this).apply {
            text = "BIG"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFB300"))
        }
        signalBadgeLayout?.addView(tvSignal)
        signalRow.addView(signalBadgeLayout)

        val spacerTimer = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(10f), 1)
        }
        signalRow.addView(spacerTimer)

        tvTimer = TextView(this).apply {
            text = "30s"
            textSize = 18f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.parseColor("#00E5FF"))
        }
        signalRow.addView(tvTimer)

        val spacerAcc = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(10f), 1)
        }
        signalRow.addView(spacerAcc)

        tvAccuracy = TextView(this).apply {
            text = "85% ACC"
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#00E676"))
            setPadding(dp(6f), dp(3f), dp(6f), dp(3f))
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#2200E676"))
                cornerRadius = dp(6f).toFloat()
            }
        }
        signalRow.addView(tvAccuracy)

        rootCard.addView(signalRow)

        // Confidence and streak summary
        tvConfidence = TextView(this).apply {
            text = "AI Confidence: 88.5% | Record: 18W-3L"
            textSize = 9f
            setTextColor(Color.parseColor("#718096"))
            setPadding(0, dp(2f), 0, 0)
        }
        rootCard.addView(tvConfidence)

        // Touch listener for dragging
        rootCard.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                val currentParams = params ?: return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = currentParams.x
                        initialY = currentParams.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        currentParams.x = initialX + (event.rawX - initialTouchX).toInt()
                        currentParams.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingRootView, currentParams)
                        return true
                    }
                }
                return false
            }
        })

        floatingRootView = rootCard
        windowManager?.addView(floatingRootView, params)
    }

    private fun observeSignalState() {
        val density = resources.displayMetrics.density
        fun dp(value: Float): Int = (value * density).toInt()

        serviceScope.launch {
            FloatingSignalStateManager.state.collectLatest { state ->
                tvPeriod?.text = "TARGET PERIOD: #${state.currentPeriod.takeLast(7)}"
                tvSignal?.text = state.targetSize.name
                tvTimer?.text = "${state.secondsLeft}s"

                val isBig = state.targetSize == BetSize.BIG
                val mainColor = if (isBig) Color.parseColor("#FFB300") else Color.parseColor("#00E5FF")
                val bgBadgeColor = if (isBig) Color.parseColor("#33FFB300") else Color.parseColor("#3300E5FF")

                signalBadgeLayout?.background = GradientDrawable().apply {
                    setColor(bgBadgeColor)
                    cornerRadius = dp(8f).toFloat()
                    setStroke(dp(1.5f), mainColor)
                }
                tvSignal?.setTextColor(mainColor)

                val timerColor = if (state.secondsLeft <= 5) Color.parseColor("#FF3D71") else Color.parseColor("#00E5FF")
                tvTimer?.setTextColor(timerColor)

                tvAccuracy?.text = String.format(Locale.US, "%.0f%% ACC", state.accuracyPercentage)
                tvConfidence?.text = "Confidence: ${String.format(Locale.US, "%.1f", state.confidence)}% | ${state.winCount}W - ${state.lossCount}L"
            }
        }
    }

    private fun launchChromeOrBrowser() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // Attempt to target Chrome if available
                setPackage("com.android.chrome")
            }
            startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to generic browser intent
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(fallbackIntent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (floatingRootView != null && windowManager != null) {
            try {
                windowManager?.removeView(floatingRootView)
            } catch (_: Exception) {
            }
        }
    }
}
