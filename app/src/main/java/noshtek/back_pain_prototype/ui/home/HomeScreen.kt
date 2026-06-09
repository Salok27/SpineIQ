package noshtek.back_pain_prototype.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val fmt = DateTimeFormatter.ofPattern("d MMM yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SpineIQ", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting
            Text(
                "Hello, ${state.userName.substringBefore(' ')} 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Last assessment summary card
            if (state.lastAssessmentId != null && state.lastScores != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Last Assessment", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                "${state.lastScores!!.totalSSSScore}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Column {
                                Text(
                                    state.lastScores!!.sssSeverityTier.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                state.lastAssessmentDate?.let {
                                    Text(it.format(fmt), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.FullReport.route(state.lastAssessmentId!!)) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("View Report") }
                    }
                }
            }

            // Primary CTA
            Button(
                onClick = { navController.navigate(Screen.AssessmentGraph.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start New Assessment")
            }

            // My Progress shortcut — shown after 2+ completed assessments (FR-19)
            if (state.completedAssessmentCount >= 2) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Progress.route) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("My Progress (${state.completedAssessmentCount} assessments)") }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "SpineIQ — SSS v1.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
