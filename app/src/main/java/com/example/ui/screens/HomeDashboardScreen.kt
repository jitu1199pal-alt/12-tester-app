package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun HomeDashboardScreen(viewModel: TestCircleViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val apps by viewModel.appsList.collectAsState()
    val activeCampaignsCount = apps.count { it.status.name == "ACTIVE_CAMPAIGN" }

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "TestCircle",
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) },
                        modifier = Modifier.testTag("home_notifications_button")
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.USER_PROFILE) },
                        modifier = Modifier.testTag("home_profile_button")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                }
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
            // User greeting & role pill
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, ${currentUser?.displayName ?: "Guest"}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentUser?.email ?: "Not signed in",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (currentUser?.isAdmin == true) {
                        StatusBadge(text = "ADMIN ACCESS", color = AmberWarning)
                    } else if (currentUser?.isTesterRegistered == true) {
                        StatusBadge(
                            text = "TESTER: ${currentUser?.testerStatus?.name ?: "PENDING"}",
                            color = BrandCyan
                        )
                    }
                }
            }

            // TESTER RECRUITMENT BANNER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tester_recruitment_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(BrandBlue, Color(0xFF1E40AF), BrandCyan)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = BrandCyanAccent,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "15 Days App Testing – Earn Rewards",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Test exciting Android apps for 15 days, report bugs, help indie developers qualify for Google Play, and earn verified XP rewards per daily report.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate100
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "*No guaranteed income promised. Rewards based on eligibility.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Slate200,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.TESTER_REGISTRATION) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = BrandBlue
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("fill_tester_form_button")
                                ) {
                                    Text("FILL TESTER FORM", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 7 MAIN ACTION BUTTONS
            item {
                Text(
                    text = "Main Navigation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                HomeNavCard(
                    title = "Free Testing",
                    subtitle = "Community peer-to-peer 15-day testing queue",
                    icon = Icons.Default.Groups,
                    color = FreeGreen,
                    testTag = "nav_free_testing",
                    onClick = { viewModel.navigateTo(Screen.FREE_DASHBOARD) }
                )
            }

            item {
                HomeNavCard(
                    title = "Pro Testing – ₹699 per request",
                    subtitle = "Dedicated 13-tester group with manual UPI verification",
                    icon = Icons.Default.WorkspacePremium,
                    color = ProGold,
                    testTag = "nav_pro_testing",
                    isHighlight = true,
                    onClick = { viewModel.navigateTo(Screen.PRO_DASHBOARD) }
                )
            }

            item {
                HomeNavCard(
                    title = "Become a Tester",
                    subtitle = "Register device details & 15-day availability",
                    icon = Icons.Default.AppRegistration,
                    color = BrandCyan,
                    testTag = "nav_become_tester",
                    onClick = { viewModel.navigateTo(Screen.TESTER_REGISTRATION) }
                )
            }

            item {
                HomeNavCard(
                    title = "My Testing Tasks",
                    subtitle = "View assigned apps, instructions & submit daily feedback",
                    icon = Icons.Default.Assignment,
                    color = BrandBlue,
                    testTag = "nav_my_tasks",
                    onClick = { viewModel.navigateTo(Screen.MY_TASKS) }
                )
            }

            item {
                HomeNavCard(
                    title = "XP & Rewards",
                    subtitle = "Verified XP ledger & rewards progress (${currentUser?.xpBalance ?: 0} XP)",
                    icon = Icons.Default.MilitaryTech,
                    color = ProGold,
                    testTag = "nav_xp_rewards",
                    onClick = { viewModel.navigateTo(Screen.XP_REWARDS) }
                )
            }

            item {
                HomeNavCard(
                    title = "Notifications",
                    subtitle = "Campaign updates, approval notices & alerts",
                    icon = Icons.Default.NotificationsActive,
                    color = BrandBlueLight,
                    testTag = "nav_notifications",
                    onClick = { viewModel.navigateTo(Screen.NOTIFICATIONS) }
                )
            }

            item {
                HomeNavCard(
                    title = "My Profile",
                    subtitle = "Tester credentials, active campaigns & settings",
                    icon = Icons.Default.Person,
                    color = Slate500,
                    testTag = "nav_my_profile",
                    onClick = { viewModel.navigateTo(Screen.USER_PROFILE) }
                )
            }

            // ADMIN PANEL ACCESS (Conditional / Explicit)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.ADMIN_PANEL) }
                        .testTag("nav_admin_panel"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentUser?.isAdmin == true)
                            AmberWarning.copy(alpha = 0.15f)
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AmberWarning.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = AmberWarning
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Admin Panel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentUser?.isAdmin == true)
                                    "Authorized access (jitu1199pal@gmail.com)"
                                else
                                    "Requires authorized admin email",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HomeNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    testTag: String,
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
