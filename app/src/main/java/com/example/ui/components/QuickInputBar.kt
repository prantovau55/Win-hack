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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AiCyan
import com.example.ui.theme.BorderDark
import com.example.ui.theme.ColorGreen
import com.example.ui.theme.ColorRed
import com.example.ui.theme.ColorViolet
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalBigBg
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SignalSmallBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuickInputBar(
    onInputNumber: (Int) -> Unit,
    onInputQuickSize: (Boolean) -> Unit, // true = Big, false = Small
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_input_bar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(BorderDark))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Input,
                        contentDescription = "Feed Data",
                        tint = AiCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "MANUAL SIGNAL FEEDER / EXTERNAL GAME INPUT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Tap to analyze",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick 1-tap Big & Small buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick Big Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SignalBigBg)
                        .border(1.dp, SignalBig, RoundedCornerShape(10.dp))
                        .clickable { onInputQuickSize(true) }
                        .testTag("quick_input_big"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+ ADD BIG",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SignalBig
                        )
                        Text(
                            text = " (5-9)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SignalBig.copy(alpha = 0.8f)
                        )
                    }
                }

                // Quick Small Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SignalSmallBg)
                        .border(1.dp, SignalSmall, RoundedCornerShape(10.dp))
                        .clickable { onInputQuickSize(false) }
                        .testTag("quick_input_small"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+ ADD SMALL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = SignalSmall
                        )
                        Text(
                            text = " (0-4)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = SignalSmall.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 0 - 9 Specific Number buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (0..9).forEach { num ->
                    val color = when (num) {
                        0 -> ColorViolet
                        1, 3, 7, 9 -> ColorGreen
                        2, 4, 6, 8 -> ColorRed
                        5 -> ColorViolet
                        else -> ColorGreen
                    }
                    val isBig = num >= 5

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f))
                            .border(1.dp, color, CircleShape)
                            .clickable { onInputNumber(num) }
                            .testTag("number_btn_$num"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = num.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = color
                        )
                    }
                }
            }
        }
    }
}
