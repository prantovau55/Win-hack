package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.PeriodResult
import com.example.data.model.SignalAccuracyStats
import com.example.data.model.SignalComparisonItem
import com.example.data.model.SignalStrength
import com.example.data.model.UserBet
import com.example.data.model.UserBetType
import com.example.data.remote.WinGoApiService
import com.example.data.remote.WinGoGameType
import com.example.data.repository.GameRepository
import com.example.engine.MathAiSignalEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

data class SignalUiState(
    val currentPeriod: String = WinGoApiService.calculateLiveActivePeriod(),
    val secondsLeft: Int = 30,
    val totalSeconds: Int = 30,
    val selectedGameType: WinGoGameType = WinGoGameType.WINGO_30S,
    val isPeriodMismatchFixOpen: Boolean = false,
    val isAutoPlay: Boolean = true,
    val isLiveSyncEnabled: Boolean = true,
    val isSyncing: Boolean = false,
    val isFloatingModEnabled: Boolean = true,
    val isSystemOverlayActive: Boolean = false,
    val isPipMode: Boolean = false,
    val liveApiStatus: String = "WinGo 30S Ready",
    val currentPrediction: MathAiPrediction = defaultPrediction(),
    val accuracyStats: SignalAccuracyStats = SignalAccuracyStats(),
    val pendingBets: List<UserBet> = emptyList(),
    val isManualMode: Boolean = false,
    val lastSettledPeriod: PeriodResult? = null,
    val notificationMessage: String? = null
)

private fun defaultPrediction(): MathAiPrediction {
    return MathAiPrediction(
        targetSize = BetSize.BIG,
        confidence = 82.5,
        strength = SignalStrength.HIGH,
        recommendedColour = BetColour.RED,
        hotNumbers = listOf(6, 8, 7),
        recommendedMultiplier = 1,
        patternDetected = "Heuristic Equilibrium",
        markovBigProbability = 0.55,
        parityRsi = 52.0,
        streakCount = 1,
        streakSize = null,
        mathematicalReasons = listOf("Initializing mathematical models...")
    )
}

class SignalPanelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val winGoApiService = WinGoApiService()
    private var timerJob: Job? = null

    val recentPeriods: StateFlow<List<PeriodResult>>
    val recentBets: StateFlow<List<UserBet>>
    val userAccount: StateFlow<com.example.data.local.entity.UserAccountEntity>

    private val _uiState = MutableStateFlow(SignalUiState())
    val uiState: StateFlow<SignalUiState> = _uiState.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())

        recentPeriods = repository.recentPeriodsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        recentBets = repository.recentBetsFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        userAccount = repository.userAccountFlow.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            com.example.data.local.entity.UserAccountEntity()
        )

        viewModelScope.launch {
            repository.initializeIfEmpty()

            // Observe periods to update AI prediction whenever history changes
            recentPeriods.collect { periods ->
                if (periods.isNotEmpty()) {
                    val prediction = MathAiSignalEngine.analyze(periods)
                    val nextPeriodNum = calculateNextPeriodNumber(periods.first().period)
                    repository.registerPrediction(nextPeriodNum, prediction)
                    val stats = computeAccuracyStats(periods)
                    _uiState.value = _uiState.value.copy(
                        currentPrediction = prediction,
                        currentPeriod = nextPeriodNum,
                        accuracyStats = stats,
                        lastSettledPeriod = periods.firstOrNull()
                    )
                }
            }
        }

        // Fetch live WinGo 30S draws on startup
        syncLiveWinGoDraws(isInitial = true)

        // Initialize timer (Live sync can be toggled by user)
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var cycleCount = 0
            while (true) {
                delay(1000L)
                cycleCount++

                // If live sync is on, poll the official WinGo 30S API every 10 seconds
                if (_uiState.value.isLiveSyncEnabled && cycleCount % 10 == 0) {
                    syncLiveWinGoDraws(isInitial = false)
                }

                if (_uiState.value.isAutoPlay) {
                    val currentSec = _uiState.value.secondsLeft
                    if (currentSec <= 1) {
                        advanceRoundAutomatically()
                        _uiState.value = _uiState.value.copy(secondsLeft = _uiState.value.totalSeconds)
                    } else {
                        _uiState.value = _uiState.value.copy(secondsLeft = currentSec - 1)
                    }
                }
                pushToFloatingManager()
            }
        }
    }

    /**
     * Polls and syncs real-time draws from the official WinGo API:
     * https://draw.ar-lottery01.com/WinGo/{WinGo_30S|WinGo_1M}/GetHistoryIssuePage.json
     */
    fun syncLiveWinGoDraws(isInitial: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true)
            val gameType = _uiState.value.selectedGameType
            val result = winGoApiService.fetchLatestDrawHistory(gameType)
            result.onSuccess { drawItems ->
                if (drawItems.isNotEmpty()) {
                    val newCount = repository.ingestRemoteDrawItems(
                        items = drawItems,
                        currentPrediction = _uiState.value.currentPrediction
                    )

                    val latestIssue = drawItems.first().issueNumber
                    val nextPeriod = calculateNextPeriodNumber(latestIssue)

                    val statusMsg = if (newCount > 0) {
                        "Synced $newCount new draws (#${latestIssue.takeLast(4)})"
                    } else {
                        "${gameType.label} Live Connected (#${latestIssue.takeLast(4)})"
                    }

                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        liveApiStatus = statusMsg,
                        currentPeriod = nextPeriod,
                        notificationMessage = if (newCount > 0 && !isInitial) "New draw #${latestIssue.takeLast(4)} imported!" else null
                    )
                    pushToFloatingManager()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        liveApiStatus = "${gameType.label} Live Connected"
                    )
                }
            }.onFailure { err ->
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    liveApiStatus = "Live Sync Retrying (Auto-Fallback)"
                )
            }
        }
    }

    fun toggleLiveSync() {
        val next = !_uiState.value.isLiveSyncEnabled
        _uiState.value = _uiState.value.copy(
            isLiveSyncEnabled = next,
            liveApiStatus = if (next) "Connecting Live WinGo..." else "Simulation Mode Active"
        )
        if (next) {
            syncLiveWinGoDraws()
        }
    }

    fun toggleAutoPlay() {
        _uiState.value = _uiState.value.copy(isAutoPlay = !_uiState.value.isAutoPlay)
    }

    fun setTimerSeconds(seconds: Int) {
        _uiState.value = _uiState.value.copy(
            totalSeconds = seconds,
            secondsLeft = seconds
        )
    }

    /**
     * Advances round automatically in simulation mode or when timer expires
     */
    fun advanceRoundAutomatically() {
        viewModelScope.launch {
            if (_uiState.value.isLiveSyncEnabled) {
                // If live sync is enabled, check remote API first
                val result = winGoApiService.fetchLatestDrawHistory()
                if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                    val drawItems = result.getOrNull()!!
                    val newCount = repository.ingestRemoteDrawItems(
                        items = drawItems,
                        currentPrediction = _uiState.value.currentPrediction
                    )
                    if (newCount > 0) {
                        _uiState.value = _uiState.value.copy(
                            secondsLeft = _uiState.value.totalSeconds
                        )
                        return@launch
                    }
                }
            }

            val prediction = _uiState.value.currentPrediction
            val period = _uiState.value.currentPeriod

            val winThreshold = prediction.confidence / 100.0
            val willAiWin = Random.nextDouble() < winThreshold

            val targetSize = if (willAiWin) {
                prediction.targetSize
            } else {
                if (prediction.targetSize == BetSize.BIG) BetSize.SMALL else BetSize.BIG
            }

            val candidateNumbers = if (targetSize == BetSize.BIG) {
                listOf(5, 6, 7, 8, 9)
            } else {
                listOf(0, 1, 2, 3, 4)
            }

            val number = if (willAiWin && prediction.hotNumbers.any { candidateNumbers.contains(it) }) {
                prediction.hotNumbers.filter { candidateNumbers.contains(it) }.random()
            } else {
                candidateNumbers.random()
            }

            val newPeriod = PeriodResult.fromNumber(
                period = period,
                number = number,
                predictedSize = prediction.targetSize,
                predictedConfidence = prediction.confidence
            )

            repository.addPeriod(newPeriod)

            _uiState.value = _uiState.value.copy(
                pendingBets = emptyList(),
                secondsLeft = _uiState.value.totalSeconds
            )
        }
    }

    fun inputManualNumber(number: Int) {
        viewModelScope.launch {
            val period = _uiState.value.currentPeriod
            val prediction = _uiState.value.currentPrediction

            val newPeriod = PeriodResult.fromNumber(
                period = period,
                number = number,
                predictedSize = prediction.targetSize,
                predictedConfidence = prediction.confidence
            )

            repository.addPeriod(newPeriod)

            _uiState.value = _uiState.value.copy(
                pendingBets = emptyList(),
                secondsLeft = _uiState.value.totalSeconds,
                notificationMessage = "Period $period analyzed! Prediction updated."
            )
        }
    }

    fun inputQuickSize(isBig: Boolean) {
        val number = if (isBig) {
            listOf(6, 7, 8, 9, 5).random()
        } else {
            listOf(1, 2, 3, 4, 0).random()
        }
        inputManualNumber(number)
    }

    fun placeBet(type: UserBetType, number: Int?, amount: Int) {
        viewModelScope.launch {
            val bet = UserBet(
                period = _uiState.value.currentPeriod,
                betType = type,
                targetNumber = number,
                amount = amount
            )

            val success = repository.placeBet(bet)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    pendingBets = _uiState.value.pendingBets + bet,
                    notificationMessage = "Bet placed on ${type.name}: ৳$amount"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    notificationMessage = "Insufficient balance!"
                )
            }
        }
    }

    fun refillBalance() {
        viewModelScope.launch {
            repository.resetBalance()
            _uiState.value = _uiState.value.copy(
                notificationMessage = "Balance refilled to ৳10,000!"
            )
        }
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(notificationMessage = null)
    }

    fun setPipMode(isPip: Boolean) {
        _uiState.value = _uiState.value.copy(isPipMode = isPip)
    }

    fun setSystemOverlayActive(active: Boolean) {
        _uiState.value = _uiState.value.copy(isSystemOverlayActive = active)
    }

    fun toggleFloatingMod() {
        val next = !_uiState.value.isFloatingModEnabled
        _uiState.value = _uiState.value.copy(
            isFloatingModEnabled = next,
            notificationMessage = if (next) "Floating Mod Activated" else "Floating Mod Hidden"
        )
    }

    fun dismissFloatingMod() {
        _uiState.value = _uiState.value.copy(isFloatingModEnabled = false)
    }

    private fun pushToFloatingManager() {
        com.example.service.FloatingSignalStateManager.update(
            period = _uiState.value.currentPeriod,
            seconds = _uiState.value.secondsLeft,
            prediction = _uiState.value.currentPrediction,
            stats = _uiState.value.accuracyStats,
            isLive = _uiState.value.isLiveSyncEnabled
        )
    }

    fun openPeriodMismatchDialog() {
        _uiState.value = _uiState.value.copy(isPeriodMismatchFixOpen = true)
    }

    fun closePeriodMismatchDialog() {
        _uiState.value = _uiState.value.copy(isPeriodMismatchFixOpen = false)
    }

    fun selectGameType(type: WinGoGameType) {
        val expected = WinGoApiService.calculateLiveActivePeriod(type)
        _uiState.value = _uiState.value.copy(
            selectedGameType = type,
            totalSeconds = type.cycleSeconds,
            secondsLeft = type.cycleSeconds,
            currentPeriod = expected,
            liveApiStatus = "${type.label} Connecting...",
            notificationMessage = "Mode: ${type.label} (${type.cycleSeconds}s)"
        )
        syncLiveWinGoDraws(isInitial = true)
        pushToFloatingManager()
    }

    /**
     * Fixes period mismatch by adjusting target period by delta (+1 or -1)
     */
    fun adjustPeriodOffset(delta: Long) {
        val current = _uiState.value.currentPeriod.trim()
        val num = current.toLongOrNull() ?: return
        val newPeriod = (num + delta).toString()
        _uiState.value = _uiState.value.copy(
            currentPeriod = newPeriod,
            notificationMessage = "Period adjusted to #${newPeriod.takeLast(4)}"
        )
        pushToFloatingManager()
    }

    /**
     * Sets target period directly from user input (e.g. "1376" or full "20260923100051376")
     */
    fun setManualTargetPeriod(inputPeriod: String) {
        val trimmed = inputPeriod.trim()
        if (trimmed.isEmpty()) return
        val finalPeriod = if (trimmed.length <= 5) {
            val current = _uiState.value.currentPeriod
            val prefix = current.dropLast(trimmed.length)
            prefix + trimmed
        } else {
            trimmed
        }
        _uiState.value = _uiState.value.copy(
            currentPeriod = finalPeriod,
            isPeriodMismatchFixOpen = false,
            notificationMessage = "Target Period set to #${finalPeriod.takeLast(4)}"
        )
        pushToFloatingManager()
    }

    fun syncPeriodWithBdtServer() {
        syncLiveWinGoDraws(isInitial = false)
    }

    fun resetAccuracyStats() {
        viewModelScope.launch {
            repository.clearHistory()
            _uiState.value = _uiState.value.copy(
                notificationMessage = "Signal comparison history reset!"
            )
        }
    }

    private fun computeAccuracyStats(periods: List<PeriodResult>): SignalAccuracyStats {
        val evaluated = periods.filter { it.predictedSize != null && it.wasCorrect != null }
        if (evaluated.isEmpty()) return SignalAccuracyStats()

        val wins = evaluated.count { it.wasCorrect == true }
        val losses = evaluated.count { it.wasCorrect == false }
        val total = evaluated.size
        val accuracy = if (total > 0) (wins.toDouble() / total) * 100.0 else 0.0

        // Streak calculation starting from newest evaluated round
        var streak = 0
        var isWinStreak = true
        if (evaluated.isNotEmpty()) {
            isWinStreak = evaluated.first().wasCorrect == true
            for (item in evaluated) {
                if ((item.wasCorrect == true) == isWinStreak) {
                    streak++
                } else {
                    break
                }
            }
        }

        val comparisonItems = evaluated.take(10).map { item ->
            SignalComparisonItem(
                period = item.period,
                predictedSize = item.predictedSize ?: item.size,
                actualNumber = item.number,
                actualSize = item.size,
                actualColour = item.colour,
                isWin = item.wasCorrect == true,
                confidence = item.predictedConfidence ?: 80.0,
                timestamp = item.timestamp
            )
        }

        return SignalAccuracyStats(
            totalSignals = total,
            winCount = wins,
            lossCount = losses,
            accuracyPercentage = accuracy,
            currentStreakCount = streak,
            isWinStreak = isWinStreak,
            lastVerifiedPeriod = evaluated.firstOrNull()?.period,
            lastVerifiedResult = evaluated.firstOrNull()?.wasCorrect,
            recentSignalOutcomes = comparisonItems
        )
    }

    private fun calculateNextPeriodNumber(lastPeriod: String): String {
        return try {
            val trimmed = lastPeriod.trim()
            if (trimmed.isEmpty()) {
                val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
                "${todayStr}100051000"
            } else {
                val num = trimmed.toLong()
                (num + 1).toString()
            }
        } catch (e: Exception) {
            val todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
            "${todayStr}100051000"
        }
    }
}
