package io.github.stcksmsh.kap.ui.screens



import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadAppSettings
import io.github.stcksmsh.kap.data.loadUserSettings
import io.github.stcksmsh.kap.ui.composables.CircularProgress
import io.github.stcksmsh.kap.ui.composables.DailyIntakeProgress
import io.github.stcksmsh.kap.ui.composables.WaterIntakeAddPanel


@Composable
fun HomeScreen(
    context: Context, modifier: Modifier = Modifier, waterIntakeRepository: WaterIntakeRepository
) {
    val userData = loadUserSettings(context)
    val appSettings = loadAppSettings(context)
    val currentIntake by waterIntakeRepository.getCurrentIntake().collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        CircularProgress(
            selectedVolumeUnit = appSettings.volumeUnit,
            current = currentIntake,
            max = userData.dailyWaterGoal,
        )

        // Display the daily target, current intake, and progress bar
        DailyIntakeProgress(
            dailyGoalMillis = userData.dailyWaterGoal,
            currentIntakeMillis = currentIntake,
            volumeUnit = appSettings.volumeUnit,
        )

        // Other components like WaterIntakeAddPanel and WaterIntakeList
        WaterIntakeAddPanel(
            appSettings = appSettings,
            waterIntakeRepository = waterIntakeRepository,
            modifier = Modifier.weight(1f)
        )
    }
}
