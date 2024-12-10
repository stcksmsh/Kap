package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.loadAppSettings
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.data.saveAppSettings
import io.github.stcksmsh.kap.data.saveUserSettings
import io.github.stcksmsh.kap.model.AppSettings
import io.github.stcksmsh.kap.model.UserSettings
import io.github.stcksmsh.kap.ui.composables.QuickWaterAdditionVolumesInput
import io.github.stcksmsh.kap.ui.composables.UnitsInput
import io.github.stcksmsh.kap.ui.composables.UserSettingsInput
import io.github.stcksmsh.kap.R

@Composable
fun SettingsScreen(context: Context, modifier: Modifier = Modifier) {

    val userData = loadUserSettings(context)
    val appSettings = loadAppSettings(context)

    var selectedWeightUnitInput by remember { mutableStateOf(appSettings.weightUnit) }
    var selectedVolumeUnitInput by remember { mutableStateOf(appSettings.volumeUnit) }
    var ageInput by remember { mutableIntStateOf(userData.age) }
    var weightInput by remember { mutableFloatStateOf(userData.weight) }
    var dailyPhysicalActivityInput by remember { mutableIntStateOf(userData.dailyPhysicalActivity) }
    var dailyWaterGoalInput by remember { mutableFloatStateOf(userData.dailyWaterGoal) }
    var quickWaterAdditionVolumesInput by remember { mutableStateOf(appSettings.quickWaterAdditionVolumes) }
    var startupAnimationEnabledInput by remember { mutableStateOf(appSettings.startupAnimationEnabled) }


    LaunchedEffect(
        ageInput,
        weightInput,
        dailyPhysicalActivityInput,
        dailyWaterGoalInput
    ) {
        saveUserSettings(
            context, UserSettings(
                age = ageInput,
                weight = weightInput,
                dailyPhysicalActivity = dailyPhysicalActivityInput,
                dailyWaterGoal = dailyWaterGoalInput
            )
        )
    }

    LaunchedEffect(
        selectedVolumeUnitInput,
        selectedWeightUnitInput,
        quickWaterAdditionVolumesInput,
        startupAnimationEnabledInput
    ) {
        saveAppSettings(
            context, AppSettings(
                weightUnit = selectedWeightUnitInput,
                volumeUnit = selectedVolumeUnitInput,
                quickWaterAdditionVolumes = quickWaterAdditionVolumesInput,
                startupAnimationEnabled = startupAnimationEnabledInput
            )
        )
    }

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
            text = stringResource(R.string.startup_animation),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary,
        )
        Row {
            Text(
                text = if (startupAnimationEnabledInput) stringResource(R.string.enabled) else stringResource(R.string.disabled),
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

    }


}