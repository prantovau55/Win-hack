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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.UserBet
import com.example.data.model.UserBetType
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.BorderDark
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

@Composable
fun BettingPanel(
    balance: Int,
    prediction: MathAiPrediction,
    activeBets: List<UserBet>,
    onPlaceBet: (type: UserBetType, number: Int?, amount: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAmount by remember { mutableIntStateOf(100) }
    val chipPresets = listOf(10, 50, 100, 500, 1000)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("betting_panel"),
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
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Simulated Trading",
                        tint = AiGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SIMULATED TRADING DESK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiGold,
                        letterSpacing = 0.5.sp
                    )
                }

                // 1-Click Follow AI Button
                Button(
                    onClick = {
                        val betType = if (prediction.targetSize == BetSize.BIG) UserBetType.BIG else UserBetType.SMALL
                        val martAmount = (selectedAmount * prediction.recommendedMultiplier).coerceAtMost(balance)
                        if (martAmount > 0) {
                            onPlaceBet(betType, null, martAmount)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AiCyan,
                        contentColor = androidx.compose.ui.graphics.Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("follow_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "FOLLOW AI (${prediction.recommendedMultiplier}X)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chip Amount Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                chipPresets.forEach { chip ->
                    val isSelected = selectedAmount == chip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AiGold else SurfaceCardHigh)
                            .border(
                                1.dp,
                                if (isSelected) AiGold else BorderDark,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedAmount = chip }
                            .testTag("chip_$chip"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chip.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) androidx.compose.ui.graphics.Color.Black else TextPrimary
                        )
                    }
                }

                // 2X Button
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                        .clickable {
                            selectedAmount = (selectedAmount * 2).coerceAtMost(balance.coerceAtLeast(10))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "2X",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AiCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Trading Buttons: BIG vs SMALL (1:2)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bet BIG
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SignalBigBg)
                        .border(1.5.dp, SignalBig, RoundedCornerShape(12.dp))
                        .clickable {
                            if (balance >= selectedAmount) {
                                onPlaceBet(UserBetType.BIG, null, selectedAmount)
                            }
                        }
                        .testTag("bet_big_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BET BIG (5-9)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SignalBig
                        )
                        Text(
                            text = "Payout 1:2",
                            fontSize = 10.sp,
                            color = SignalBig.copy(alpha = 0.8f)
                        )
                    }
                }

                // Bet SMALL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SignalSmallBg)
                        .border(1.5.dp, SignalSmall, RoundedCornerShape(12.dp))
                        .clickable {
                            if (balance >= selectedAmount) {
                                onPlaceBet(UserBetType.SMALL, null, selectedAmount)
                            }
                        }
                        .testTag("bet_small_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BET SMALL (0-4)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SignalSmall
                        )
                        Text(
                            text = "Payout 1:2",
                            fontSize = 10.sp,
                            color = SignalSmall.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Colour Trading Buttons: GREEN, VIOLET, RED
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Green Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorGreen.copy(alpha = 0.2f))
                        .border(1.dp, ColorGreen, RoundedCornerShape(10.dp))
                        .clickable {
                            if (balance >= selectedAmount) {
                                onPlaceBet(UserBetType.GREEN, null, selectedAmount)
                            }
                        }
                        .testTag("bet_green_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Green (1:2)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorGreen
                    )
                }

                // Violet Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorViolet.copy(alpha = 0.2f))
                        .border(1.dp, ColorViolet, RoundedCornerShape(10.dp))
                        .clickable {
                            if (balance >= selectedAmount) {
                                onPlaceBet(UserBetType.VIOLET, null, selectedAmount)
                            }
                        }
                        .testTag("bet_violet_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Violet (1:4.5)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorViolet
                    )
                }

                // Red Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(ColorRed.copy(alpha = 0.2f))
                        .border(1.dp, ColorRed, RoundedCornerShape(10.dp))
                        .clickable {
                            if (balance >= selectedAmount) {
                                onPlaceBet(UserBetType.RED, null, selectedAmount)
                            }
                        }
                        .testTag("bet_red_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Red (1:2)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorRed
                    )
                }
            }

            // Display Active Bets Placed for Upcoming Period
            if (activeBets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCardHigh)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "ACTIVE BETS PLACED FOR NEXT ROUND:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AiCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        activeBets.forEach { bet ->
                            val label = when (bet.betType) {
                                UserBetType.BIG -> "BIG"
                                UserBetType.SMALL -> "SMALL"
                                UserBetType.GREEN -> "GREEN"
                                UserBetType.RED -> "RED"
                                UserBetType.VIOLET -> "VIOLET"
                                UserBetType.NUMBER -> "No.${bet.targetNumber}"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BorderDark)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$label ৳${bet.amount}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
