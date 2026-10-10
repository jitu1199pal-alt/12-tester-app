package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.TestCircleRepository
import com.example.model.PaymentStatus
import com.example.ui.components.StatusBadge
import com.example.ui.components.TestCircleTopAppBar
import com.example.ui.theme.*
import com.example.viewmodel.Screen
import com.example.viewmodel.TestCircleViewModel

@Composable
fun PaymentVerificationScreen(viewModel: TestCircleViewModel) {
    val context = LocalContext.current
    val selectedApp by viewModel.selectedApp.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var payerName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var utrNumber by remember { mutableStateOf("") }
    var screenshotNote by remember { mutableStateOf("") }
    var adminVerificationNotes by remember { mutableStateOf("Verified in Axis UPI merchant statement.") }

    val upiId = TestCircleRepository.OFFICIAL_UPI_ID
    val amount = TestCircleRepository.PRO_TESTING_PRICE_INR

    Scaffold(
        topBar = {
            TestCircleTopAppBar(
                title = "Pro Payment – ₹699",
                onBack = { viewModel.navigateBack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Pro Testing Request Details",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedApp?.appName ?: "Selected App",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedApp?.packageName ?: "com.app.package",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount Payable: ₹699.00",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        selectedApp?.let {
                            StatusBadge(
                                text = it.paymentStatus.name,
                                color = when (it.paymentStatus) {
                                    PaymentStatus.PAYMENT_APPROVED -> EmeraldGreen
                                    PaymentStatus.PAYMENT_PENDING_VERIFICATION -> AmberWarning
                                    PaymentStatus.PAYMENT_REJECTED -> RoseError
                                    else -> Slate400
                                }
                            )
                        }
                    }
                }
            }

            // UPI QR CODE DISPLAY & INSTRUCTIONS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Official TestCircle UPI Payment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Scan with GPay, PhonePe, Paytm or any UPI app",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clean QR Code Graphic Box
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(2.dp, BrandBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                            .testTag("upi_qr_code_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "UPI QR Code",
                                tint = Slate900,
                                modifier = Modifier.size(90.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "₹699 INR",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = upiId,
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Slate800
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // UPI ID Copy Row
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("UPI ID", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(upiId, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                                    Toast.makeText(context, "UPI ID copied to clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy UPI ID")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Launch UPI App Button
                    OutlinedButton(
                        onClick = {
                            val upiUri = Uri.parse("upi://pay?pa=$upiId&pn=${Uri.encode("TestCircle Official")}&am=699.00&cu=INR&tn=${Uri.encode("TestCircle Pro Testing")}")
                            val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
                            try {
                                context.startActivity(Intent.createChooser(upiIntent, "Pay with UPI"))
                            } catch (_: Exception) {
                                Toast.makeText(context, "No UPI app found. Please copy the UPI ID above.", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Installed UPI App")
                    }
                }
            }

            // USER SUBMISSION FORM (UTR & Payer Name)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Submit Payment Proof",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your 12-digit UPI Reference/UTR number after paying in your UPI app.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = payerName,
                        onValueChange = { payerName = it },
                        label = { Text("Payer Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("payer_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = utrNumber,
                        onValueChange = { utrNumber = it },
                        label = { Text("12-Digit UTR / Transaction Reference *") },
                        singleLine = true,
                        placeholder = { Text("e.g. 482910394821") },
                        modifier = Modifier.fillMaxWidth().testTag("utr_number_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = screenshotNote,
                        onValueChange = { screenshotNote = it },
                        label = { Text("Screenshot filename / reference (Optional)") },
                        singleLine = true,
                        placeholder = { Text("e.g. payment_screenshot_gpay.jpg") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedApp?.let { app ->
                                viewModel.submitUpiPayment(
                                    appId = app.id,
                                    payerName = payerName,
                                    utr = utrNumber,
                                    screenshotRef = screenshotNote
                                )
                            }
                        },
                        enabled = utrNumber.isNotBlank() && selectedApp != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_payment_proof_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit for Verification", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ADMIN APPROVAL ACTION BOX (Shown when current user is authorized admin)
            if (currentUser?.isAdmin == true && selectedApp != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = AmberWarning)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Admin Verification Controls",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Verify the actual receipt in your bank/UPI merchant app before approving. Never approve solely based on UTR string without checking real payment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = adminVerificationNotes,
                            onValueChange = { adminVerificationNotes = it },
                            label = { Text("Admin Verification Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.adminVerifyPayment(selectedApp!!.id, false, adminVerificationNotes)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseError),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reject")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    viewModel.adminVerifyPayment(selectedApp!!.id, true, adminVerificationNotes)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier.weight(1f).testTag("admin_approve_payment_button")
                            ) {
                                Text("Approve & Token")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
