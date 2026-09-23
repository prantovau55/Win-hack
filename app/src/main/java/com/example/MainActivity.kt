package com.example

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Rational
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.service.FloatingSignalService
import com.example.ui.SignalPanelScreen
import com.example.ui.SignalPanelViewModel
import com.example.ui.components.PipFloatingView
import com.example.ui.theme.BgDark
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SignalPanelViewModel by viewModels()

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                startFloatingService()
                viewModel.setSystemOverlayActive(true)
                Toast.makeText(this, "Floating Mod Activated over Chrome!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Overlay permission not granted. Using PiP Mode.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgDark
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    if (uiState.isPipMode) {
                        PipFloatingView(
                            currentPeriod = uiState.currentPeriod,
                            secondsLeft = uiState.secondsLeft,
                            prediction = uiState.currentPrediction,
                            accuracyStats = uiState.accuracyStats
                        )
                    } else {
                        SignalPanelScreen(
                            viewModel = viewModel,
                            onLaunchChromeWithFloatingMod = { launchChromeWithFloatingMod() },
                            onTogglePipMode = { enterPipMode() },
                            onToggleSystemOverlay = { toggleSystemOverlay() }
                        )
                    }
                }
            }
        }
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val params = PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .build()
                viewModel.setPipMode(true)
                enterPictureInPictureMode(params)
            } catch (e: Exception) {
                Toast.makeText(this, "PiP Mode not available on this device", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "PiP requires Android 8.0+", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleSystemOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                Toast.makeText(
                    this,
                    "Please allow 'Display over other apps' to float signal over Chrome",
                    Toast.LENGTH_LONG
                ).show()
                overlayPermissionLauncher.launch(intent)
            } else {
                val currentState = viewModel.uiState.value.isSystemOverlayActive
                if (currentState) {
                    stopFloatingService()
                    viewModel.setSystemOverlayActive(false)
                    Toast.makeText(this, "Floating Overlay Stopped", Toast.LENGTH_SHORT).show()
                } else {
                    startFloatingService()
                    viewModel.setSystemOverlayActive(true)
                    Toast.makeText(this, "Floating Overlay Started!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            startFloatingService()
            viewModel.setSystemOverlayActive(true)
        }
    }

    private fun startFloatingService() {
        FloatingSignalService.start(this)
    }

    private fun stopFloatingService() {
        FloatingSignalService.stop(this)
    }

    private fun launchChromeWithFloatingMod() {
        // First ensure floating mod is enabled (either system overlay or PiP)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
            startFloatingService()
            viewModel.setSystemOverlayActive(true)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPipMode()
        }

        // Launch Chrome
        try {
            val chromeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setPackage("com.android.chrome")
            }
            startActivity(chromeIntent)
        } catch (e: Exception) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(browserIntent)
            } catch (ex: Exception) {
                Toast.makeText(this, "Browser could not be opened", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        viewModel.setPipMode(isInPictureInPictureMode)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // If floating mod is enabled and user presses home to open Chrome, auto-enter PiP
        if (viewModel.uiState.value.isFloatingModEnabled &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || !Settings.canDrawOverlays(this))
        ) {
            enterPipMode()
        }
    }
}
