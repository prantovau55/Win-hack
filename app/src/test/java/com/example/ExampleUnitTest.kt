package com.example

import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.PeriodResult
import com.example.engine.MathAiSignalEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMathematicalAnalysisWithStreak() {
        val history = listOf(
            PeriodResult.fromNumber("20260923001", 6),
            PeriodResult.fromNumber("20260923002", 7),
            PeriodResult.fromNumber("20260923003", 8),
            PeriodResult.fromNumber("20260923004", 9)
        )

        val prediction = MathAiSignalEngine.analyze(history)
        assertNotNull(prediction)
        assertEquals(4, prediction.streakCount)
        assertEquals(BetSize.BIG, prediction.streakSize)
        assertTrue(prediction.confidence in 50.0..99.0)
        assertTrue(prediction.mathematicalReasons.isNotEmpty())
    }

    @Test
    fun testPingPongPatternDetection() {
        val history = listOf(
            PeriodResult.fromNumber("20260923001", 8), // Big
            PeriodResult.fromNumber("20260923002", 2), // Small
            PeriodResult.fromNumber("20260923003", 7), // Big
            PeriodResult.fromNumber("20260923004", 3)  // Small
        )

        val prediction = MathAiSignalEngine.analyze(history)
        assertNotNull(prediction)
        assertTrue(prediction.patternDetected.contains("Ping-Pong"))
        // After B-S-B-S, Ping Pong expects Big
        assertEquals(BetSize.BIG, prediction.targetSize)
    }

    @Test
    fun testColourAndNumberClassification() {
        val pr0 = PeriodResult.fromNumber("1", 0)
        assertEquals(BetSize.SMALL, pr0.size)
        assertEquals(BetColour.RED, pr0.colour)
        assertEquals(BetColour.VIOLET, pr0.secondaryColour)

        val pr5 = PeriodResult.fromNumber("2", 5)
        assertEquals(BetSize.BIG, pr5.size)
        assertEquals(BetColour.GREEN, pr5.colour)
        assertEquals(BetColour.VIOLET, pr5.secondaryColour)

        val pr8 = PeriodResult.fromNumber("3", 8)
        assertEquals(BetSize.BIG, pr8.size)
        assertEquals(BetColour.RED, pr8.colour)
    }

    @Test
    fun testSignalHistoryLogFiltering() {
        val list = (1..20).map { i ->
            PeriodResult.fromNumber(
                period = "202609230$i",
                number = i % 10,
                predictedSize = if (i % 2 == 0) BetSize.BIG else BetSize.SMALL,
                predictedConfidence = 80.0
            )
        }

        val last10Predictions = list.filter { it.predictedSize != null }.take(10)
        assertEquals(10, last10Predictions.size)
        assertEquals("2026092301", last10Predictions.first().period)
    }

    @Test
    fun testWinGoDrawItemConversion() {
        val item = com.example.data.remote.WinGoDrawItem(
            issueNumber = "20260923100051289",
            number = 4,
            color = "red"
        )
        val periodResult = PeriodResult.fromNumber(
            period = item.issueNumber,
            number = item.number
        )
        assertEquals("20260923100051289", periodResult.period)
        assertEquals(4, periodResult.number)
        assertEquals(BetSize.SMALL, periodResult.size)
        assertEquals(BetColour.RED, periodResult.colour)
    }

    @Test
    fun testAccuracyStatsCalculation() {
        val periods = listOf(
            PeriodResult.fromNumber("001", 7, predictedSize = BetSize.BIG, predictedConfidence = 85.0), // Win
            PeriodResult.fromNumber("002", 8, predictedSize = BetSize.BIG, predictedConfidence = 88.0), // Win
            PeriodResult.fromNumber("003", 2, predictedSize = BetSize.BIG, predictedConfidence = 70.0), // Loss
            PeriodResult.fromNumber("004", 1, predictedSize = BetSize.SMALL, predictedConfidence = 90.0) // Win
        )

        val evaluated = periods.filter { it.predictedSize != null && it.wasCorrect != null }
        val wins = evaluated.count { it.wasCorrect == true }
        val losses = evaluated.count { it.wasCorrect == false }
        val accuracy = (wins.toDouble() / evaluated.size) * 100.0

        assertEquals(4, evaluated.size)
        assertEquals(3, wins)
        assertEquals(1, losses)
        assertEquals(75.0, accuracy, 0.01)
    }

    @Test
    fun testPeriodFormatting() {
        val currentPeriod = "20260923100051289"
        val nextPeriod = (currentPeriod.toLong() + 1).toString()
        assertEquals("20260923100051290", nextPeriod)
        assertEquals(17, nextPeriod.length)
    }

    @Test
    fun testLiveActivePeriodCalculation() {
        val period30s = com.example.data.remote.WinGoApiService.calculateLiveActivePeriod(
            com.example.data.remote.WinGoGameType.WINGO_30S
        )
        val period1m = com.example.data.remote.WinGoApiService.calculateLiveActivePeriod(
            com.example.data.remote.WinGoGameType.WINGO_1M
        )
        assertTrue(period30s.isNotBlank())
        assertTrue(period1m.isNotBlank())
        assertTrue(period30s.contains("10005"))
        assertTrue(period1m.contains("10001"))
    }
}
