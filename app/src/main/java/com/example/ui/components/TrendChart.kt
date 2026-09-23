package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
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
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TrendChart(
    periods: List<PeriodResult>,
    modifier: Modifier = Modifier
) {
    val recent = periods.take(30)
    val bigCount = recent.count { it.size == BetSize.BIG }
    val smallCount = recent.count { it.size == BetSize.SMALL }
    val total = if (recent.isNotEmpty()) recent.size else 1
    val bigPercent = (bigCount * 100) / total
    val smallPercent = (smallCount * 100) / total

    val greenCount = recent.count { it.colour == BetColour.GREEN }
    val redCount = recent.count { it.colour == BetColour.RED }
    val violetCount = recent.count { it.number == 0 || it.number == 5 }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trend_chart"),
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
                        imageVector = Icons.Default.GridOn,
                        contentDescription = "Trend Road",
                        tint = AiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE TREND ROAD & STATS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Last ${recent.size} Rounds",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Big vs Small Ratio Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "BIG: $bigCount ($bigPercent%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SignalBig
                )
                Text(
                    text = "SMALL: $smallCount ($smallPercent%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SignalSmall
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Two-tone split ratio bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(if (bigCount > 0) bigCount.toFloat() else 0.01f)
                        .background(SignalBig)
                )
                Box(
                    modifier = Modifier
                        .weight(if (smallCount > 0) smallCount.toFloat() else 0.01f)
                        .background(SignalSmall)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bead Road (Horizontal scroll of beads)
            Text(
                text = "BEAD ROAD MAP",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Show most recent first or in order
                recent.forEach { item ->
                    val isBig = item.size == BetSize.BIG
                    val sizeColor = if (isBig) SignalBig else SignalSmall
                    val colColor = when (item.colour) {
                        BetColour.GREEN -> ColorGreen
                        BetColour.RED -> ColorRed
                        BetColour.VIOLET -> ColorViolet
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(36.dp)
                    ) {
                        // Bead circle
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(colColor.copy(alpha = 0.25f))
                                .border(1.5.dp, colColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isBig) "B" else "S",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = sizeColor
                                )
                                Text(
                                    text = item.number.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Period suffix
                        Text(
                            text = item.period.takeLast(2),
                            fontSize = 8.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Colour Distribution Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Green: $greenCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Red: $redCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorRed
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Violet: $violetCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorViolet
                    )
                }
            }
        }
    }
}
