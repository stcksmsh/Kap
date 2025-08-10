package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.calculateOptimalWaterIntake
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits
import io.github.stcksmsh.kap.R

@Composable
fun UserSettingsInput(
    selectedWeightUnit: WeightUnits,
    selectedVolumeUnit: VolumeUnits,
    age: Int,
    weight: Double,
    dailyPhysicalActivity: Int,
    dailyWaterGoal: Double,
    onAgeChanged: (Int) -> Unit,
    onWeightChanged: (Double) -> Unit,
    onDailyPhysicalActivityChanged: (Int) -> Unit,
    onDailyWaterGoalChanged: (Double) -> Unit,
    modifier: Modifier = Modifier
) {

    // Remember and initialize states for user input fields
    var ageString by remember(age) {
        mutableStateOf(
            if (age > 0) age.toString() else ""
        )
    }

    var weightString by remember(weight) {
        mutableStateOf(
            if (weight > 0f) selectedWeightUnit.toUnitString(weight)
            else ""
        )
    }

    var dailyPhysicalActivityString by remember(dailyPhysicalActivity) {
        mutableStateOf(
            if (dailyPhysicalActivity >= 0) dailyPhysicalActivity.toString() else ""

        )
    }

    var dailyWaterGoalString by remember(dailyWaterGoal) {
        mutableStateOf(
            if (dailyWaterGoal > 0f) selectedVolumeUnit.toUnitString(dailyWaterGoal)
            else ""
        )
    }

    var isDailyWaterGoalManuallySet by remember { mutableStateOf(false) }

    // Validation checks
    val isAgeError by remember(ageString) { derivedStateOf { (ageString == "" && age != 0) || age > 120 } }
    val isWeightError by remember(weightString) { derivedStateOf { (weightString == "" && weight != 0.0) || weight > 500f} }

    val isDailyPhysicalActivityError by remember(dailyPhysicalActivityString) { derivedStateOf { (dailyPhysicalActivityString == "" && dailyPhysicalActivity != -1) || dailyPhysicalActivity > 1440 } }

    val isDailyWaterGoalError by remember(dailyWaterGoalString) { derivedStateOf { (dailyWaterGoalString == "" && dailyWaterGoal != 0.0) || dailyWaterGoal > 10000f } }

    LaunchedEffect(age, weight, dailyPhysicalActivity, isDailyWaterGoalManuallySet) {
        if (!isDailyWaterGoalManuallySet) {
            if (ageString == "" || weightString == "" || dailyPhysicalActivityString == "") return@LaunchedEffect
            onDailyWaterGoalChanged(
                calculateOptimalWaterIntake(
                    age = age, weight = weight, dailyPhysicalActivity = dailyPhysicalActivity
                )
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(12.dp),
    ) {
        Column {

            Text(
                text = stringResource(R.string.user_information),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary,
            )


            TextField(
                value = ageString,
                onValueChange = {
                    onValueChange(
                        it, 0
                    ) {
                        ageString = it
                        it.toIntOrNull()?.let {
                            onAgeChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.age),
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isAgeError,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = weightString,
                onValueChange = {
                    onValueChange(
                        it, selectedWeightUnit.decimals
                    ) {
                        weightString = it
                        selectedWeightUnit.fromUnitToKilos(it)?.let {
                            onWeightChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.weight),
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isWeightError,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            // Daily Physical Activity Input Field
            TextField(
                value = dailyPhysicalActivityString,
                onValueChange = {
                    onValueChange(
                        it, 0
                    ) {
                        dailyPhysicalActivityString = it
                        it.toIntOrNull()?.let {
                            onDailyPhysicalActivityChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.daily_physical_activity),
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isDailyPhysicalActivityError,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = dailyWaterGoalString,
                onValueChange = {
                    onValueChange(
                        it, selectedVolumeUnit.decimals
                    ) {
                        dailyWaterGoalString = it
                        isDailyWaterGoalManuallySet = true
                        selectedVolumeUnit.fromUnitToMl(it)?.let {
                            onDailyWaterGoalChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.daily_water_intake_goal),
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isDailyWaterGoalError,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    isDailyWaterGoalManuallySet = false // Reset to automatic calculation
                }, modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.calculate))
            }
        }
    }
}


fun onValueChange(
    newValue: String,
    decimalPlaces: Int,
    onValueChange: (String) -> Unit,
) {
    // Check if the input is valid
    val isValidInput = if (decimalPlaces == 0) {
        newValue.all { it.isDigit() }
    } else {
        newValue.count { it == '.' } <= 1 && newValue.all { it.isDigit() || it == '.' }
    }

    if (!isValidInput) return

    onValueChange(newValue)
}
