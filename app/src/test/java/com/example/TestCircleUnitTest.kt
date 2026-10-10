package com.example

import com.example.data.TestCircleRepository
import com.example.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class TestCircleUnitTest {

    private lateinit var repository: TestCircleRepository

    @Before
    fun setUp() {
        repository = TestCircleRepository()
    }

    @Test
    fun testLoginAndAdminRoleDetection() {
        // Normal user login
        repository.loginWithGoogle("user@gmail.com", "John Doe")
        assertNotNull(repository.currentUser.value)
        assertEquals("user@gmail.com", repository.currentUser.value?.email)
        assertFalse(repository.currentUser.value?.isAdmin == true)

        // Authorized admin login
        repository.loginWithGoogle(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, "Jitu Pal")
        assertTrue(repository.currentUser.value?.isAdmin == true)
    }

    @Test
    fun testFreeAndProAppSeparation() {
        repository.loginWithGoogle("tester@test.com", "Tester")
        val freeRes = repository.submitFreeApp(
            appName = "Free Calc",
            packageName = "com.free.calc",
            testingLink = "https://play.google.com/test",
            minAndroidVersion = "Android 9",
            testingInstructions = "Test math equations",
            appLogoUrl = ""
        )
        assertTrue(freeRes.isSuccess)
        assertEquals(TestingTier.FREE, freeRes.getOrNull()?.tier)
        assertEquals(PaymentStatus.NOT_APPLICABLE, freeRes.getOrNull()?.paymentStatus)

        val proRes = repository.submitProApp(
            appName = "Pro Vault",
            packageName = "com.pro.vault",
            testingLink = "https://play.google.com/test",
            minAndroidVersion = "Android 10",
            testingInstructions = "Test pin and biometric lock",
            appLogoUrl = ""
        )
        assertTrue(proRes.isSuccess)
        assertEquals(TestingTier.PRO, proRes.getOrNull()?.tier)
        assertEquals(PaymentStatus.PAYMENT_PENDING_SUBMISSION, proRes.getOrNull()?.paymentStatus)
        assertEquals(699.0, proRes.getOrNull()?.paymentAmount ?: 0.0, 0.01)
    }

    @Test
    fun testManualUpiWorkflowAndAdminTokenApproval() {
        repository.loginWithGoogle("dev@pro.com", "Dev")
        val proApp = repository.submitProApp(
            appName = "CryptoWatch",
            packageName = "com.crypto.watch",
            testingLink = "https://play.google.com/cryptowatch",
            minAndroidVersion = "Android 12",
            testingInstructions = "Test price notifications",
            appLogoUrl = ""
        ).getOrThrow()

        // Submit UTR
        val submitUtrRes = repository.submitUpiPaymentDetails(
            appId = proApp.id,
            payerName = "Dev Patel",
            utrNumber = "UTR998877665544",
            screenshotReference = "payment_receipt.jpg"
        )
        assertTrue(submitUtrRes.isSuccess)

        // Non-admin cannot verify payment
        val nonAdminVerify = repository.adminVerifyPayment(proApp.id, true, "I approved myself")
        assertTrue(nonAdminVerify.isFailure)

        // Switch to Admin
        repository.loginWithGoogle(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, "Jitu Pal (Admin)")
        val adminVerify = repository.adminVerifyPayment(proApp.id, true, "Verified in Axis statement")
        assertTrue(adminVerify.isSuccess)
        val token = adminVerify.getOrThrow()
        assertTrue(token.startsWith("TC-PRO-"))

        val verifiedApp = repository.appsList.value.find { it.id == proApp.id }
        assertEquals(PaymentStatus.PAYMENT_APPROVED, verifiedApp?.paymentStatus)
        assertEquals(token, verifiedApp?.adminApprovalToken)
    }

    @Test
    fun testAntiFraudDuplicateXpPrevention() {
        // Switch to Admin
        repository.loginWithGoogle(TestCircleRepository.AUTHORIZED_ADMIN_EMAIL, "Jitu Pal (Admin)")

        // Check report rep_01 from seed
        val report = repository.dailyReports.value.find { it.id == "rep_01" }
        assertNotNull(report)

        // Attempting to approve rep_01 again should trigger anti-fraud prevent duplicate
        val secondApproval = repository.adminReviewDailyReport("rep_01", true, "Second approval try")
        assertTrue(secondApproval.isFailure)
        assertTrue(secondApproval.exceptionOrNull()?.message?.contains("Anti-fraud") == true)
    }
}
