package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.TestCircleRepository
import com.example.model.*
import com.example.ui.components.MetricCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun AdminPanelScreen(viewModel: TestCircleViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val apps by viewModel.appsList.collectAsState()
    val testers by viewModel.testerProfiles.collectAsState()
    val reports by viewModel.dailyReports.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "App Requests", "Payments", "Testers", "13-Tester Groups")

    val isAuthorized = currentUser?.isAdmin == true

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Admin Panel",
                onBack = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        if (!isAuthorized) {
            // Unauthorized warning
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = RoseError,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Restricted Admin Area",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Access is restricted to authorized admin account:\n${TestCircleRepository.AUTHORIZED_ADMIN_EMAIL}\n\nSecurity is enforced via Firestore rules and server-side authorization checks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.loginAsAdminQuickSwitch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarning, contentColor = Slate900),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_authenticate_prompt_button")
                ) {
                    Text("Authenticate as ${TestCircleRepository.AUTHORIZED_ADMIN_EMAIL}", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Admin Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                when (selectedTab) {
                    0 -> AdminOverviewTab(apps, testers, reports, viewModel)
                    1 -> AdminAppRequestsTab(apps, viewModel)
                    2 -> AdminPaymentManagementTab(apps, viewModel)
                    3 -> AdminTesterManagementTab(testers, viewModel)
                    4 -> Admin13TesterGroupTab(apps, testers, viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminOverviewTab(
    apps: List<TestingApp>,
    testers: List<TesterProfile>,
    reports: List<DailyReport>,
    viewModel: TestCircleViewModel
) {
    val freeRequests = apps.count { it.tier == TestingTier.FREE }
    val proRequests = apps.count { it.tier == TestingTier.PRO }
    val pendingPayments = apps.count { it.paymentStatus == PaymentStatus.PAYMENT_PENDING_VERIFICATION }
    val approvedPayments = apps.count { it.paymentStatus == PaymentStatus.PAYMENT_APPROVED }
    val pendingTesters = testers.count { it.status == TesterStatus.PENDING }
    val activeCampaigns = apps.count { it.status == AppStatus.ACTIVE_CAMPAIGN }
    val completedCampaigns = apps.count { it.status == AppStatus.COMPLETED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AmberWarning)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Authorized Administrator", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            Text("Overview Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Free Requests", "$freeRequests", Icons.Default.Groups, FreeGreen, Modifier.weight(1f))
                MetricCard("Pro Requests", "$proRequests", Icons.Default.WorkspacePremium, ProGold, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Pending UPI", "$pendingPayments", Icons.Default.HourglassTop, AmberWarning, Modifier.weight(1f))
                MetricCard("Approved UPI", "$approvedPayments", Icons.Default.CheckCircle, EmeraldGreen, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Pending Testers", "$pendingTesters", Icons.Default.PersonAdd, BrandCyan, Modifier.weight(1f))
                MetricCard("Active Campaigns", "$activeCampaigns", Icons.Default.RocketLaunch, BrandBlue, Modifier.weight(1f))
            }
        }

        item {
            MetricCard("Completed 15-Day Campaigns", "$completedCampaigns", Icons.Default.DoneAll, EmeraldGreen, Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun AdminAppRequestsTab(apps: List<TestingApp>, viewModel: TestCircleViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(apps, key = { it.id }) { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(app.appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        StatusBadge(
                            text = "${app.tier.name} • ${app.status.name}",
                            color = if (app.tier == TestingTier.PRO) ProGold else FreeGreen
                        )
                    }
                    Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = BrandBlueLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("By: ${app.createdByUserEmail}", style = MaterialTheme.typography.bodySmall)
                    Text("Instructions: ${app.testingInstructions}", style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (app.status == AppStatus.PENDING_APPROVAL) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.adminAssignTesters(app.id, emptyList(), "")
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                            ) {
                                Text("Reject")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.adminAssignTesters(app.id, listOf("tester_1", "tester_2", "tester_3"), "https://groups.google.com/g/testcircle")
                                }
                            ) {
                                Text("Approve & Activate")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPaymentManagementTab(apps: List<TestingApp>, viewModel: TestCircleViewModel) {
    val proWithPayments = apps.filter { it.tier == TestingTier.PRO }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Manual UPI Payment Verification (₹699 INR)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Check actual merchant bank transactions before approving. Approval generates a unique anti-tamper token.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(proWithPayments, key = { it.id }) { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(app.appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        StatusBadge(
                            text = app.paymentStatus.name,
                            color = when (app.paymentStatus) {
                                PaymentStatus.PAYMENT_APPROVED -> EmeraldGreen
                                PaymentStatus.PAYMENT_PENDING_VERIFICATION -> AmberWarning
                                else -> RoseError
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Payer Name: ${app.upiPayerName ?: "Not submitted"}", style = MaterialTheme.typography.bodySmall)
                    Text("UTR Number: ${app.upiUtrNumber ?: "Not submitted"}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text("Amount: ₹${app.paymentAmount}", style = MaterialTheme.typography.bodySmall)
                    if (app.paymentScreenshotUrl != null) {
                        Text("Screenshot Ref: ${app.paymentScreenshotUrl}", style = MaterialTheme.typography.bodySmall, color = BrandCyan)
                    }
                    if (app.adminApprovalToken != null) {
                        Text("Generated Token: ${app.adminApprovalToken}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    }

                    if (app.paymentStatus == PaymentStatus.PAYMENT_PENDING_VERIFICATION) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { viewModel.adminVerifyPayment(app.id, false, "Payment not received in merchant account") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                            ) {
                                Text("Reject Payment")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.adminVerifyPayment(app.id, true, "Verified in merchant statement") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text("Verify & Approve")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminTesterManagementTab(testers: List<TesterProfile>, viewModel: TestCircleViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(testers, key = { it.id }) { tester ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(tester.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        StatusBadge(
                            text = tester.status.name,
                            color = when (tester.status) {
                                TesterStatus.APPROVED -> EmeraldGreen
                                TesterStatus.PENDING -> AmberWarning
                                else -> RoseError
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(tester.gmailAddress, style = MaterialTheme.typography.bodySmall, color = BrandBlueLight)
                    Text("City/State: ${tester.cityState}", style = MaterialTheme.typography.bodySmall)
                    Text("Device: ${tester.deviceModel} • OS: ${tester.androidVersion}", style = MaterialTheme.typography.bodySmall)
                    Text("Verified XP: ${tester.totalVerifiedXp} • Campaigns: ${tester.completedCampaignsCount}", style = MaterialTheme.typography.bodySmall)

                    if (tester.status == TesterStatus.PENDING) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = { viewModel.adminReviewTester(tester.id, false) },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError)
                            ) {
                                Text("Reject")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.adminReviewTester(tester.id, true) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                            ) {
                                Text("Approve Tester")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Admin13TesterGroupTab(
    apps: List<TestingApp>,
    testers: List<TesterProfile>,
    viewModel: TestCircleViewModel
) {
    val approvedTesters = testers.filter { it.status == TesterStatus.APPROVED }
    var selectedAppId by remember { mutableStateOf(apps.firstOrNull()?.id ?: "") }
    var googleGroupUrlInput by remember { mutableStateOf("https://groups.google.com/g/testcircle-cohort") }
    val selectedApp = apps.find { it.id == selectedAppId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "13-Tester Group Assembly",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Approved Testers Available: ${approvedTesters.size}\nRequired: 13 eligible testers per campaign.\nNote: Google Group is created manually in Google Groups admin; URL is saved here.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Text("Select Target App", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            apps.forEach { app ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAppId == app.id,
                        onClick = { selectedAppId = app.id }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(app.appName, fontWeight = FontWeight.SemiBold)
                        Text("${app.tier.name} • ${app.packageName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = googleGroupUrlInput,
                onValueChange = { googleGroupUrlInput = it },
                label = { Text("Google Group URL (Manually Created)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    if (selectedAppId.isNotBlank()) {
                        val idsToAssign = approvedTesters.take(13).map { it.id }
                        viewModel.adminAssignTesters(selectedAppId, idsToAssign, googleGroupUrlInput)
                    }
                },
                enabled = approvedTesters.isNotEmpty() && selectedAppId.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.GroupAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assign Testers & Activate Campaign", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Text("Approved Testers Roster (Admin Only View):", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }

        items(approvedTesters, key = { it.id }) { tester ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(tester.fullName, fontWeight = FontWeight.SemiBold)
                    Text(tester.gmailAddress, style = MaterialTheme.typography.bodySmall, color = BrandBlueLight)
                }
            }
        }
    }
}
