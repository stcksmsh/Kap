package io.github.stcksmsh.kap.wearable

import android.os.Bundle
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
import io.github.stcksmsh.kap.wearable.ui.composables.*
import io.github.stcksmsh.kap.wearable.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            AppTheme{
                WearApp()
            }
        }
    }
}

@Composable
fun WearApp() {
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
            HydrationStatus()
        }
    }
}

@Composable
fun HydrationStatus() {
    val hydrationGoal = 2800 // Example: daily water goal in milliliters
    val currentIntake = remember { mutableIntStateOf(1500) } // Mock current intake

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hydration Goal", style = MaterialTheme.typography.title2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${currentIntake.value} / $hydrationGoal ml",
            style = MaterialTheme.typography.body2
        )

        Spacer(modifier = Modifier.height(16.dp))
//        CircularCurvedButtonSelector(
        CircularScrollableRow(
            buttonLabels = listOf("100ml", "200ml", "300ml"),
        ) {
            currentIntake.value += it.dropLast(2).toInt()
        }
    }
}
