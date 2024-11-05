package io.github.stcksmsh.kap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import io.github.stcksmsh.kap.data.hasUserData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.data.saveUserData
import io.github.stcksmsh.kap.ui.UserInputScreen
import io.github.stcksmsh.kap.ui.WaterFillAnimationScreen
import io.github.stcksmsh.kap.ui.theme.MyAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var showInputScreen by remember { mutableStateOf(!hasUserData(this)) }
            var showAnimation by remember { mutableStateOf(true) }
            val context = this

            // Single fadeAlpha for both fade-out and fade-in transitions
            var fadeAlpha by remember { mutableStateOf(1f) }
            val animationDuration = 500
            val animatedAlpha = animateFloatAsState(
                targetValue = fadeAlpha,
                animationSpec = tween(durationMillis = animationDuration), label = ""
            )

            val coroutineScope = rememberCoroutineScope()

            MyAppTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        showAnimation -> {
                            // Display the initial water fill animation
                            WaterFillAnimationScreen(
                                onAnimationEnd = {
                                    fadeAlpha = 0f // Start fade-out
                                    // Delay the change to let fade-out complete
                                    coroutineScope.launch {
                                        delay(animationDuration.toLong())
                                        showAnimation = false
                                        fadeAlpha = 1f // Start fade-in
                                    }
                                }
                            )
                        }
                        showInputScreen -> {
                            // User input screen with fade-in/fade-out on exit
                            UserInputScreen(
                                modifier = Modifier.graphicsLayer { alpha = animatedAlpha.value },
                            ) { userData ->
                                fadeAlpha = 0f // Start fade-out when saving data
                                // Delay transition to allow fade-out to complete
                                coroutineScope.launch {
                                    delay(animationDuration.toLong())
                                    saveUserData(context, userData)
                                    showInputScreen = false
                                    fadeAlpha = 1f // Start fade-in for WaterAdditionScreen
                                }
                            }
                        }
                        else -> {
                            // Main Water Addition Screen with fade-in effect
                            Text(
                                text = "Daily Intake: ${loadUserData(context)?.dalyWaterGoal ?: 0  } ml",
                                modifier = Modifier.graphicsLayer { alpha = animatedAlpha.value }
                            )
                        }
                    }
                }
            }
        }
    }
}
