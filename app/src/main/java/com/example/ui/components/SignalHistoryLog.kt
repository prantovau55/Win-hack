package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.PeriodResult
import com.example.ui.theme.AiCyan
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorViolet
import com.example.ui.theme.LossRed
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalBigBg
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SignalSmallBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignalHistoryLog(
    periods: List<PeriodResult>,
    modifier: Modifier = Modifier
) {
    // Take the last 10 generated predictions
    val recentPredictions = remember(periods) {
        periods.filter { it.predictedSize != null }.take(10)
    }

    val timeFormatter = remember {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("signal_history_log"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = SolidColor(BorderDark))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Signal History",
                        tint = AiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIGNAL HISTORY LOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceCardHigh)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Last 10 Predictions",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Table Header Columns: TIME | PERIOD | PREDICTION | RESULT | STATUS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceCardHigh)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TIME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "PERIOD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "PREDICT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "RESULT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "STATUS",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (recentPredictions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No prediction signals recorded yet",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    recentPredictions.forEach { item ->
                        val formattedTime = timeFormatter.format(Date(item.timestamp))
                        val isActualBig = item.size == BetSize.BIG
                        val isPredictBig = item.predictedSize == BetSize.BIG
                        val isWin = item.wasCorrect == true

                        val numColor = when (item.colour) {
                            BetColour.GREEN -> ColorGreen
                            BetColour.RED -> ColorRed
                            BetColour.VIOLET -> ColorViolet
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceCardHigh.copy(alpha = 0.6f))
                                .border(0.5.dp, BorderDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Time
                            Text(
                                text = formattedTime,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextSecondary,
                                modifier = Modifier.weight(1.1f)
                            )

                            // Period (last 5 digits)
                            Text(
                                text = item.period.takeLast(5),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary,
                                modifier = Modifier.weight(1.1f)
                            )

                            // Prediction (Big/Small)
                            Box(modifier = Modifier.weight(1.2f)) {
                                if (item.predictedSize != null) {
                                    val confStr = item.predictedConfidence?.let { " ${it.toInt()}%" } ?: ""
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isPredictBig) SignalBigBg else SignalSmallBg)
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${item.predictedSize.name}$confStr",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPredictBig) SignalBig else SignalSmall
                                            )
                                        }
                                    }
                                } else {
                                    Text(text = "-", fontSize = 10.sp, color = TextMuted)
                                }
                            }

                            // Actual Result (Big/Small + number badge)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(numColor.copy(alpha = 0.25f))
                                        .border(1.dp, numColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.number.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = numColor
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.size.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActualBig) SignalBig else SignalSmall
                                )
                            }

                            // Status (WIN / LOSS)
                            Box(modifier = Modifier.weight(0.9f)) {
                                if (item.wasCorrect != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isWin) WinGreen.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f))
                                            .border(
                                                0.5.dp,
                                                if (isWin) WinGreen.copy(alpha = 0.5f) else LossRed.copy(alpha = 0.5f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isWin) Icons.Default.Check else Icons.Default.Close,
                                                contentDescription = null,
                                                tint = if (isWin) WinGreen else LossRed,
                                                modifier = Modifier.size(9.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = if (isWin) "WIN" else "FAIL",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (isWin) WinGreen else LossRed
                                            )
                                        }
                                    }
                                } else {
                                    Text(text = "-", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
