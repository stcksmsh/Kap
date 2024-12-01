package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.calculateOptimalWaterIntake
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits

@Composable
fun UserDataInput(
    selectedWeightUnit: WeightUnits,
    selectedVolumeUnit: VolumeUnits,
    age: Int,
    weight: Float,
    dailyPhysicalActivity: Int,
    dailyWaterGoal: Float,
    onAgeChanged: (Int) -> Unit,
    onWeightChanged: (Float) -> Unit,
    onDailyPhysicalActivityChanged: (Int) -> Unit,
    onDailyWaterGoalChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    // Remember and initialize states for user input fields
    val ageString = if (age > 0) age.toString() else ""

    val weightString = remember(selectedWeightUnit, weight) {
        mutableStateOf(
            if (weight > 0f) selectedWeightUnit.convertKilosToString(weight)
            else ""
        )
    }

    val dailyPhysicalActivityString =
        if (dailyPhysicalActivity >= 0) dailyPhysicalActivity.toString() else ""

    val dailyWaterGoalString = remember(selectedVolumeUnit, dailyWaterGoal) {
        mutableStateOf(
            if (dailyWaterGoal > 0f) selectedVolumeUnit.convertMillisToString(dailyWaterGoal)
            else ""
        )
    }

    var isDailyWaterGoalManuallySet by remember { mutableStateOf(false) }

    // Validation checks
    val isAgeError = ageString.toIntOrNull()?.let { it < 0 || it > 120 } != false
    val isWeightError =
        weightString.value.toFloatOrNull()
            ?.let { it < 0f || it > 500f / selectedWeightUnit.kgs } != false
    val isDailyPhysicalActivityError =
        dailyPhysicalActivityString.toIntOrNull()?.let { it < 0 || it > 300 } != false
    val isDailyWaterGoalError = dailyWaterGoalString.value.toFloatOrNull()
        ?.let { it < 0f || it > 10_000f / selectedVolumeUnit.milliliters } != false

    // Trigger calculation when weight, age, or daily activity changes, if not manually set
    LaunchedEffect(age, weight, dailyPhysicalActivity, isDailyWaterGoalManuallySet) {
        if (!isDailyWaterGoalManuallySet) {
            if (ageString == "" || weightString.value == "" || dailyPhysicalActivityString == "") return@LaunchedEffect
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
                text = "User Information",
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
                        it,
                        0
                    ) {
                        it.toIntOrNull()?.let {
                            onAgeChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        "Age",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isAgeError,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = weightString.value,
                onValueChange = {
                    onValueChange(
                        it,
                        selectedWeightUnit.decimals
                    ) {
                        weightString.value = it
                        selectedWeightUnit.convertStringToKilos(it)?.let {
                            onWeightChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        "Weight",
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
                        it,
                        0
                    ) {
                        it.toIntOrNull()?.let {
                            onDailyPhysicalActivityChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        "Daily Physical Activity (min)",
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = isDailyPhysicalActivityError,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextField(
                value = dailyWaterGoalString.value,
                onValueChange = {
                    onValueChange(
                        it,
                        selectedVolumeUnit.decimals
                    ) {
                        isDailyWaterGoalManuallySet = true
                        dailyWaterGoalString.value = it
                        selectedVolumeUnit.convertStringToMillis(it)?.let {
                            onDailyWaterGoalChanged(it)
                        }
                    }
                },
                label = {
                    Text(
                        "Daily water intake goal",
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
                Text("Calculate")
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
