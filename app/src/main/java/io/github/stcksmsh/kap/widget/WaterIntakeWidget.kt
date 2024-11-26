package io.github.stcksmsh.kap.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.stcksmsh.kap.MainActivity
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.loadSettingsData
import java.util.Date

class WaterIntakeWidget : GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val prefs = currentState<Preferences>()
            val intake = prefs[floatPreferencesKey("intake")] ?: 0f
            val goal = prefs[floatPreferencesKey("goal")] ?: 0f
            val applicationContext = context.applicationContext
            val settingsData = loadSettingsData(applicationContext)
            val volumeUnit = settingsData.volumeUnit // For potential customization


            val addVolumes = settingsData.quickWaterAdditionVolumes
            // Calculate progress
            val progress = if (goal > 0) intake / goal else 0f
            Row {
                Column(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier.padding(16.dp).width(125.dp).clickable(
                        actionStartActivity<MainActivity>(
                            actionParametersOf(
                                ActionParameters.Key<Boolean>(MainActivity.KEY_FROM_WIDGET) to true
                            )
                        )
                    )
                ) {
                    // Progress Bar Container
                    Box(
                        modifier = GlanceModifier
                            .width(125.dp)
                            .height(16.dp)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // get width of the progress bar
                        // Progress Bar Indicator
                        Box(
                            modifier = GlanceModifier
                                .width((progress * 125).dp) // Adjust width dynamically
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        ) {

                        }
                    }

                    Spacer(GlanceModifier.height(16.dp))

                    // Water Intake Text
                    Text(
                        "${intake.toInt()}ml / ${goal}ml",
                        style = TextStyle(color = ColorProvider(MaterialTheme.colorScheme.primary))
                    )
                }
                Column(
                    modifier = GlanceModifier.padding(16.dp).fillMaxSize()
                ){
                    for(i in 0 until 2){
                        Row(modifier = GlanceModifier.fillMaxWidth()){
                            for(j in 0 until 3){
                                if(i * 3 + j > 4){
                                    Button(
                                        text = "pop",
                                        onClick = actionRunCallback<AddWaterCallback>(
                                            actionParametersOf(AddWaterCallback.KeyAmount to 0f)
                                        ),
                                        style = TextStyle(
                                            color = ColorProvider(MaterialTheme.colorScheme.primary),
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }else{
                                    Button(
                                        text = "${volumeUnit.convertMillisToString(addVolumes[i*3+j])}${volumeUnit.symbol}",
                                        onClick = actionRunCallback<AddWaterCallback>(
                                            actionParametersOf(AddWaterCallback.KeyAmount to addVolumes[i*3+j])
                                        ),
                                        style = TextStyle(
                                            color = ColorProvider(MaterialTheme.colorScheme.primary),
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                                        ),
                                        modifier = GlanceModifier.defaultWeight()
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}


class WaterIntakeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WaterIntakeWidget()
}

class AddWaterCallback : ActionCallback {
    companion object {
        val KeyAmount = ActionParameters.Key<Float>("key_amount")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Extract the amount to add
        val intakeAmount = parameters[KeyAmount] ?: 0f
        if (intakeAmount > 0) {
            val waterIntakeDatabase = WaterIntakeDatabase.getDatabase(context.applicationContext)
            waterIntakeDatabase.waterIntakeDao().insert(
                WaterIntake(
                    intakeAmount = intakeAmount,
                    date = Date()
                )
            )
        }else{
            val waterIntakeDAO = WaterIntakeDatabase.getDatabase(context.applicationContext).waterIntakeDao()
            waterIntakeDAO.getLastIntake()?.let{
                waterIntakeDAO.delete(
                    it
                )
            }
        }
    }
}

