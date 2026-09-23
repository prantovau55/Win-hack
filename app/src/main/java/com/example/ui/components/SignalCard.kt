package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.SignalStrength
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.AiPurple
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorViolet
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

@Composable
fun SignalCard(
    prediction: MathAiPrediction,
    modifier: Modifier = Modifier
) {
    var expandedDetails by remember { mutableStateOf(false) }

    val isBig = prediction.targetSize == BetSize.BIG
    val signalColor = if (isBig) SignalBig else SignalSmall
    val signalBg = if (isBig) SignalBigBg else SignalSmallBg

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("signal_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    signalColor.copy(alpha = 0.6f),
                    BorderDark,
                    signalColor.copy(alpha = 0.2f)
                )
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Badge Row: Mathematical AI Engine & Strength
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Mathematics AI",
                        tint = AiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MATHEMATICS AI SIGNAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan,
                        letterSpacing = 0.8.sp
                    )
                }

                // Strength Pill
                val strengthColor = when (prediction.strength) {
                    SignalStrength.SUPERIOR -> AiGold
                    SignalStrength.HIGH -> WinGreen
                    SignalStrength.MODERATE -> AiCyan
                    SignalStrength.NEUTRAL -> TextMuted
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(strengthColor.copy(alpha = 0.15f))
                        .border(1.dp, strengthColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = prediction.strength.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = strengthColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Signal Hero Box: BIG or SMALL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scaleAnim)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                signalBg,
                                SurfaceCardHigh.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .border(1.5.dp, signalColor.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "NEXT PREDICTED RESULT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = prediction.targetSize.name,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = signalColor,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBig) "Numbers 5, 6, 7, 8, 9" else "Numbers 0, 1, 2, 3, 4",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Confidence Level Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = "Accuracy",
                            tint = signalColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Mathematical Confidence",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "${prediction.confidence}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = signalColor
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (prediction.confidence / 100.0).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = signalColor,
                    trackColor = BorderDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secondary Insights Row: Recommended Colour & Hot Numbers & Martingale
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Colour Prediction Box
                val colColor = when (prediction.recommendedColour) {
                    BetColour.GREEN -> ColorGreen
                    BetColour.RED -> ColorRed
                    BetColour.VIOLET -> ColorViolet
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, colColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "AI COLOUR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(colColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = prediction.recommendedColour.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = colColor
                            )
                        }
                    }
                }

                // Hot Numbers Box
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, BorderGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "TOP NUMBERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            prediction.hotNumbers.forEach { num ->
                                val numColor = when (num) {
                                    1, 3, 7, 9 -> ColorGreen
                                    2, 4, 6, 8 -> ColorRed
                                    else -> ColorViolet
                                }
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(numColor.copy(alpha = 0.25f))
                                        .border(1.dp, numColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = num.toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = numColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Money Management / Martingale Step
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, AiGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "RISK STEP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${prediction.recommendedMultiplier}X Bet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Mathematical Analysis Details Accordion
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { expandedDetails = !expandedDetails }
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Analysis",
                        tint = AiCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (expandedDetails) "Hide Mathematical AI Breakdown" else "View Mathematical AI Proof & Breakdown",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AiCyan
                    )
                }

                Icon(
                    imageVector = if (expandedDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle",
                    tint = AiCyan,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expandedDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgDark)
                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pattern Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AiGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detected Structure: ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        Text(
                            text = prediction.patternDetected,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AiGold
                        )
                    }

                    // Key Math Indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Markov P(Big)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${String.format("%.1f", prediction.markovBigProbability * 100)}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Parity RSI",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = String.format("%.1f", prediction.parityRsi),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (prediction.parityRsi > 65 || prediction.parityRsi < 35) AiGold else TextPrimary
                            )
                        }

                        Column {
                            Text(
                                text = "Active Streak",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${prediction.streakCount}x ${prediction.streakSize?.name ?: "-"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Reasons List
                    prediction.mathematicalReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = WinGreen,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = reason,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
