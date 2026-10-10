package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.TestCircleViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(viewModel: TestCircleViewModel) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Notifications",
                onBack = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Recent Alerts & Updates (${notifications.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No notifications yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(notifications, key = { it.id }) { notif ->
                    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(notif.timestamp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when (notif.type) {
                                            "PAYMENT" -> ProGold.copy(alpha = 0.2f)
                                            "XP" -> EmeraldGreen.copy(alpha = 0.2f)
                                            "CAMPAIGN" -> BrandCyan.copy(alpha = 0.2f)
                                            else -> BrandBlue.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (notif.type) {
                                        "PAYMENT" -> Icons.Default.CurrencyRupee
                                        "XP" -> Icons.Default.MilitaryTech
                                        "CAMPAIGN" -> Icons.Default.RocketLaunch
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when (notif.type) {
                                        "PAYMENT" -> ProGold
                                        "XP" -> EmeraldGreen
                                        "CAMPAIGN" -> BrandCyan
                                        else -> BrandBlue
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = notif.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
