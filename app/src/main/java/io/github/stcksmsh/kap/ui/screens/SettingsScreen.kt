package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.data.saveSettingsData
import io.github.stcksmsh.kap.data.saveUserSettings
import io.github.stcksmsh.kap.model.AppSettings
import io.github.stcksmsh.kap.ui.composables.QuickWaterAdditionVolumesInput
import io.github.stcksmsh.kap.ui.composables.UnitsInput
import io.github.stcksmsh.kap.ui.composables.UserSettingsInput

@Composable
fun SettingsScreen(context: Context, modifier: Modifier = Modifier) {

    val userData = loadUserSettings(context)
    val settingsData = loadSettingsData(context)

    var selectedWeightUnitInput by remember { mutableStateOf(settingsData.weightUnit) }
    var selectedVolumeUnitInput by remember { mutableStateOf(settingsData.volumeUnit) }
    var ageInput by remember { mutableIntStateOf(userData.age) }
    var weightInput by remember { mutableFloatStateOf(userData.weight) }
    var dailyPhysicalActivityInput by remember { mutableIntStateOf(userData.dailyPhysicalActivity) }
    var dailyWaterGoalInput by remember { mutableFloatStateOf(userData.dailyWaterGoal) }
    var quickWaterAdditionVolumesInput by remember { mutableStateOf(settingsData.quickWaterAdditionVolumes) }
    var startupAnimationEnabledInput by remember { mutableStateOf(settingsData.startupAnimationEnabled) }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        UserSettingsInput(selectedWeightUnit = selectedWeightUnitInput,
            selectedVolumeUnit = selectedVolumeUnitInput,
            age = ageInput,
            weight = weightInput,
            dailyPhysicalActivity = dailyPhysicalActivityInput,
            dailyWaterGoal = dailyWaterGoalInput,
            onAgeChanged = { ageInput = it },
            onWeightChanged = { weightInput = it },
            onDailyPhysicalActivityChanged = { dailyPhysicalActivityInput = it },
            onDailyWaterGoalChanged = {
                dailyWaterGoalInput = it
            })

        UnitsInput(selectedWeightUnit = selectedWeightUnitInput,
            selectedVolumeUnit = selectedVolumeUnitInput,
            onSelectedWeightUnitChanged = { selectedWeightUnitInput = it },
            onSelectedVolumeUnitChanged = { selectedVolumeUnitInput = it })


        Text(
            text = "Startup Animation",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
        )
        Row {
            Text(
                text = if (startupAnimationEnabledInput) "Enabled" else "Disabled",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(
                        Alignment.CenterVertically
                    )
                    .padding(end = 12.dp)
            )

            Switch(
                checked = startupAnimationEnabledInput,
                onCheckedChange = { startupAnimationEnabledInput = !startupAnimationEnabledInput },
                modifier = Modifier.padding(start = 12.dp)
            )
        }


        QuickWaterAdditionVolumesInput(selectedVolumeUnit = selectedVolumeUnitInput,
            quickWaterAdditionVolumes = quickWaterAdditionVolumesInput,
            onQuickWaterAdditionVolumesChanged = { quickWaterAdditionVolumesInput = it })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                saveUserSettings(
                    context = context,
                    userSettings = userData.copy(
                        age = ageInput,
                        weight = weightInput,
                        dailyPhysicalActivity = dailyPhysicalActivityInput,
                        dailyWaterGoal = dailyWaterGoalInput
                    ),
                )
                saveSettingsData(
                    context = context, appSettings = AppSettings(
                        startupAnimationEnabled = startupAnimationEnabledInput,
                        weightUnit = selectedWeightUnitInput,
                        volumeUnit = selectedVolumeUnitInput,
                        quickWaterAdditionVolumes = quickWaterAdditionVolumesInput
                    )
                )
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp)
        ) {
            Text("Save")
        }
    }


}