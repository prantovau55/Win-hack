package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.remote.WinGoGameType
import com.example.ui.components.ChromeFloatingBar
import com.example.ui.components.HistoryList
import com.example.ui.components.LiveApiSyncBar
import com.example.ui.components.PeriodMatchDialog
import com.example.ui.components.QuickInputBar
import com.example.ui.components.SignalAccuracyCounter
import com.example.ui.components.SignalCard
import com.example.ui.components.SignalHistoryLog
import com.example.ui.components.SingleFloatingMod
import com.example.ui.components.TimerBar
import com.example.ui.components.TrendChart
import com.example.ui.theme.AiCyan
import com.example.ui.theme.AiGold
import com.example.ui.theme.BgDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.SignalBig
import com.example.ui.theme.SignalSmall
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardHigh
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalPanelScreen(
    viewModel: SignalPanelViewModel,
    onLaunchChromeWithFloatingMod: () -> Unit = {},
    onTogglePipMode: () -> Unit = {},
    onToggleSystemOverlay: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val periods by viewModel.recentPeriods.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.notificationMessage) {
        uiState.notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            containerColor = BgDark,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(SignalBig, SignalSmall)
                                    )
                                )
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(SurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Functions,
                                    contentDescription = "Math AI",
                                    tint = AiCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BIG / SMALL",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AiCyan.copy(alpha = 0.2f))
                                        .border(0.5.dp, AiCyan, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "AI ENGINE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AiCyan
                                    )
                                }
                            }
                            Text(
                                text = "Mathematical Signal & Probability Panel",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    // Floating Mod Toggle Button
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (uiState.isFloatingModEnabled) AiCyan.copy(alpha = 0.2f) else SurfaceCardHigh)
                            .border(
                                1.dp,
                                if (uiState.isFloatingModEnabled) AiCyan else BorderDark,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.toggleFloatingMod() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("floating_mod_toggle_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureInPictureAlt,
                                contentDescription = "Floating Mod",
                                tint = if (uiState.isFloatingModEnabled) AiCyan else TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (uiState.isFloatingModEnabled) "FLOAT: ON" else "FLOAT: OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isFloatingModEnabled) AiCyan else TextMuted
                            )
                        }
                    }

                    // Quick stats pill
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceCardHigh)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                contentDescription = null,
                                tint = AiGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE MATH",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiGold
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                // Game Mode Selector (WinGo 30S / 1Min / 3Min / 5Min)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WinGoGameType.values().forEach { type ->
                        val isSelected = type == uiState.selectedGameType
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AiCyan else SurfaceCard)
                                .border(1.dp, if (isSelected) AiCyan else BorderDark, RoundedCornerShape(10.dp))
                                .clickable { viewModel.selectGameType(type) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color.Black else TextSecondary
                            )
                        }
                    }
                }
            }

            item {
                // Timer & Period Bar with Period Match Trigger
                TimerBar(
                    currentPeriod = uiState.currentPeriod,
                    secondsLeft = uiState.secondsLeft,
                    totalSeconds = uiState.totalSeconds,
                    selectedGameType = uiState.selectedGameType,
                    isAutoPlay = uiState.isAutoPlay,
                    onToggleAutoPlay = { viewModel.toggleAutoPlay() },
                    onManualAdvance = { viewModel.advanceRoundAutomatically() },
                    onOpenPeriodMatch = { viewModel.openPeriodMismatchDialog() }
                )
            }

            item {
                // Live WinGo 30S API Sync Bar (draw.ar-lottery01.com)
                LiveApiSyncBar(
                    isLiveSyncEnabled = uiState.isLiveSyncEnabled,
                    isSyncing = uiState.isSyncing,
                    statusText = uiState.liveApiStatus,
                    onToggleLiveSync = { viewModel.toggleLiveSync() },
                    onManualSync = { viewModel.syncLiveWinGoDraws() }
                )
            }

            item {
                // Chrome & Browser Floating Mod Launcher
                ChromeFloatingBar(
                    isSystemOverlayActive = uiState.isSystemOverlayActive,
                    onLaunchChromeWithFloatingMod = onLaunchChromeWithFloatingMod,
                    onTogglePipMode = onTogglePipMode,
                    onToggleSystemOverlay = onToggleSystemOverlay
                )
            }

            item {
                // Main Mathematics AI Signal Hero Card
                SignalCard(
                    prediction = uiState.currentPrediction
                )
            }

            item {
                // Real-time Win/Loss Counter & Accuracy Display
                SignalAccuracyCounter(
                    stats = uiState.accuracyStats,
                    onResetStats = { viewModel.resetAccuracyStats() }
                )
            }

            item {
                // Manual Result Input / External Feeder
                QuickInputBar(
                    onInputNumber = { num -> viewModel.inputManualNumber(num) },
                    onInputQuickSize = { isBig -> viewModel.inputQuickSize(isBig) }
                )
            }

            item {
                // Signal History log component (Last 10 predictions)
                SignalHistoryLog(
                    periods = periods
                )
            }

            item {
                // Bead Road & Trend Chart
                TrendChart(
                    periods = periods
                )
            }

            item {
                // Past Results & AI Accuracy Track
                HistoryList(
                    periods = periods,
                    totalProfit = 0
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Single Floating Mod on top of full home scroll
    if (uiState.isFloatingModEnabled) {
        SingleFloatingMod(
            currentPeriod = uiState.currentPeriod,
            secondsLeft = uiState.secondsLeft,
            prediction = uiState.currentPrediction,
            accuracyStats = uiState.accuracyStats,
            onClose = { viewModel.dismissFloatingMod() },
            onAdjustPeriod = { delta -> viewModel.adjustPeriodOffset(delta) },
            onFixPeriod = { viewModel.openPeriodMismatchDialog() },
            modifier = Modifier.align(Alignment.TopStart)
        )
    }

    // Period Mismatch / Correction Dialog
    if (uiState.isPeriodMismatchFixOpen) {
        PeriodMatchDialog(
            currentPeriod = uiState.currentPeriod,
            selectedGameType = uiState.selectedGameType,
            onSelectGameType = { type -> viewModel.selectGameType(type) },
            onAdjustOffset = { delta -> viewModel.adjustPeriodOffset(delta) },
            onSetManualPeriod = { period -> viewModel.setManualTargetPeriod(period) },
            onAutoSyncServer = { viewModel.syncPeriodWithBdtServer() },
            onDismiss = { viewModel.closePeriodMismatchDialog() }
        )
    }
}
}
