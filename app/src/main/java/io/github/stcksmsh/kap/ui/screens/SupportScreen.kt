package io.github.stcksmsh.kap.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun SupportScreen(title: String = "Coming Soon", gotoHome: () -> Unit) = TodoScreen(title, gotoHome)