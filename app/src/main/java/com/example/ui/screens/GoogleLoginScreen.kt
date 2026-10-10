package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.TestCircleRepository
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun GoogleLoginScreen(viewModel: TestCircleViewModel) {
    var emailInput by remember { mutableStateOf("tester.community@gmail.com") }
    var nameInput by remember { mutableStateOf("Alex Tester") }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Sign In",
                onBack = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sign in to TestCircle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Join 15-day testing campaigns or submit your Android app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Google Account Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("login_email_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("login_name_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (emailInput.isNotBlank()) {
                        viewModel.login(emailInput.trim(), nameInput.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_submit_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Continue with Google", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick switch to authorized admin for reviewing all admin features
            OutlinedButton(
                onClick = {
                    viewModel.loginAsAdminQuickSwitch()
                    viewModel.navigateTo(Screen.ADMIN_PANEL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_as_admin_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandBlueLight
                )
            ) {
                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign in as Authorized Admin (${TestCircleRepository.AUTHORIZED_ADMIN_EMAIL})")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Zero-Trust Authorization Notice",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Admin privileges are strictly verified by backend authorization and Firestore Security Rules. Only '${TestCircleRepository.AUTHORIZED_ADMIN_EMAIL}' can perform payment verifications and tester assignments.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
