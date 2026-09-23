package com.example.service

import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.SignalAccuracyStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class FloatingStateData(
    val currentPeriod: String = "20260923100051000",
    val secondsLeft: Int = 30,
    val targetSize: BetSize = BetSize.BIG,
    val confidence: Double = 86.5,
    val accuracyPercentage: Double = 80.0,
    val winCount: Int = 0,
    val lossCount: Int = 0,
    val isLiveConnected: Boolean = true
)

object FloatingSignalStateManager {
    private val _state = MutableStateFlow(FloatingStateData())
    val state: StateFlow<FloatingStateData> = _state.asStateFlow()

    fun update(
        period: String,
        seconds: Int,
        prediction: MathAiPrediction,
        stats: SignalAccuracyStats,
        isLive: Boolean
    ) {
        _state.value = FloatingStateData(
            currentPeriod = period,
            secondsLeft = seconds,
            targetSize = prediction.targetSize,
            confidence = prediction.confidence,
            accuracyPercentage = stats.accuracyPercentage,
            winCount = stats.winCount,
            lossCount = stats.lossCount,
            isLiveConnected = isLive
        )
    }

    fun adjustPeriod(delta: Long) {
        val current = _state.value.currentPeriod
        val num = current.toLongOrNull() ?: return
        val newPeriod = (num + delta).toString()
        _state.value = _state.value.copy(currentPeriod = newPeriod)
    }
}
