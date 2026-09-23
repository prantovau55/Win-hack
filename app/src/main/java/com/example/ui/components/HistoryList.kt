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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HistoryList(
    periods: List<PeriodResult>,
    totalProfit: Int,
    modifier: Modifier = Modifier
) {
    val evaluatedPeriods = periods.filter { it.wasCorrect != null }
    val correctCount = evaluatedPeriods.count { it.wasCorrect == true }
    val totalEvaluated = if (evaluatedPeriods.isNotEmpty()) evaluatedPeriods.size else 1
    val accuracyPercent = if (evaluatedPeriods.isNotEmpty()) {
        (correctCount * 100) / totalEvaluated
    } else {
        0
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("history_list"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = SolidColor(BorderDark))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = AiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PAST RESULTS & AI ACCURACY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Real-time verification",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Verification Stats Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Accuracy Rate Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "AI WIN RATE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "$accuracyPercent%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (accuracyPercent >= 70) WinGreen else AiGold
                        )
                    }
                }

                // AI Predictions Count
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "TOTAL TRACKED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "${evaluatedPeriods.size} Rounds",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }

                // Net Profit/Loss
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "NET P&L",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "${if (totalProfit >= 0) "+" else ""}৳$totalProfit",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (totalProfit >= 0) WinGreen else LossRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Column Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERIOD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "NUMBER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "RESULT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "AI PREDICT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "STATUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // History Items
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                periods.take(15).forEach { item ->
                    val isBig = item.size == BetSize.BIG
                    val numColor = when (item.colour) {
                        BetColour.GREEN -> ColorGreen
                        BetColour.RED -> ColorRed
                        BetColour.VIOLET -> ColorViolet
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCardHigh)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Period
                        Text(
                            text = item.period.takeLast(5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary,
                            modifier = Modifier.weight(1.3f)
                        )

                        // Number badge
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(numColor.copy(alpha = 0.25f))
                                    .border(1.dp, numColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.number.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = numColor
                                )
                            }
                        }

                        // Size badge
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = item.size.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBig) SignalBig else SignalSmall
                            )
                        }

                        // Predicted Size
                        Box(
                            modifier = Modifier.weight(1.2f)
                        ) {
                            if (item.predictedSize != null) {
                                Text(
                                    text = item.predictedSize.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.predictedSize == BetSize.BIG) SignalBig else SignalSmall
                                )
                            } else {
                                Text(
                                    text = "-",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        // Status (Win / Loss)
                        Box(
                            modifier = Modifier.weight(0.9f)
                        ) {
                            if (item.wasCorrect != null) {
                                val isWin = item.wasCorrect
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isWin) WinGreen.copy(alpha = 0.2f) else LossRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isWin) Icons.Default.Check else Icons.Default.Close,
                                            contentDescription = null,
                                            tint = if (isWin) WinGreen else LossRed,
                                            modifier = Modifier.size(11.dp)
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
                                Text(
                                    text = "-",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
