package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.ui.theme.AppTypography
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SimpleWaterFillAnimationScreen(
    modifier: Modifier = Modifier,
    animationDuration: Int = 2500,
    onAnimationEnd: () -> Unit = {}
) {
    val waterLevel = remember { Animatable(0f) }

    // Drive the water fill
    LaunchedEffect(Unit) {
        waterLevel.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration, easing = LinearEasing)
        ).also { result ->
            if (result.endReason == AnimationEndReason.Finished) {
                // tiny cushion so the fade/slide can settle visually
                onAnimationEnd()
            }
        }
    }

    // Horizontal wave offset
    val waveTransition = rememberInfiniteTransition(label = "wave")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (6 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDuration, easing = LinearEasing)
        ),
        label = "wave"
    )

    val backgroundColor = MaterialTheme.colorScheme.background
    val waterColor = MaterialTheme.colorScheme.primary
    val appName = stringResource(R.string.app_name).uppercase()

    // Text fade/slide near the end (last ~12% of the fill)
    val fadeStart = 0.88f
    val tRaw = ((waterLevel.value - fadeStart) / (1f - fadeStart)).coerceIn(0f, 1f)
    val t = FastOutSlowInEasing.transform(tRaw)               // eased 0..1
    val textAlpha = 1f - t                                    // 1 → 0

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        val screenHeight = this@BoxWithConstraints.constraints.maxHeight.toFloat()
        val screenWidth = this@BoxWithConstraints.constraints.maxWidth.toFloat()
        val slidePx = (16 * t) // max 16dp downward as it fades

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWavyWater(
                level = waterLevel.value,
                screenHeight = screenHeight,
                screenWidth = screenWidth,
                waveOffset = waveOffset,
                color = waterColor
            )
        }

        // Centered logo text; fades/slides in the final stretch so it doesn't "teleport"
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = appName,
                color = backgroundColor,
                style = AppTypography.displayLarge.copy(letterSpacing = 5.sp),
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha
                    translationY = slidePx
                }
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
    val amplitude = 20.dp.toPx()
    val waterHeight = screenHeight * level + amplitude
    val path = Path().apply {
        moveTo(0f, screenHeight)
        lineTo(0f, screenHeight - waterHeight)

        val frequency = 2 * PI / screenWidth
        for (x in 0 until screenWidth.toInt()) {
            val y = amplitude * sin(frequency * x + waveOffset)
            lineTo(x.toFloat(), screenHeight - waterHeight + y.toFloat())
        }

        lineTo(screenWidth, screenHeight)
        close()
    }
    drawPath(path = path, color = color)
}
