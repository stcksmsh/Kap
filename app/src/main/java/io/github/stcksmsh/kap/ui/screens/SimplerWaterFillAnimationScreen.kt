package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.stcksmsh.kap.ui.theme.AppTypography
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SimpleWaterFillAnimationScreen(
    context: Context,
    modifier: Modifier = Modifier,
    animationDuration: Int = 2500,
    onAnimationEnd: () -> Unit = {}
) {
    val waterLevel = remember { Animatable(0f) }


    // Define a custom easing with a slow start

    // Start the water fill animation
    LaunchedEffect(Unit) {
        waterLevel.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = LinearEasing)
        ).also { result ->
            if (result.endReason == AnimationEndReason.Finished) {
                onAnimationEnd() // Trigger the end action
            }
        }
    }

    // Infinite transition to animate wave offset for horizontal movement
    val waveTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ), label = "wave"
    )

    // Background and water colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val waterColor = MaterialTheme.colorScheme.primary // Light blue color for water

    val appNameRes = context.applicationInfo.labelRes
    val appName = if (appNameRes != 0) {
        context.getString(appNameRes)
    } else {
        "App Name Not Found" // Fallback if the app name resource is missing
    }.uppercase()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val screenHeight = this@BoxWithConstraints.constraints.maxHeight.toFloat()
        val screenWidth = this@BoxWithConstraints.constraints.maxWidth.toFloat()

        // Draw the water rising effect with a wavy top
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWavyWater(waterLevel.value, screenHeight, screenWidth, waveOffset, waterColor)
        }

        // Centered text logo that becomes visible as water rises
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = appName,
                color = backgroundColor, // Same as background initially
                style = AppTypography.displayLarge.copy(
                    letterSpacing = 5.sp,
                )
            )
        }
    }
}

// Draw water with a wavy surface
fun DrawScope.drawWavyWater(
    level: Float,
    screenHeight: Float,
    screenWidth: Float,
    waveOffset: Float,
    color: Color
) {
    val amplitude = 20.dp.toPx() // Wave height
    val waterHeight = screenHeight * level + amplitude // Adjust for wave height
    val path = Path().apply {
        // Start from the bottom-left corner
        moveTo(0f, screenHeight)

        // Draw the left side up to the water level
        lineTo(0f, screenHeight - waterHeight)

        // Draw a sine wave across the top of the water with moving offset
        val frequency = 2 * PI / screenWidth // Wave frequency
        for (x in 0 until screenWidth.toInt()) {
            val y = amplitude * sin(frequency * x + waveOffset) // Apply offset for movement
            lineTo(x.toFloat(), screenHeight - waterHeight + y.toFloat())
        }

        // Draw the right side and close the path
        lineTo(screenWidth, screenHeight)
        close()
    }

    // Draw the water shape with the wave effect
    drawPath(
        path = path,
        color = color
    )
}