package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: TestCircleViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()
                val errorMessage by viewModel.errorMessage.collectAsState()
                val context = LocalContext.current

                // Toast status/error message listeners
                LaunchedEffect(statusMessage) {
                    statusMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessages()
                    }
                }

                LaunchedEffect(errorMessage) {
                    errorMessage?.let {
                        Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                        viewModel.clearMessages()
                    }
                }

                // Global Android BackHandler
                BackHandler(enabled = currentScreen != Screen.HOME && currentScreen != Screen.SPLASH) {
                    viewModel.navigateBack()
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        Screen.SPLASH -> SplashScreen(viewModel)
                        Screen.LOGIN -> GoogleLoginScreen(viewModel)
                        Screen.HOME -> HomeDashboardScreen(viewModel)
                        Screen.FREE_DASHBOARD -> FreeTestingDashboardScreen(viewModel)
                        Screen.PRO_DASHBOARD -> ProTestingDashboardScreen(viewModel)
                        Screen.TESTER_REGISTRATION -> TesterRegistrationScreen(viewModel)
                        Screen.MY_TASKS -> MyTestingTasksScreen(viewModel)
                        Screen.XP_REWARDS -> XpRewardsScreen(viewModel)
                        Screen.NOTIFICATIONS -> NotificationsScreen(viewModel)
                        Screen.USER_PROFILE -> UserProfileScreen(viewModel)
                        Screen.ADMIN_PANEL -> AdminPanelScreen(viewModel)
                        Screen.PAYMENT_VERIFICATION -> PaymentVerificationScreen(viewModel)
                        Screen.CAMPAIGN_MANAGEMENT -> CampaignManagementScreen(viewModel)
                        Screen.DAILY_REPORTS -> DailyTestingReportsScreen(viewModel)
                    }
                }
            }
        }
    }
}
