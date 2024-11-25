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
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.hasUserData
import io.github.stcksmsh.kap.data.loadSettingsData
import io.github.stcksmsh.kap.ui.composables.NavigationDrawerContent
import io.github.stcksmsh.kap.ui.composables.TopNavBar
import io.github.stcksmsh.kap.ui.screens.*
import io.github.stcksmsh.kap.ui.theme.MyAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var waterIntakeRepository: WaterIntakeRepository
        private set

    private lateinit var database: WaterIntakeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Room.databaseBuilder(
            applicationContext, WaterIntakeDatabase::class.java, "water_intake_database"
        ).build()

        waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())

        setContent {
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val settingsData = loadSettingsData(this)
            val showInputScreen = !hasUserData(this)
            val showAnimation = settingsData.startupAnimationEnabled
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

            val drawerState = rememberDrawerState(DrawerValue.Closed)

            MyAppTheme {
                // Observe current backstack entry for determining navMode
                val currentBackStackEntry =
                    navController.currentBackStackEntryFlow.collectAsState(null)

                val navMode = when (currentBackStackEntry.value?.destination?.route) {
                    "input" -> "input"
                    "animation" -> "animation"
                    else -> "default"
                }

                ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                    NavigationDrawerContent(onMenuItemClicked = { menuItem ->
                        coroutineScope.launch {
                            drawerState.close()
                            navigateWithClearBackStack(navController, menuItem)
                        }
                    })
                }) {
                    Scaffold(topBar = {
                        if (navMode != "input" && navMode != "animation") {
                            TopNavBar(onMenuClick = {
                                coroutineScope.launch { drawerState.open() }
                            }, title = navController.currentBackStackEntry?.destination?.route?.replaceFirstChar { it.uppercase() }
                                ?: "Home")
                        }
                    }) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(paddingValues)
                        ) {
                            composable(
                                route = "animation",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                SimpleWaterFillAnimationScreen(
                                    context = context, animationDuration = 1500
                                ) {
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = true } // Clears all previous destinations
                                    }
                                }
                            }
                            composable(
                                route = "input",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                InitialSetupScreen(
                                    context = context
                                ) {
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = true } // Clears all previous destinations
                                    }
                                }
                            }
                            composable(
                                route = "home",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                HomeScreen(
                                    context = context, waterIntakeRepository = waterIntakeRepository
                                )
                            }
                            composable(
                                route = "overview",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                OverviewScreen(
                                    context = context, waterIntakeRepository = waterIntakeRepository
                                )
                            }
                            composable(
                                route = "settings",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                TodoScreen("Settings"){
                                    navigateWithClearBackStack(
                                        navController,
                                        "home"
                                    )
                                }
                            }
                            composable(
                                route = "about",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                TodoScreen("About"){
                                    navigateWithClearBackStack(
                                        navController,
                                        "home"
                                    )
                                }
                            }
                            composable(
                                route = "donate",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                TodoScreen("Donate"){
                                    navigateWithClearBackStack(
                                        navController,
                                        "home"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateWithClearBackStack(navController: NavController, route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true } // Clears all destinations before navigating
        }
    }
}
