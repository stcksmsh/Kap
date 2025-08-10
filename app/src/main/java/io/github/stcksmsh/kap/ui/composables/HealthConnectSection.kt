package io.github.stcksmsh.kap.ui.composables

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import io.github.stcksmsh.kap.R
import io.github.stcksmsh.kap.health.HealthConnectManager
import kotlinx.coroutines.launch

@Composable
fun HealthConnectSection(context: Context) {
    val mgr = remember { HealthConnectManager(context) }
    val scope = rememberCoroutineScope()

    var status by remember { mutableIntStateOf(mgr.sdkStatus()) }
    var granted by remember { mutableStateOf<Set<String>>(emptySet()) }
    var hasAll by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Activity Result launcher for HC permissions
    val launcher = rememberLauncherForActivityResult(mgr.permissionRequestContract()) { result ->
        // result is Set<HealthPermission>
        scope.launch {
            try {
                granted = mgr.getGrantedPermissions()
                Log.d("TESTING", "granted permissions $granted $result")
                hasAll = granted.containsAll(mgr.permissions)
                error = null
            } catch (t: Throwable) {
                error = "Failed to re-check permissions: ${t.message}"
            }
        }
    }

    // Initial load
    LaunchedEffect(Unit) {
        runCatching {
            status = mgr.sdkStatus()
            granted = mgr.getGrantedPermissions()
            hasAll = granted.containsAll(mgr.permissions)
        }.onFailure { error = it.message }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (status) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                Text(
                    stringResource(R.string.health_connect_SDK_UNAVAILABLE),
                    textAlign = TextAlign.Center
                )
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                Text(
                    stringResource(R.string.health_connect_SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED),
                    textAlign = TextAlign.Center
                )
                Button(onClick = { launcher.launch(mgr.permissions) }) {
                    Text(
                        stringResource(R.string.health_connect_SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED_enable_install),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                if (hasAll) {
                    Text(
                        stringResource(R.string.health_connect_CONNECTED),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        stringResource(R.string.health_connect_SDK_AVAILABLE),
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = {

                        launcher.launch(mgr.permissions)
                    }) {
                        Text(
                            stringResource(R.string.health_connect_connect),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (error != null) {
            Text(
                stringResource(R.string.health_connect_error, error ?: ""),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}
