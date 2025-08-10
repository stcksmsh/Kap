package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.R

@Composable
fun QuickWaterAdditionVolumesInput(
    selectedVolumeUnit: VolumeUnits,
    quickWaterAdditionVolumes: List<Double>,
    onQuickWaterAdditionVolumesChanged: (List<Double>) -> Unit
) {
    val context = LocalContext.current

    // Use a mutable state to keep track of the raw string input for each TextField
    val textFieldStates = remember(
        selectedVolumeUnit,
        quickWaterAdditionVolumes
    ) {
        mutableStateListOf(*quickWaterAdditionVolumes.map {
            selectedVolumeUnit.toUnitWithLabel(
                context,
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
                    text = stringResource(R.string.quick_addition_volumes),
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
                        text = stringResource(R.string.quick_addition_volumes_description),
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
                        selectedVolumeUnit.fromUnitToMl(input)?.let {
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
