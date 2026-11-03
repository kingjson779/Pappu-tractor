package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.ContactUtils

@Composable
fun BookingSuccessScreen(
    booking: Booking,
    onTrackBookingClick: (String) -> Unit,
    onBackToHomeClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("booking_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Success Icon
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(AgriGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = AgriGreenPrimary,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "बुकिंग सुरक्षित रूप से दर्ज हुई!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AgriGreenDark,
            textAlign = TextAlign.Center
        )

        Text(
            text = "आपकी मांग डेटाबेस में सुरक्षित सहेज ली गई है। नीचे दिए गए यूनिक रेफरेंस आईडी द्वारा कभी भी स्टेटस देख सकते हैं।",
            fontSize = 12.sp,
            color = Color(0xFF4A5E4F),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Unique Booking ID Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "आपकी विशिष्ट बुकिंग आईडी (Unique Booking ID):",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    color = AgriGreenContainer,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = booking.bookingId,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AgriGreenPrimary,
                        letterSpacing = 2.sp,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .testTag("success_booking_id_text")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(booking.bookingId))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "कॉपी करें",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "आईडी कॉपी करें",
                        fontSize = 12.sp,
                        color = AgriGreenPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "बुकिंग विवरण सारांश:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )

                Divider(color = Color(0xFFEEEEEE))

                SummaryRow(label = "किसान का नाम", value = booking.customerName)
                SummaryRow(label = "मोबाइल नंबर", value = booking.mobileNumber)
                SummaryRow(label = "गांव व पता", value = "${booking.village}, ${booking.fullAddress}")
                SummaryRow(label = "मशीन", value = booking.serviceNameHindi)
                SummaryRow(label = "कार्य मात्रा", value = "${booking.landArea} ${booking.landUnit}")
                SummaryRow(label = "तारीख व समय", value = "${booking.bookingDate} (${booking.preferredTime})")
                SummaryRow(label = "वर्तमान स्थिति", value = booking.status.titleHindi, highlight = true)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WhatsApp Share Button with Clear Disclaimer
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F2)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Button(
                    onClick = {
                        val summaryText = ContactUtils.createWhatsAppBookingSummary(booking)
                        ContactUtils.openWhatsApp(context, whatsAppNumber, summaryText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("whatsapp_summary_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "व्हाट्सएप पर बुकिंग सारांश साझा करें",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Important Technical Compliance Notice
                Text(
                    text = "⚠️ आवश्यक सूचना: व्हाट्सएप पर सारांश भेजने से त्वरित बातचीत शुरू होती है। बुकिंग की आधिकारिक पुष्टि ऐप में ऊपर दिए गए 'बुकिंग आईडी' के अनुसार ट्रैक होगी।",
                    fontSize = 10.sp,
                    color = Color(0xFF4A5E4F),
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Track Booking Primary Button
        Button(
            onClick = { onTrackBookingClick(booking.bookingId) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("track_booking_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "बुकिंग स्थिति ट्रैक करें (Track Booking)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackToHomeClick,
            modifier = Modifier.testTag("back_to_home_button")
        ) {
            Text(
                text = "मुख्य पृष्ठ (होम) पर लौटें",
                color = AgriGreenDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) AgriGreenPrimary else Color(0xFF1F2937)
        )
    }
}
