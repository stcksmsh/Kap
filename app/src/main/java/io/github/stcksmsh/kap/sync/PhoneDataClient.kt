package io.github.stcksmsh.kap.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import io.github.stcksmsh.kap.model.AppSettings


private const val TAG = "PhoneDataClient"

fun sendSettingsData(context: Context, settings: AppSettings) {
    val dataClient = Wearable.getDataClient(context)

    val putDataReq = PutDataMapRequest.create(DataLayerPaths.SETTINGS_PATH).apply {
        dataMap.putString("settings", settings.toString())
    }.asPutDataRequest()

    dataClient.putDataItem(putDataReq).addOnSuccessListener {
        Log.d(TAG, "Settings data sent successfully.")
    }.addOnFailureListener {
        Log.e(TAG, "Failed to send settings data", it)
    }
}
