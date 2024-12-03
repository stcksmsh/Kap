package io.github.stcksmsh.kap.ui.screens

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.github.stcksmsh.kap.BuildConfig
import io.github.stcksmsh.kap.data.loadReminderSettings
import io.github.stcksmsh.kap.data.saveReminderSettings
import io.github.stcksmsh.kap.model.ReminderSettings
import io.github.stcksmsh.kap.model.TimeOfDay
import io.github.stcksmsh.kap.notifications.NotificationHelper
import io.github.stcksmsh.kap.notifications.ReminderScheduler
import java.util.Locale

@Composable
fun RemindersScreen(context: Context, modifier: Modifier = Modifier) {

    var reminderSettings = loadReminderSettings(context)

    var remindersEnabled by remember { mutableStateOf(reminderSettings.remindersEnabled) }
    var intervalMinutes by remember { mutableIntStateOf(reminderSettings.intervalMinutes) }
    var startTime by remember { mutableStateOf(reminderSettings.startTime) }
    var endTime by remember { mutableStateOf(reminderSettings.endTime) }
    var soundEnabled by remember { mutableStateOf(reminderSettings.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(reminderSettings.vibrationEnabled) }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    LaunchedEffect(remindersEnabled, intervalMinutes, startTime, endTime, soundEnabled, vibrationEnabled, hasNotificationPermission) {
        reminderSettings = ReminderSettings(
            remindersEnabled = remindersEnabled && hasNotificationPermission,
            intervalMinutes = intervalMinutes,
            startTime = startTime,
            endTime = endTime,
            soundEnabled = soundEnabled,
            vibrationEnabled = vibrationEnabled
        )
        saveReminderSettings(
            context,
            reminderSettings
        )
        ReminderScheduler.cancelReminders(context)
        if (reminderSettings.remindersEnabled) {
            ReminderScheduler.scheduleReminders(context, reminderSettings)
        }
    }

    Column(
        modifier = modifier
            .padding(64.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SwitchOptionRow(
            label = "Enable reminders",
            isEnabled = remindersEnabled && hasNotificationPermission,
            onToggle = { newCheckedState ->
                if (!hasNotificationPermission) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                remindersEnabled = newCheckedState
            }
        )

        // TODO: This doesn't work in the current version of the app
//        SwitchOptionRow(
//            label = "Enable vibration",
//            isEnabled = vibrationEnabled,
//            onToggle = { newCheckedState ->
//                vibrationEnabled = newCheckedState
//            }
//        )
//
//        SwitchOptionRow(
//            label = "Enable sound",
//            isEnabled = soundEnabled,
//            onToggle = { newCheckedState ->
//                soundEnabled = newCheckedState
//            }
//        )


        TimeOfDayInputRow(
            label = "Start of the day",
            initialTime = startTime,
            onTimeChanged = { newStartTime ->
                startTime = newStartTime
                if (endTime - newStartTime < 15) {
                    endTime = newStartTime + 15
                }
            },
            minimumTime = TimeOfDay(0, 15),
            context = context
        )

        TimeOfDayInputRow(
            label = "End of the day",
            initialTime = endTime,
            onTimeChanged = { newEndTime ->
                endTime = newEndTime
                if (newEndTime - startTime < 15) {
                    startTime = newEndTime - 15
                }
            },
            context = context
        )

        TimeOfDayInputRow(
            label = "Reminder interval",
            initialTime = TimeOfDay(
                hour = reminderSettings.intervalMinutes / 60,
                minute = reminderSettings.intervalMinutes % 60
            ),
            onTimeChanged = { newInterval ->
                intervalMinutes = newInterval.hour * 60 + newInterval.minute
            },
            context = context
        )

        if (BuildConfig.DEBUG) {
            Spacer(Modifier.weight(1f)) // Push the button to the bottom in debug builds
            Button(
                onClick = { NotificationHelper.showNotification(context) },
                enabled = remindersEnabled && hasNotificationPermission
            ) {
                Text(text = "Send notification")
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
        Text(
            text = label,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
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
        // Label for the row
        Text(
            text = label,
            modifier = Modifier.weight(1f)
        )

        // Button to display the current time and open the time picker
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
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    selectedTime.hour,
                    selectedTime.minute
                )
            )
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
        true // Use 24-hour format
    ).show()
}