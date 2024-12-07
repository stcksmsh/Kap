package io.github.stcksmsh.kap.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.stcksmsh.kap.data.*
import io.github.stcksmsh.kap.model.VolumeUnits
import io.github.stcksmsh.kap.ui.theme.GlanceAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class SimpleWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {


        provideContent {
            val prefs = currentState<Preferences>()

            val volumeUnit = VolumeUnits.valueOf(prefs[PreferencesKeys.SELECTED_VOLUME_UNIT] ?: VolumeUnits.MILLILITERS.name)
            val dailyWaterGoal = prefs[PreferencesKeys.DAILY_GOAL] ?: 0f
            val currentIntake = prefs[PreferencesKeys.CURRENT_INTAKE] ?: 0f
            val quickWaterAdditionVolumes = prefs[PreferencesKeys.QUICK_WATER_ADDITIONS]?.split(";")?.map { it.toFloat() } ?: emptyList()

            Log.d("SimpleWidget", "Volume Unit: $volumeUnit")

            if(quickWaterAdditionVolumes.isEmpty()){
                Text(
                    text = "Open the app to set up the widget",
                    style = TextStyle(
                        color = ColorProvider(MaterialTheme.colorScheme.onPrimary),
                        fontSize = 14.sp
                    ),
                    modifier = GlanceModifier.padding(bottom = 8.dp)
                )
            }else{
                WidgetLayout(volumeUnit, dailyWaterGoal, currentIntake, quickWaterAdditionVolumes)
            }
        }
    }
}

@Composable
fun WidgetLayout(volumeUnit: VolumeUnits, dailyWaterGoal: Float, currentIntake: Float, quickWaterAdditionVolumes: List<Float>) {
    GlanceAppTheme {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            // Progress Section
            WaterProgressBar(
                progress = (currentIntake/dailyWaterGoal).coerceAtMost(1f), // Example progress value
                currentIntake = currentIntake, // Example current intake
                goalIntake = dailyWaterGoal, // Example goal intake
                volumeUnit = volumeUnit
            )

            Spacer(modifier = GlanceModifier.height(16.dp))

            // Add Water Buttons
            AddWaterButtonGroup(
                intakeAmounts = quickWaterAdditionVolumes,
                volumeUnit = volumeUnit
            )
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun WaterProgressBar(progress: Float, currentIntake: Float, goalIntake: Float, volumeUnit: VolumeUnits) {
    Column(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally
    ) {
        Text(
            text = "Progress: ${volumeUnit.convertMillisToUnitString(currentIntake)} / ${volumeUnit.convertMillisToUnitString(goalIntake)}",
            style = TextStyle(
                color = ColorProvider(MaterialTheme.colorScheme.onPrimary),
                fontSize = 14.sp
            ),
            modifier = GlanceModifier.padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(8.dp),
            color = ColorProvider(MaterialTheme.colorScheme.primary),
            backgroundColor = ColorProvider(MaterialTheme.colorScheme.secondaryContainer)
        )
    }
}

@Composable
fun AddWaterButtonGroup(intakeAmounts: List<Float>, volumeUnit: VolumeUnits) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
        intakeAmounts.forEach { intake ->
            Spacer(modifier = GlanceModifier.width(8.dp))
            Button(
                text = volumeUnit.convertMillisToUnitString(intake),
                onClick = actionRunCallback<AddWaterAction>(
                    parameters = actionParametersOf(ActionParameters.Key<Float>("amount") to intake)
                ),
                modifier = GlanceModifier.padding(4.dp).defaultWeight()
            )
        }
        Spacer(modifier = GlanceModifier.width(8.dp))
    }
}

class AddWaterAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val amount = parameters[ActionParameters.Key<Float>("amount")] ?: 0f
        val waterIntakeRepository = WaterIntakeRepository(
            WaterIntakeDatabase.getDatabase(context).waterIntakeDao()
        )

        waterIntakeRepository.insertWaterIntake(
            WaterIntake(
                intakeAmount = amount,
                date = Date()
            )
        )

        updateWaterIntakeWidgetState(context.applicationContext)
    }
}

object PreferencesKeys {
    val CURRENT_INTAKE = floatPreferencesKey("current_intake")
    val DAILY_GOAL = floatPreferencesKey("daily_goal")
    val SELECTED_VOLUME_UNIT = stringPreferencesKey("selected_volume_unit")
    val QUICK_WATER_ADDITIONS = stringPreferencesKey("quick_water_additions")
}

suspend fun updateWaterIntakeWidgetState(context: Context) {
    val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(SimpleWidget::class.java)

    // Fetch data from your database or repository in a background thread
    val currentIntake = withContext(Dispatchers.IO){
        WaterIntakeDatabase.getDatabase(context).waterIntakeDao().getTodaysIntakeValue()
    }

    // Update the widget state for each instance of the widget
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
        // Trigger a widget update
        SimpleWidget().update(context, glanceId)
    }
}