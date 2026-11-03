package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgriService
import com.example.data.repository.AgriRepository
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    onBackClick: () -> Unit,
    onBookNowClick: (AgriService) -> Unit
) {
    val services by AgriRepository.services.collectAsState()
    val service = services.find { it.id == serviceId } ?: services.first()

    // Gallery active image
    var activeImageRes by remember(service.id) { mutableStateOf(service.imageRes) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("service_detail_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Main Active Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = activeImageRes),
                    contentDescription = service.nameEnglish,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Back Button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "पीछे",
                        tint = Color.White
                    )
                }

                // Horsepower Badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.75f)
                ) {
                    Text(
                        text = service.horsepower,
                        color = Color(0xFFFFD54F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Image Gallery Thumbnails Row
        if (service.galleryImages.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "मशीन फोटो गैलरी (Photo Gallery):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        service.galleryImages.forEachIndexed { index, imgRes ->
                            val isSelected = activeImageRes == imgRes
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 65.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { activeImageRes = imgRes }
                                    .background(Color.White)
                                    .then(
                                        if (isSelected) Modifier.padding(2.dp)
                                            .background(AgriGreenPrimary, RoundedCornerShape(8.dp))
                                        else Modifier
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = imgRes),
                                    contentDescription = "Gallery item $index",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(6.dp))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Title and Availability
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.nameHindi,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Text(
                                text = service.nameEnglish,
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (service.isAvailable) AgriGreenContainer else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = if (service.isAvailable) "उपलब्ध (Ready)" else "वर्तमान में व्यस्त",
                                color = if (service.isAvailable) AgriGreenPrimary else Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Transparent Price Breakdown
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AgriGreenContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "किराया दर (Rental Price):",
                                    fontSize = 12.sp,
                                    color = AgriGreenDark,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = service.priceUnit,
                                    fontSize = 11.sp,
                                    color = Color(0xFF334E39)
                                )
                            }
                            Text(
                                text = service.priceRateHindi,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = AgriGreenPrimary
                            )
                        }
                    }
                }
            }
        }

        // Service Description
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "विस्तृत सेवा विवरण (Service Description):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = service.fullDescHindi,
                        fontSize = 13.sp,
                        color = Color(0xFF374151),
                        lineHeight = 21.sp
                    )
                }
            }
        }

        // Work Details & Technical Specifications
        if (service.workDetails.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "कार्य विवरण व तकनीकी क्षमता (Work Details):",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )

                        service.workDetails.forEach { (specTitle, specValue) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = specTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4B5563),
                                    modifier = Modifier.weight(0.45f)
                                )
                                Text(
                                    text = specValue,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1F2937),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(0.55f)
                                )
                            }
                            Divider(color = Color(0xFFF3F4F6))
                        }
                    }
                }
            }
        }

        // Suitable For Crops / Activities
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "उपयुक्त कार्य / फसलें (Suitable Crops):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        service.suitableFor.forEach { item ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = "✓ $item",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1F2937),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons: Book Now & WhatsApp Contact
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onBookNowClick(service) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("detail_screen_book_now_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "यह मशीन अभी बुक करें (Book Now)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                WhatsAppCallActionRow(
                    customMessage = "नमस्ते पप्पू ट्रैक्टर, मुझे ${service.nameHindi} (${service.nameEnglish}) के किराए व बुकिंग के संबंध में जानकारी चाहिए।"
                )
            }
        }
    }
}
