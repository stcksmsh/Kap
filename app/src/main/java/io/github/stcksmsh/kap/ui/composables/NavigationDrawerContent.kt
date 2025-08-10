package io.github.stcksmsh.kap.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.stcksmsh.kap.R

@Composable
fun NavigationDrawerContent(
    onMenuItemClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(230.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header: identical styling to the app bar
        TopNavBar(
            title = stringResource(R.string.menu),
            onMenuClick = null // ← no burger inside the drawer
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            val menuItemDestinations = listOf("Home", "Insights", "Settings", "Reminders", "Support me")
            val menuItems = listOf(
                stringResource(R.string.home_screen_title),
                stringResource(R.string.insights_screen_title),
                stringResource(R.string.settings_screen_title),
                stringResource(R.string.reminders_screen_title),
                stringResource(R.string.support_me_screen_title)
            )

            menuItems.forEachIndexed { index, label ->
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMenuItemClicked(menuItemDestinations[index]) }
                            .padding(vertical = 15.dp)
                    ) {
                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    if (index != menuItems.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            thickness = 1.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.trade_mark),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
