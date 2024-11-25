package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.data.WaterIntake
import io.github.stcksmsh.kap.data.WaterIntakeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WaterIntakeList(
    coroutineScope: CoroutineScope,
    waterIntakeRepository: WaterIntakeRepository,
    enableDelete: Boolean = true,
    modifier: Modifier = Modifier
) {

    val allWaterIntakes by waterIntakeRepository.allIntakes.collectAsState()
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        items(allWaterIntakes) { waterIntake ->
            WaterIntakeRow(
                waterIntake = waterIntake,
                enableDelete = enableDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                coroutineScope.launch {
                    waterIntakeRepository.deleteWaterIntake(waterIntake)
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


@Composable
private fun WaterIntakeRow(
    waterIntake: WaterIntake,
    enableDelete: Boolean,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Water intake amount
        Text(
            text = "${waterIntake.intakeAmount} ml",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f),
            color = MaterialTheme.colorScheme.primary
        )

        // Formatted date and time
        FormattedDateTime(
            instant = waterIntake.date.toInstant(),
            modifier = Modifier.weight(3f)
        )

        // Delete button (if enabled)
        if (enableDelete) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun FormattedDateTime(
    instant: Instant,
    zoneId: ZoneId = ZoneId.systemDefault(),
    modifier: Modifier = Modifier
) {
    val now = LocalDateTime.now(zoneId)
    val dateTime = LocalDateTime.ofInstant(instant, zoneId)

    val formattedDate = when {
        dateTime.toLocalDate().isEqual(now.toLocalDate()) -> "Today"
        dateTime.toLocalDate().isEqual(now.minusDays(1).toLocalDate()) -> "Yesterday"
        dateTime.isAfter(now.minusDays(7)) -> dateTime.format(DateTimeFormatter.ofPattern("EEEE"))
        else -> dateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd."))
    }

    val formattedTime = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedDate,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(2f),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = formattedTime,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

