package io.github.stcksmsh.kap.ui.composables

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.model.AppSettings
import io.github.stcksmsh.kap.model.VolumeUnits
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WaterIntakeAddPanel(
    modifier: Modifier = Modifier,
    appSettings: AppSettings,
    waterIntakeRepository: WaterIntakeRepository,
) {
    val coroutineScope = rememberCoroutineScope()
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
                            selectedVolumeUnits = appSettings.volumeUnit,
                        )
                    } else {
                        // Regular water intake buttons
                        val waterAmount = appSettings.quickWaterAdditionVolumes[buttonIndex]
                        WaterIntakeAddButton(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            waterAmount = waterAmount,
                            waterIntakeRepository = waterIntakeRepository,
                            coroutineScope = coroutineScope,
                            selectedVolumeUnits = appSettings.volumeUnit
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
                        intakeAmount = waterAmount
                    )
                )
            }
        },
        modifier = modifier
    ) {
        Text(
            text = selectedVolumeUnits.convertMillisToUnitString(waterAmount),
            maxLines = 1
        )
    }
}

@Composable
private fun CustomWaterIntakeAddButton(
    modifier: Modifier = Modifier,
    waterIntakeRepository: WaterIntakeRepository,
    coroutineScope: CoroutineScope,
    selectedVolumeUnits: VolumeUnits,
) {
    // State to manage the visibility of the dialog
    var showDialog by remember { mutableStateOf(false) }

    // Button to trigger the dialog
    Button(
        onClick = {
            showDialog = true },
        modifier = modifier
    ) {
        Text(
            text = "Custom",
            maxLines = 1
        )
    }

    // Dialog for entering a custom amount
    if (showDialog) {
        CustomAmountDialog(
            onConfirm = { customAmount ->
                coroutineScope.launch {
                    waterIntakeRepository.insertWaterIntake(
                        WaterIntake(
                            intakeAmount = customAmount
                        )
                    )
                }
                showDialog = false
            },
            onDismiss = { showDialog = false },
            selectedVolumeUnits = selectedVolumeUnits
        )
    }
}

@Composable
fun CustomAmountDialog(
    onConfirm: (Float) -> Unit,
    onDismiss: () -> Unit,
    selectedVolumeUnits: VolumeUnits
) {
    var customAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Enter Custom Amount")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = { customAmount = it },
                    label = { Text("Amount (${selectedVolumeUnits.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = customAmount.toFloatOrNull()
                    if (amount != null && amount > 0) {
                        onConfirm(amount)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}