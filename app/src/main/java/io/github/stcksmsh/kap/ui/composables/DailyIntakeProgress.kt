package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.R

@Composable
fun DailyIntakeProgress(
    dailyGoalMillis: Double,
    currentIntakeMillis: Double,
    volumeUnit: VolumeUnits,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Display daily target
        Text(
            text = stringResource(R.string.daily_goal_template, volumeUnit.toUnitWithLabel(dailyGoalMillis)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Display current intake
        Text(
            text = stringResource(R.string.current_intake_template, volumeUnit.toUnitWithLabel(currentIntakeMillis)),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Progress bar with percentage
        val progress = (currentIntakeMillis / dailyGoalMillis).coerceIn(0.0, 1.0)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ),

            ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress.toFloat())
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    ),
            )
        }

        // Percentage Text Below Progress Bar
        Text(
            text = stringResource(R.string.daily_progress_template, (progress * 100).toInt()),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
