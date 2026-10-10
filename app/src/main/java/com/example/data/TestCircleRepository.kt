package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Robust repository managing TestCircle data state with strict authorization checks,
 * idempotency for XP awards, and separate Free and Pro campaign workflows.
 */
class TestCircleRepository {

    companion object {
        const val AUTHORIZED_ADMIN_EMAIL = "jitu1199pal@gmail.com"
        const val PRO_TESTING_PRICE_INR = 699.0
        const val OFFICIAL_UPI_ID = "jitu1199pal@okaxis"
        const val OFFICIAL_UPI_PAYEE_NAME = "TestCircle Official"
    }

    // Current user state
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Apps (Free and Pro)
    private val _appsList = MutableStateFlow<List<TestingApp>>(emptyList())
    val appsList: StateFlow<List<TestingApp>> = _appsList.asStateFlow()

    // Tester Profiles
    private val _testerProfiles = MutableStateFlow<List<TesterProfile>>(emptyList())
    val testerProfiles: StateFlow<List<TesterProfile>> = _testerProfiles.asStateFlow()

    // Daily Reports
    private val _dailyReports = MutableStateFlow<List<DailyReport>>(emptyList())
    val dailyReports: StateFlow<List<DailyReport>> = _dailyReports.asStateFlow()

    // XP Transactions Ledger
    private val _xpTransactions = MutableStateFlow<List<XpTransaction>>(emptyList())
    val xpTransactions: StateFlow<List<XpTransaction>> = _xpTransactions.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val initialApps = listOf(
            TestingApp(
                id = "app_pro_101",
                tier = TestingTier.PRO,
                appName = "FinTrack India – Budget & UPI",
                appLogoUrl = "",
                packageName = "com.fintrack.india.app",
                testingLink = "https://play.google.com/apps/testing/com.fintrack.india.app",
                minAndroidVersion = "Android 9.0 (API 28)+",
                testingInstructions = "1. Install app via Google Play Closed Testing link.\n2. Open app daily for at least 3 minutes.\n3. Verify SMS expense parsing and create 2 test budgets.\n4. Submit daily report with feedback.",
                requiredTesterCount = 13,
                currentTesterCount = 13,
                status = AppStatus.ACTIVE_CAMPAIGN,
                paymentStatus = PaymentStatus.PAYMENT_APPROVED,
                upiPayerName = "Rahul Verma",
                upiUtrNumber = "UTR482910394821",
                paymentAmount = 699.0,
                adminApprovalToken = "TC-PRO-VERIFIED-9812A",
                verifiedByAdminEmail = AUTHORIZED_ADMIN_EMAIL,
                verifiedAtTimestamp = System.currentTimeMillis() - 86400000L * 4,
                assignedTesterIds = listOf("tester_1", "tester_2", "tester_3", "tester_user_demo"),
                googleGroupUrl = "https://groups.google.com/g/testcircle-fintrack-testers",
                campaignDay = 4,
                createdByUserId = "dev_user_1",
                createdByUserEmail = "developer@fintrack.in",
                createdAt = System.currentTimeMillis() - 86400000L * 5
            ),
            TestingApp(
                id = "app_free_201",
                tier = TestingTier.FREE,
                appName = "HabitOrbit – Daily Habit Tracker",
                appLogoUrl = "",
                packageName = "com.habitorbit.community",
                testingLink = "https://play.google.com/apps/testing/com.habitorbit.community",
                minAndroidVersion = "Android 8.0 (API 26)+",
                testingInstructions = "Community test: Add 3 habits, set reminders, and test dark mode theme toggle. Verify widget functionality.",
                requiredTesterCount = 13,
                currentTesterCount = 8,
                status = AppStatus.PENDING_APPROVAL,
                paymentStatus = PaymentStatus.NOT_APPLICABLE,
                assignedTesterIds = emptyList(),
                googleGroupUrl = "https://groups.google.com/g/testcircle-habitorbit",
                campaignDay = 1,
                createdByUserId = "dev_user_2",
                createdByUserEmail = "indie.builder@gmail.com",
                createdAt = System.currentTimeMillis() - 86400000L * 2
            ),
            TestingApp(
                id = "app_pro_102",
                tier = TestingTier.PRO,
                appName = "QuickDoc – Offline PDF Scanner",
                appLogoUrl = "",
                packageName = "com.quickdoc.scanner.ocr",
                testingLink = "https://play.google.com/apps/testing/com.quickdoc.scanner.ocr",
                minAndroidVersion = "Android 10.0 (API 29)+",
                testingInstructions = "Test camera auto-edge detection, multi-page scan PDF generation, and export to Google Drive.",
                requiredTesterCount = 13,
                currentTesterCount = 0,
                status = AppStatus.PENDING_APPROVAL,
                paymentStatus = PaymentStatus.PAYMENT_PENDING_VERIFICATION,
                upiPayerName = "Amit Sharma",
                upiUtrNumber = "UTR994821048123",
                paymentAmount = 699.0,
                paymentScreenshotUrl = "screenshot_utr994821048123.png",
                assignedTesterIds = emptyList(),
                googleGroupUrl = "",
                campaignDay = 0,
                createdByUserId = "dev_user_3",
                createdByUserEmail = "amit.sharma.dev@gmail.com",
                createdAt = System.currentTimeMillis() - 3600000L * 5
            )
        )
        _appsList.value = initialApps

        val initialTesters = listOf(
            TesterProfile(
                id = "tester_1",
                fullName = "Rohan Mehta",
                gmailAddress = "rohan.mehta.tech@gmail.com",
                cityState = "Pune, Maharashtra",
                androidVersion = "Android 14 (One UI 6.1)",
                deviceModel = "Samsung Galaxy S23",
                availableFor15Days = true,
                agreedToRules = true,
                status = TesterStatus.APPROVED,
                assignedCampaignIds = listOf("app_pro_101"),
                completedCampaignsCount = 2,
                totalVerifiedXp = 28
            ),
            TesterProfile(
                id = "tester_2",
                fullName = "Priya Nair",
                gmailAddress = "priya.nair.qa@gmail.com",
                cityState = "Bengaluru, Karnataka",
                androidVersion = "Android 13",
                deviceModel = "Google Pixel 7a",
                availableFor15Days = true,
                agreedToRules = true,
                status = TesterStatus.APPROVED,
                assignedCampaignIds = listOf("app_pro_101"),
                completedCampaignsCount = 1,
                totalVerifiedXp = 15
            ),
            TesterProfile(
                id = "tester_3",
                fullName = "Aakash Singh",
                gmailAddress = "aakash.singh99@gmail.com",
                cityState = "Noida, Uttar Pradesh",
                androidVersion = "Android 12",
                deviceModel = "OnePlus Nord 2",
                availableFor15Days = true,
                agreedToRules = true,
                status = TesterStatus.APPROVED,
                assignedCampaignIds = listOf("app_pro_101"),
                completedCampaignsCount = 3,
                totalVerifiedXp = 42
            ),
            TesterProfile(
                id = "tester_4",
                fullName = "Sunita Rao",
                gmailAddress = "sunita.rao.testers@gmail.com",
                cityState = "Hyderabad, Telangana",
                androidVersion = "Android 14",
                deviceModel = "Motorola Edge 40",
                availableFor15Days = true,
                agreedToRules = true,
                status = TesterStatus.PENDING,
                assignedCampaignIds = emptyList(),
                completedCampaignsCount = 0,
                totalVerifiedXp = 0
            )
        )
        _testerProfiles.value = initialTesters

        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val initialReports = listOf(
            DailyReport(
                id = "rep_01",
                campaignId = "app_pro_101",
                appName = "FinTrack India – Budget & UPI",
                testerId = "tester_1",
                testerEmail = "rohan.mehta.tech@gmail.com",
                testerName = "Rohan Mehta",
                dateIso = todayDate,
                campaignDayNumber = 4,
                checklistCompleted = true,
                feedbackText = "App launched seamlessly. Successfully tested SMS reader permission and monthly budget chart. Smooth 60fps animations.",
                bugReportText = "No bugs observed on Android 14.",
                screenshotUrl = "",
                status = ReportStatus.APPROVED,
                xpAwarded = true,
                adminNotes = "Verified genuine daily test session.",
                reviewedAt = System.currentTimeMillis() - 3600000L,
                reviewedByAdminEmail = AUTHORIZED_ADMIN_EMAIL
            )
        )
        _dailyReports.value = initialReports

        val initialXpLedger = listOf(
            XpTransaction(
                id = "xp_tx_1001",
                testerId = "tester_1",
                testerEmail = "rohan.mehta.tech@gmail.com",
                campaignId = "app_pro_101",
                reportId = "rep_01",
                dayNumber = 4,
                xpAmount = 1,
                reason = "Verified Day 4 testing report for FinTrack India",
                awardedAt = System.currentTimeMillis() - 3600000L,
                verifiedBy = AUTHORIZED_ADMIN_EMAIL
            )
        )
        _xpTransactions.value = initialXpLedger

        _notifications.value = listOf(
            AppNotification(
                id = "notif_1",
                title = "Welcome to TestCircle!",
                message = "Join the 15-day Android testing community. Submit your app or register to test.",
                type = "INFO"
            ),
            AppNotification(
                id = "notif_2",
                title = "New Pro Testing Request",
                message = "QuickDoc scanner submitted a ₹699 Pro request pending UPI admin verification.",
                type = "PAYMENT"
            )
        )
    }

    // -------------------------------------------------------------
    // Authentication & Profile
    // -------------------------------------------------------------
    fun loginWithGoogle(email: String, displayName: String) {
        val isAdmin = email.trim().equals(AUTHORIZED_ADMIN_EMAIL, ignoreCase = true)
        val existingTester = _testerProfiles.value.find { it.gmailAddress.equals(email, ignoreCase = true) }
        val userXp = _xpTransactions.value
            .filter { it.testerEmail.equals(email, ignoreCase = true) }
            .sumOf { it.xpAmount }

        _currentUser.value = UserAccount(
            uid = "usr_" + email.replace(Regex("[^a-zA-Z0-9]"), "_"),
            email = email,
            displayName = displayName.ifBlank { email.substringBefore("@") },
            isAdmin = isAdmin,
            isTesterRegistered = existingTester != null,
            testerStatus = existingTester?.status,
            xpBalance = userXp
        )
    }

    fun logout() {
        _currentUser.value = null
    }

    // -------------------------------------------------------------
    // App Submissions (Free & Pro)
    // -------------------------------------------------------------
    fun submitFreeApp(
        appName: String,
        packageName: String,
        testingLink: String,
        minAndroidVersion: String,
        testingInstructions: String,
        appLogoUrl: String
    ): Result<TestingApp> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first"))
        val id = "free_" + UUID.randomUUID().toString().substring(0, 8)
        val newApp = TestingApp(
            id = id,
            tier = TestingTier.FREE,
            appName = appName.trim(),
            appLogoUrl = appLogoUrl.trim(),
            packageName = packageName.trim(),
            testingLink = testingLink.trim(),
            minAndroidVersion = minAndroidVersion.trim().ifBlank { "Android 8.0+" },
            testingInstructions = testingInstructions.trim(),
            requiredTesterCount = 13,
            currentTesterCount = 0,
            status = AppStatus.PENDING_APPROVAL,
            paymentStatus = PaymentStatus.NOT_APPLICABLE,
            createdByUserId = user.uid,
            createdByUserEmail = user.email
        )
        _appsList.value = listOf(newApp) + _appsList.value
        addNotification("Free App Submitted", "Submitted '$appName' for community review.", "INFO")
        return Result.success(newApp)
    }

    fun submitProApp(
        appName: String,
        packageName: String,
        testingLink: String,
        minAndroidVersion: String,
        testingInstructions: String,
        appLogoUrl: String
    ): Result<TestingApp> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first"))
        val id = "pro_" + UUID.randomUUID().toString().substring(0, 8)
        val newApp = TestingApp(
            id = id,
            tier = TestingTier.PRO,
            appName = appName.trim(),
            appLogoUrl = appLogoUrl.trim(),
            packageName = packageName.trim(),
            testingLink = testingLink.trim(),
            minAndroidVersion = minAndroidVersion.trim().ifBlank { "Android 8.0+" },
            testingInstructions = testingInstructions.trim(),
            requiredTesterCount = 13,
            currentTesterCount = 0,
            status = AppStatus.PENDING_APPROVAL,
            paymentStatus = PaymentStatus.PAYMENT_PENDING_SUBMISSION,
            paymentAmount = PRO_TESTING_PRICE_INR,
            createdByUserId = user.uid,
            createdByUserEmail = user.email
        )
        _appsList.value = listOf(newApp) + _appsList.value
        addNotification("Pro App Created", "Complete ₹699 manual UPI payment to proceed with '$appName'.", "PAYMENT")
        return Result.success(newApp)
    }

    fun submitUpiPaymentDetails(
        appId: String,
        payerName: String,
        utrNumber: String,
        screenshotReference: String
    ): Result<Unit> {
        val app = _appsList.value.find { it.id == appId }
            ?: return Result.failure(Exception("App request not found"))

        if (utrNumber.trim().length < 8) {
            return Result.failure(Exception("Invalid UTR / UPI Reference number (minimum 8 digits)."))
        }

        // Anti-fraud: prevent reuse of the same UTR number across already verified or pending apps
        val duplicateUtr = _appsList.value.find {
            it.id != appId && it.upiUtrNumber.equals(utrNumber.trim(), ignoreCase = true)
        }
        if (duplicateUtr != null) {
            return Result.failure(Exception("This UTR number has already been submitted for another request."))
        }

        val updated = app.copy(
            upiPayerName = payerName.trim(),
            upiUtrNumber = utrNumber.trim(),
            paymentScreenshotUrl = screenshotReference.trim(),
            paymentStatus = PaymentStatus.PAYMENT_PENDING_VERIFICATION
        )

        _appsList.value = _appsList.value.map { if (it.id == appId) updated else it }
        addNotification("Payment Under Verification", "UTR $utrNumber submitted for '${app.appName}'. Admin will verify shortly.", "PAYMENT")
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // Admin Actions (Strict authorization: jitu1199pal@gmail.com)
    // -------------------------------------------------------------
    private fun requireAdmin(): Result<UserAccount> {
        val user = _currentUser.value
        return if (user != null && user.isAdmin) {
            Result.success(user)
        } else {
            Result.failure(SecurityException("Unauthorized: Admin authorization required (jitu1199pal@gmail.com)"))
        }
    }

    fun adminVerifyPayment(
        appId: String,
        approved: Boolean,
        adminNotes: String
    ): Result<String> {
        val admin = requireAdmin().getOrElse { return Result.failure(it) }
        val app = _appsList.value.find { it.id == appId }
            ?: return Result.failure(Exception("App not found"))

        if (approved) {
            val token = "TC-PRO-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
            val updated = app.copy(
                paymentStatus = PaymentStatus.PAYMENT_APPROVED,
                status = AppStatus.APPROVED,
                adminApprovalToken = token,
                verifiedByAdminEmail = admin.email,
                verifiedAtTimestamp = System.currentTimeMillis()
            )
            _appsList.value = _appsList.value.map { if (it.id == appId) updated else it }
            addNotification("Pro Payment Approved", "Payment verified for '${app.appName}'. Token generated: $token", "PAYMENT")
            return Result.success(token)
        } else {
            val updated = app.copy(
                paymentStatus = PaymentStatus.PAYMENT_REJECTED,
                status = AppStatus.REJECTED,
                verifiedByAdminEmail = admin.email,
                verifiedAtTimestamp = System.currentTimeMillis()
            )
            _appsList.value = _appsList.value.map { if (it.id == appId) updated else it }
            addNotification("Payment Rejected", "Payment rejected for '${app.appName}': $adminNotes", "PAYMENT")
            return Result.success("REJECTED")
        }
    }

    fun adminUpdateApp(
        appId: String,
        appName: String,
        packageName: String,
        testingLink: String,
        instructions: String,
        googleGroupUrl: String,
        status: AppStatus
    ): Result<Unit> {
        requireAdmin().getOrElse { return Result.failure(it) }
        val app = _appsList.value.find { it.id == appId } ?: return Result.failure(Exception("App not found"))
        val updated = app.copy(
            appName = appName.trim(),
            packageName = packageName.trim(),
            testingLink = testingLink.trim(),
            testingInstructions = instructions.trim(),
            googleGroupUrl = googleGroupUrl.trim(),
            status = status
        )
        _appsList.value = _appsList.value.map { if (it.id == appId) updated else it }
        return Result.success(Unit)
    }

    fun adminReviewTester(testerId: String, approved: Boolean): Result<Unit> {
        val admin = requireAdmin().getOrElse { return Result.failure(it) }
        val tester = _testerProfiles.value.find { it.id == testerId } ?: return Result.failure(Exception("Tester not found"))
        val updated = tester.copy(
            status = if (approved) TesterStatus.APPROVED else TesterStatus.REJECTED,
            reviewedByAdminEmail = admin.email
        )
        _testerProfiles.value = _testerProfiles.value.map { if (it.id == testerId) updated else it }

        // Update current user cache if it was the tester
        if (_currentUser.value?.uid == testerId) {
            _currentUser.value = _currentUser.value?.copy(
                isTesterRegistered = true,
                testerStatus = updated.status
            )
        }
        return Result.success(Unit)
    }

    fun adminAssign13Testers(appId: String, testerIds: List<String>, googleGroupUrl: String): Result<Unit> {
        requireAdmin().getOrElse { return Result.failure(it) }
        val app = _appsList.value.find { it.id == appId } ?: return Result.failure(Exception("App not found"))

        val approvedTesters = _testerProfiles.value.filter { it.id in testerIds && it.status == TesterStatus.APPROVED }
        if (approvedTesters.size < 1) {
            return Result.failure(Exception("At least 1 approved tester must be selected."))
        }

        val updatedApp = app.copy(
            assignedTesterIds = testerIds,
            currentTesterCount = testerIds.size,
            googleGroupUrl = googleGroupUrl.trim(),
            status = AppStatus.ACTIVE_CAMPAIGN,
            campaignDay = if (app.campaignDay == 0) 1 else app.campaignDay
        )
        _appsList.value = _appsList.value.map { if (it.id == appId) updatedApp else it }

        // Link campaign to testers
        _testerProfiles.value = _testerProfiles.value.map { profile ->
            if (profile.id in testerIds) {
                profile.copy(
                    assignedCampaignIds = (profile.assignedCampaignIds + appId).distinct()
                )
            } else profile
        }
        addNotification("Testers Assigned", "Assigned ${testerIds.size} testers to '${app.appName}'. Campaign is ACTIVE.", "CAMPAIGN")
        return Result.success(Unit)
    }

    // -------------------------------------------------------------
    // Tester Registration
    // -------------------------------------------------------------
    fun registerTester(
        fullName: String,
        gmailAddress: String,
        cityState: String,
        androidVersion: String,
        deviceModel: String,
        availableFor15Days: Boolean,
        agreedToRules: Boolean
    ): Result<TesterProfile> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in with Google first"))
        if (!agreedToRules) {
            return Result.failure(Exception("You must agree to the 15-day testing rules."))
        }
        if (!availableFor15Days) {
            return Result.failure(Exception("You must be available for all 15 consecutive testing days."))
        }

        val profile = TesterProfile(
            id = user.uid,
            fullName = fullName.trim(),
            gmailAddress = gmailAddress.trim(),
            cityState = cityState.trim(),
            androidVersion = androidVersion.trim(),
            deviceModel = deviceModel.trim(),
            availableFor15Days = true,
            agreedToRules = true,
            status = TesterStatus.PENDING
        )

        _testerProfiles.value = _testerProfiles.value.filter { it.id != user.uid } + profile
        _currentUser.value = user.copy(
            isTesterRegistered = true,
            testerStatus = TesterStatus.PENDING
        )
        addNotification("Application Submitted", "Your tester application is under admin review.", "TESTER")
        return Result.success(profile)
    }

    // -------------------------------------------------------------
    // Daily Testing Reports & Anti-Fraud XP Awarding
    // -------------------------------------------------------------
    fun submitDailyReport(
        campaignId: String,
        campaignDayNumber: Int,
        checklistCompleted: Boolean,
        feedbackText: String,
        bugReportText: String,
        screenshotUrl: String
    ): Result<DailyReport> {
        val user = _currentUser.value ?: return Result.failure(Exception("Please sign in first"))
        val app = _appsList.value.find { it.id == campaignId }
            ?: return Result.failure(Exception("Campaign app not found"))

        val todayIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Anti-fraud: Verify tester is assigned to this campaign
        val testerProfile = _testerProfiles.value.find { it.id == user.uid }
        val isAssigned = (user.uid in app.assignedTesterIds) || (user.email.lowercase() in listOf(AUTHORIZED_ADMIN_EMAIL.lowercase()))
        if (!isAssigned) {
            return Result.failure(SecurityException("You are not an assigned tester for this campaign."))
        }

        // Anti-fraud: One report per tester, per campaign, per date
        val existingReportToday = _dailyReports.value.find {
            it.campaignId == campaignId && it.testerId == user.uid && it.dateIso == todayIso
        }
        if (existingReportToday != null) {
            return Result.failure(Exception("You have already submitted today's report ($todayIso) for this campaign."))
        }

        if (feedbackText.trim().length < 15) {
            return Result.failure(Exception("Please provide meaningful feedback (at least 15 characters)."))
        }

        val reportId = "rep_" + UUID.randomUUID().toString().substring(0, 8)
        val report = DailyReport(
            id = reportId,
            campaignId = campaignId,
            appName = app.appName,
            testerId = user.uid,
            testerEmail = user.email,
            testerName = testerProfile?.fullName ?: user.displayName,
            dateIso = todayIso,
            campaignDayNumber = campaignDayNumber.coerceIn(1, 15),
            checklistCompleted = checklistCompleted,
            feedbackText = feedbackText.trim(),
            bugReportText = bugReportText.trim(),
            screenshotUrl = screenshotUrl.trim(),
            status = ReportStatus.PENDING_REVIEW,
            xpAwarded = false
        )

        _dailyReports.value = listOf(report) + _dailyReports.value
        addNotification("Report Submitted", "Submitted Day $campaignDayNumber report for '${app.appName}'. Pending admin verification for +1 XP.", "XP")
        return Result.success(report)
    }

    /**
     * Admin verifies daily report and awards 1 XP strictly once (auditable ledger).
     */
    fun adminReviewDailyReport(
        reportId: String,
        approved: Boolean,
        adminNotes: String
    ): Result<Unit> {
        val admin = requireAdmin().getOrElse { return Result.failure(it) }
        val report = _dailyReports.value.find { it.id == reportId }
            ?: return Result.failure(Exception("Report not found"))

        if (!approved) {
            val updated = report.copy(
                status = ReportStatus.REJECTED,
                adminNotes = adminNotes,
                reviewedAt = System.currentTimeMillis(),
                reviewedByAdminEmail = admin.email
            )
            _dailyReports.value = _dailyReports.value.map { if (it.id == reportId) updated else it }
            return Result.success(Unit)
        }

        // Anti-fraud: strictly prevent duplicate XP award for the same report/date
        val alreadyAwarded = _xpTransactions.value.any { it.reportId == reportId }
        val duplicateSameDay = _xpTransactions.value.any {
            it.campaignId == report.campaignId &&
            it.testerId == report.testerId &&
            it.dayNumber == report.campaignDayNumber
        }

        if (alreadyAwarded || duplicateSameDay) {
            return Result.failure(Exception("Anti-fraud trigger: 1 XP has already been awarded for this tester on Day ${report.campaignDayNumber}."))
        }

        // Create immutable ledger entry
        val transaction = XpTransaction(
            id = "xp_" + UUID.randomUUID().toString().substring(0, 8),
            testerId = report.testerId,
            testerEmail = report.testerEmail,
            campaignId = report.campaignId,
            reportId = report.id,
            dayNumber = report.campaignDayNumber,
            xpAmount = 1,
            reason = "Verified Day ${report.campaignDayNumber} testing report for ${report.appName}",
            awardedAt = System.currentTimeMillis(),
            verifiedBy = admin.email
        )

        _xpTransactions.value = listOf(transaction) + _xpTransactions.value

        // Update report status
        val updatedReport = report.copy(
            status = ReportStatus.APPROVED,
            xpAwarded = true,
            adminNotes = adminNotes,
            reviewedAt = System.currentTimeMillis(),
            reviewedByAdminEmail = admin.email
        )
        _dailyReports.value = _dailyReports.value.map { if (it.id == reportId) updatedReport else it }

        // Update tester total XP
        _testerProfiles.value = _testerProfiles.value.map { tester ->
            if (tester.id == report.testerId) {
                tester.copy(totalVerifiedXp = tester.totalVerifiedXp + 1)
            } else tester
        }

        // Refresh current user balance if reviewing current user
        if (_currentUser.value?.uid == report.testerId) {
            val updatedBalance = _currentUser.value!!.xpBalance + 1
            _currentUser.value = _currentUser.value?.copy(xpBalance = updatedBalance)
        }

        addNotification("XP Verified", "+1 XP awarded to ${report.testerName} for ${report.appName} Day ${report.campaignDayNumber}.", "XP")
        return Result.success(Unit)
    }

    private fun addNotification(title: String, message: String, type: String) {
        val notif = AppNotification(
            id = "notif_" + UUID.randomUUID().toString().substring(0, 6),
            title = title,
            message = message,
            type = type
        )
        _notifications.value = listOf(notif) + _notifications.value
    }
}
