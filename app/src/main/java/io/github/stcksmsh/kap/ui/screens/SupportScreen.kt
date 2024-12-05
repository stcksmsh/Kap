package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SupportScreen(
    context: Context,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Support the Developer",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Thank you for using the app! If you’d like to support me, here’s how you can help:",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { onDonateClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Donate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShareAppClick(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Share the App")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = AnnotatedString("Follow me on Social Media"),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable(
                    onClick = { onFollowMeClick() },
                )
        )
    }
}

fun onDonateClick() {
    // TODO: Open donation link
}

fun onShareAppClick(context: Context) {
    val shareText = """
        Hey! I’ve been using this app called KAP to track my water intake. 🥤
        It’s super simple and helps me stay hydrated.
        
        Check it out here: [Your App Link]
        Got questions? Just ask me!
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooser = Intent.createChooser(intent, "Share KAP with:")
    context.startActivity(chooser)
}

fun onFollowMeClick() {
    // TODO: Open social media link
}