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
import io.github.stcksmsh.kap.data.loadAppSettings
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.data.saveAppSettings
import io.github.stcksmsh.kap.data.saveUserSettings
import io.github.stcksmsh.kap.model.AppSettings
import io.github.stcksmsh.kap.model.UserSettings
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.model.WeightUnits
import io.github.stcksmsh.kap.ui.composables.UnitsInput
import io.github.stcksmsh.kap.ui.composables.UserSettingsInput

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
        val appSettings = loadAppSettings(context)

        var selectedWeightUnitInput by remember { mutableStateOf(appSettings.weightUnit) }
        var selectedVolumeUnitInput by remember { mutableStateOf(appSettings.volumeUnit) }
        var ageInput by remember { mutableIntStateOf(0) }
        var weightInput by remember { mutableFloatStateOf(0f) }
        var dailyPhysicalActivityInput by remember { mutableIntStateOf(-1) }
        var dailyWaterGoalInput by remember { mutableFloatStateOf(0f) }


        UserSettingsInput(
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
                saveUserSettings(
                    context = context,
                    userSettings = UserSettings(
                        age = ageInput,
                        weight = weightInput,
                        dailyPhysicalActivity = dailyPhysicalActivityInput,
                        dailyWaterGoal = dailyWaterGoalInput
                    ),
                )
                saveAppSettings(
                    context = context,
                    appSettings = appSettings.copy(
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
