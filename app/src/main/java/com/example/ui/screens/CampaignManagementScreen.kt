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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AppStatus
import com.example.model.TestingApp
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun CampaignManagementScreen(viewModel: TestCircleViewModel) {
    val apps by viewModel.appsList.collectAsState()
    val activeCampaigns = apps.filter { it.status == AppStatus.ACTIVE_CAMPAIGN || it.status == AppStatus.APPROVED }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Campaign Management",
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = BrandBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "15-Day Testing Progression",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BrandBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tracks daily participation and bug reports from Day 1 to Day 15 for each enrolled application to meet Google Play closed test guidelines.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(activeCampaigns, key = { it.id }) { app ->
                CampaignCard(
                    app = app,
                    onViewReports = {
                        viewModel.selectApp(app)
                        viewModel.navigateTo(Screen.DAILY_REPORTS)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CampaignCard(app: TestingApp, onViewReports: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(app.appName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusBadge(text = "Day ${app.campaignDay}/15", color = BrandCyan)
            }
            Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = BrandBlueLight)

            Spacer(modifier = Modifier.height(10.dp))
            val progress = (app.campaignDay.toFloat() / 15f).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = BrandCyan,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Assigned Testers: ${app.currentTesterCount} / 13", style = MaterialTheme.typography.bodySmall)
                Text("Tier: ${app.tier.name}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }

            if (app.googleGroupUrl.isNotBlank()) {
                Text("Google Group: ${app.googleGroupUrl}", style = MaterialTheme.typography.labelSmall, color = BrandBlueLight)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onViewReports,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View Daily Reports & Submissions")
            }
        }
    }
}
