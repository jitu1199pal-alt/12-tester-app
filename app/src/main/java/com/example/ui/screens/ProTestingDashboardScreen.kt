package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.PaymentStatus
import com.example.model.TestingApp
import com.example.model.TestingTier
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun ProTestingDashboardScreen(viewModel: TestCircleViewModel) {
    val context = LocalContext.current
    val apps by viewModel.appsList.collectAsState()
    val proApps = apps.filter { it.tier == TestingTier.PRO }

    var showSubmitDialog by remember { mutableStateOf(false) }

    // Dialog inputs
    var appName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var testingLink by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Pro Testing Dashboard",
                onBack = { viewModel.navigateBack() }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSubmitDialog = true },
                containerColor = ProGold,
                contentColor = Slate900,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Pro Request (₹699)", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("submit_pro_app_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ProGold.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = ProGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pro 15-Day Guaranteed Testers – ₹699",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ProGold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Get 13 verified, active Android testers for full 15 consecutive days. Includes daily quality reports, crash feedback, and Play Console compliance tracking. Manual UPI verification protects both developers and community testers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Pro Testing Campaigns (${proApps.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (proApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No Pro testing campaigns found. Request one for ₹699 below!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(proApps, key = { it.id }) { app ->
                    ProAppItemCard(
                        app = app,
                        onMakePayment = {
                            viewModel.selectApp(app)
                            viewModel.navigateTo(Screen.PAYMENT_VERIFICATION)
                        },
                        onOpenTestingLink = {
                            if (app.testingLink.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.testingLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        onOpenGoogleGroup = {
                            if (app.googleGroupUrl.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.googleGroupUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Create Pro Request – ₹699") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = appName,
                        onValueChange = { appName = it },
                        label = { Text("App Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pro_app_name_input")
                    )
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Package Name (e.g. com.my.proapp) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pro_pkg_input")
                    )
                    OutlinedTextField(
                        value = testingLink,
                        onValueChange = { testingLink = it },
                        label = { Text("Google Play Closed Testing Link *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pro_link_input")
                    )
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Detailed Testing Instructions *") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("pro_instructions_input")
                    )
                    Text(
                        text = "Amount: ₹699 INR. After creation, scan the official UPI QR code and submit your UTR reference number.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (appName.isNotBlank() && packageName.isNotBlank() && testingLink.isNotBlank()) {
                            viewModel.submitProApp(
                                name = appName,
                                pkg = packageName,
                                link = testingLink,
                                minVer = "Android 8.0+",
                                instr = instructions,
                                logo = ""
                            )
                            showSubmitDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProGold, contentColor = Slate900),
                    modifier = Modifier.testTag("pro_dialog_submit_button")
                ) {
                    Text("Proceed to Payment", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProAppItemCard(
    app: TestingApp,
    onMakePayment: () -> Unit,
    onOpenTestingLink: () -> Unit,
    onOpenGoogleGroup: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    text = "PRO: ₹699",
                    color = ProGold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelMedium,
                color = BrandBlueLight
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Payment status pill
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Payment: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusBadge(
                    text = app.paymentStatus.name,
                    color = when (app.paymentStatus) {
                        PaymentStatus.PAYMENT_APPROVED -> EmeraldGreen
                        PaymentStatus.PAYMENT_PENDING_VERIFICATION -> AmberWarning
                        PaymentStatus.PAYMENT_REJECTED -> RoseError
                        else -> BrandBlueLight
                    }
                )
            }

            if (app.adminApprovalToken != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Approval Token: ${app.adminApprovalToken}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen
                )
            }

            if (app.upiUtrNumber != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Submitted UTR: ${app.upiUtrNumber}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            // 15-day campaign progress
            val progress = (app.campaignDay.toFloat() / 15f).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Assigned: ${app.currentTesterCount} / 13 Testers",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Day ${app.campaignDay}/15",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandCyan
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = ProGold,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (app.paymentStatus == PaymentStatus.PAYMENT_PENDING_SUBMISSION) {
                    Button(
                        onClick = onMakePayment,
                        colors = ButtonDefaults.buttonColors(containerColor = ProGold, contentColor = Slate900),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay ₹699 via UPI", fontWeight = FontWeight.Bold)
                    }
                } else {
                    if (app.googleGroupUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = onOpenGoogleGroup,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Google Group")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                        onClick = onOpenTestingLink,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Testing Link")
                    }
                }
            }
        }
    }
}
