package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.stcksmsh.kap.model.VolumeUnits
import kotlin.math.roundToInt

@Composable
fun CircularProgress(
    selectedVolumeUnit: VolumeUnits,
    current: Double,
    max: Double,
    modifier: Modifier = Modifier
) {
    val progress = current / max
    // Display a circular progress bar
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp)
    ) {
        // Circular progress indicator
        CircularProgressIndicator(
            progress = {
                progress.toFloat()
            },
            strokeWidth = 12.dp,
            modifier = Modifier
                .size(200.dp),
            strokeCap = StrokeCap.Butt,
            gapSize = 2.dp
        )

        // Counter in the center of the circle
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = selectedVolumeUnit.toUnitWithLabel(current),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 24.dp)
            )
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .width(100.dp)
                    .height(1.dp)
                    .padding(top = 4.dp)
            )
            Text(
                text = selectedVolumeUnit.toUnitWithLabel(max),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${progress.times(100).roundToInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}