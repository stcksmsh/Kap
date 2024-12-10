package io.github.stcksmsh.kap.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.stcksmsh.kap.MainActivity
import io.github.stcksmsh.kap.data.*
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.ui.theme.GlanceAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

// Main Widget Class
class SimpleWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()

            val volumeUnit = VolumeUnits.valueOf(
                prefs[PreferencesKeys.SELECTED_VOLUME_UNIT] ?: VolumeUnits.MILLILITERS.name
            )
            val dailyWaterGoal = prefs[PreferencesKeys.DAILY_GOAL] ?: 0f
            val currentIntake = prefs[PreferencesKeys.CURRENT_INTAKE] ?: 0f
            val quickAdditionVolumes = prefs[PreferencesKeys.QUICK_WATER_ADDITIONS]
                ?.split(";")
                ?.map { it.toFloat() }
                ?: emptyList()

            if (quickAdditionVolumes.isEmpty()) {
                SetupPrompt(context)
            } else {
                WidgetLayout(context, volumeUnit, dailyWaterGoal, currentIntake, quickAdditionVolumes)
            }
        }
    }
}

// Composable to Show a Setup Prompt
@SuppressLint("RestrictedApi")
@Composable
fun SetupPrompt(context: Context) {
    Text(
        text = context.getString(R.string.open_app_to_configure),
        style = TextStyle(
            color = GlanceTheme.colors.background,
            fontSize = 14.sp
        ),
        modifier = GlanceModifier
            .padding(16.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun WidgetLayout(
    context: Context,
    volumeUnit: VolumeUnits,
    dailyGoal: Float,
    currentIntake: Float,
    quickAddVolumes: List<Float>
) {
    GlanceAppTheme {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(8.dp)
                .clickable(actionStartActivity(MainActivity::class.java)),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Title Section
            Text(
                text = context.getString(R.string.water_intake),
                style = TextStyle(
                    color = GlanceTheme.colors.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 12.dp) // More spacing after the title
            )

            // Progress Bar Section
            WaterProgressBar(
                context = context,
                progress = (currentIntake / dailyGoal).coerceAtMost(1f),
                currentIntake = currentIntake,
                goalIntake = dailyGoal,
                volumeUnit = volumeUnit
            )

            Spacer(modifier = GlanceModifier.height(8.dp)) // Spacing after progress bar

            // Button Section
            AddWaterButtonGroup(context, quickAddVolumes, volumeUnit)

            Spacer(modifier = GlanceModifier.height(8.dp)) // Extra spacing before motivational text

            // Motivational Text
            Text(
                text = if ((currentIntake / dailyGoal) >= 1f) {
                    context.getString(R.string.great_job)
                } else {
                    context.getString(R.string.keep_it_up)
                },
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontSize = 14.sp
                )
            )
        }
    }
}


// Composable for Progress Bar
@SuppressLint("RestrictedApi")
@Composable
fun WaterProgressBar(
    context: Context,
    progress: Float,
    currentIntake: Float,
    goalIntake: Float,
    volumeUnit: VolumeUnits
) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Vertical padding for separation
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Text(
            text = "${volumeUnit.convertMillisToUnitString(context, currentIntake)} / ${volumeUnit.convertMillisToUnitString(context, goalIntake)}",
            style = TextStyle(
                color = GlanceTheme.colors.primary,
                fontSize = 16.sp
            ),
            modifier = GlanceModifier.padding(bottom = 8.dp) // Space below text
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(10.dp) // Slightly taller progress bar
                .padding(horizontal = 8.dp), // Padding inside progress bar
            color = GlanceTheme.colors.primary,
            backgroundColor = GlanceTheme.colors.secondaryContainer
        )
    }
}


// Composable for Quick Add Buttons
@Composable
fun AddWaterButtonGroup(context: Context, quickAddVolumes: List<Float>, volumeUnit: VolumeUnits) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Vertical padding for row spacing
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        quickAddVolumes.forEachIndexed { index, intake ->
            Button(
                text = "+${volumeUnit.convertMillisToUnitString(context, intake)}",
                onClick = actionRunCallback<AddWaterAction>(
                    parameters = actionParametersOf(ActionParameters.Key<Float>("amount") to intake)
                ),
                modifier = GlanceModifier
                    .padding(4.dp) // Spacing around each button
                    .background(GlanceTheme.colors.primaryContainer)
                    .height(50.dp) // Consistent button height
                    .defaultWeight() // Evenly distribute button space
            )
//            // Add spacing between buttons except the last one
            if (index < quickAddVolumes.size - 1) {
                Spacer(modifier = GlanceModifier.width(4.dp))
            }
        }
    }
}


// Action for Adding Water
class AddWaterAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val amount = parameters[ActionParameters.Key<Float>("amount")] ?: 0f
        val repository = WaterIntakeRepository(WaterIntakeDatabase.getDatabase(context).waterIntakeDao())
        repository.insertWaterIntake(WaterIntake(intakeAmount = amount, date = Date()))
        updateWaterIntakeWidgetState(context)
    }
}

// Preference Keys for Widget State
object PreferencesKeys {
    val CURRENT_INTAKE = floatPreferencesKey("current_intake")
    val DAILY_GOAL = floatPreferencesKey("daily_goal")
    val SELECTED_VOLUME_UNIT = stringPreferencesKey("selected_volume_unit")
    val QUICK_WATER_ADDITIONS = stringPreferencesKey("quick_water_additions")
}

// Update Widget State
suspend fun updateWaterIntakeWidgetState(context: Context) {
    val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(SimpleWidget::class.java)
    val currentIntake = withContext(Dispatchers.IO) {
        WaterIntakeDatabase.getDatabase(context).waterIntakeDao().getTodaysIntakeValue()
    }
    glanceIds.forEach { glanceId ->
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val userSettings = loadUserSettings(context)
            val appSettings = loadAppSettings(context)
            prefs.toMutablePreferences().apply {
                this[PreferencesKeys.CURRENT_INTAKE] = currentIntake
                this[PreferencesKeys.DAILY_GOAL] = userSettings.dailyWaterGoal
                this[PreferencesKeys.SELECTED_VOLUME_UNIT] = appSettings.volumeUnit.name
                this[PreferencesKeys.QUICK_WATER_ADDITIONS] = appSettings.quickWaterAdditionVolumes.joinToString(";")
            }
        }
        SimpleWidget().update(context, glanceId)
    }
}
