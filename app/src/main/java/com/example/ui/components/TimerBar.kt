package com.example.ui.components

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
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.WinGoGameType
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ColorRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WinGreen

@Composable
fun TimerBar(
    currentPeriod: String,
    secondsLeft: Int,
    totalSeconds: Int,
    selectedGameType: WinGoGameType,
    isAutoPlay: Boolean,
    onToggleAutoPlay: () -> Unit,
    onManualAdvance: () -> Unit,
    onOpenPeriodMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = (secondsLeft.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val timerColor = if (secondsLeft <= 5) ColorRed else AiCyan

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
            .testTag("timer_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Period Number Section
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AvTimer,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "TARGET PERIOD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Clickable Game Type Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AiCyan.copy(alpha = 0.15f))
                                .clickable { onOpenPeriodMatch() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = selectedGameType.label.replace("WinGo ", ""),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiCyan
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentPeriod,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Period Match / Fix Button & Countdown Timer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "Fix / Match Period" quick pill button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AiGold.copy(alpha = 0.15f))
                            .border(1.dp, AiGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { onOpenPeriodMatch() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("fix_period_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Fix Period",
                                tint = AiGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ম্যাচ করুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiGold
                            )
                        }
                    }

                    // Circular Countdown Timer
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(42.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(42.dp),
                            color = timerColor,
                            strokeWidth = 3.5.dp,
                            trackColor = BorderDark
                        )
                        Text(
                            text = if (secondsLeft < 10) "0$secondsLeft" else "$secondsLeft",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = timerColor,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Auto Play / Next Trigger button
                    IconButton(
                        onClick = {
                            if (isAutoPlay) {
                                onToggleAutoPlay()
                            } else {
                                onManualAdvance()
                            }
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isAutoPlay) AiCyan.copy(alpha = 0.2f) else SurfaceCardHigh)
                            .border(1.dp, if (isAutoPlay) AiCyan else BorderDark, CircleShape)
                            .testTag("timer_action_button")
                    ) {
                        Icon(
                            imageVector = if (isAutoPlay) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = "Trigger next round",
                            tint = if (isAutoPlay) AiCyan else TextPrimary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}
