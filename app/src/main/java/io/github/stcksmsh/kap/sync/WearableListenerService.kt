package io.github.stcksmsh.kap.sync

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeDatabase
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WearableMessageListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WearableMessageListener"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Received message: ${String(messageEvent.data)}")
        when (messageEvent.path) {
            DataLayerPaths.WATER_INTAKE_UPDATE_PATH -> {
                val intakeAmount = String(messageEvent.data).toFloat()
                Log.d(TAG, "Received water intake update: $intakeAmount")

                // Handle the water intake update
                processWaterIntakeUpdate(intakeAmount)
            }

            else -> {
                Log.d(TAG, "Unknown message path: ${messageEvent.path}")
            }
        }
    }

    private fun processWaterIntakeUpdate(intakeAmount: Float) {
        // Update your app's database, state, or notify the user
        // For example:
        val waterIntakeRepository = WaterIntakeRepository(
            WaterIntakeDatabase.getDatabase(applicationContext).waterIntakeDao()
        )
        CoroutineScope(Dispatchers.IO).launch {
            waterIntakeRepository.insertWaterIntake(
                WaterIntake(
                    intakeAmount = intakeAmount,
                )
            )
        }
    }
}
