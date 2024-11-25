package io.github.stcksmsh.kap.widget

import android.content.Context
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*

class WaterIntakeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            Column(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = GlanceModifier.padding(16.dp)
            ) {
                // Progress Bar

                ProgressBar(context).apply {
                    max = 2000
                    progress = 1000
                }

                Spacer(GlanceModifier.height(16.dp))

                // Buttons
                Row(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(text="Add 250ml", onClick = {actionRunCallback<AddWaterCallback>()})
                    Spacer(GlanceModifier.width(8.dp))
                }
            }
        }
    }
}

class WaterIntakeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WaterIntakeWidget()
}

class AddWaterCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        Log.d("WaterIntakeWidget", "Add 250ml")
    }
}