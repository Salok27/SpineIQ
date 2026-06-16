package noshtek.back_pain_prototype.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import noshtek.back_pain_prototype.ui.common.MotionTokens
import noshtek.back_pain_prototype.ui.common.glass
import noshtek.back_pain_prototype.ui.common.neonGlow
import noshtek.back_pain_prototype.ui.theme.DockShape
import noshtek.back_pain_prototype.ui.theme.PillShape
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

/** The four V2 hub tabs. Everything else (wizard, results, settings…) is a full-screen push. */
enum class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    Home(Screen.Home.route, "Home", Icons.Outlined.Home, Icons.Filled.Home),
    Journey(Screen.Journey.route, "Journey", Icons.Outlined.Timeline, Icons.Filled.Timeline),
    Progress(Screen.Progress.route, "Progress", Icons.AutoMirrored.Outlined.TrendingUp, Icons.AutoMirrored.Filled.TrendingUp),
    Profile(Screen.Profile.route, "Profile", Icons.Outlined.Person, Icons.Filled.Person);

    companion object {
        val routes = entries.map { it.route }.toSet()
    }
}

/**
 * DS 3.0 floating glow dock: a glass pill hovering above the bottom edge with
 * an aurora hairline and a cyan glow on the selected tab. Handles its own
 * navigation-bar insets (no M3 NavigationBar underneath).
 */
@Composable
fun SpineIQNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val colors = SpineIQTheme.colors
    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 6.dp, bottom = 12.dp),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .neonGlow(colors.glow, DockShape, elevation = 18.dp, alpha = 0.30f)
                .glass(
                    shape = DockShape,
                    surface = colors.glassSurface,
                    border = colors.glassBorder,
                )
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BottomNavDestination.entries.forEach { destination ->
                DockItem(
                    destination = destination,
                    selected = currentRoute == destination.route,
                    onClick = { if (currentRoute != destination.route) onNavigate(destination.route) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.DockItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = SpineIQTheme.colors
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        label = "dock-icon-scale",
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.accentText else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(MotionTokens.DurationFast),
        label = "dock-tint",
    )
    val orbColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(MotionTokens.DurationMedium),
        label = "dock-orb",
    )
    Column(
        Modifier
            .weight(1f)
            .clip(PillShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .width(48.dp)
                .height(30.dp)
                .then(
                    if (selected) Modifier.neonGlow(colors.glow, PillShape, elevation = 12.dp, alpha = 0.40f)
                    else Modifier
                )
                .clip(PillShape)
                .background(orbColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (selected) destination.selectedIcon else destination.icon,
                contentDescription = destination.label,
                tint = contentColor,
                modifier = Modifier
                    .size(22.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale },
            )
        }
        Text(
            destination.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
        )
    }
}
