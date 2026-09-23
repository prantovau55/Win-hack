package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.SignalAccuracyStats
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorViolet
import com.example.ui.theme.LossRed
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
fun SignalAccuracyCounter(
    stats: SignalAccuracyStats,
    onResetStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val accuracyFloat = (stats.accuracyPercentage / 100.0).toFloat().coerceIn(0f, 1f)
    val accuracyFormatted = String.format(Locale.US, "%.1f", stats.accuracyPercentage)

    val accuracyColor = when {
        stats.accuracyPercentage >= 75.0 -> WinGreen
        stats.accuracyPercentage >= 60.0 -> AiGold
        else -> LossRed
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("signal_accuracy_counter"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = SolidColor(BorderDark))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Title & Live Badge & Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = "Signal Accuracy",
                        tint = AiCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "AI ACCURACY & WIN/LOSS TRACKER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = AiCyan,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Signal vs Real-Time API Draw Verification",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Reset Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .clickable { onResetStats() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("reset_accuracy_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Stats",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "RESET",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Hero Accuracy Gauge Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceCardHigh)
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CURRENT AI ACCURACY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$accuracyFormatted%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accuracyColor
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        stats.accuracyPercentage >= 80.0 -> "EXCELLENT"
                                        stats.accuracyPercentage >= 70.0 -> "HIGH PRECISION"
                                        stats.accuracyPercentage >= 50.0 -> "MODERATE"
                                        else -> "CALIBRATING"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = accuracyColor,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Streak Indicator Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (stats.isWinStreak) WinGreen.copy(alpha = 0.18f) else LossRed.copy(alpha = 0.18f))
                                .border(
                                    1.dp,
                                    if (stats.isWinStreak) WinGreen.copy(alpha = 0.5f) else LossRed.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "STREAK",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                                Text(
                                    text = if (stats.currentStreakCount > 0) {
                                        "${stats.currentStreakCount} ${if (stats.isWinStreak) "WINS 🔥" else "LOSS"}"
                                    } else {
                                        "READY"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (stats.isWinStreak) WinGreen else LossRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { accuracyFloat },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = accuracyColor,
                        trackColor = SurfaceCard,
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-Column Stat Cards: WINS | LOSSES | EVALUATED | LATEST STATUS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Wins Counter Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(WinGreen.copy(alpha = 0.12f))
                        .border(1.dp, WinGreen.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = WinGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WINS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = WinGreen
                            )
                        }
                        Text(
                            text = stats.winCount.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = WinGreen
                        )
                    }
                }

                // Losses Counter Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LossRed.copy(alpha = 0.12f))
                        .border(1.dp, LossRed.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = LossRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LOSSES",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LossRed
                            )
                        }
                        Text(
                            text = stats.lossCount.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = LossRed
                        )
                    }
                }

                // Total Signals Evaluated Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "${stats.totalSignals}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }

                // Win Ratio / Factor
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    val ratioStr = if (stats.lossCount > 0) {
                        String.format(Locale.US, "%.1f:1", stats.winCount.toDouble() / stats.lossCount)
                    } else if (stats.winCount > 0) {
                        "${stats.winCount}:0"
                    } else {
                        "0:0"
                    }
                    Column {
                        Text(
                            text = "RATIO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = ratioStr,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AiGold
                        )
                    }
                }
            }

            // Expandable Comparison Strip
            if (stats.recentSignalOutcomes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT SIGNAL MATCHES (${stats.recentSignalOutcomes.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isExpanded) "Hide" else "Show Details",
                            fontSize = 10.sp,
                            color = AiCyan
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = AiCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Horizontal Carousel of Recent Verified Rounds
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(stats.recentSignalOutcomes) { item ->
                        val isWin = item.isWin
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isWin) WinGreen.copy(alpha = 0.15f) else LossRed.copy(alpha = 0.15f))
                                .border(
                                    0.5.dp,
                                    if (isWin) WinGreen.copy(alpha = 0.4f) else LossRed.copy(alpha = 0.4f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${item.period.takeLast(4)}: ",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted
                                )
                                Text(
                                    text = item.predictedSize.name,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.predictedSize == BetSize.BIG) SignalBig else SignalSmall
                                )
                                Text(
                                    text = " ➜ ",
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${item.actualNumber} (${item.actualSize.name})",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = if (isWin) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (isWin) WinGreen else LossRed,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    }
                }

                // Expanded Breakdown Table
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        stats.recentSignalOutcomes.take(5).forEach { item ->
                            val numColor = when (item.actualColour) {
                                BetColour.GREEN -> ColorGreen
                                BetColour.RED -> ColorRed
                                BetColour.VIOLET -> ColorViolet
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceCardHigh.copy(alpha = 0.7f))
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Period #${item.period.takeLast(5)}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Signal: ${item.predictedSize.name} (${item.confidence.toInt()}%)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.predictedSize == BetSize.BIG) SignalBig else SignalSmall
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Result: ${item.actualSize.name}",
                                        fontSize = 10.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(numColor.copy(alpha = 0.25f))
                                            .border(1.dp, numColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = item.actualNumber.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = numColor
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (item.isWin) WinGreen.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (item.isWin) "WIN" else "LOSS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (item.isWin) WinGreen else LossRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
