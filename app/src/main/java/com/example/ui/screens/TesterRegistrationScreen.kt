package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun TesterRegistrationScreen(viewModel: TestCircleViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()

    var fullName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var gmailAddress by remember { mutableStateOf(currentUser?.email ?: "") }
    var cityState by remember { mutableStateOf("") }
    var androidVersion by remember { mutableStateOf("Android 16 (API 36)") }
    var deviceModel by remember { mutableStateOf("") }
    var availableFor15Days by remember { mutableStateOf(true) }
    var agreedToRules by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Tester Registration",
                onBack = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AppRegistration, contentDescription = null, tint = BrandBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join 15-Day Quality Testing Pool",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Approved testers are assigned to 15-day closed testing cohorts. Submit daily reports to help developers pass Google Play 14/15-day testing criteria and earn verified XP rewards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tester_fullname_input")
                    )

                    OutlinedTextField(
                        value = gmailAddress,
                        onValueChange = { gmailAddress = it },
                        label = { Text("Gmail Address (For Google Play / Group) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tester_gmail_input")
                    )

                    OutlinedTextField(
                        value = cityState,
                        onValueChange = { cityState = it },
                        label = { Text("City and State (e.g. Pune, Maharashtra) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tester_city_input")
                    )

                    OutlinedTextField(
                        value = androidVersion,
                        onValueChange = { androidVersion = it },
                        label = { Text("Android Version (e.g. Android 14, 15, 16 - API 36) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tester_os_version_input")
                    )

                    OutlinedTextField(
                        value = deviceModel,
                        onValueChange = { deviceModel = it },
                        label = { Text("Device Model (e.g. Pixel 8, Samsung S23, OnePlus 11) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("tester_device_model_input")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Checkbox 1: 15-day availability
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = availableFor15Days,
                            onCheckedChange = { availableFor15Days = it },
                            modifier = Modifier.testTag("tester_avail_checkbox")
                        )
                        Text(
                            text = "I am available to test actively for 15 consecutive days.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Checkbox 2: Testing rules agreement
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = agreedToRules,
                            onCheckedChange = { agreedToRules = it },
                            modifier = Modifier.testTag("tester_rules_checkbox")
                        )
                        Text(
                            text = "I agree to submit honest daily reports & genuine feedback.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && gmailAddress.isNotBlank() && cityState.isNotBlank() && deviceModel.isNotBlank()) {
                                viewModel.registerTester(
                                    fullName = fullName,
                                    gmail = gmailAddress,
                                    cityState = cityState,
                                    androidVer = androidVersion,
                                    deviceModel = deviceModel,
                                    avail15 = availableFor15Days,
                                    agreed = agreedToRules
                                )
                            }
                        },
                        enabled = availableFor15Days && agreedToRules,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_tester_registration_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit Application", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "Privacy Note: We collect only City/State and device specs needed for test cohort distribution. Residential address is never required.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
