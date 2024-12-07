package io.github.stcksmsh.kap.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SimpleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SimpleWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                updateWaterIntakeWidgetState(it)
            }
        }
    }
}
