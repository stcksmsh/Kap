package io.github.stcksmsh.kap.ui.screens


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.ui.composables.DailyIntakeProgress
import io.github.stcksmsh.kap.ui.composables.WaterIntakeAddPanel
import io.github.stcksmsh.kap.ui.composables.WaterIntakeList


@Composable
fun HomeScreen(
    context: Context, modifier: Modifier = Modifier, waterIntakeRepository: WaterIntakeRepository
) {
    val userData = loadUserData(context)
    val settingsData = loadSettingsData(context)
    val currentIntake by waterIntakeRepository.getCurrentIntake().collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Display the daily target, current intake, and progress bar
        DailyIntakeProgress(
            dailyGoalMillis = userData.dailyWaterGoal,
            currentIntakeMillis = currentIntake,
            volumeUnit = settingsData.volumeUnit,
        )

        // Other components like WaterIntakeAddPanel and WaterIntakeList
        WaterIntakeAddPanel(
            settingsData = settingsData,
            waterIntakeRepository = waterIntakeRepository,
            modifier = Modifier.weight(1f)
        )

        HorizontalDivider(
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        WaterIntakeList(
            waterIntakeRepository = waterIntakeRepository,
            modifier = Modifier.weight(3f)
        )
    }
}
