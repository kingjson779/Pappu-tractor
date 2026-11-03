package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyurvedicOrder
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

@Composable
fun AyurvedicOrderSuccessScreen(
    order: AyurvedicOrder,
    onBackToStoreClick: () -> Unit,
    onBackToHomeClick: () -> Unit
) {
    val context = LocalContext.current
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()
    val shopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val shopHours by AgriRepository.ayurvedicShopHours.collectAsState()
    val businessPhone by AgriRepository.businessPhone.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .padding(16.dp)
            .testTag("ayurvedic_order_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))

            // Success Circle Icon
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "दवा ऑर्डर सफलतापूर्वक दर्ज हुआ!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center
            )

            Text(
                text = "कृपया WhatsApp पर संदेश भेजकर दुकान से पुष्टि अवश्य प्राप्त करें।",
                fontSize = 13.sp,
                color = Color(0xFF4A5E4F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Order Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ऑर्डर संदर्भ संख्या:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = order.orderId,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))

                    // Customer Details
                    Text("ग्राहक का नाम: ${order.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("मोबाइल: ${order.mobileNumber}", fontSize = 12.sp, color = Color.DarkGray)
                    Text("गांव: ${order.village}", fontSize = 12.sp, color = Color.DarkGray)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                    // Ordered items
                    Text("चयनित औषधियां:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B381E))
                    Spacer(modifier = Modifier.height(4.dp))
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.productNameHindi} (${item.packSize}) x ${item.quantity}",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "₹${item.totalPrice.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("कुल राशि:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = order.totalAmountFormatted,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = AyurvedaAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF9C4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "दुकान से संग्रह (Shop Pickup Only) • दुकान पर ही भुगतान",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBF360C)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Action: Resend or Open WhatsApp
            Button(
                onClick = {
                    val orderMsg = ContactUtils.createWhatsAppAyurvedicOrderMessage(order)
                    ContactUtils.openWhatsApp(context, whatsAppNumber, orderMsg)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("resend_whatsapp_order_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("WhatsApp पर विवरण पुनः भेजें", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { ContactUtils.dialPhoneNumber(context, businessPhone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("दुकान पर कॉल करें ($businessPhone)", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onBackToStoreClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("अन्य दवाएं देखें (आयुर्वेदिक स्टोर)", fontSize = 13.sp, color = AyurvedaHerbGreen)
            }

            TextButton(
                onClick = onBackToHomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("मुख्य पृष्ठ पर लौटें (Home)", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}
