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
            .width(230.dp) // Set fixed width for the drawer
            .background(MaterialTheme.colorScheme.surface) // Drawer background color
    ) {
        // Header Section with Primary Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Standard TopAppBar height
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.menu),
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Divider Below Header
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            thickness = 1.dp
        )

        // Menu Items
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Padding around the menu items
            verticalArrangement = Arrangement.spacedBy(12.dp), // Consistent spacing
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
            menuItems.forEachIndexed { index, menuItem ->
                Column {
                    // Menu Item
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMenuItemClicked(menuItemDestinations[index]) }
                            .padding(vertical = 15.dp) // Padding inside each box
                    ) {
                        Text(
                            text = menuItem,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 16.dp) // Horizontal padding for text
                        )
                    }

                    // Divider Between Items, Except After the Last One
                    if (index != menuItems.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            thickness = 1.dp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push footer to the bottom

            // Footer Section
            Text(
                text = stringResource(R.string.trade_mark),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
