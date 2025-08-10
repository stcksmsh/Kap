package io.github.stcksmsh.kap.wearable

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import io.github.stcksmsh.kap.wearable.ui.composables.*
import io.github.stcksmsh.kap.wearable.ui.theme.AppTheme
import io.github.stcksmsh.kap.wearable.model.AppSettings
import io.github.stcksmsh.kap.wearable.model.VolumeUnits
import io.github.stcksmsh.kap.wearable.sync.DataLayerPaths
import io.github.stcksmsh.kap.wearable.sync.sendWaterIntakeUpdate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AppTheme{
                WearApp(this)
            }
        }
    }
}

@Composable
fun WearApp(context: Context) {
    var quickAdditionVolumes by remember { mutableStateOf(AppSettings.defaultQuickWaterAdditionVolumes) }
    var selectedVolumeUnit by remember { mutableStateOf(VolumeUnits.MILLILITERS) }
    var intakeAmount by remember { mutableFloatStateOf(0f) }
    var goalAmount by remember { mutableFloatStateOf(0f) }

    // Listen for changes in the app settings
    Wearable.getDataClient(context).addListener { dataEventBuffer ->
        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == DataLayerPaths.SETTINGS_PATH) {
                DataMapItem.fromDataItem(event.dataItem).dataMap.getString("settings")?.let{ appSettingsString ->
                    val appSettings = AppSettings.fromString(appSettingsString)

                    quickAdditionVolumes = appSettings.quickWaterAdditionVolumes
                    selectedVolumeUnit = appSettings.volumeUnit
                }
            }
        }
    }

    // Listen for changes in the water intake
    Wearable.getMessageClient(context).addListener { messageEvent ->
        when (messageEvent.path) {
            DataLayerPaths.WATER_INTAKE_UPDATE_PATH -> {
                val intakeAndGoal = String(messageEvent.data).split("/").map { it.toFloat() }
                intakeAmount = intakeAndGoal[0]
                goalAmount = intakeAndGoal[1]
            }
        }
    }



    Scaffold(
        timeText = {
            TimeText()
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            HydrationStatus(quickAdditionVolumes, selectedVolumeUnit, intakeAmount, goalAmount){
                sendWaterIntakeUpdate(context, it)
            }
        }
    }
}

@Composable
fun HydrationStatus(
    quickAdditionVolumes: List<Float>,
    selectedVolumeUnit: VolumeUnits,
    currentIntake: Float,
    hydrationGoal: Float,
    onIntakeAdd: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hydration Goal", style = MaterialTheme.typography.title2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${selectedVolumeUnit.convertMillisToString(currentIntake)} / ${selectedVolumeUnit.toUnitWithLabel(hydrationGoal)}",
            style = MaterialTheme.typography.body2
        )

        Spacer(modifier = Modifier.height(16.dp))
        CircularScrollableRow(
            buttonLabels = quickAdditionVolumes.map { selectedVolumeUnit.toUnitWithLabel(it) },
        ) { index ->
            onIntakeAdd(quickAdditionVolumes[index])
        }
    }
}
