package noshtek.back_pain_prototype.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/** The four V2 hub tabs. Everything else (wizard, results, settings…) is a full-screen push. */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home(Screen.Home.route, "Home", Icons.Outlined.Home, Icons.Filled.Home),
    Progress(Screen.Progress.route, "Progress", Icons.AutoMirrored.Outlined.TrendingUp, Icons.AutoMirrored.Filled.TrendingUp),
    Shop(Screen.Shop.route, "Shop", Icons.Outlined.Storefront, Icons.Filled.Storefront),
    Awards(Screen.Achievements.route, "Awards", Icons.Outlined.EmojiEvents, Icons.Filled.EmojiEvents);

    companion object {
        val routes = entries.map { it.route }.toSet()
    }
}

/**
 * V2 bottom navigation. The reward-violet selection indicator is the one
 * place the gamification identity shows app-wide chrome. M3 NavigationBar
 * consumes the navigation-bar insets itself (edge-to-edge safe).
 */
@Composable
fun SpineIQNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val colors = SpineIQTheme.colors
    Column {
        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp,
        ) {
            BottomNavDestination.entries.forEach { destination ->
                val selected = currentRoute == destination.route
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.12f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                    label = "nav-icon-scale",
                )
                NavigationBarItem(
                    selected = selected,
                    onClick = { if (!selected) onNavigate(destination.route) },
                    icon = {
                        Icon(
                            imageVector = if (selected) destination.selectedIcon else destination.icon,
                            contentDescription = destination.label,
                            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
                        )
                    },
                    label = {
                        Text(
                            destination.label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.rewardText,
                        selectedTextColor = colors.rewardText,
                        indicatorColor = colors.rewardContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}
