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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppStatus
import com.example.model.TestingApp
import com.example.model.TestingTier
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun FreeTestingDashboardScreen(viewModel: TestCircleViewModel) {
    val context = LocalContext.current
    val apps by viewModel.appsList.collectAsState()
    val freeApps = apps.filter { it.tier == TestingTier.FREE }

    var showSubmitDialog by remember { mutableStateOf(false) }

    // Dialog inputs
    var appName by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var testingLink by remember { mutableStateOf("") }
    var minAndroid by remember { mutableStateOf("Android 8.0+") }
    var instructions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Free Testing Dashboard",
                onBack = { viewModel.navigateBack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSubmitDialog = true },
                containerColor = FreeGreen,
                contentColor = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.testTag("submit_free_app_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Submit Free App")
            }
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
                        containerColor = FreeGreen.copy(alpha = 0.12f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = FreeGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Community Peer-To-Peer Queue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FreeGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Free testing enables community exchange: test other developers' apps to get testers for yours. Each campaign requires 13 active testers over 15 days.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Free Testing Requests (${freeApps.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (freeApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No free testing requests yet. Tap '+' to submit your app!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(freeApps, key = { it.id }) { app ->
                    FreeAppItemCard(
                        app = app,
                        onOpenLink = {
                            if (app.testingLink.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.testingLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        onReportFeedback = {
                            viewModel.selectApp(app)
                            viewModel.navigateTo(Screen.DAILY_REPORTS)
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
            title = { Text("Submit App for Free Testing") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = appName,
                        onValueChange = { appName = it },
                        label = { Text("App Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("free_app_name_input")
                    )
                    OutlinedTextField(
                        value = packageName,
                        onValueChange = { packageName = it },
                        label = { Text("Package Name (e.g. com.my.app) *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("free_pkg_input")
                    )
                    OutlinedTextField(
                        value = testingLink,
                        onValueChange = { testingLink = it },
                        label = { Text("Play Console Closed Testing URL *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("free_link_input")
                    )
                    OutlinedTextField(
                        value = minAndroid,
                        onValueChange = { minAndroid = it },
                        label = { Text("Minimum Android Version") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        label = { Text("Testing Instructions *") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("free_instructions_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (appName.isNotBlank() && packageName.isNotBlank() && testingLink.isNotBlank()) {
                            viewModel.submitFreeApp(
                                name = appName,
                                pkg = packageName,
                                link = testingLink,
                                minVer = minAndroid,
                                instr = instructions,
                                logo = ""
                            )
                            showSubmitDialog = false
                        }
                    },
                    modifier = Modifier.testTag("free_dialog_submit_button")
                ) {
                    Text("Submit App")
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
fun FreeAppItemCard(
    app: TestingApp,
    onOpenLink: () -> Unit,
    onReportFeedback: () -> Unit
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
                    text = app.status.name,
                    color = when (app.status) {
                        AppStatus.APPROVED, AppStatus.ACTIVE_CAMPAIGN -> EmeraldGreen
                        AppStatus.PENDING_APPROVAL -> AmberWarning
                        else -> Slate400
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelMedium,
                color = BrandBlueLight
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Min Android: ${app.minAndroidVersion} • Required: ${app.requiredTesterCount} testers",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = app.testingInstructions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))
            // Tester progress bar
            val progress = (app.currentTesterCount.toFloat() / app.requiredTesterCount).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Testers: ${app.currentTesterCount} / ${app.requiredTesterCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                color = FreeGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onOpenLink,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Testing Link")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onReportFeedback,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreeGreen)
                ) {
                    Text("Daily Feedback")
                }
            }
        }
    }
}
