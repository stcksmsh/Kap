package io.github.stcksmsh.kap.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.stcksmsh.kap.BuildConfig
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.data.loadReminderSettings
import io.github.stcksmsh.kap.data.saveReminderSettings
import io.github.stcksmsh.kap.model.ReminderSettings
import io.github.stcksmsh.kap.model.TimeOfDay
import io.github.stcksmsh.kap.notifications.ReminderScheduler
import java.util.Locale

@Composable
fun RemindersScreen(context: Context, modifier: Modifier = Modifier) {
    // Load persisted settings once
    var persisted by remember { mutableStateOf(loadReminderSettings(context)) }

    var remindersEnabled by remember { mutableStateOf(persisted.remindersEnabled) }
    var intervalMinutes by remember { mutableIntStateOf(persisted.intervalMinutes) }
    var startTime by remember { mutableStateOf(persisted.startTime) }
    var endTime by remember { mutableStateOf(persisted.endTime) }
    var soundEnabled by remember { mutableStateOf(persisted.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(persisted.vibrationEnabled) }

    // Notifications permission (POST_NOTIFICATIONS on Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= 33)
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            else true
        )
    }

    val hasRequiredPermissions by remember { derivedStateOf { hasNotificationPermission } }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasNotificationPermission = granted || Build.VERSION.SDK_INT < 33
    }

    // Persist + (re)schedule when relevant state changes
    LaunchedEffect(
        remindersEnabled, intervalMinutes, startTime, endTime,
        soundEnabled, vibrationEnabled, hasRequiredPermissions
    ) {
        val newSettings = ReminderSettings(
            remindersEnabled = remindersEnabled && hasRequiredPermissions,
            intervalMinutes = intervalMinutes,
            startTime = startTime,
            endTime = endTime,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled
        )
        saveReminderSettings(context, newSettings)
        persisted = newSettings

        if (newSettings.remindersEnabled) {
            ReminderScheduler.scheduleReminders(context, newSettings)
        } else {
            ReminderScheduler.cancelReminders(context)
        }
    }

    // UI
    Column(
        modifier = modifier
            .padding(64.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwitchOptionRow(
            label = stringResource(R.string.enable_reminders),
            isEnabled = remindersEnabled,
            onToggle = { enable ->
                if (enable && Build.VERSION.SDK_INT >= 33 && !hasNotificationPermission) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                remindersEnabled = enable
            }
        )

        Spacer(Modifier.height(16.dp))

        SwitchOptionRow(
            label = stringResource(R.string.enable_vibration),
            isEnabled = vibrationEnabled,
            onToggle = { vibrationEnabled = it },
        )

        SwitchOptionRow(
            label = stringResource(R.string.enable_sound),
            isEnabled = soundEnabled,
            onToggle = { soundEnabled = it }
        )

        TimeOfDayInputRow(
            label = stringResource(R.string.start_of_day),
            initialTime = startTime,
            onTimeChanged = { newStart ->
                startTime = newStart
                if (endTime - newStart < 15) endTime = newStart + 15
            },
            minimumTime = TimeOfDay(0, 15),
            context = context
        )

        TimeOfDayInputRow(
            label = stringResource(R.string.end_of_day),
            initialTime = endTime,
            onTimeChanged = { newEnd ->
                endTime = newEnd
                if (newEnd - startTime < 15) startTime = newEnd - 15
            },
            context = context
        )

        TimeOfDayInputRow(
            label = stringResource(R.string.remind_me_every),
            initialTime = TimeOfDay(
                hour = intervalMinutes / 60,
                minute = intervalMinutes % 60
            ),
            onTimeChanged = { newInterval ->
                intervalMinutes = newInterval.hour * 60 + newInterval.minute
            },
            context = context
        )

        if (BuildConfig.DEBUG) {
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { NotificationHelper.showNotification(context) },
                enabled = remindersEnabled && hasNotificationPermission
            ) {
                Text(text = stringResource(R.string.send_notification_debug))
            }
        }
    }
}

@Composable
fun SwitchOptionRow(
    label: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
        Switch(checked = isEnabled, onCheckedChange = onToggle)
    }
}

@Composable
fun TimeOfDayInputRow(
    label: String,
    initialTime: TimeOfDay,
    onTimeChanged: (TimeOfDay) -> Unit,
    minimumTime: TimeOfDay = TimeOfDay(0, 0),
    context: Context,
    modifier: Modifier = Modifier
) {
    var selectedTime by remember(initialTime) { mutableStateOf(initialTime) }

    Row(
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.weight(1f))

        Button(onClick = {
            showTimePickerDialog(
                context = context,
                initialTime = selectedTime,
                onTimeSelected = { newTime ->
                    if (newTime - minimumTime >= 0) {
                        selectedTime = newTime
                        onTimeChanged(newTime)
                    }
                }
            )
        }) {
            Text(String.format(Locale.getDefault(), "%02d:%02d", selectedTime.hour, selectedTime.minute))
        }
    }
}

fun showTimePickerDialog(
    context: Context,
    initialTime: TimeOfDay,
    onTimeSelected: (TimeOfDay) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(TimeOfDay(hourOfDay, minute))
        },
        initialTime.hour,
        initialTime.minute,
        true
    ).show()
}
