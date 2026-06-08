package noshtek.back_pain_prototype.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen

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

    when (state.currentPage) {
        0 -> OnboardingPage0(onGetStarted = { viewModel.nextPage() })
        1 -> OnboardingPage1(
            onNext = { viewModel.nextPage() },
            onSkip = { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }
        )
        else -> OnboardingPage2(
            onStart = { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } },
            onSkip = { navController.navigate(Screen.Profile.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }
        )
    }
}

@Composable
private fun OnboardingPage0(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SpineIQ", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(
            "Understand your back pain.\nTrack your progress.",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(48.dp))
        Button(onClick = onGetStarted, modifier = Modifier.fillMaxWidth()) {
            Text("Get Started")
        }
    }
}

@Composable
private fun OnboardingPage1(onNext: () -> Unit, onSkip: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("How It Works", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Text(
            "SpineIQ uses the Spine Severity System (SSS) — a structured, evidence-based questionnaire to compute your personal spine health score (0–11).\n\n" +
            "You answer questions about your lifestyle, pain, and daily function. The app calculates your score, identifies contributing risk factors, and tracks your progress over time.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Skip") }
            Button(onClick = onNext, modifier = Modifier.weight(1f)) { Text("Next") }
        }
    }
}

@Composable
private fun OnboardingPage2(onStart: () -> Unit, onSkip: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your Privacy", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        Text(
            "All your data stays on this device.\n\n" +
            "SpineIQ never sends your health information to any server without your explicit consent. Optional cloud backup is entirely user-controlled.\n\n" +
            "No data is shared with anyone without your permission.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onSkip, modifier = Modifier.weight(1f)) { Text("Skip") }
            Button(onClick = onStart, modifier = Modifier.weight(1f)) { Text("Let's Start") }
        }
    }
}
