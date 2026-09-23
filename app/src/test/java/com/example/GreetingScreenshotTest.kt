package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.BetColour
import com.example.data.model.BetSize
import com.example.data.model.MathAiPrediction
import com.example.data.model.SignalStrength
import com.example.ui.components.SignalCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val samplePrediction = MathAiPrediction(
        targetSize = BetSize.BIG,
        confidence = 88.5,
        strength = SignalStrength.SUPERIOR,
        recommendedColour = BetColour.RED,
        hotNumbers = listOf(6, 8, 7),
        recommendedMultiplier = 1,
        patternDetected = "Ping-Pong Wave",
        markovBigProbability = 0.72,
        parityRsi = 48.0,
        streakCount = 2,
        streakSize = BetSize.SMALL,
        mathematicalReasons = listOf(
            "Markov 2nd-order indicates 72.0% Big bias",
            "Parity RSI Equilibrium at 48.0"
        )
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        SignalCard(prediction = samplePrediction)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
