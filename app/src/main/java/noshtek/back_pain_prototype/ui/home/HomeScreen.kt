package noshtek.back_pain_prototype.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.AnimatedCountText
import noshtek.back_pain_prototype.ui.common.GradientHeroCard
import noshtek.back_pain_prototype.ui.common.PressableCard
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.SecondaryButton
import noshtek.back_pain_prototype.ui.common.ShimmerBox
import noshtek.back_pain_prototype.ui.common.SssTierBadge
import noshtek.back_pain_prototype.ui.common.entrance
import noshtek.back_pain_prototype.ui.theme.ButtonShape
import noshtek.back_pain_prototype.ui.theme.CardShape
import noshtek.back_pain_prototype.ui.theme.HeroShape
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fmt = remember { DateTimeFormatter.ofPattern("d MMM yyyy") }
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("SpineIQ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    ) { padding ->
        if (state.isLoading) {
            HomeLoading(Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Welcome hero
            GradientHeroCard(modifier = Modifier.entrance(0)) {
                Icon(
                    Icons.Filled.MonitorHeart,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.22f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(64.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "$greeting,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                    Text(
                        state.userName.substringBefore(' ').ifBlank { "there" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Ready for your next spine check-in?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.80f),
                    )
                }
            }

            // Last assessment summary
            if (state.lastAssessmentId != null && state.lastScores != null) {
                PressableCard(
                    onClick = { navController.navigate(Screen.FullReport.route(state.lastAssessmentId!!)) },
                    modifier = Modifier.entrance(1),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                "LAST ASSESSMENT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                AnimatedCountText(
                                    target = state.lastScores!!.totalSSSScore,
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    "/ 11",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                )
                            }
                            SssTierBadge(state.lastScores!!.sssSeverityTier)
                            state.lastAssessmentDate?.let {
                                Text(
                                    it.format(fmt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = "View report",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Primary CTA
            PrimaryButton(
                onClick = { navController.navigate(Screen.AssessmentGraph.route) },
                label = "Start New Assessment",
                icon = Icons.Filled.Add,
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(2),
            )

            // Progress shortcut — shown once 2+ assessments are complete (FR-19)
            if (state.completedAssessmentCount >= 2) {
                SecondaryButton(
                    onClick = { navController.navigate(Screen.Progress.route) },
                    label = "My Progress (${state.completedAssessmentCount} assessments)",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .entrance(3),
                )
            }

            Spacer(Modifier.height(4.dp))

            // Privacy assurance — fills space meaningfully and reinforces trust
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(4)
                    .clip(CardShape)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    "Private & encrypted — your health data never leaves this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                "SpineIQ · SSS v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun HomeLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(132.dp),
            shape = HeroShape,
        )
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = CardShape,
        )
        ShimmerBox(
            Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = ButtonShape,
        )
    }
}
