package io.github.stcksmsh.kap.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.stcksmsh.kap.data.loadReminderSettings

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val settings = loadReminderSettings(context)
        ReminderScheduler.scheduleReminders(context, settings)
    }
}
