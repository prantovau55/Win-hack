package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.SignalAccuracyStats
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.AiPurple
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SingleFloatingMod(
    currentPeriod: String,
    secondsLeft: Int,
    prediction: MathAiPrediction,
    accuracyStats: SignalAccuracyStats,
    onClose: () -> Unit,
    onAdjustPeriod: ((Long) -> Unit)? = null,
    onFixPeriod: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Draggable position state (starts at top-right area)
    var offsetX by remember { mutableFloatStateOf(40f) }
    var offsetY by remember { mutableFloatStateOf(160f) }
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val signalColor = if (prediction.targetSize == BetSize.BIG) SignalBig else SignalSmall
    val timerColor = if (secondsLeft <= 5) Color(0xFFFF3D71) else AiCyan

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(0f, 600f)
                    offsetY = (offsetY + dragAmount.y).coerceIn(0f, 1500f)
                }
            }
            .testTag("single_floating_mod")
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark.copy(alpha = 0.94f),
            shadowElevation = 14.dp,
            modifier = Modifier
                .widthIn(max = if (isExpanded) 280.dp else 230.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(signalColor, AiPurple)),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Drag Handle & Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag Floating Mod",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(WinGreen.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI MOD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AiCyan,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Expand / Collapse toggle
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { isExpanded = !isExpanded }
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Close button
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onClose() }
                                .padding(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Floating Mod",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Compact Main Bar: Signal + Timer + Accuracy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCardHigh)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Big/Small Signal Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(signalColor.copy(alpha = 0.2f))
                            .border(1.dp, signalColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = prediction.targetSize.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = signalColor
                        )
                    }

                    // Countdown Timer
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = timerColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${secondsLeft}s",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = timerColor
                        )
                    }

                    // Accuracy Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WinGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        val accText = String.format(Locale.US, "%.0f%%", accuracyStats.accuracyPercentage)
                        Text(
                            text = accText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WinGreen
                        )
                    }
                }

                // Expanded Floating HUD Details
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        // Target Period
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Period:",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "#${currentPeriod.takeLast(7)}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Confidence & Multiplier
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "AI Confidence:",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", prediction.confidence)}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        // Win / Loss record
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Record (W-L):",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${accuracyStats.winCount}W - ${accuracyStats.lossCount}L",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (accuracyStats.isWinStreak) WinGreen else AiGold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Hot Numbers Suggestion
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hot Numbers:",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                prediction.hotNumbers.take(3).forEach { num ->
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceCardHigh)
                                            .border(0.5.dp, BorderGlow, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = num.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Period Quick Alignment (-1 / Fix / +1)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceCardHigh)
                                    .clickable { onAdjustPeriod?.invoke(-1) }
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("-1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(2.2f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AiGold.copy(alpha = 0.2f))
                                    .clickable { onFixPeriod?.invoke() }
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("ম্যাচ পিরিয়ড", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AiGold)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SurfaceCardHigh)
                                    .clickable { onAdjustPeriod?.invoke(1) }
                                    .padding(vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
