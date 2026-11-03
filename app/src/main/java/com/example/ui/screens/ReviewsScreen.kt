package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerReview
import com.example.data.model.ReviewModerationStatus
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    onBackClick: () -> Unit,
    onSubmitReview: (name: String, village: String, rating: Int, serviceId: String, comment: String) -> Unit
) {
    val reviews by AgriRepository.reviews.collectAsState()
    val services by AgriRepository.services.collectAsState()

    var showAddReviewDialog by remember { mutableStateOf(false) }
    var reviewerName by remember { mutableStateOf("") }
    var reviewerVillage by remember { mutableStateOf("") }
    var reviewRating by remember { mutableStateOf(5) }
    var selectedServiceId by remember { mutableStateOf(services.firstOrNull()?.id ?: "") }
    var reviewComment by remember { mutableStateOf("") }
    var reviewFormError by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    val averageRating = remember(reviews) {
        if (reviews.isEmpty()) 5.0
        else reviews.map { it.rating }.average()
    }

    // Add Review Modal Dialog
    if (showAddReviewDialog) {
        AlertDialog(
            onDismissRequest = { showAddReviewDialog = false },
            title = {
                Text(
                    text = "अपनी समीक्षा लिखें (Write a Review)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Star Rating Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            IconButton(onClick = { reviewRating = star }) {
                                Icon(
                                    imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$star stars",
                                    tint = if (star <= reviewRating) Color(0xFFFFB300) else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = {
                            reviewerName = it
                            reviewFormError = null
                        },
                        label = { Text("आपका नाम *") },
                        placeholder = { Text("उदा. रामनरेश") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = reviewerVillage,
                        onValueChange = {
                            reviewerVillage = it
                            reviewFormError = null
                        },
                        label = { Text("गांव का नाम *") },
                        placeholder = { Text("उदा. बहादुरपुर") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = {
                            reviewComment = it
                            reviewFormError = null
                        },
                        label = { Text("आपका अनुभव व प्रतिक्रिया *") },
                        placeholder = { Text("मशीन का काम, चालक का व्यवहार व समय पर सेवा कैसी लगी...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 3
                    )

                    if (reviewFormError != null) {
                        Text(
                            text = reviewFormError ?: "",
                            color = Color(0xFFD32F2F),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "ℹ️ भविष्य में समीक्षा मॉडरेशन नीति के तहत प्रकाशित होगी।",
                        fontSize = 10.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            reviewerName.isBlank() -> reviewFormError = "कृपया अपना नाम लिखें।"
                            reviewerVillage.isBlank() -> reviewFormError = "कृपया गांव का नाम लिखें।"
                            reviewComment.isBlank() -> reviewFormError = "कृपया अपनी समीक्षा लिखें।"
                            else -> {
                                onSubmitReview(
                                    reviewerName,
                                    reviewerVillage,
                                    reviewRating,
                                    selectedServiceId,
                                    reviewComment
                                )
                                showAddReviewDialog = false
                                showSuccessMessage = true
                                reviewerName = ""
                                reviewerVillage = ""
                                reviewComment = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("समीक्षा भेजें", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddReviewDialog = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("reviews_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Text(
                                text = "किसान समीक्षाएं (Reviews)",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { showAddReviewDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGoldHarvest),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("समीक्षा लिखें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Average Rating Summary
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.1f", averageRating),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD54F)
                                )
                                Row {
                                    repeat(5) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Divider(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.3f)
                            )

                            Column {
                                Text(
                                    text = "कुल ${reviews.size} किसान समीक्षाएं",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "100% सत्यापित ग्रामीण अन्नदाता",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC8E6C9)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showSuccessMessage) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AgriGreenContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "आपकी समीक्षा प्राप्त हो गई है! धन्यवाद।",
                            color = AgriGreenDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Reviews List
        items(reviews) { review ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${review.customerName} (${review.village})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Text(
                                text = "यंत्र: ${review.serviceNameHindi}",
                                fontSize = 11.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        // Star badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFF8E1)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${review.rating}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF57F17)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF57F17),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = review.commentHindi,
                        fontSize = 12.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = review.moderationStatus.titleHindi,
                                fontSize = 10.sp,
                                color = AgriGreenPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = review.date,
                            fontSize = 10.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }
        }
    }
}
