package noshtek.back_pain_prototype.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // After deletion, restart at onboarding
    LaunchedEffect(state.dataDeleted) {
        if (state.dataDeleted) {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile
            SectionCard(title = "Profile") {
                TextButton(
                    onClick = { navController.navigate(Screen.Profile.route) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Edit Personal Information") }
            }

            // Reminders (FR-18)
            SectionCard(title = "Reassessment Reminders") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable reminders", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = state.reminderEnabled,
                        onCheckedChange = viewModel::setReminderEnabled
                    )
                }
                if (state.reminderEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Text("Remind me every ${state.reminderIntervalWeeks} week${if (state.reminderIntervalWeeks != 1) "s" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = state.reminderIntervalWeeks.toFloat(),
                        onValueChange = { viewModel.setReminderInterval(it.toInt()) },
                        valueRange = 1f..12f,
                        steps = 10
                    )
                }
                Text(
                    "Reminder notifications require notification permission on Android 13+.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Health Connect (FR-04 — placeholder for Phase 1)
            SectionCard(title = "Health Connect") {
                Text(
                    "Health Connect integration is coming in a future update. Once enabled, steps, sleep, and activity data will be automatically imported from your wearable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // About
            SectionCard(title = "About") {
                SettingsRow("App", "SpineIQ")
                SettingsRow("Version", "1.0 (Phase 1)")
                SettingsRow("Scoring System", "SSS v1.0 — Dr. Ayush Sharma")
            }

            // Privacy
            SectionCard(title = "Privacy") {
                Text(
                    "All your health data is stored only on this device and encrypted at rest (AES-256 / SQLCipher). No data is transmitted to any server without your consent.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Data Management
            SectionCard(title = "Data Management") {
                Text(
                    "Deleting all data will permanently remove your profile and all assessments. This cannot be undone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Delete All My Data") }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete All My Data?") },
            text = { Text("This will permanently remove your profile and all assessments. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; viewModel.deleteAllData() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
