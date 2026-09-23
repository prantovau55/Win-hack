package com.example.data.model

enum class BetSize {
    BIG,   // Numbers 5, 6, 7, 8, 9
    SMALL  // Numbers 0, 1, 2, 3, 4
}

enum class BetColour {
    GREEN,  // 1, 3, 7, 9 (+ 5 half violet)
    RED,    // 2, 4, 6, 8 (+ 0 half violet)
    VIOLET  // 0, 5
}

data class PeriodResult(
    val period: String,
    val number: Int,
    val size: BetSize,
    val colour: BetColour,
    val secondaryColour: BetColour? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val predictedSize: BetSize? = null,
    val predictedConfidence: Double? = null,
    val wasCorrect: Boolean? = null
) {
    companion object {
        fun fromNumber(period: String, number: Int, predictedSize: BetSize? = null, predictedConfidence: Double? = null): PeriodResult {
            val validNum = number.coerceIn(0, 9)
            val size = if (validNum >= 5) BetSize.BIG else BetSize.SMALL
            val primaryColour = when (validNum) {
                0 -> BetColour.RED
                1, 3, 7, 9 -> BetColour.GREEN
                2, 4, 6, 8 -> BetColour.RED
                5 -> BetColour.GREEN
                else -> BetColour.GREEN
            }
            val secondary = if (validNum == 0 || validNum == 5) BetColour.VIOLET else null
            val wasCorrect = if (predictedSize != null) predictedSize == size else null

            return PeriodResult(
                period = period,
                number = validNum,
                size = size,
                colour = primaryColour,
                secondaryColour = secondary,
                predictedSize = predictedSize,
                predictedConfidence = predictedConfidence,
                wasCorrect = wasCorrect
            )
        }
    }
}

enum class SignalStrength(val label: String) {
    SUPERIOR("ULTRA CONFLUENCE"),
    HIGH("HIGH ACCURACY"),
    MODERATE("MODERATE SIGNAL"),
    NEUTRAL("NEUTRAL / CAUTION")
}

data class MathAiPrediction(
    val targetSize: BetSize,
    val confidence: Double, // 50.0 to 98.0
    val strength: SignalStrength,
    val recommendedColour: BetColour,
    val hotNumbers: List<Int>,
    val recommendedMultiplier: Int, // 1, 2, 3, 5, 8
    val patternDetected: String,
    val markovBigProbability: Double,
    val parityRsi: Double,
    val streakCount: Int,
    val streakSize: BetSize?,
    val mathematicalReasons: List<String>
)

enum class UserBetType {
    BIG,
    SMALL,
    RED,
    GREEN,
    VIOLET,
    NUMBER
}

data class UserBet(
    val id: Long = 0,
    val period: String,
    val betType: UserBetType,
    val targetNumber: Int? = null,
    val amount: Int,
    val winAmount: Int = 0,
    val isSettled: Boolean = false,
    val isWin: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class SignalComparisonItem(
    val period: String,
    val predictedSize: BetSize,
    val actualNumber: Int,
    val actualSize: BetSize,
    val actualColour: BetColour,
    val isWin: Boolean,
    val confidence: Double,
    val timestamp: Long
)

data class SignalAccuracyStats(
    val totalSignals: Int = 0,
    val winCount: Int = 0,
    val lossCount: Int = 0,
    val accuracyPercentage: Double = 0.0,
    val currentStreakCount: Int = 0,
    val isWinStreak: Boolean = true,
    val lastVerifiedPeriod: String? = null,
    val lastVerifiedResult: Boolean? = null,
    val recentSignalOutcomes: List<SignalComparisonItem> = emptyList()
)
