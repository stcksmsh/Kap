package io.github.stcksmsh.kap.ui.composables

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadAppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.github.stcksmsh.kap.R

@Composable
fun WaterIntakeInput(
    waterIntakeRepository: WaterIntakeRepository,
    context: Context,
    modifier: Modifier
) {

    val volumes = listOf(
        200.0f, 250.0f, 330.0f, 500.0f, 750.0f, -1.0f
    )

    val selectedVolumeUnit = loadAppSettings(context).volumeUnit

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(1.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(volumes) { volume ->
            if (volume == -1.0f) {
                Text(stringResource(R.string.custom))
            } else {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            waterIntakeRepository.insertWaterIntake(
                                WaterIntake(
                                    intakeAmount = volume
                                )
                            )
                        }
                    }
                ) {
                    Text(selectedVolumeUnit.convertMillisToString(volume))
                }
            }
        }

    }
}