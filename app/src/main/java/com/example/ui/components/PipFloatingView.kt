package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.SignalAccuracyStats
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.util.Locale

@Composable
fun PipFloatingView(
    currentPeriod: String,
    secondsLeft: Int,
    prediction: MathAiPrediction,
    accuracyStats: SignalAccuracyStats,
    modifier: Modifier = Modifier
) {
    val signalColor = if (prediction.targetSize == BetSize.BIG) SignalBig else SignalSmall
    val timerColor = if (secondsLeft <= 5) Color(0xFFFF3D71) else AiCyan

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceCard)
                .border(1.dp, signalColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Period & Acc
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(WinGreen)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "#${currentPeriod.takeLast(6)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(WinGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.0f%% ACC", accuracyStats.accuracyPercentage),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = WinGreen
                    )
                }
            }

            // Center Signal & Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Signal Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(signalColor.copy(alpha = 0.2f))
                        .border(1.5.dp, signalColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prediction.targetSize.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = signalColor
                    )
                }

                // Countdown Timer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${secondsLeft}s",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = timerColor
                    )
                    Text(
                        text = "30S TIMER",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }

            // Bottom Bar: Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI Conf: ${String.format(Locale.US, "%.1f", prediction.confidence)}%",
                    fontSize = 9.sp,
                    color = AiCyan,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${accuracyStats.winCount}W - ${accuracyStats.lossCount}L",
                    fontSize = 9.sp,
                    color = if (accuracyStats.isWinStreak) WinGreen else AiGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
