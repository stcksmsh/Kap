package io.github.stcksmsh.kap.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun WaterFillAnimationScreen(modifier: Modifier = Modifier, onAnimationEnd: () -> Unit = {}) {
    val waterLevel = remember { Animatable(0f) }
    val waveOffset = remember { Animatable(0f) }
    val bubbles = remember { generateBubbles() }
    val coroutineScope = rememberCoroutineScope()

    // Define a custom easing with a slow start
    val slowStartEasing = CubicBezierEasing(0.1f, 0.05f, 0.35f, 1.0f)

    // Animation state to control fade-out effect
    var isFadeOut by remember { mutableStateOf(false) }

    // Start the water fill animation
    LaunchedEffect(Unit) {
        waterLevel.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4000, easing = slowStartEasing)
        ).also { result ->
            if (result.endReason == AnimationEndReason.Finished) {
                isFadeOut = true // Trigger fade-out effect
                delay(500) // Wait for fade-out to complete
                onAnimationEnd() // Notify parent composable to show main content
            }
        }
    }
    LaunchedEffect(Unit) {
        waveOffset.animateTo(
            targetValue = 3f,
            animationSpec = tween(durationMillis = 4000, easing = EaseOut)
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val screenHeight = this@BoxWithConstraints.constraints.maxHeight.toFloat()
        val screenWidth = this@BoxWithConstraints.constraints.maxWidth.toFloat()

        // Draw water and bubbles
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw the rising water with a wavy top
            drawWavyWater(waterLevel.value, screenHeight, screenWidth, waveOffset.value)

            // Animate bubbles
            bubbles.forEach { bubble ->
                drawBubble(bubble, screenHeight, waterLevel)
            }
        }
    }

    val slowBubbleEasing = CubicBezierEasing(0.3f, 0.0f, 0.6f, 1.0f)
    // Trigger bubble animations
    bubbles.forEach { bubble ->
        coroutineScope.launch {
            delay(Random.nextLong(0, 2000))
            repeat(Int.MAX_VALUE) {
                bubble.progress.snapTo(0f)
                bubble.progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = bubble.duration,
                        easing = slowBubbleEasing
                    )
                )
            }
        }
    }
}

data class Bubble(val x: Float, val radius: Float, val duration: Int, val progress: Animatable<Float, AnimationVector1D>)

fun generateBubbles(): List<Bubble> {
    return List(200) {
        Bubble(
            x = 1 - Random.nextFloat() *  2,
            radius = Random.nextFloat() * 20 + 7,
            duration = Random.nextInt(2000, 4000),
            progress = Animatable(0f)
        )
    }
}

// Draw water with a wavy surface
fun DrawScope.drawWavyWater(level: Float, screenHeight: Float, screenWidth: Float, waveOffset: Float) {
    val amplitude = 20.dp.toPx() // Wave height
    val waterHeight = screenHeight * level + amplitude
    val path = Path().apply {
        // Start from the bottom-left corner
        moveTo(0f, screenHeight)

        // Draw the left side up to the water level
        lineTo(0f, screenHeight - waterHeight)

        // Draw a sine wave across the top of the water
        val frequency = 2 * Math.PI / screenWidth // Wave frequency
        for (x in 0 until screenWidth.toInt()) {
            val y = amplitude * sin(frequency * (x + waveOffset * screenWidth)) // Sine wave
            lineTo(x.toFloat(), screenHeight - waterHeight + y.toFloat())
        }

        // Draw the right side and close the path
        lineTo(screenWidth, screenHeight)
        close()
    }

    // Draw the water shape with the wave effect
    drawPath(
        path = path,
        color = Color(0xFF00BFFF) // Light blue water color
    )
}

// Draw a bubble that moves from the bottom to the top of the screen
fun DrawScope.drawBubble(bubble: Bubble, screenHeight: Float, waterLevel: Animatable<Float, AnimationVector1D>) {
    // yOffset starts at screenHeight (bottom of the screen) and moves to 0f (top of the screen)
    if(bubble.progress.value == 0f || bubble.progress.value > waterLevel.value) return

    var bubbleYOffset = screenHeight * (0.5f - bubble.progress.value)

    translate(left = bubble.x * size.width, top = bubbleYOffset) {
        drawCircle(
            color = Color.White,
            radius = bubble.radius
        )
    }
}
