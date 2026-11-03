package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Booking
import com.example.data.model.BookingStatus
import com.example.data.repository.AgriRepository
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackBookingScreen(
    initialQuery: String? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allBookings by AgriRepository.bookings.collectAsState()

    var bookingIdInput by remember { mutableStateOf(initialQuery ?: "") }
    var mobileInput by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(initialQuery != null) }
    var trackedBooking by remember {
        mutableStateOf(
            if (!initialQuery.isNullOrBlank()) {
                AgriRepository.findBooking(initialQuery)
            } else {
                allBookings.firstOrNull()
            }
        )
    }

    var showCancelDialog by remember { mutableStateOf(false) }

    fun doTrack() {
        coroutineScope.launch {
            isSearching = true
            hasSearched = true
            delay(300) // Brief smooth loading state
            val query = when {
                bookingIdInput.isNotBlank() -> bookingIdInput.trim()
                mobileInput.isNotBlank() -> mobileInput.trim()
                else -> ""
            }
            trackedBooking = if (query.isNotBlank()) {
                AgriRepository.findBooking(query)
            } else null
            isSearching = false
        }
    }

    // Cancel dialog
    if (showCancelDialog && trackedBooking != null) {
        val b = trackedBooking!!
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "क्या आप बुकिंग रद्द करना चाहते हैं?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "बुकिंग आईडी ${b.bookingId} (${b.serviceNameHindi}) को रद्द कर दिया जाएगा। क्या आप पुष्टि करते हैं?",
                    fontSize = 13.sp,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateBookingStatus(b.bookingId, BookingStatus.CANCELLED, "ग्राहक द्वारा रद्द")
                        trackedBooking = AgriRepository.findBooking(b.bookingId)
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("रद्द करें", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("नहीं")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("track_booking_screen"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "बुकिंग लाइव ट्रैकिंग (Tracking UI V2.0)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "बुकिंग आईडी या पंजीकृत मोबाइल नंबर डालकर मशीन की वर्तमान स्थिति देखें।",
                        fontSize = 12.sp,
                        color = Color(0xFFC8E6C9),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Search Inputs Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "बुकिंग खोजें (Search Booking):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )

                    // Booking ID Input
                    OutlinedTextField(
                        value = bookingIdInput,
                        onValueChange = { bookingIdInput = it },
                        label = { Text("बुकिंग आईडी (Booking ID)") },
                        placeholder = { Text("उदा. PT-8024") },
                        leadingIcon = {
                            Icon(Icons.Default.Numbers, contentDescription = null, tint = AgriGreenPrimary)
                        },
                        trailingIcon = {
                            if (bookingIdInput.isNotEmpty()) {
                                IconButton(onClick = { bookingIdInput = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("track_booking_id_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text(
                        text = "या मोबाइल नंबर द्वारा खोजें:",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    // Mobile Number Input
                    OutlinedTextField(
                        value = mobileInput,
                        onValueChange = {
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                mobileInput = it
                            }
                        },
                        label = { Text("पंजीकृत मोबाइल नंबर (10 अंक)") },
                        placeholder = { Text("उदा. 7388403923") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = AgriGreenPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("track_mobile_number_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Search Button
                    Button(
                        onClick = { doTrack() },
                        enabled = !isSearching,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("track_submit_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("स्थिति खोजी जा रही है...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("स्थिति देखें (Track Status)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Sample ID chips
                    Column(modifier = Modifier.padding(top = 2.dp)) {
                        Text(
                            text = "त्वरित परीक्षण हेतु उदाहरण आईडी (Quick Test IDs):",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            allBookings.take(4).forEach { b ->
                                SuggestionChip(
                                    onClick = {
                                        bookingIdInput = b.bookingId
                                        trackedBooking = b
                                        hasSearched = true
                                    },
                                    label = { Text(b.bookingId, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tracking Status Display
        item {
            val booking = trackedBooking
            if (booking != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_status_display")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Dynamic Status Badge Colors
                        val (badgeBg, badgeFg) = when (booking.status) {
                            BookingStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                            BookingStatus.CONFIRMED -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                            BookingStatus.IN_PROGRESS -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                            BookingStatus.COMPLETED -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
                            BookingStatus.REJECTED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                            BookingStatus.CANCELLED -> Color(0xFFF5F5F5) to Color(0xFF616161)
                        }

                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "बुकिंग आईडी: ${booking.bookingId}",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenDark
                                )
                                Text(
                                    text = "दर्ज तिथि: ${booking.createdAtFormatted}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = badgeBg
                            ) {
                                Text(
                                    text = booking.status.titleHindi,
                                    color = badgeFg,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 12.dp))

                        // Status Description banner
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9FAF9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (booking.status) {
                                        BookingStatus.COMPLETED -> Icons.Default.CheckCircle
                                        BookingStatus.REJECTED -> Icons.Default.Cancel
                                        BookingStatus.CANCELLED -> Icons.Default.Block
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = badgeFg,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = booking.status.descriptionHindi,
                                    fontSize = 12.sp,
                                    color = Color(0xFF374151),
                                    lineHeight = 17.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Multi-step Progress Timeline (if not rejected/cancelled)
                        if (booking.status != BookingStatus.REJECTED && booking.status != BookingStatus.CANCELLED) {
                            Text(
                                text = "प्रगति विवरण (Progress Timeline):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            val steps = listOf(
                                Triple("1. मांग प्राप्त (Pending)", "सत्यापन दल को सूचना भेजी गई", 1),
                                Triple("2. पुष्टि व स्वीकृति (Confirmed)", "मशीन व ऑपरेटर निर्धारित", 2),
                                Triple("3. मशीन रवाना (In Progress)", "खेत पर कार्य प्रगति पर है", 3),
                                Triple("4. कार्य संपन्न (Completed)", "सफलतापूर्वक कार्य पूरा हुआ", 4)
                            )

                            steps.forEachIndexed { index, (stepTitle, stepDesc, stepNum) ->
                                val isCompleted = stepNum <= booking.status.stepIndex
                                val isCurrent = stepNum == booking.status.stepIndex

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isCompleted) AgriGreenPrimary else Color(0xFFE0E0E0)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = "$stepNum",
                                                    color = Color(0xFF757575),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        if (index < steps.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(28.dp)
                                                    .background(
                                                        if (stepNum < booking.status.stepIndex) AgriGreenPrimary else Color(0xFFE0E0E0)
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 4.dp)) {
                                        Text(
                                            text = stepTitle,
                                            fontSize = 13.sp,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCompleted) AgriGreenDark else Color(0xFF757575)
                                        )
                                        Text(
                                            text = stepDesc,
                                            fontSize = 11.sp,
                                            color = if (isCurrent) AgriGreenPrimary else Color(0xFF9E9E9E)
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 12.dp))

                        // Assigned Details
                        Text(
                            text = "बुकिंग व कार्य विवरण:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "• मशीन: ${booking.serviceNameHindi}", fontSize = 12.sp, color = Color(0xFF374151))
                        Text(text = "• किसान: ${booking.customerName} (${booking.mobileNumber})", fontSize = 12.sp, color = Color(0xFF374151))
                        Text(text = "• गांव व पता: ${booking.village}, ${booking.fullAddress}", fontSize = 12.sp, color = Color(0xFF374151))
                        Text(text = "• क्षेत्रफल: ${booking.landArea} ${booking.landUnit}", fontSize = 12.sp, color = Color(0xFF374151))
                        Text(text = "• समय: ${booking.bookingDate} (${booking.preferredTime})", fontSize = 12.sp, color = Color(0xFF374151))
                        Text(text = "• भुगतान स्थिति: ${booking.paymentStatus.titleHindi} (${booking.estimatedAmount})", fontSize = 12.sp, color = Color(0xFF374151))
                        if (booking.adminNotes.isNotBlank()) {
                            Text(text = "• व्यवस्थापक टिप्पणी: ${booking.adminNotes}", fontSize = 12.sp, color = AgriGreenPrimary, fontWeight = FontWeight.SemiBold)
                        }

                        // Allow cancellation if still pending
                        if (booking.status == BookingStatus.PENDING) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("बुकिंग रद्द करने का अनुरोध करें (Cancel Request)", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        WhatsAppCallActionRow(
                            customMessage = "नमस्ते पप्पू ट्रैक्टर, मेरी बुकिंग आईडी ${booking.bookingId} (${booking.serviceNameHindi}) की स्थिति के संबंध में सहायता चाहिए।"
                        )
                    }
                }
            } else if (hasSearched) {
                // Empty state
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "कोई बुकिंग नहीं मिली",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF374151)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "कृपया सही बुकिंग आईडी (उदा. PT-8024) या पंजीकृत 10 अंकों का मोबाइल नंबर जांचें।",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }
        }
    }
}
