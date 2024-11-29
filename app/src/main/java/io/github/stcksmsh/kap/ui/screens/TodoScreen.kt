package io.github.stcksmsh.kap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TodoScreen(title: String = "Coming Soon", gotoHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "This feature is under development.",
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(
                onClick = gotoHome,
            ) {
                Text(
                    text = "Back to Home",
                )
            }
        }
    }
}
