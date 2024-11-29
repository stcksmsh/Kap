package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.data.saveSettingsData
import io.github.stcksmsh.kap.data.saveUserData
import io.github.stcksmsh.kap.ui.composables.UnitsInput
import io.github.stcksmsh.kap.ui.composables.UserDataInput

@Composable
fun InitialSetupScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onSetupCompleted: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        val userData = loadUserData(context)
        val settingsData = loadSettingsData(context)

        var selectedWeightUnitInput by remember { mutableStateOf(settingsData.weightUnit) }
        var selectedVolumeUnitInput by remember { mutableStateOf(settingsData.volumeUnit) }
        var ageInput by remember { mutableIntStateOf(userData.age) }
        var weightInput by remember { mutableFloatStateOf(userData.weight) }
        var dailyPhysicalActivityInput by remember { mutableIntStateOf(userData.dailyPhysicalActivity) }
        var dailyWaterGoalInput by remember { mutableFloatStateOf(userData.dailyWaterGoal) }


        UserDataInput(
            selectedWeightUnit = selectedWeightUnitInput,
            selectedVolumeUnit = selectedVolumeUnitInput,
            age = ageInput,
            weight = weightInput,
            dailyPhysicalActivity = dailyPhysicalActivityInput,
            dailyWaterGoal = dailyWaterGoalInput,
            onAgeChanged = { ageInput = it },
            onWeightChanged = { weightInput = it },
            onDailyPhysicalActivityChanged = { dailyPhysicalActivityInput = it },
            onDailyWaterGoalChanged = { dailyWaterGoalInput = it },
            modifier = Modifier.padding(12.dp)
        )


        UnitsInput(
            selectedWeightUnit = selectedWeightUnitInput,
            selectedVolumeUnit = selectedVolumeUnitInput,
            onSelectedWeightUnitChanged = { selectedWeightUnitInput = it },
            onSelectedVolumeUnitChanged = { selectedVolumeUnitInput = it },
            modifier = Modifier.padding(12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                saveUserData(
                    context = context,
                    userData = userData.copy(
                        age = ageInput,
                        weight = weightInput,
                        dailyPhysicalActivity = dailyPhysicalActivityInput,
                        dailyWaterGoal = dailyWaterGoalInput
                    ),
                )
                saveSettingsData(
                    context = context,
                    settingsData = settingsData.copy(
                        weightUnit = selectedWeightUnitInput,
                        volumeUnit = selectedVolumeUnitInput
                    )
                )
                onSetupCompleted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text("Save")
        }

    }

}
