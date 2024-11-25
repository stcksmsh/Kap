package io.github.stcksmsh.kap.ui.composables

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.calculateOptimalWaterIntake
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.data.saveSettingsData
import io.github.stcksmsh.kap.data.saveUserData
import io.github.stcksmsh.kap.model.UserData
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits
import io.github.stcksmsh.kap.ui.theme.Typography

@Composable
fun UserDataInput(
    context: Context, modifier: Modifier = Modifier, onSave: () -> Unit
) {

    val userData = loadUserData(context)

    val settingsData = loadSettingsData(context)
    var selectedWeightUnit by remember { mutableStateOf(settingsData.weightUnit) }
    var selectedVolumeUnit by remember { mutableStateOf(settingsData.volumeUnit) }

    var ageInt by remember { mutableIntStateOf(userData.age) }
    var weightFloat by remember { mutableFloatStateOf(userData.weight) }
    var dailyPhysicalActivityInt by remember {
        mutableIntStateOf(
            userData.dailyPhysicalActivity
        )
    }
    var dailyWaterGoalFloat by remember { mutableFloatStateOf(userData.dailyWaterGoal) }

    // Remember and initialize states for user input fields
    val age by remember {
        derivedStateOf {
            if (ageInt > 0) ageInt.toString() else ""
        }
    }
    val weight by remember {
        derivedStateOf {
            if (weightFloat > 0f) selectedWeightUnit.convertKilosToString(weightFloat) else ""
        }
    }
    val dailyPhysicalActivity by remember {
        derivedStateOf {
            if (dailyPhysicalActivityInt >= 0) dailyPhysicalActivityInt.toString() else ""
        }
    }
    val dailyWaterGoal by remember {
        derivedStateOf {
            if (dailyWaterGoalFloat > 0f) selectedVolumeUnit.convertMillisToString(
                dailyWaterGoalFloat
            ) else ""
        }
    }
    var isDailyWaterGoalManuallySet by remember { mutableStateOf(false) }

    // Validation checks
    val isAgeError = ageInt !in 0..150
    val isWeightError =
        weight.toFloatOrNull()?.let { it < 0f || it > 500f / selectedWeightUnit.kgs } == true
    val isDailyPhysicalActivityError =
        dailyPhysicalActivity.toFloatOrNull()?.let { it < 0f || it > 300f } == true
    val isDailyWaterGoalError = dailyWaterGoal.toFloatOrNull()
        ?.let { it < 0f || it > 10_000f / selectedVolumeUnit.milliliters } == true

    // Trigger calculation when weight, age, or daily activity changes, if not manually set
    LaunchedEffect(ageInt, weightFloat, dailyPhysicalActivityInt, isDailyWaterGoalManuallySet) {
        if (!isDailyWaterGoalManuallySet) {
            Log.d("UserDataInput", "Recalculating daily water goal")
            if (age == "" || weight == "" || dailyPhysicalActivity == "") return@LaunchedEffect
            dailyWaterGoalFloat = calculateOptimalWaterIntake(
                age = ageInt, weight = weightFloat, dailyPhysicalActivity = dailyPhysicalActivityInt
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Heading Text
        Column {
            Text(
                text = "User Information",
                style = Typography.headlineLarge,
                modifier = Modifier
                    .padding(24.dp)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary,
            )

            // Age Input Field
            TextField(value = age,
                onValueChange = {
                    ageInt = it.toIntOrNull() ?: 0
                },
                label = {
                    Text(
                        "Age",
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isAgeError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                textStyle = Typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Weight Input with Unit Selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                TextField(value = weight,
                    onValueChange = {
                        weightFloat = it.toFloatOrNull()?.times(selectedWeightUnit.kgs) ?: 0f
                    },
                    label = {
                        Text(
                            "Weight",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isWeightError,
                    modifier = Modifier.weight(1f),
                    textStyle = Typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    WeightUnits.entries.forEach { unit ->
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            modifier = Modifier.padding(horizontal = 4.dp)
//                        ) {
//                            Text(
//                                text = unit.symbol,
//                                style = Typography.bodySmall,
//                                textAlign = TextAlign.Center,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                            RadioButton(selected = unit == selectedWeightUnit, onClick = {
//                                selectedWeightUnit = unit
//                                // remove focus from text field to update the displayed value
//
//                            })
//                        }
//                    }
//                }
                WeightUnitDropdown(
                    selectedWeightUnit,
                    Modifier
                        .wrapContentWidth()
                        .weight(0.5f)
                ) { unit ->
                    selectedWeightUnit = unit
                }

            }

            // Daily Physical Activity Input Field
            TextField(
                value = dailyPhysicalActivity,
                onValueChange = {
                    dailyPhysicalActivityInt = it.toIntOrNull() ?: 0
                },
                label = {
                    Text(
                        "Daily Physical Activity (min)",
                        style = Typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isDailyPhysicalActivityError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textStyle = Typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Water Input with Unit Selection
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                TextField(value = dailyWaterGoal,
                    onValueChange = {
                        isDailyWaterGoalManuallySet = true
                        dailyWaterGoalFloat = it.toFloatOrNull()?.times(selectedVolumeUnit.milliliters) ?: 0f
                    },
                    label = {
                        Text(
                            "Daily water intake goal",
                            style = Typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = isDailyWaterGoalError,
                    modifier = Modifier.weight(1f),
                    textStyle = Typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                VolumeUnitDropdown(
                    selectedVolumeUnit,
                    Modifier
                        .wrapContentWidth()
                        .weight(0.5f)
                ) { unit ->
                    selectedVolumeUnit = unit
                }
            }

            // Calculate Button
            Button(
                onClick = {
                    isDailyWaterGoalManuallySet = false // Reset to automatic calculation
                }, modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                Text("Calculate", style = Typography.titleMedium)
            }
        }

        // Save Button
        Button(
            onClick = {
                val updatedUserData = UserData(
                    age = ageInt,
                    weight = weightFloat,
                    dailyPhysicalActivity = dailyPhysicalActivityInt,
                    dailyWaterGoal = dailyWaterGoalFloat
                )
                saveUserData(context, updatedUserData)
                saveSettingsData(
                    context, settingsData.copy(
                        weightUnit = selectedWeightUnit, volumeUnit = selectedVolumeUnit
                    )
                )
                onSave()
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Save", style = Typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeUnitDropdown(
    selectedVolumeUnit: VolumeUnits,
    modifier: Modifier = Modifier,
    onUnitSelected: (VolumeUnits) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        TextField(value = selectedVolumeUnit.symbol,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = Typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            VolumeUnits.entries.forEach { unit ->
                DropdownMenuItem(text = {
                    Text(
                        text = "${unit.symbol} - ${unit.fullName}",
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                }, onClick = {
                    onUnitSelected(unit)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightUnitDropdown(
    selectedWeightUnit: WeightUnits,
    modifier: Modifier = Modifier,
    onUnitSelected: (WeightUnits) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        TextField(value = selectedWeightUnit.symbol,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = Typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            WeightUnits.entries.forEach { unit ->
                DropdownMenuItem(text = {
                    Text(
                        text = "${unit.symbol} - ${unit.fullName}",
                        style = Typography.bodySmall,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.primary
                    )
                }, onClick = {
                    onUnitSelected(unit)
                    expanded = false
                })
            }
        }
    }
}
