package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.model.SettingsData
import io.github.stcksmsh.kap.model.VolumeUnits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun WaterIntakeAddPanel(
    modifier: Modifier = Modifier,
    settingsData: SettingsData,
    waterIntakeRepository: WaterIntakeRepository,
    coroutineScope: CoroutineScope
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between rows
    ) {
        repeat(2) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space between columns
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { columnIndex ->
                    // Calculate the current button index
                    val buttonIndex = rowIndex * 3 + columnIndex

                    if (rowIndex == 1 && columnIndex == 2) {
                        // Custom button for the last cell
                        CustomWaterIntakeAddButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            waterIntakeRepository = waterIntakeRepository,
                            coroutineScope = coroutineScope,
                            selectedVolumeUnits = settingsData.volumeUnit
                        )
                    } else {
                        // Regular water intake buttons
                        val waterAmount = settingsData.quickWaterAdditionVolumes[buttonIndex]
                        WaterIntakeAddButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            waterAmount = waterAmount,
                            waterIntakeRepository = waterIntakeRepository,
                            coroutineScope = coroutineScope,
                            selectedVolumeUnits = settingsData.volumeUnit
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WaterIntakeAddButton(
    modifier: Modifier = Modifier,
    waterAmount: Float,
    waterIntakeRepository: WaterIntakeRepository,
    coroutineScope: CoroutineScope,
    selectedVolumeUnits: VolumeUnits
) {
    Button(
        onClick = {
            coroutineScope.launch {
                waterIntakeRepository.insertWaterIntake(
                    WaterIntake(
                        intakeAmount = waterAmount,
                        date = Date()
                    )
                )
            }
        },
        modifier = modifier
    ) {
        Text(
            text = "${selectedVolumeUnits.convertMillisToString(waterAmount)} ${selectedVolumeUnits.symbol}",
            maxLines = 1
        )
    }
}

@Composable
private fun CustomWaterIntakeAddButton(
    modifier: Modifier = Modifier,
    waterIntakeRepository: WaterIntakeRepository,
    coroutineScope: CoroutineScope,
    selectedVolumeUnits: VolumeUnits
) {
    Button(
        onClick = {
            // TODO: Implement custom dialog logic for entering a custom amount
        },
        modifier = modifier
    ) {
        Text(
            text = "Custom",
            maxLines = 1
        )
    }
}
