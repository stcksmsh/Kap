package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.stcksmsh.kap.R

@Composable
fun InfoPopUp(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var showPopup by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        // Information icon
        Icon(imageVector = Icons.Filled.Info,
            contentDescription = stringResource(R.string.information),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(8.dp)
                .clickable { showPopup = true })
    }

    // Popup to display the provided content
    if (showPopup) {
        Popup(
            alignment = Alignment.Center,
            onDismissRequest = { showPopup = false },
            properties = PopupProperties(focusable = true),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium
                    )
                    .widthIn(min = 200.dp, max = 300.dp)
                    .wrapContentHeight()
                    .padding(8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title
                    Text(
                        text = stringResource(R.string.information),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    // Content passed from the caller
                    content()

                    // Dismiss button
                    Button(
                        onClick = { showPopup = false }, modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}
