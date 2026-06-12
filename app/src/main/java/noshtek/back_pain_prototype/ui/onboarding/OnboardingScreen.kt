package noshtek.back_pain_prototype.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.avatar.Avatar
import noshtek.back_pain_prototype.ui.avatar.AvatarSize
import noshtek.back_pain_prototype.ui.avatar.AvatarSpec
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.TextActionButton
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme
import noshtek.back_pain_prototype.ui.theme.brandGradient

@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // If a profile already exists, skip onboarding entirely (FR-17 — shown on first launch only)
    LaunchedEffect(state.hasProfile, state.isLoading) {
        if (!state.isLoading && state.hasProfile) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val toProfile: () -> Unit = {
        navController.navigate(Screen.Profile.route) {
            popUpTo(Screen.Onboarding.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedContent(
            targetState = state.currentPage,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it / 3 } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it / 3 } + fadeOut())
                }
            },
            label = "onboarding-page",
        ) { page ->
            when (page) {
                0 -> OnboardingPage0(onGetStarted = { viewModel.nextPage() })
                1 -> OnboardingPage1(onNext = { viewModel.nextPage() }, onSkip = toProfile)
                else -> OnboardingPage2(onStart = toProfile, onSkip = toProfile)
            }
        }
        PageDots(
            current = state.currentPage,
            total = 3,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
        )
    }
}

@Composable
private fun IconBadge(icon: ImageVector, accent: Color) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun PageDots(current: Int, total: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(total) { i ->
            val active = i == current
            val width by animateDpAsState(if (active) 26.dp else 8.dp, label = "dot-width")
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (active) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer,
                    )
            )
        }
    }
}

@Composable
private fun OnboardingPage0(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // The spine buddy greets new users — the avatar they'll level up and style.
        Box(
            modifier = Modifier
                .size(132.dp)
                .clip(CircleShape)
                .background(SpineIQTheme.colors.rewardContainer.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center,
        ) {
            Avatar(spec = AvatarSpec.Default, size = AvatarSize.Medium)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            "SpineIQ",
            style = MaterialTheme.typography.displayMedium.copy(brush = brandGradient()),
            fontWeight = FontWeight.ExtraBold,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Understand your back pain.\nTrack progress, build streaks,\nand level up your spine buddy.",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(56.dp))
        PrimaryButton(
            onClick = onGetStarted,
            label = "Get Started",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OnboardingPage1(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingInfoPage(
        icon = Icons.AutoMirrored.Filled.Assignment,
        accent = MaterialTheme.colorScheme.secondary,
        title = "How It Works",
        body = "SpineIQ uses the Spine Severity System (SSS) — a structured, evidence-based " +
            "questionnaire to compute your personal spine health score (0–11).\n\n" +
            "You answer questions about your lifestyle, pain, and daily function. The app calculates " +
            "your score, identifies contributing risk factors, and tracks your progress over time.",
        primaryLabel = "Next",
        onPrimary = onNext,
        onSkip = onSkip,
    )
}

@Composable
private fun OnboardingPage2(onStart: () -> Unit, onSkip: () -> Unit) {
    OnboardingInfoPage(
        icon = Icons.Filled.Lock,
        accent = MaterialTheme.colorScheme.tertiary,
        title = "Your Privacy",
        body = "All your data stays on this device.\n\n" +
            "SpineIQ never sends your health information to any server without your explicit consent. " +
            "Optional cloud backup is entirely user-controlled.\n\n" +
            "No data is shared with anyone without your permission.",
        primaryLabel = "Let's Start",
        onPrimary = onStart,
        onSkip = onSkip,
    )
}

@Composable
private fun OnboardingInfoPage(
    icon: ImageVector,
    accent: Color,
    title: String,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconBadge(icon, accent)
        Spacer(Modifier.height(28.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(48.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextActionButton(onClick = onSkip, label = "Skip", modifier = Modifier.weight(1f))
            PrimaryButton(onClick = onPrimary, label = primaryLabel, modifier = Modifier.weight(1f))
        }
    }
}
