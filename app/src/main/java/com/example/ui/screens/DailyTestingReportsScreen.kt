package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.DailyReport
import com.example.model.ReportStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.TestCircleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyTestingReportsScreen(viewModel: TestCircleViewModel) {
    val selectedApp by viewModel.selectedApp.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allReports by viewModel.dailyReports.collectAsState()

    // Filter reports for selected app or all if none selected
    val displayReports = if (selectedApp != null) {
        allReports.filter { it.campaignId == selectedApp!!.id }
    } else {
        allReports
    }

    var showSubmitDialog by remember { mutableStateOf(false) }

    // Dialog inputs
    var checklistCompleted by remember { mutableStateOf(true) }
    var feedbackText by remember { mutableStateOf("") }
    var bugReportText by remember { mutableStateOf("") }
    var screenshotUrl by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = if (selectedApp != null) "Reports: ${selectedApp!!.appName}" else "Daily Testing Reports",
                onBack = { viewModel.navigateBack() }
            )
        },
        floatingActionButton = {
            if (selectedApp != null) {
                FloatingActionButton(
                    onClick = { showSubmitDialog = true },
                    containerColor = BrandBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("submit_daily_report_fab")
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = "Submit Daily Report")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = BrandBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "15-Day Quality Audit & Verified Feedback",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Daily reports record authentic test interaction, bug tracking, and OS compatibility. Each approved report awards 1 XP to the tester ledger.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Submitted Reports (${displayReports.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (displayReports.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reports submitted yet. Tap '+' to write today's test report!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(displayReports, key = { it.id }) { report ->
                    ReportItemCard(
                        report = report,
                        isAdmin = currentUser?.isAdmin == true,
                        onAdminReview = { approve, notes ->
                            viewModel.adminReviewReport(report.id, approve, notes)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    if (showSubmitDialog && selectedApp != null) {
        val currentDay = if (selectedApp!!.campaignDay == 0) 1 else selectedApp!!.campaignDay
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit Day $currentDay Report") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "App: ${selectedApp!!.appName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checklistCompleted,
                            onCheckedChange = { checklistCompleted = it }
                        )
                        Text("Completed 3+ mins testing & checklist", style = MaterialTheme.typography.bodySmall)
                    }

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        label = { Text("Daily Feedback & UX Experience *") },
                        placeholder = { Text("Tested navigation, layout responsiveness, and main buttons...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("report_feedback_input")
                    )

                    OutlinedTextField(
                        value = bugReportText,
                        onValueChange = { bugReportText = it },
                        label = { Text("Bugs / Errors Observed (Optional)") },
                        placeholder = { Text("Crash on rotate, UI overflow, etc.") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("report_bugs_input")
                    )

                    OutlinedTextField(
                        value = screenshotUrl,
                        onValueChange = { screenshotUrl = it },
                        label = { Text("Screenshot link / note (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (feedbackText.trim().length >= 10) {
                            viewModel.submitDailyReport(
                                campaignId = selectedApp!!.id,
                                dayNum = currentDay,
                                checklist = checklistCompleted,
                                feedback = feedbackText,
                                bugs = bugReportText,
                                screenshot = screenshotUrl
                            )
                            showSubmitDialog = false
                        }
                    },
                    modifier = Modifier.testTag("confirm_submit_report_button")
                ) {
                    Text("Submit Report")
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
fun ReportItemCard(
    report: DailyReport,
    isAdmin: Boolean,
    onAdminReview: (Boolean, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(report.appName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text("Day ${report.campaignDayNumber}/15 • By ${report.testerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(
                    text = report.status.name,
                    color = when (report.status) {
                        ReportStatus.APPROVED -> EmeraldGreen
                        ReportStatus.PENDING_REVIEW -> AmberWarning
                        ReportStatus.REJECTED -> RoseError
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Date: ${report.dateIso}", style = MaterialTheme.typography.labelSmall, color = BrandCyan)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Feedback: ${report.feedbackText}",
                style = MaterialTheme.typography.bodySmall
            )

            if (report.bugReportText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bugs: ${report.bugReportText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoseError
                )
            }

            if (report.xpAwarded) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = ProGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+1 XP Awarded to Tester Ledger", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ProGold)
                }
            }

            // Admin Review controls
            if (isAdmin && report.status == ReportStatus.PENDING_REVIEW) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = { onAdminReview(false, "Incomplete or generic feedback") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                    ) {
                        Text("Reject")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAdminReview(true, "Verified daily test contribution") },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        modifier = Modifier.testTag("admin_approve_report_${report.id}")
                    ) {
                        Text("Approve (+1 XP)")
                    }
                }
            }
        }
    }
}
