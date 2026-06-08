package noshtek.back_pain_prototype.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import noshtek.back_pain_prototype.core.data.model.Gender
import noshtek.back_pain_prototype.navigation.Screen
import noshtek.back_pain_prototype.ui.common.SectionCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            if (state.isEditMode) {
                navController.popBackStack()
            } else {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Profile.route) { inclusive = true }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Profile" else "Set Up Your Profile") },
                navigationIcon = {
                    if (state.isEditMode) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!state.isEditMode) {
                Text(
                    "This information stays on your device and personalises your assessment results.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SectionCard(title = "Personal Information") {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                DobField(dob = state.dateOfBirth, onDobChange = viewModel::onDobChange)
                Spacer(Modifier.height(12.dp))
                Text("Gender *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Gender.entries.chunked(2).forEach { row ->
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            row.forEach { g ->
                                FilterChip(
                                    selected = state.gender == g,
                                    onClick = { viewModel.onGenderChange(g) },
                                    label = { Text(g.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }, maxLines = 1) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            SectionCard(title = "Body Measurements") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.heightCm,
                        onValueChange = viewModel::onHeightChange,
                        label = { Text("Height (cm) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.weightKg,
                        onValueChange = viewModel::onWeightChange,
                        label = { Text("Weight (kg) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
                val h = state.heightCm.toFloatOrNull()
                val w = state.weightKg.toFloatOrNull()
                if (h != null && w != null && h > 0f) {
                    val bmi = w / ((h / 100f) * (h / 100f))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "BMI: ${"%.1f".format(bmi)} — ${bmiCategory(bmi)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = viewModel::saveProfile,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text(if (state.isEditMode) "Save Changes" else "Save and Continue")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DobField(dob: LocalDate, onDobChange: (LocalDate) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val age = java.time.Period.between(dob, LocalDate.now()).years

    OutlinedButton(
        onClick = { showPicker = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Date of Birth: ${dob.format(formatter)}  (Age: $age)")
    }

    if (showPicker) {
        YearMonthDayPicker(
            initial = dob,
            onConfirm = { onDobChange(it); showPicker = false },
            onDismiss = { showPicker = false }
        )
    }
}

@Composable
private fun YearMonthDayPicker(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var year by remember { mutableIntStateOf(initial.year) }
    var month by remember { mutableIntStateOf(initial.monthValue) }
    var day by remember { mutableIntStateOf(initial.dayOfMonth) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Date of Birth") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = year.toString(),
                    onValueChange = { year = it.toIntOrNull() ?: year },
                    label = { Text("Year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = month.toString(),
                        onValueChange = { month = (it.toIntOrNull() ?: month).coerceIn(1, 12) },
                        label = { Text("Month") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = day.toString(),
                        onValueChange = { day = (it.toIntOrNull() ?: day).coerceIn(1, 31) },
                        label = { Text("Day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                try { onConfirm(LocalDate.of(year, month, day)) } catch (_: Exception) {}
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun bmiCategory(bmi: Float) = when {
    bmi < 18.5f -> "Underweight"
    bmi < 25f   -> "Normal"
    bmi < 30f   -> "Overweight"
    else        -> "Obese"
}
