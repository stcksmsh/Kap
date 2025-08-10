// io/github/stcksmsh/kap/health/HealthConnectManager.kt
package io.github.stcksmsh.kap.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Volume
import java.time.*

class HealthConnectManager(private val context: Context) {
    private val client by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getReadPermission(HydrationRecord::class),
        HealthPermission.getWritePermission(HydrationRecord::class)
    )

    fun permissionRequestContract() =
        PermissionController.createRequestPermissionResultContract()


    suspend fun getGrantedPermissions(): Set<String> =
        client.permissionController.getGrantedPermissions()
    fun sdkStatus(): Int = HealthConnectClient.getSdkStatus(context)

    /** WRITE Kap → Health */
    suspend fun writeHydration(ml: Double, at: Instant = Instant.now()) {
        client.insertRecords(
            listOf(
                HydrationRecord(
                    startTime = at,
                    startZoneOffset = null,
                    endTime = at,
                    endZoneOffset = null,
                    volume = Volume.milliliters(ml),
                    metadata = Metadata.manualEntry(
                        Device(Device.TYPE_PHONE)
                    )
                )
            )
        )
    }

    /** READ total in [from, to] (ml). */
    suspend fun readTotalMl(from: Instant, to: Instant): Double {
        val page = client.readRecords(
            ReadRecordsRequest(
                HydrationRecord::class,
                timeRangeFilter = TimeRangeFilter.between(from, to)
            )
        )
        return page.records.sumOf { it.volume.inMilliliters }
    }

    /** READ today total (ml). */
    suspend fun readTodayTotalMl(): Double {
        val zone = ZoneId.systemDefault()
        val start = LocalDate.now(zone).atStartOfDay(zone).toInstant()
        return readTotalMl(start, Instant.now())
    }
}
