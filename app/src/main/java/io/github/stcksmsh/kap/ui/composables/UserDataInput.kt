package io.github.stcksmsh.kap.ui.composables

import android.util.Log
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

    Log.d(
        "UserDataInput",
        "Rendering UserDataInput: $age, $weight, $dailyPhysicalActivity, $dailyWaterGoal"
    )

    // Remember and initialize states for user input fields
    val ageString = if (age > 0) age.toString() else ""

    val weightString = if (weight > 0f) selectedWeightUnit.convertKilosToString(weight) else ""

    val dailyPhysicalActivityString =
        if (dailyPhysicalActivity >= 0) dailyPhysicalActivity.toString() else ""

    val dailyWaterGoalString = if (dailyWaterGoal > 0f) selectedVolumeUnit.convertMillisToString(
        dailyWaterGoal
    ) else ""

    var isDailyWaterGoalManuallySet by remember { mutableStateOf(false) }

    // Validation checks
    val isAgeError = age !in 0..150
    val isWeightError =
        weightString.toFloatOrNull()?.let { it < 0f || it > 500f / selectedWeightUnit.kgs } == true
    val isDailyPhysicalActivityError =
        dailyPhysicalActivityString.toFloatOrNull()?.let { it < 0f || it > 300f } == true
    val isDailyWaterGoalError = dailyWaterGoalString.toFloatOrNull()
        ?.let { it < 0f || it > 10_000f / selectedVolumeUnit.milliliters } == true

    // Trigger calculation when weight, age, or daily activity changes, if not manually set
    LaunchedEffect(age, weight, dailyPhysicalActivity, isDailyWaterGoalManuallySet) {
        if (!isDailyWaterGoalManuallySet) {
            Log.d("UserDataInput", "Recalculating daily water goal")
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
                    onAgeChanged(it.toIntOrNull() ?: 0)
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
                value = weightString,
                onValueChange = {
                    onWeightChanged(
                        it.toFloatOrNull()?.times(selectedWeightUnit.kgs) ?: 0f
                    )
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
                    onDailyPhysicalActivityChanged(it.toIntOrNull() ?: 0)
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
                value = dailyWaterGoalString,
                onValueChange = {
                    isDailyWaterGoalManuallySet = true
                    onDailyWaterGoalChanged(
                        it.toFloatOrNull()?.times(selectedVolumeUnit.milliliters) ?: 0f
                    )
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
                },
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
            ) {
                Text("Calculate")
            }
        }
    }
}
