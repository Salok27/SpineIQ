package noshtek.back_pain_prototype.ui.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import noshtek.back_pain_prototype.ui.common.SliderWithLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientInformationScreen(
    navController: NavController,
    patientId: String?,
    viewModel: PatientInfoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showActionButtons = state.isExistingPatient || state.isSaved
    val resolvedPatientId = patientId ?: state.patientId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isExistingPatient) "Patient Details" else "New Patient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
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
            SectionCard(title = "Basic Information") {
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = viewModel::setFullName,
                    label = { Text("Full Name *") },
                    isError = state.error != null && state.fullName.isBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Text("Date of Birth", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.birthYear,
                        onValueChange = viewModel::setBirthYear,
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.birthMonth,
                        onValueChange = viewModel::setBirthMonth,
                        label = { Text("Month") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.birthDay,
                        onValueChange = viewModel::setBirthDay,
                        label = { Text("Day") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f),
                        singleLine = true
                    )
                }
            }

            SectionCard(title = "Gender") {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Gender.entries.forEach { gender ->
                        FilterChip(
                            selected = state.gender == gender,
                            onClick = { viewModel.setGender(gender) },
                            label = {
                                Text(gender.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() })
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionCard(title = "Physical Measurements") {
                SliderWithLabel(
                    label = "Height",
                    value = state.heightCm,
                    onValueChange = viewModel::setHeightCm,
                    valueRange = 120f..220f,
                    unit = "cm"
                )
                Spacer(Modifier.height(8.dp))
                SliderWithLabel(
                    label = "Weight",
                    value = state.weightKg,
                    onValueChange = viewModel::setWeightKg,
                    valueRange = 30f..200f,
                    unit = "kg"
                )
            }

            SectionCard(title = "Optional") {
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = viewModel::setPhoneNumber,
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.patientIdExternal,
                    onValueChange = viewModel::setPatientIdExternal,
                    label = { Text("OPD / External Patient ID") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            if (state.error != null) {
                Text(
                    state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.isSaved && !state.isExistingPatient) {
                Text(
                    "Patient saved successfully.",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = viewModel::savePatient,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isExistingPatient) "Update Patient" else "Save Patient")
            }

            if (showActionButtons && resolvedPatientId.isNotEmpty()) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.Occupation.route(resolvedPatientId)) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Start New Assessment") }

                OutlinedButton(
                    onClick = { navController.navigate(Screen.PatientHistory.route(resolvedPatientId)) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("View Assessment History") }
            }
        }
    }
}
