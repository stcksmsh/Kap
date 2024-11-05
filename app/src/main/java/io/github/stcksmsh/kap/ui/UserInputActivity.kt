package io.github.stcksmsh.kap.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import io.github.stcksmsh.kap.data.calculateOptimalWaterIntake
import io.github.stcksmsh.kap.model.UserData

@Composable
fun UserInputScreen(modifier: Modifier = Modifier, onSave: (UserData) -> Unit) {
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var dailyPhysicalActivity by remember { mutableStateOf("") }

    var isAgeError = age.toIntOrNull()?.let { it < 0 || it > 150 } == true
    var isWeightError = weight.toFloatOrNull()?.let { it < 0f || it > 500f } == true
    var isDailyPhysicalActivityError = dailyPhysicalActivity.toFloatOrNull()?.let { it < 0f || it > 300f } == true

    Column(modifier = modifier) {
        TextField(
            value = age,
            onValueChange = { age = it.filter { it.isDigit() } },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isAgeError
        )
        TextField(
            value = weight,
            onValueChange = { weight = it.filter { it.isDigit() } },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isWeightError
        )
        TextField(
            value = dailyPhysicalActivity,
            onValueChange = { dailyPhysicalActivity = it.filter { it.isDigit() } },
            label = { Text("Daily physical activity (min)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isDailyPhysicalActivityError
        )
        Button(onClick = {
            val ageInt = age.toIntOrNull() ?: 0
            val weightInt = weight.toIntOrNull() ?: 0
            val dailyPhysicalActivityInt = dailyPhysicalActivity.toIntOrNull() ?: 0
            
            val userData = UserData(
                age = ageInt,
                weight = weightInt,
                dailyPhysicalActivityDuration = dailyPhysicalActivityInt,
                dalyWaterGoal = calculateOptimalWaterIntake(
                    ageInt,
                    weightInt,
                    dailyPhysicalActivityInt
                )
            )
            onSave(userData)
        }) {
            Text("Save")
        }
    }
}
