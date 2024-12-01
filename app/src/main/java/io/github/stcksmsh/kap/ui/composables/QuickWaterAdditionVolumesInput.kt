package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.model.VolumeUnits

@Composable
fun QuickWaterAdditionVolumesInput(
    selectedVolumeUnit: VolumeUnits,
    quickWaterAdditionVolumes: List<Float>,
    onQuickWaterAdditionVolumesChanged: (List<Float>) -> Unit
) {
    // Use a mutable state to keep track of the raw string input for each TextField
    val textFieldStates = remember(
        selectedVolumeUnit,
        quickWaterAdditionVolumes
    ) {
        mutableStateListOf(*quickWaterAdditionVolumes.map {
            selectedVolumeUnit.convertMillisToString(
                it
            )
        }.toTypedArray())
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = "Quick Addition Volumes",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.primary,
                )

                InfoPopUp(
                    modifier = Modifier.align(Alignment.CenterVertically),
                ) {
                    Text(
                        text = "Volumes of water that can be added with a single tap, also used in the widget.",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            textFieldStates.forEachIndexed { index, text ->
                TextField(
                    value = text,
                    onValueChange = { input ->
                        // Update the string state for the TextField
                        textFieldStates[index] = input

                        // Try parsing the input and update the quickWaterAdditionVolumes list if valid
                        selectedVolumeUnit.convertStringToMillis(input)?.let {
                            val updatedList =
                                quickWaterAdditionVolumes.mapIndexed { i, currentValue ->
                                    if (i == index) it else currentValue
                                }
                            onQuickWaterAdditionVolumesChanged(updatedList)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .width(200.dp) // Restrict the width of the TextField
                )
            }
        }
    }
}
