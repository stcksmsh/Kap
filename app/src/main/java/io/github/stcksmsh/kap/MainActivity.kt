package io.github.stcksmsh.kap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.hasUserData
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.ui.screens.HomeScreen
import io.github.stcksmsh.kap.ui.screens.SimpleWaterFillAnimationScreen
import io.github.stcksmsh.kap.ui.screens.InitialSetupScreen
import io.github.stcksmsh.kap.ui.theme.MyAppTheme

class MainActivity : ComponentActivity() {

    lateinit var waterIntakeRepository: WaterIntakeRepository
        private set

    private lateinit var database: WaterIntakeDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         database = Room.databaseBuilder(
            applicationContext,
            WaterIntakeDatabase::class.java,
            "water_intake_database"
        ).build()

        waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())

        setContent {
            val navController = rememberNavController()

            val settingsData = loadSettingsData(this)

            var showInputScreen = !hasUserData(this)
            var showAnimation = settingsData.startupAnimationEnabled
            val context = this


            val startDestination = when {
                showAnimation -> "animation"
                showInputScreen -> "input"
                else -> "home"
            }

            val animationDuration = 500

            val navigationEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition? =
                {
                    slideInHorizontally(
                        initialOffsetX = { -1000 }, animationSpec = tween(animationDuration)
                    ) + fadeIn(animationSpec = tween(animationDuration))
                }

            val navigationExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition? =
                {
                    slideOutHorizontally(
                        targetOffsetX = { 1000 }, animationSpec = tween(animationDuration)
                    ) + fadeOut(animationSpec = tween(animationDuration))
                }

            MyAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    composable(
                        route = "animation",
                        enterTransition = navigationEnterTransition,
                        exitTransition = navigationExitTransition
                    ) {
                        // Display the initial water fill animation
                        SimpleWaterFillAnimationScreen(
                            context = context, animationDuration = 1500
                        ) {
                            navController.navigate(if (showInputScreen) "input" else "home")
                        }
                    }
                    composable(
                        route = "input",
                        enterTransition = navigationEnterTransition,
                        exitTransition = navigationExitTransition
                    ) {
                        // User input screen with fade-in/fade-out on exit
                        InitialSetupScreen(
                            context = context
                        ) {
                            navController.navigate("home")
                        }
                    }
                    composable(
                        route = "home",
                        enterTransition = navigationEnterTransition,
                        exitTransition = navigationExitTransition
                    ) {
                        // Main Water Addition Screen with fade-in effect
                        HomeScreen(
                            context = context,
                            waterIntakeRepository = waterIntakeRepository
                        )
                    }
                }
            }

        }
    }
}
