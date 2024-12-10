package io.github.stcksmsh.kap.wearable.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.Wearable

private const val TAG = "WearableMessageClient"

fun sendWaterIntakeUpdate(context: Context, intakeAmount: Float) {
    val messageClient = Wearable.getMessageClient(context)

    val message = intakeAmount.toString().toByteArray()

    Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener { nodes ->
        for (node in nodes) {
            messageClient.sendMessage(node.id, DataLayerPaths.WATER_INTAKE_UPDATE_PATH, message)
                .addOnSuccessListener {
                    Log.d(TAG, "Water intake update sent successfully.")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Failed to send water intake update", it)
                }
        }
    }
}
