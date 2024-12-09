package io.github.stcksmsh.kap.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.stcksmsh.kap.R

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
            text = stringResource(R.string.support_the_developer),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(R.string.thank_you_how_to_help),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = { onDonateClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.donate))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShareAppClick(context) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.share_app))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onFollowMeClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.follow_me))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.want_to_contribute_or_translate),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = { onGithubClick(context) },
        ) {
            Text(text = stringResource(R.string.visit_my_github))
        }
    }
}

fun onDonateClick() {
    // TODO: Open donation link
}

fun onShareAppClick(context: Context) {
    val googlePlayUrl = context.getString(R.string.google_play_url)
    val shareText = context.getString(R.string.share_message_template, googlePlayUrl).trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooser = Intent.createChooser(intent, context.getString(R.string.share_app_with))
    context.startActivity(chooser)
}

fun onFollowMeClick() {
    // TODO: Open social media link
}

fun onGithubClick(context: Context) {
    val githubLink = context.getString(R.string.github_url)

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubLink))

    context.startActivity(intent)
}