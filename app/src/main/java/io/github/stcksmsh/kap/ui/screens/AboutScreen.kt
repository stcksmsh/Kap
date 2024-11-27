package io.github.stcksmsh.kap.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun AboutScreen(title: String = "Coming Soon", gotoHome: () -> Unit) = TodoScreen(title, gotoHome)