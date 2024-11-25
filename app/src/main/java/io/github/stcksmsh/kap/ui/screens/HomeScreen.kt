package io.github.stcksmsh.kap.ui.screens


import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.data.loadUserData
import io.github.stcksmsh.kap.ui.composables.DailyIntakeProgress
import io.github.stcksmsh.kap.ui.composables.NavigationDrawerContent
import io.github.stcksmsh.kap.ui.composables.TopNavBar
import io.github.stcksmsh.kap.ui.composables.WaterIntakeAddPanel
import io.github.stcksmsh.kap.ui.composables.WaterIntakeList
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    context: Context,
    modifier: Modifier = Modifier,
    waterIntakeRepository: WaterIntakeRepository
) {
    val userData = loadUserData(context)
    val settingsData = loadSettingsData(context)
    val currentIntake by waterIntakeRepository.getCurrentIntake().collectAsState()

    // State for controlling the drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onMenuItemClicked = { item ->
                    println("Menu Item Clicked: $item")
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopNavBar(
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
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
                    coroutineScope = coroutineScope,
                    modifier = Modifier.weight(1f)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                WaterIntakeList(
                    coroutineScope = coroutineScope,
                    waterIntakeRepository = waterIntakeRepository,
                    modifier = Modifier.weight(3f)
                )
            }
        }
    }
}
