package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.AgriService
import com.example.data.model.Booking
import com.example.data.model.Customer
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    preselectedServiceId: String? = null,
    isSubmitting: Boolean = false,
    externalErrorMessage: String? = null,
    onSubmitBooking: (
        name: String,
        fatherName: String,
        mobile: String,
        village: String,
        address: String,
        serviceId: String,
        area: String,
        unit: String,
        date: String,
        time: String,
        notes: String
    ) -> Unit
) {
    val context = LocalContext.current
    val services by AgriRepository.services.collectAsState()
    val phoneToDial by AgriRepository.businessPhone.collectAsState()
    val businessWhatsApp by AgriRepository.businessWhatsApp.collectAsState()
    val allCustomers by AgriRepository.customers.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var selectedServiceId by remember {
        mutableStateOf(preselectedServiceId ?: services.first().id)
    }
    var landArea by remember { mutableStateOf("") }
    var selectedUnit by remember { mutableStateOf("बीघा") }
    var bookingDate by remember { mutableStateOf("कल") }
    var preferredTime by remember { mutableStateOf("सुबह (07:00 AM - 10:00 AM)") }
    var additionalNotes by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }

    // Consent dialog for autofill suggestion
    var candidateCustomerForAutofill by remember { mutableStateOf<Customer?>(null) }

    // WhatsApp Dispatch confirmation dialog
    var pendingPreparedBookingForWhatsApp by remember { mutableStateOf<Booking?>(null) }

    val landUnits = listOf("बीघा", "एकड़", "घंटा", "क्विंटल", "ट्रॉली")
    val quickDates = listOf("आज", "कल", "परसों", "3 दिन बाद")
    val quickTimes = listOf(
        "सुबह (06:00 AM - 09:00 AM)",
        "सुबह (09:00 AM - 12:00 PM)",
        "दोपहर (12:00 PM - 03:00 PM)",
        "शाम (03:00 PM - 07:00 PM)"
    )

    val currentService = services.find { it.id == selectedServiceId } ?: services.first()

    // Matching suggestions while typing (Search by name or mobile)
    val customerSuggestions = remember(customerName, mobileNumber, allCustomers) {
        val query = if (customerName.length >= 2) customerName.trim().lowercase()
        else if (mobileNumber.length >= 3) mobileNumber.trim()
        else ""

        if (query.isBlank()) emptyList()
        else {
            allCustomers.filter {
                (it.name.lowercase().contains(query) || it.mobileNumber.contains(query)) &&
                it.name != customerName
            }.take(3)
        }
    }

    // Consent Dialog for Autofill
    candidateCustomerForAutofill?.let { candidate ->
        AlertDialog(
            onDismissRequest = { candidateCustomerForAutofill = null },
            title = {
                Text(
                    text = "स्वतः विवरण भरें (Autofill Consent)?",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Text(
                    text = "क्या आप पूर्व-पंजीकृत विवरण:\n\n" +
                            "• नाम: ${candidate.name}\n" +
                            (if (candidate.fatherName.isNotBlank()) "• पिता: ${candidate.fatherName}\n" else "") +
                            "• मोबाइल: ${candidate.mobileNumber}\n" +
                            "• गांव: ${candidate.village}\n" +
                            "• पता: ${candidate.fullAddress}\n\n" +
                            "को फॉर्म में स्वतः भरना चाहते हैं?",
                    fontSize = 12.sp,
                    color = Color(0xFF374151),
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        customerName = candidate.name
                        fatherName = candidate.fatherName
                        mobileNumber = candidate.mobileNumber
                        village = candidate.village
                        fullAddress = candidate.fullAddress
                        candidateCustomerForAutofill = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("हां, विवरण भरें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { candidateCustomerForAutofill = null }) {
                    Text("नहीं")
                }
            }
        )
    }

    // WhatsApp Dispatch Confirmation Dialog (V3.0 & V4.0 Compliance)
    pendingPreparedBookingForWhatsApp?.let { booking ->
        AlertDialog(
            onDismissRequest = { pendingPreparedBookingForWhatsApp = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "व्हाट्सएप पर बुकिंग भेजें",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AgriGreenContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "बुकिंग आईडी: ${booking.bookingId}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(
                        text = "आपकी बुकिंग जानकारी तैयार कर ली गई है। अब आधिकारिक व्हाट्सएप खुलेगा।",
                        fontSize = 12.sp,
                        color = Color(0xFF374151)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ महत्वपूर्ण: व्हाट्सएप खुलने पर आपको स्वयं 'Send' बटन दबाना होगा। संदेश भेजने के बाद ही बुकिंग मालिक (+91 $businessWhatsApp) तक पहुंचेगी।",
                            fontSize = 11.sp,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(8.dp),
                            lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val whatsAppMsg = ContactUtils.createWhatsAppBookingMessage(booking)
                        val opened = ContactUtils.openWhatsApp(context, businessWhatsApp, whatsAppMsg)
                        pendingPreparedBookingForWhatsApp = null
                        // Finalize submission
                        onSubmitBooking(
                            customerName,
                            fatherName,
                            mobileNumber,
                            village,
                            fullAddress,
                            selectedServiceId,
                            landArea,
                            selectedUnit,
                            bookingDate,
                            preferredTime,
                            additionalNotes
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("व्हाट्सएप खोलें (Open WhatsApp)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { pendingPreparedBookingForWhatsApp = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("रद्द करें (Cancel)")
                }
            }
        )
    }

    // Review Dialog before preparation
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showReviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = AgriGreenPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "बुकिंग विवरण की पुष्टि करें",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "कृपया अपनी भरी हुई जानकारी ध्यानपूर्वक जांच लें:",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Divider(color = Color(0xFFEEEEEE))
                    ReviewDetailRow(label = "किसान का नाम:", value = customerName)
                    if (fatherName.isNotBlank()) {
                        ReviewDetailRow(label = "पिता का नाम:", value = fatherName)
                    }
                    ReviewDetailRow(label = "मोबाइल नंबर:", value = mobileNumber)
                    ReviewDetailRow(label = "गांव व पता:", value = "$village, $fullAddress")
                    ReviewDetailRow(label = "चयनित सेवा:", value = currentService.nameHindi)
                    ReviewDetailRow(label = "मात्रा / क्षेत्र:", value = "$landArea $selectedUnit")
                    ReviewDetailRow(label = "तारीख व समय:", value = "$bookingDate ($preferredTime)")
                    if (additionalNotes.isNotBlank()) {
                        ReviewDetailRow(label = "टिप्पणी:", value = additionalNotes)
                    }
                    ReviewDetailRow(label = "अनुमानित दर:", value = currentService.priceRateHindi, highlight = true)

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "* पुष्टि करने पर व्हाट्सएप संदेश तैयार किया जाएगा।",
                        fontSize = 11.sp,
                        color = Color(0xFF757575)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReviewDialog = false
                        // Generate temporary reference for WhatsApp message
                        val tempBooking = Booking(
                            bookingId = "PT-${(1000..9999).random()}",
                            customerName = customerName,
                            fatherName = fatherName,
                            mobileNumber = mobileNumber,
                            village = village,
                            fullAddress = fullAddress,
                            serviceId = selectedServiceId,
                            serviceNameHindi = currentService.nameHindi,
                            landArea = landArea,
                            landUnit = selectedUnit,
                            bookingDate = bookingDate,
                            preferredTime = preferredTime,
                            additionalNotes = additionalNotes
                        )
                        pendingPreparedBookingForWhatsApp = tempBooking
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("आगे बढ़ें (Proceed to WhatsApp)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showReviewDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("बदलाव करें")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("booking_form_screen"),
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
                        Icon(Icons.Default.Agriculture, contentDescription = null, tint = Color(0xFFFFD54F))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "व्हाट्सएप-कनेक्टेड बुकिंग फॉर्म (V5.0)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "फॉर्म भरकर सबमिट करने पर विवरण सीधे मालिक (+91 $businessWhatsApp) के व्हाट्सएप पर जाएगा।",
                        fontSize = 12.sp,
                        color = Color(0xFFC8E6C9),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 1. Service Selection Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. मशीन / यंत्र चुनें (Select Machine) *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    services.forEach { s ->
                        val isSelected = selectedServiceId == s.id
                        Surface(
                            onClick = { selectedServiceId = s.id },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AgriGreenContainer else Color(0xFFF9FAF9),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, AgriGreenPrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("select_service_${s.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = s.nameHindi,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AgriGreenDark else Color(0xFF1F2937)
                                    )
                                    Text(
                                        text = "${s.nameEnglish} • ${s.priceRateHindi}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedServiceId = s.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = AgriGreenPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Farmer Details with Smart Autocomplete Suggestions
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. किसान का विवरण (Farmer Details) *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    // Smart Autocomplete Suggestions Chips
                    if (customerSuggestions.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AgriGreenContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "क्या आप इनमें से हैं? (स्वतः भरने हेतु टैप करें):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    customerSuggestions.forEach { suggestion ->
                                        SuggestionChip(
                                            onClick = { candidateCustomerForAutofill = suggestion },
                                            label = {
                                                Text("${suggestion.name} (${suggestion.village})", fontSize = 11.sp)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Name
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it; validationError = null },
                        label = { Text("किसान का पूरा नाम *") },
                        placeholder = { Text("उदा. रामप्रसाद यादव") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = AgriGreenPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_customer_name"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Father's Name
                    OutlinedTextField(
                        value = fatherName,
                        onValueChange = { fatherName = it },
                        label = { Text("पिता का नाम (Father's Name)") },
                        placeholder = { Text("उदा. स्व. रामसेवक यादव") },
                        leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null, tint = AgriGreenPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Mobile Number
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = {
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                mobileNumber = it
                                validationError = null
                            }
                        },
                        label = { Text("मोबाइल नंबर (10 अंक) *") },
                        placeholder = { Text("उदा. 7388403923") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = AgriGreenPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_mobile_number"),
                        shape = RoundedCornerShape(10.dp),
                        supportingText = {
                            Text(
                                text = if (mobileNumber.isNotEmpty() && mobileNumber.length < 10)
                                    "10 में से ${mobileNumber.length} अंक दर्ज" else "सत्यापन और ड्राइवर संपर्क हेतु आवश्यक",
                                fontSize = 11.sp,
                                color = if (mobileNumber.isNotEmpty() && mobileNumber.length < 10) Color(0xFFD32F2F) else Color.Gray
                            )
                        }
                    )

                    // Village
                    OutlinedTextField(
                        value = village,
                        onValueChange = { village = it; validationError = null },
                        label = { Text("गांव का नाम (Village Name) *") },
                        placeholder = { Text("उदा. कल्याणपुर / रामपुर") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenPrimary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_village"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Full Address
                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it; validationError = null },
                        label = { Text("खेत का पूरा पता व लैंडमार्क *") },
                        placeholder = { Text("उदा. नहर पुलिया के पास, प्राथमिक विद्यालय के पीछे") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = AgriGreenPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("input_full_address"),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2
                    )
                }
            }
        }

        // 3. Work Details (Area & Unit, Date, Time, Notes)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Terrain, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. कार्य व समय निर्धारण (Work & Timing) *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    // Land Area & Unit
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = landArea,
                                onValueChange = { landArea = it; validationError = null },
                                label = { Text("क्षेत्रफल / मात्रा *") },
                                placeholder = { Text("उदा. 4") },
                                leadingIcon = { Icon(Icons.Default.SquareFoot, contentDescription = null, tint = AgriGreenPrimary) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f).testTag("input_land_area"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AgriGreenContainer,
                                modifier = Modifier.height(56.dp)
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedUnit,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriGreenDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            landUnits.forEach { unit ->
                                FilterChip(
                                    selected = selectedUnit == unit,
                                    onClick = { selectedUnit = unit },
                                    label = { Text(unit, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Booking Date
                    Column {
                        OutlinedTextField(
                            value = bookingDate,
                            onValueChange = { bookingDate = it },
                            label = { Text("बुकिंग की तारीख (Date) *") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AgriGreenPrimary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_booking_date"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickDates.forEach { qDate ->
                                FilterChip(
                                    selected = bookingDate == qDate,
                                    onClick = { bookingDate = qDate },
                                    label = { Text(qDate, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Preferred Time
                    Column {
                        OutlinedTextField(
                            value = preferredTime,
                            onValueChange = { preferredTime = it },
                            label = { Text("पसंदीदा समय (Preferred Time Slot) *") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = AgriGreenPrimary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_preferred_time"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        quickTimes.take(2).forEach { qTime ->
                            FilterChip(
                                selected = preferredTime == qTime,
                                onClick = { preferredTime = qTime },
                                label = { Text(qTime, fontSize = 11.sp) },
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    // Additional Notes
                    OutlinedTextField(
                        value = additionalNotes,
                        onValueChange = { additionalNotes = it },
                        label = { Text("अतिरिक्त निर्देश (Additional Instructions)") },
                        placeholder = { Text("उदा. 2 चक्कर जुताई, खेत सूखा है...") },
                        leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = AgriGreenPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("input_additional_notes"),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                }
            }
        }

        // Error message
        val errorToShow = validationError ?: externalErrorMessage
        if (errorToShow != null) {
            item {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = errorToShow, color = Color(0xFFC62828), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Review & Proceed to WhatsApp Button
        item {
            Button(
                onClick = {
                    when {
                        customerName.isBlank() -> validationError = "कृपया किसान का नाम दर्ज करें।"
                        mobileNumber.length != 10 -> validationError = "कृपया सही 10 अंकों का मोबाइल नंबर दर्ज करें।"
                        village.isBlank() -> validationError = "कृपया अपने गांव का नाम दर्ज करें।"
                        fullAddress.isBlank() -> validationError = "कृपया खेत का पूरा पता या लैंडमार्क दर्ज करें।"
                        landArea.isBlank() -> validationError = "कृपया क्षेत्रफल या कार्य मात्रा दर्ज करें।"
                        else -> {
                            validationError = null
                            showReviewDialog = true
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("review_and_submit_booking_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "जांचें व व्हाट्सएप पर भेजें (Review & WhatsApp)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Direct Assistance Note
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "फॉर्म भरने में कोई समस्या?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "सीधे फोन कॉल करके भी बुक कर सकते हैं।",
                            fontSize = 11.sp,
                            color = Color(0xFF334E39)
                        )
                    }
                    Button(
                        onClick = { ContactUtils.dialPhoneNumber(context, phoneToDial) },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("कॉल करें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewDetailRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.SemiBold,
            color = if (highlight) AgriGreenPrimary else Color(0xFF1F2937),
            modifier = Modifier.weight(0.55f)
        )
    }
}
