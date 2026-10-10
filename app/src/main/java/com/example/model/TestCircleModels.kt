package com.example.model

/**
 * Status of an app submitted for community testing.
 */
enum class AppStatus {
    PENDING_APPROVAL,
    APPROVED,
    ACTIVE_CAMPAIGN,
    COMPLETED,
    REJECTED
}

/**
 * Type of campaign / submission
 */
enum class TestingTier {
    FREE,
    PRO
}

/**
 * Manual UPI payment status
 */
enum class PaymentStatus {
    NOT_APPLICABLE, // For Free testing
    PAYMENT_PENDING_SUBMISSION,
    PAYMENT_PENDING_VERIFICATION,
    PAYMENT_APPROVED,
    PAYMENT_REJECTED
}

/**
 * Tester registration status
 */
enum class TesterStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}

/**
 * Daily testing feedback status
 */
enum class ReportStatus {
    PENDING_REVIEW,
    APPROVED,
    REJECTED
}

/**
 * App testing request (Free or Pro)
 */
data class TestingApp(
    val id: String,
    val tier: TestingTier,
    val appName: String,
    val appLogoUrl: String = "",
    val packageName: String,
    val testingLink: String,
    val minAndroidVersion: String = "Android 8.0 (API 26)+",
    val testingInstructions: String,
    val requiredTesterCount: Int = 13,
    val currentTesterCount: Int = 0,
    val status: AppStatus = AppStatus.PENDING_APPROVAL,
    val paymentStatus: PaymentStatus = if (tier == TestingTier.PRO) PaymentStatus.PAYMENT_PENDING_SUBMISSION else PaymentStatus.NOT_APPLICABLE,
    val upiPayerName: String? = null,
    val upiUtrNumber: String? = null,
    val paymentAmount: Double = if (tier == TestingTier.PRO) 699.0 else 0.0,
    val paymentScreenshotUrl: String? = null,
    val adminApprovalToken: String? = null,
    val verifiedByAdminEmail: String? = null,
    val verifiedAtTimestamp: Long? = null,
    val assignedTesterIds: List<String> = emptyList(),
    val googleGroupUrl: String = "",
    val campaignDay: Int = 0, // 0 to 15
    val createdByUserId: String,
    val createdByUserEmail: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Tester Registration profile
 */
data class TesterProfile(
    val id: String, // Matches user ID
    val fullName: String,
    val gmailAddress: String,
    val cityState: String,
    val androidVersion: String,
    val deviceModel: String,
    val availableFor15Days: Boolean = true,
    val agreedToRules: Boolean = true,
    val status: TesterStatus = TesterStatus.PENDING,
    val assignedCampaignIds: List<String> = emptyList(),
    val completedCampaignsCount: Int = 0,
    val totalVerifiedXp: Int = 0,
    val appliedAt: Long = System.currentTimeMillis(),
    val reviewedByAdminEmail: String? = null
)

/**
 * 15-Day Daily Testing Report
 */
data class DailyReport(
    val id: String,
    val campaignId: String,
    val appName: String,
    val testerId: String,
    val testerEmail: String,
    val testerName: String,
    val dateIso: String, // e.g. "2026-10-10"
    val campaignDayNumber: Int, // 1 to 15
    val checklistCompleted: Boolean,
    val feedbackText: String,
    val bugReportText: String = "",
    val screenshotUrl: String = "",
    val status: ReportStatus = ReportStatus.PENDING_REVIEW,
    val xpAwarded: Boolean = false,
    val adminNotes: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewedByAdminEmail: String? = null
)

/**
 * Immutable auditable XP transaction ledger
 */
data class XpTransaction(
    val id: String,
    val testerId: String,
    val testerEmail: String,
    val campaignId: String,
    val reportId: String,
    val dayNumber: Int,
    val xpAmount: Int = 1,
    val reason: String,
    val awardedAt: Long = System.currentTimeMillis(),
    val verifiedBy: String // Admin email or verification system
)

/**
 * User account representation
 */
data class UserAccount(
    val uid: String,
    val email: String,
    val displayName: String,
    val isAdmin: Boolean = false,
    val isTesterRegistered: Boolean = false,
    val testerStatus: TesterStatus? = null,
    val xpBalance: Int = 0
)

/**
 * In-app Notification
 */
data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "INFO", // "PAYMENT", "CAMPAIGN", "XP", "TESTER"
    val read: Boolean = false
)
