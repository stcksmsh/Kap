package io.github.stcksmsh.kap.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import io.github.stcksmsh.kap.data.loadSettingsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class WaterIntakeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widgets
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    companion object {
        /// template to add some amount of water (100ml, 250ml, 500ml, etc.)
        const val ACTION_ADD_ML = "io.github.stcksmsh.kap.widget.ADD_"


        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val settingsData = loadSettingsData(context.applicationContext)

            val intent1 = Intent(context, WaterIntakeWidgetProvider::class.java)
            intent1.action = ACTION_ADD_ML + (settingsData.quickWaterAdditionVolumes[0]).toString()
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.add_intake_button_1, pendingIntent)

            val intent2 = Intent(context, WaterIntakeWidgetProvider::class.java)
            intent2.action = ACTION_ADD_ML + (settingsData.quickWaterAdditionVolumes[1]).toString()
            val pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.add_intake_button_2, pendingIntent2)

            val intent3 = Intent(context, WaterIntakeWidgetProvider::class.java)
            intent3.action = ACTION_ADD_ML + (settingsData.quickWaterAdditionVolumes[2]).toString()
            val pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.add_intake_button_3, pendingIntent3)

            val intent4 = Intent(context, WaterIntakeWidgetProvider::class.java)
            intent4.action = ACTION_ADD_ML + (settingsData.quickWaterAdditionVolumes[3]).toString()
            val pendingIntent4 = PendingIntent.getBroadcast(context, 0, intent4, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.add_intake_button_4, pendingIntent4)

            val colors = dynamicDarkColorScheme(context.applicationContext)

            views.setColor(R.id.background, colors.surface)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        /// Add water intake when the user clicks on the buttons
        if (intent.action?.startsWith(ACTION_ADD_ML) == true) {
            val waterIntakeRepository = WaterIntakeRepository(
                WaterIntakeDatabase.getDatabase(context.applicationContext).waterIntakeDao()
            )
            val intakeAmount = intent.action?.substringAfterLast("_")?.toFloatOrNull() ?: return
            if (intakeAmount <= 0) return
            CoroutineScope(Dispatchers.IO).launch {
                waterIntakeRepository.insertWaterIntake(
                    WaterIntake(
                        intakeAmount = intakeAmount,
                        date = Date()
                    )
                )
            }
        }
    }
}
