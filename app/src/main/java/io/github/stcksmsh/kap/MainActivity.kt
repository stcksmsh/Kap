package io.github.stcksmsh.kap

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.wearable.Wearable
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.hasUserSettings
import io.github.stcksmsh.kap.data.loadAppSettings
import io.github.stcksmsh.kap.sync.DataLayerPaths
import io.github.stcksmsh.kap.sync.sendSettingsData
import io.github.stcksmsh.kap.sync.sendWaterIntakeUpdate
import io.github.stcksmsh.kap.ui.composables.NavigationDrawerContent
import io.github.stcksmsh.kap.ui.composables.TopNavBar
import io.github.stcksmsh.kap.ui.screens.*
import io.github.stcksmsh.kap.ui.theme.AppTheme
import io.github.stcksmsh.kap.widget.updateWaterIntakeWidgetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onStop() {
        super.onStop()
        val context = this
        CoroutineScope(Dispatchers.IO).launch{
            updateWaterIntakeWidgetState(context)
        }
    }

    lateinit var waterIntakeRepository: WaterIntakeRepository
        private set

    private lateinit var database: WaterIntakeDatabase

    companion object {
        const val KEY_FROM_WIDGET = "from_widget"
    }

    @OptIn(DelicateCoroutinesApi::class, FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        database = WaterIntakeDatabase.getDatabase(applicationContext)

        waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())

        sendSettingsData(this, loadAppSettings(this))
        sendWaterIntakeUpdate(this, 100f, 200f)

        setContent {
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val appSettings = loadAppSettings(this)
            val showInputScreen = !hasUserSettings(this)
            val fromWidget = intent.getBooleanExtra(KEY_FROM_WIDGET, false)
            val showAnimation = appSettings.startupAnimationEnabled && !fromWidget
            val context = this

            val startDestination = when {
                showAnimation -> "animation"
                showInputScreen -> "input"
                else -> "home"
            }
            LaunchedEffect(Unit) {
                updateWaterIntakeWidgetState(context)
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

            AppTheme {
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
                            TopNavBar(
                                onMenuClick = {
                                    coroutineScope.launch { drawerState.open() }
                                },
                                title = getTitleFromBackStack(
                                    context,
                                    navController.currentBackStackEntry?.destination?.route
                                )
                            )
                        }
                    }) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            composable(
                                route = "animation",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                SimpleWaterFillAnimationScreen(
                                    animationDuration = 1500
                                ) {
                                    navigateWithClearBackStack(
                                        navController,
                                        if (showInputScreen) "input" else "home"
                                    )
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
                                    navigateWithClearBackStack(
                                        navController,
                                        "home"
                                    )
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
                                route = "insights",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                InsightsScreen(
                                    context = context, waterIntakeRepository = waterIntakeRepository
                                )
                            }
                            composable(
                                route = "settings",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                SettingsScreen(context)
                            }
                            composable(
                                route = "reminders",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                RemindersScreen(context = context)
                            }
                            composable(
                                route = "support me",
                                enterTransition = navigationEnterTransition,
                                exitTransition = navigationExitTransition
                            ) {
                                SupportScreen(context)
                            }
                        }
                    }
                }
            }
        }
    }

}

fun navigateWithClearBackStack(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(0) { inclusive = true } // Clears all destinations before navigating
    }
}

fun getTitleFromBackStack(context: Context, route: String?): String {
    return when (route) {
        "insights" -> context.getString(R.string.insights_screen_title)
        "settings" -> context.getString(R.string.settings_screen_title)
        "reminders" -> context.getString(R.string.reminders_screen_title)
        "support me" -> context.getString(R.string.support_me_screen_title)
        else -> context.getString(R.string.home_screen_title)
    }
}
