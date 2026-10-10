package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TestCircleRepository
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    SPLASH,
    LOGIN,
    HOME,
    FREE_DASHBOARD,
    PRO_DASHBOARD,
    TESTER_REGISTRATION,
    MY_TASKS,
    XP_REWARDS,
    NOTIFICATIONS,
    USER_PROFILE,
    ADMIN_PANEL,
    PAYMENT_VERIFICATION,
    CAMPAIGN_MANAGEMENT,
    DAILY_REPORTS
}

class TestCircleViewModel(
    private val repository: TestCircleRepository = TestCircleRepository()
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.SPLASH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _navigationStack = mutableListOf<Screen>()

    // Selected items for detail/action views
    private val _selectedApp = MutableStateFlow<TestingApp?>(null)
    val selectedApp: StateFlow<TestingApp?> = _selectedApp.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Exposed Flows from repository
    val currentUser: StateFlow<UserAccount?> = repository.currentUser
    val appsList: StateFlow<List<TestingApp>> = repository.appsList
    val testerProfiles: StateFlow<List<TesterProfile>> = repository.testerProfiles
    val dailyReports: StateFlow<List<DailyReport>> = repository.dailyReports
    val xpTransactions: StateFlow<List<XpTransaction>> = repository.xpTransactions
    val notifications: StateFlow<List<AppNotification>> = repository.notifications

    init {
        // Auto-login with demo account for immediate usability, default as developer/tester
        repository.loginWithGoogle("tester.community@gmail.com", "Alex Hunter")
    }

    fun navigateTo(screen: Screen) {
        _navigationStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (_navigationStack.isNotEmpty()) {
            _currentScreen.value = _navigationStack.removeAt(_navigationStack.lastIndex)
            return true
        } else if (_currentScreen.value != Screen.HOME) {
            _currentScreen.value = Screen.HOME
            return true
        }
        return false
    }

    fun clearMessages() {
        _statusMessage.value = null
        _errorMessage.value = null
    }

    fun selectApp(app: TestingApp?) {
        _selectedApp.value = app
    }

    // ---------------- Auth ----------------
    fun login(email: String, name: String) {
        repository.loginWithGoogle(email, name)
        _statusMessage.value = "Signed in as $email"
        _currentScreen.value = Screen.HOME
    }

    fun loginAsAdminQuickSwitch() {
        repository.loginWithGoogle(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, "Jitu Pal (Admin)")
        _statusMessage.value = "Switched to authorized Admin: ${TestCircleRepository.AUTHORIZED_ADMIN_EMAIL}"
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = Screen.LOGIN
    }

    // ---------------- App Submissions ----------------
    fun submitFreeApp(
        name: String,
        pkg: String,
        link: String,
        minVer: String,
        instr: String,
        logo: String
    ) {
        val result = repository.submitFreeApp(name, pkg, link, minVer, instr, logo)
        result.onSuccess {
            _statusMessage.value = "Free testing request submitted successfully!"
            navigateTo(Screen.FREE_DASHBOARD)
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    fun submitProApp(
        name: String,
        pkg: String,
        link: String,
        minVer: String,
        instr: String,
        logo: String
    ) {
        val result = repository.submitProApp(name, pkg, link, minVer, instr, logo)
        result.onSuccess { app ->
            _selectedApp.value = app
            _statusMessage.value = "Pro testing request created. Proceed to UPI payment."
            navigateTo(Screen.PAYMENT_VERIFICATION)
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    fun submitUpiPayment(appId: String, payerName: String, utr: String, screenshotRef: String) {
        val result = repository.submitUpiPaymentDetails(appId, payerName, utr, screenshotRef)
        result.onSuccess {
            _statusMessage.value = "UTR submitted! Status set to PAYMENT_PENDING_VERIFICATION."
            navigateTo(Screen.PRO_DASHBOARD)
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    // ---------------- Admin Actions ----------------
    fun adminVerifyPayment(appId: String, approve: Boolean, notes: String) {
        val result = repository.adminVerifyPayment(appId, approve, notes)
        result.onSuccess { token ->
            _statusMessage.value = if (approve) "Payment approved! Token: $token" else "Payment rejected."
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    fun adminReviewTester(testerId: String, approve: Boolean) {
        val result = repository.adminReviewTester(testerId, approve)
        result.onSuccess {
            _statusMessage.value = if (approve) "Tester application approved!" else "Tester application rejected."
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    fun adminAssignTesters(appId: String, testerIds: List<String>, groupUrl: String) {
        val result = repository.adminAssign13Testers(appId, testerIds, groupUrl)
        result.onSuccess {
            _statusMessage.value = "Campaign activated with ${testerIds.size} testers!"
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    // ---------------- Tester Form ----------------
    fun registerTester(
        fullName: String,
        gmail: String,
        cityState: String,
        androidVer: String,
        deviceModel: String,
        avail15: Boolean,
        agreed: Boolean
    ) {
        val result = repository.registerTester(fullName, gmail, cityState, androidVer, deviceModel, avail15, agreed)
        result.onSuccess {
            _statusMessage.value = "Tester application submitted! Awaiting admin approval."
            navigateTo(Screen.HOME)
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    // ---------------- Daily Reports & XP ----------------
    fun submitDailyReport(
        campaignId: String,
        dayNum: Int,
        checklist: Boolean,
        feedback: String,
        bugs: String,
        screenshot: String
    ) {
        val result = repository.submitDailyReport(campaignId, dayNum, checklist, feedback, bugs, screenshot)
        result.onSuccess {
            _statusMessage.value = "Day $dayNum report submitted! Admin will verify for +1 XP."
            navigateTo(Screen.MY_TASKS)
        }.onFailure {
            _errorMessage.value = it.message
        }
    }

    fun adminReviewReport(reportId: String, approve: Boolean, notes: String) {
        val result = repository.adminReviewDailyReport(reportId, approve, notes)
        result.onSuccess {
            _statusMessage.value = if (approve) "Report approved and +1 XP awarded to tester ledger!" else "Report rejected."
        }.onFailure {
            _errorMessage.value = it.message
        }
    }
}
