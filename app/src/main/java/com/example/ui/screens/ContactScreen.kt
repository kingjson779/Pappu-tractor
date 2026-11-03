package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AgriRepository
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.*
import com.example.util.ContactUtils

@Composable
fun ContactScreen() {
    val context = LocalContext.current
    val phoneDisplay by AgriRepository.businessPhoneDisplay.collectAsState()
    val phoneRaw by AgriRepository.businessPhone.collectAsState()
    val whatsAppRaw by AgriRepository.businessWhatsApp.collectAsState()
    val addressHindi by AgriRepository.businessAddressHindi.collectAsState()
    val hoursHindi by AgriRepository.workingHoursHindi.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("contact_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "संपर्क केंद्र (Contact Us)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "पप्पू ट्रैक्टर कृषि सेवा केंद्र से सीधे संपर्क करें। किसी भी समय सहायता के लिए हम तैयार हैं।",
                        fontSize = 12.sp,
                        color = Color(0xFFC8E6C9),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Phone Contact Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CallBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = CallBlue,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "फोन द्वारा संपर्क (Phone Contact)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Text(
                                text = "सीधी बातचीत व बुकिंग सहायता",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ContactItemRow(
                        title = "मुख्य हेल्पलाइन नंबर:",
                        detail = phoneDisplay,
                        actionLabel = "कॉल करें",
                        actionColor = CallBlue,
                        onAction = {
                            ContactUtils.dialPhoneNumber(context, phoneRaw)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ContactItemRow(
                        title = "वैकल्पिक नंबर:",
                        detail = "+91 91234 56789",
                        actionLabel = "कॉल करें",
                        actionColor = CallBlue,
                        onAction = {
                            ContactUtils.dialPhoneNumber(context, "+919123456789")
                        }
                    )
                }
            }
        }

        // WhatsApp Contact Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = WhatsAppGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "व्हाट्सएप चैट (WhatsApp Contact)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Text(
                                text = "मशीन की फोटो व दर की तुरंत जानकारी",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    ContactItemRow(
                        title = "आधिकारिक व्हाट्सएप नंबर:",
                        detail = phoneDisplay,
                        actionLabel = "मैसेज करें",
                        actionColor = WhatsAppGreen,
                        onAction = {
                            ContactUtils.openWhatsApp(
                                context,
                                whatsAppRaw,
                                "नमस्ते पप्पू ट्रैक्टर, मुझे कृषि सेवाओं के बारे में जानकारी चाहिए।"
                            )
                        }
                    )
                }
            }
        }

        // Business Address Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AgriGreenPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "कार्यालय व वर्कशॉप का पता (Business Address)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Text(
                                text = "मुख्य गैराज व सेवा केंद्र",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFF9FAF9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = addressHindi,
                                fontSize = 13.sp,
                                color = Color(0xFF1F2937),
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "लैंडमार्क: किसान मंडी मुख्य द्वार के सामने, पेट्रोल पंप के निकट",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            ContactUtils.openGoogleMaps(context, addressHindi)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriGreenPrimary)
                    ) {
                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("नक्शे पर स्थान देखें (Open in Maps)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Working Hours & Service Coverage Area
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = AgriGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "कार्य समय (Working Hours):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    Text(
                        text = hoursHindi,
                        fontSize = 12.sp,
                        color = Color(0xFF2C3E2F)
                    )

                    Divider(color = Color(0xFFC8E6C9))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ShareLocation,
                            contentDescription = null,
                            tint = AgriGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "सेवा क्षेत्र (Service Coverage):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    Text(
                        text = "गाजीपुर, बहादुरपुर, कल्याणपुर, जंगीपुर, सैदपुर, रामपुर व 30 किमी के दायरे में आने वाले सभी गांव।",
                        fontSize = 12.sp,
                        color = Color(0xFF2C3E2F),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactItemRow(
    title: String,
    detail: String,
    actionLabel: String,
    actionColor: Color,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = detail,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        }

        Button(
            onClick = onAction,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = actionColor),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text(text = actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
