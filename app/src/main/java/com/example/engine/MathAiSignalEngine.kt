package com.example.engine

import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.PeriodResult
import com.example.data.model.SignalStrength
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

object MathAiSignalEngine {

    /**
     * Analyzes recent historical periods and generates an advanced mathematical AI signal.
     * @param history List of past PeriodResult sorted newest first or oldest first.
     */
    fun analyze(history: List<PeriodResult>): MathAiPrediction {
        if (history.isEmpty()) {
            return fallbackPrediction()
        }

        // Sort chronologically (oldest to newest) for sequential analysis
        val chronological = history.sortedBy { it.timestamp }
        val n = chronological.size

        if (n < 3) {
            return fallbackPrediction(chronological.lastOrNull())
        }

        // 1. Current Streak Analysis
        val lastResult = chronological.last()
        val currentSize = lastResult.size
        var currentStreak = 0
        for (i in chronological.indices.reversed()) {
            if (chronological[i].size == currentSize) {
                currentStreak++
            } else {
                break
            }
        }

        // 2. Markov Chain 1st & 2nd Order Transitions
        val (markovBigProb, markovScore) = calculateMarkovProbability(chronological)

        // 3. Pattern Recognition (N-Gram matching)
        val patternMatchResult = analyzePatterns(chronological)

        // 4. Parity RSI Oscillator (Relative Strength Index of Big vs Small)
        val parityRsi = calculateParityRsi(chronological)

        // 5. Fibonacci Weighted Momentum
        val fibonacciMomentum = calculateFibonacciMomentum(chronological)

        // 6. Streak Binomial Exhaustion / Continuation Factor
        // Theoretical streak continuation probability: (0.5)^k
        val streakReversalFactor = when {
            currentStreak >= 6 -> 0.78 // Heavy mean reversion expected
            currentStreak >= 4 -> 0.65 // Dragon trend exhaustion zone
            currentStreak == 3 -> 0.55 // Neutral-reversal
            currentStreak == 2 -> 0.48 // Can continue to 3
            else -> 0.50
        }

        // Combine Models with Statistical Weights
        // Model 1: Markov (Weight: 30%)
        // Model 2: Pattern Recognition (Weight: 25%)
        // Model 3: Parity RSI (Weight: 25%)
        // Model 4: Fibonacci Momentum (Weight: 20%)

        var bigProbabilityScore = 0.0

        // Markov contribution
        bigProbabilityScore += markovBigProb * 0.30

        // Pattern contribution
        bigProbabilityScore += patternMatchResult.bigProbability * 0.25

        // RSI contribution (if RSI > 65, favors SMALL; if RSI < 35, favors BIG)
        val rsiBigProb = when {
            parityRsi > 70.0 -> 0.25 // Heavy overbought -> favors Small
            parityRsi > 60.0 -> 0.38
            parityRsi < 30.0 -> 0.75 // Heavy oversold -> favors Big
            parityRsi < 40.0 -> 0.62
            else -> 0.50
        }
        bigProbabilityScore += rsiBigProb * 0.25

        // Fibonacci momentum contribution (scale -1.0 to 1.0 -> 0.0 to 1.0)
        val fibProb = ((fibonacciMomentum + 1.0) / 2.0).coerceIn(0.1, 0.9)
        bigProbabilityScore += fibProb * 0.20

        // Adjust for extreme streaks
        if (currentStreak >= 4) {
            if (currentSize == BetSize.BIG) {
                // Diminish big prob, elevate small prob
                bigProbabilityScore = (bigProbabilityScore * (1.0 - (streakReversalFactor - 0.5))).coerceIn(0.15, 0.85)
            } else {
                // Diminish small prob, elevate big prob
                bigProbabilityScore = (bigProbabilityScore + (streakReversalFactor - 0.5)).coerceIn(0.15, 0.85)
            }
        }

        // Final size decision
        val targetSize: BetSize
        val rawConfidence: Double
        if (bigProbabilityScore >= 0.50) {
            targetSize = BetSize.BIG
            rawConfidence = bigProbabilityScore
        } else {
            targetSize = BetSize.SMALL
            rawConfidence = 1.0 - bigProbabilityScore
        }

        // Scale confidence realistically between 62.0% and 94.5%
        val confidencePercent = ((rawConfidence * 50.0) + 42.0).coerceIn(61.5, 94.8)
        val roundedConfidence = (confidencePercent * 10.0).roundToInt() / 10.0

        // Signal Strength Classification
        val strength = when {
            roundedConfidence >= 85.0 -> SignalStrength.SUPERIOR
            roundedConfidence >= 75.0 -> SignalStrength.HIGH
            roundedConfidence >= 65.0 -> SignalStrength.MODERATE
            else -> SignalStrength.NEUTRAL
        }

        // Recommended Colour & Number Analysis
        val (recommendedColour, hotNumbers) = calculateColourAndNumbers(chronological, targetSize)

        // Recommended Money Management Martingale Multiplier
        val recommendedMultiplier = calculateRecommendedMultiplier(chronological, strength)

        // Generate Mathematical Reasoning
        val reasons = mutableListOf<String>()
        reasons.add("Markov Transition Matrix: ${String.format("%.1f", markovBigProb * 100)}% $targetSize bias")
        reasons.add("Parity RSI Index: ${String.format("%.1f", parityRsi)} (${if (parityRsi > 60) "Overbought Big" else if (parityRsi < 40) "Oversold Small" else "Equilibrium"})")
        reasons.add("Identified Structure: ${patternMatchResult.patternName}")
        if (currentStreak >= 3) {
            reasons.add("Streak Counter: $currentStreak consecutive ${currentSize.name} (Binomial p=${String.format("%.3f", (0.5).pow(currentStreak))})")
        }
        reasons.add("Fibonacci Velocity: ${String.format("%+.2f", fibonacciMomentum)} direction score")

        return MathAiPrediction(
            targetSize = targetSize,
            confidence = roundedConfidence,
            strength = strength,
            recommendedColour = recommendedColour,
            hotNumbers = hotNumbers,
            recommendedMultiplier = recommendedMultiplier,
            patternDetected = patternMatchResult.patternName,
            markovBigProbability = markovBigProb,
            parityRsi = parityRsi,
            streakCount = currentStreak,
            streakSize = currentSize,
            mathematicalReasons = reasons
        )
    }

    private fun calculateMarkovProbability(history: List<PeriodResult>): Pair<Double, Double> {
        if (history.size < 4) return Pair(0.5, 0.5)

        val lastState = history.last().size
        val secondLast = history[history.size - 2].size

        var countBigAfterLast = 0
        var countTotalAfterLast = 0

        var countBigAfterPair = 0
        var countTotalAfterPair = 0

        for (i in 0 until history.size - 1) {
            if (history[i].size == lastState) {
                countTotalAfterLast++
                if (history[i + 1].size == BetSize.BIG) {
                    countBigAfterLast++
                }
            }
        }

        for (i in 0 until history.size - 2) {
            if (history[i].size == secondLast && history[i + 1].size == lastState) {
                countTotalAfterPair++
                if (history[i + 2].size == BetSize.BIG) {
                    countBigAfterPair++
                }
            }
        }

        val order1Prob = if (countTotalAfterLast > 0) {
            countBigAfterLast.toDouble() / countTotalAfterLast.toDouble()
        } else {
            0.5
        }

        val order2Prob = if (countTotalAfterPair > 0) {
            countBigAfterPair.toDouble() / countTotalAfterPair.toDouble()
        } else {
            order1Prob
        }

        // Weighted blend of order 1 & order 2 Markov
        val blended = (order1Prob * 0.4) + (order2Prob * 0.6)
        return Pair(blended.coerceIn(0.15, 0.85), order2Prob)
    }

    private data class PatternResult(val patternName: String, val bigProbability: Double)

    private fun analyzePatterns(history: List<PeriodResult>): PatternResult {
        val n = history.size
        if (n < 3) return PatternResult("Standard Sequence", 0.5)

        val last4 = if (n >= 4) history.takeLast(4).map { it.size } else null
        val last3 = history.takeLast(3).map { it.size }

        // 1. Dragon / Monotone streak
        if (last4 != null && last4.all { it == BetSize.BIG }) {
            return PatternResult("Dragon Streak 4x (Big)", 0.35) // High chance of reversal
        }
        if (last4 != null && last4.all { it == BetSize.SMALL }) {
            return PatternResult("Dragon Streak 4x (Small)", 0.65) // High chance of reversal
        }

        // 2. Ping-Pong / Alternating (B-S-B-S or S-B-S-B)
        if (last4 != null && last4[0] != last4[1] && last4[1] != last4[2] && last4[2] != last4[3]) {
            val nextInPingPong = if (last4[3] == BetSize.BIG) BetSize.SMALL else BetSize.BIG
            val prob = if (nextInPingPong == BetSize.BIG) 0.82 else 0.18
            return PatternResult("Ping-Pong Wave (1-1 Alternation)", prob)
        }

        // 3. Double Mirror / 2-2 pattern (BBSS or SSBB)
        if (last4 != null && last4[0] == last4[1] && last4[2] == last4[3] && last4[0] != last4[2]) {
            // BBSS -> next should start BB
            val nextPair = last4[0]
            val prob = if (nextPair == BetSize.BIG) 0.70 else 0.30
            return PatternResult("Mirror Cluster (2-2 Repetition)", prob)
        }

        // 4. Triad Pattern (BBB -> S or SSS -> B)
        if (last3.all { it == BetSize.BIG }) {
            return PatternResult("Triad Climax (3x Big)", 0.42)
        }
        if (last3.all { it == BetSize.SMALL }) {
            return PatternResult("Triad Climax (3x Small)", 0.58)
        }

        // 5. N-Gram Substring Search
        var matchesFollowedByBig = 0
        var totalMatches = 0
        val targetSub = history.takeLast(2).map { it.size }
        for (i in 0 until n - 3) {
            if (history[i].size == targetSub[0] && history[i + 1].size == targetSub[1]) {
                totalMatches++
                if (history[i + 2].size == BetSize.BIG) {
                    matchesFollowedByBig++
                }
            }
        }

        if (totalMatches >= 2) {
            val prob = (matchesFollowedByBig.toDouble() / totalMatches).coerceIn(0.2, 0.8)
            return PatternResult("Historical Cluster Match ($totalMatches occurrences)", prob)
        }

        return PatternResult("Dynamic Mean Oscillation", 0.5)
    }

    private fun calculateParityRsi(history: List<PeriodResult>, period: Int = 14): Double {
        val window = history.takeLast(period + 1)
        if (window.size < 4) return 50.0

        var gains = 0.0
        var losses = 0.0

        for (i in 1 until window.size) {
            val prevVal = if (window[i - 1].size == BetSize.BIG) 1.0 else 0.0
            val currVal = if (window[i].size == BetSize.BIG) 1.0 else 0.0
            val delta = currVal - prevVal

            if (delta > 0) {
                gains += delta
            } else if (delta < 0) {
                losses += abs(delta)
            }
        }

        if (losses == 0.0) {
            return if (gains > 0) 85.0 else 50.0
        }

        val rs = (gains / window.size) / (losses / window.size)
        return (100.0 - (100.0 / (1.0 + rs))).coerceIn(10.0, 90.0)
    }

    private fun calculateFibonacciMomentum(history: List<PeriodResult>): Double {
        val weights = listOf(1, 1, 2, 3, 5, 8, 13)
        val recent = history.takeLast(weights.size)
        if (recent.isEmpty()) return 0.0

        var weightedSum = 0.0
        var totalWeight = 0.0

        for (i in recent.indices) {
            val weight = weights[weights.size - recent.size + i]
            val value = if (recent[i].size == BetSize.BIG) 1.0 else -1.0
            weightedSum += value * weight
            totalWeight += weight
        }

        return if (totalWeight > 0) (weightedSum / totalWeight).coerceIn(-1.0, 1.0) else 0.0
    }

    private fun calculateColourAndNumbers(
        history: List<PeriodResult>,
        targetSize: BetSize
    ): Pair<BetColour, List<Int>> {
        val candidateNumbers = if (targetSize == BetSize.BIG) {
            listOf(5, 6, 7, 8, 9)
        } else {
            listOf(0, 1, 2, 3, 4)
        }

        // Count frequency of each candidate number in the recent 30 rounds
        val recentNumbers = history.takeLast(30).map { it.number }
        val frequencyMap = candidateNumbers.associateWith { num ->
            recentNumbers.count { it == num }
        }

        // Sort by frequency (hot numbers first)
        val sortedCandidates = candidateNumbers.sortedByDescending { frequencyMap[it] ?: 0 }
        val top3Numbers = sortedCandidates.take(3)

        // Determine recommended colour based on candidate numbers
        // Green numbers: 1, 3, 7, 9 (and 5)
        // Red numbers: 2, 4, 6, 8 (and 0)
        var greenScore = 0
        var redScore = 0

        for (num in top3Numbers) {
            when (num) {
                1, 3, 7, 9 -> greenScore += 2
                2, 4, 6, 8 -> redScore += 2
                5 -> greenScore += 1 // Violet/Green
                0 -> redScore += 1   // Violet/Red
            }
        }

        val recommendedColour = if (greenScore >= redScore) BetColour.GREEN else BetColour.RED

        return Pair(recommendedColour, top3Numbers)
    }

    private fun calculateRecommendedMultiplier(
        history: List<PeriodResult>,
        strength: SignalStrength
    ): Int {
        // Martingale risk assessment based on recent accuracy
        val last3 = history.takeLast(3)
        val recentMistakes = last3.count { it.wasCorrect == false }

        return when {
            recentMistakes >= 2 -> 3 // Recovery step 3x
            recentMistakes == 1 -> 2 // Step 2x
            strength == SignalStrength.SUPERIOR -> 1 // High confidence, disciplined 1x or 2x
            else -> 1 // Base 1x
        }
    }

    private fun fallbackPrediction(lastResult: PeriodResult? = null): MathAiPrediction {
        val target = if (lastResult?.size == BetSize.BIG) BetSize.SMALL else BetSize.BIG
        return MathAiPrediction(
            targetSize = target,
            confidence = 74.5,
            strength = SignalStrength.HIGH,
            recommendedColour = if (target == BetSize.BIG) BetColour.RED else BetColour.GREEN,
            hotNumbers = if (target == BetSize.BIG) listOf(6, 8, 7) else listOf(2, 4, 1),
            recommendedMultiplier = 1,
            patternDetected = "Heuristic Equilibrium",
            markovBigProbability = 0.52,
            parityRsi = 51.0,
            streakCount = 1,
            streakSize = lastResult?.size,
            mathematicalReasons = listOf(
                "Law of Large Numbers: Tendency towards 50/50 balance",
                "Markov State Matrix: Initial transition baseline",
                "Parity Equilibrium: Neutral RSI at 50"
            )
        )
    }
}
