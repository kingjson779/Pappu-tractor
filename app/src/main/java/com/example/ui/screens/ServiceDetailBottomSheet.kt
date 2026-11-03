package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgriService
import com.example.data.repository.AgriRepository
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailBottomSheet(
    service: AgriService,
    onDismiss: () -> Unit,
    onBookNowClick: (AgriService) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
                .testTag("service_detail_sheet")
        ) {
            // Machine Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = service.imageRes),
                    contentDescription = service.nameEnglish,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "बंद करें",
                        tint = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Header Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = service.nameHindi,
                            fontSize = 20.sp,
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
                        color = AgriGoldHarvest
                    ) {
                        Text(
                            text = service.horsepower,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Rate Card
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
                                text = "अनुमानित किराया दर:",
                                fontSize = 12.sp,
                                color = AgriGreenDark
                            )
                            Text(
                                text = service.priceUnit,
                                fontSize = 11.sp,
                                color = Color(0xFF4A5E4F)
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

                Spacer(modifier = Modifier.height(16.dp))

                // Full Description
                Text(
                    text = "यंत्र का विवरण:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = service.fullDescHindi,
                    fontSize = 13.sp,
                    color = Color(0xFF374151),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Suitable For Chips
                Text(
                    text = "उपयुक्त कार्य / फसलें:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    service.suitableFor.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = "✓ $tag",
                                fontSize = 12.sp,
                                color = Color(0xFF1F2937),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Highlights
                Text(
                    text = "विशेषताएं (Key Features):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                service.highlights.forEach { highlight ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AgriGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = highlight,
                            fontSize = 13.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = {
                        onBookNowClick(service)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("detail_book_now_button"),
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

                Spacer(modifier = Modifier.height(12.dp))

                WhatsAppCallActionRow(
                    customMessage = "नमस्ते पप्पू ट्रैक्टर! मुझे ${service.nameHindi} (${service.nameEnglish}) बुक करने के संबंध में जानकारी चाहिए।"
                )
            }
        }
    }
}
