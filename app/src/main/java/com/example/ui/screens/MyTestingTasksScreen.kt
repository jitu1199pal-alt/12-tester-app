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
import com.example.data.TestCircleRepository
import com.example.model.AppStatus
import com.example.model.TestingApp
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun MyTestingTasksScreen(viewModel: TestCircleViewModel) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val apps by viewModel.appsList.collectAsState()
    val reports by viewModel.dailyReports.collectAsState()

    // Apps where user is assigned or active campaigns
    val assignedApps = apps.filter { app ->
        val uid = currentUser?.uid ?: ""
        val email = currentUser?.email ?: ""
        app.assignedTesterIds.contains(uid) || email.equals(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, ignoreCase = true) || app.status == AppStatus.ACTIVE_CAMPAIGN
    }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "My Testing Tasks",
                onBack = { viewModel.navigateBack() }
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily Testing Guidelines (15 Days)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Open each assigned app daily for at least 3 minutes.\n• Test key workflows, buttons, and responsive layouts.\n• Submit 1 quality daily report with constructive feedback.\n• Earn verified +1 XP per approved daily report.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Assigned Campaigns (${assignedApps.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (assignedApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No assigned testing campaigns right now. Make sure you have completed Tester Registration!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(assignedApps, key = { it.id }) { app ->
                    val myReportsForApp = reports.filter { it.campaignId == app.id && it.testerId == currentUser?.uid }
                    TaskAppCard(
                        app = app,
                        userReportsCount = myReportsForApp.size,
                        onOpenTestingLink = {
                            if (app.testingLink.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.testingLink))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        onSubmitDailyReport = {
                            viewModel.selectApp(app)
                            viewModel.navigateTo(Screen.DAILY_REPORTS)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TaskAppCard(
    app: TestingApp,
    userReportsCount: Int,
    onOpenTestingLink: () -> Unit,
    onSubmitDailyReport: () -> Unit
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
                    text = "Day ${app.campaignDay} / 15",
                    color = BrandCyan
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlueLight
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Testing Instructions:\n${app.testingInstructions}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Campaign Day indicator
            val dayFraction = (app.campaignDay.toFloat() / 15f).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "My Reports Submitted: $userReportsCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(dayFraction * 100).toInt()}% Elapsed",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandBlue
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { dayFraction },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = BrandBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onOpenTestingLink,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open App Link")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onSubmitDailyReport,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("submit_daily_report_btn_${app.id}")
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Daily Report")
                }
            }
        }
    }
}
