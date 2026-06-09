package noshtek.back_pain_prototype.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
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
import noshtek.back_pain_prototype.ui.common.PrimaryButton
import noshtek.back_pain_prototype.ui.common.SectionCard
import noshtek.back_pain_prototype.ui.common.entrance
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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) "Edit Profile" else "Set Up Your Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    if (state.isEditMode) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.entrance(0),
                )
            }

            SectionCard(
                title = "Personal Information",
                icon = Icons.Filled.Person,
                modifier = Modifier.entrance(1),
            ) {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                DobField(dob = state.dateOfBirth, onDobChange = viewModel::onDobChange)
                Spacer(Modifier.height(12.dp))
                Text("Gender *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Gender.entries.forEach { g ->
                        FilterChip(
                            selected = state.gender == g,
                            onClick = { viewModel.onGenderChange(g) },
                            label = { Text(g.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            SectionCard(
                title = "Body Measurements",
                icon = Icons.Filled.Straighten,
                accent = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.entrance(2),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.heightCm,
                        onValueChange = viewModel::onHeightChange,
                        label = { Text("Height (cm) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.weightKg,
                        onValueChange = viewModel::onWeightChange,
                        label = { Text("Weight (kg) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
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

            PrimaryButton(
                onClick = viewModel::saveProfile,
                label = if (state.isEditMode) "Save Changes" else "Save and Continue",
                loading = state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .entrance(3),
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DobField(dob: LocalDate, onDobChange: (LocalDate) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
    val age = java.time.Period.between(dob, LocalDate.now()).years

    Text("Date of Birth *", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(4.dp))
    OutlinedButton(
        onClick = { showPicker = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
    ) {
        Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("${dob.format(formatter)}   •   Age $age")
    }

    if (showPicker) {
        // Material 3 calendar picker. rememberDatePickerState owns the selection, so a
        // tapped day is reflected immediately and always yields a valid LocalDate.
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = dob.toUtcEpochMillis(),
            yearRange = 1900..LocalDate.now().year,
            selectableDates = PastOrTodayDates
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { onDobChange(it.toLocalDateUtc()) }
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

/**
 * DatePicker works in UTC epoch-millis while the profile stores DOB as epoch-days
 * (timezone-independent). Convert through UTC midnight on both sides so the day a
 * user taps is exactly the day stored — no off-by-one from the local timezone.
 */
private fun LocalDate.toUtcEpochMillis(): Long =
    atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDateUtc(): LocalDate =
    java.time.Instant.ofEpochMilli(this).atZone(java.time.ZoneOffset.UTC).toLocalDate()

@OptIn(ExperimentalMaterial3Api::class)
private val PastOrTodayDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis <= LocalDate.now().toUtcEpochMillis()

    override fun isSelectableYear(year: Int): Boolean =
        year <= LocalDate.now().year
}

private fun bmiCategory(bmi: Float) = when {
    bmi < 18.5f -> "Underweight"
    bmi < 25f   -> "Normal"
    bmi < 30f   -> "Overweight"
    else        -> "Obese"
}
