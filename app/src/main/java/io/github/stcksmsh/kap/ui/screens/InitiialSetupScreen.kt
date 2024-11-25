package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.ui.composables.UserDataInput

@Composable
fun InitialSetupScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onSave: () -> Unit
) {
    UserDataInput(context, modifier.fillMaxSize().padding(24.dp), onSave)
}
