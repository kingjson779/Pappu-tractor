package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.data.model.*
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

enum class AdminTab(val titleHindi: String) {
    BOOKINGS("कृषि बुकिंग"),
    AYURVEDIC_ORDERS("दवा ऑर्डर्स (पिकअप)"),
    AYURVEDIC_PRODUCTS("दवा सूची व स्टॉक"),
    AYURVEDIC_CATEGORIES("दवा श्रेणियां"),
    PAYMENTS("भुगतान विवरण"),
    SERVICES("कृषि यंत्र"),
    CUSTOMERS("किसान डायरेक्टरी"),
    WHATSAPP_CONFIG("व्हाट्सएप व सेटिंग्स")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    currentAdmin: AdminAccount? = null,
    onNavigateToCustomerDirectory: () -> Unit = {},
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val bookings by AgriRepository.bookings.collectAsState()
    val services by AgriRepository.services.collectAsState()
    val customers by AgriRepository.customers.collectAsState()
    val ayurvedicProducts by AgriRepository.ayurvedicProducts.collectAsState()
    val ayurvedicOrders by AgriRepository.ayurvedicOrders.collectAsState()
    val ayurvedicCategories by AgriRepository.ayurvedicCategories.collectAsState()
    val currentPhone by AgriRepository.businessPhone.collectAsState()
    val currentWhatsApp by AgriRepository.businessWhatsApp.collectAsState()
    val currentAddress by AgriRepository.businessAddressHindi.collectAsState()
    val currentHours by AgriRepository.workingHoursHindi.collectAsState()
    val adminAccounts by AgriRepository.adminAccounts.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(AdminTab.BOOKINGS) }
    var bookingSearchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("सभी") }

    // Dialogs state
    var selectedBookingForStatusChange by remember { mutableStateOf<Booking?>(null) }
    var newStatusChoice by remember { mutableStateOf(BookingStatus.CONFIRMED) }
    var adminNoteInput by remember { mutableStateOf("") }

    var selectedBookingForPaymentChange by remember { mutableStateOf<Booking?>(null) }
    var newPaymentStatusChoice by remember { mutableStateOf(PaymentStatus.FULLY_PAID) }
    var newPaymentMethodChoice by remember { mutableStateOf(PaymentMethod.CASH) }
    var newPaidAmountInput by remember { mutableStateOf("") }
    var newPaymentDateInput by remember { mutableStateOf("आज") }

    // Add Service dialog state
    var showAddServiceDialog by remember { mutableStateOf(false) }
    var newServiceNameHindi by remember { mutableStateOf("") }
    var newServiceNameEnglish by remember { mutableStateOf("") }
    var newServiceDesc by remember { mutableStateOf("") }
    var newServicePrice by remember { mutableStateOf("") }
    var newServiceUnit by remember { mutableStateOf("प्रति घंटा") }
    var newServiceHp by remember { mutableStateOf("45 HP") }

    // Edit Service Dialog state
    var serviceToEdit by remember { mutableStateOf<AgriService?>(null) }
    var editNameHindi by remember { mutableStateOf("") }
    var editNameEnglish by remember { mutableStateOf("") }
    var editDesc by remember { mutableStateOf("") }
    var editPrice by remember { mutableStateOf("") }
    var editUnit by remember { mutableStateOf("") }
    var editHp by remember { mutableStateOf("") }
    var editAvailable by remember { mutableStateOf(true) }

    // WhatsApp & Business Config Form State
    var editBusinessWhatsApp by remember(currentWhatsApp) { mutableStateOf(currentWhatsApp) }
    var editBusinessPhone by remember(currentPhone) { mutableStateOf(currentPhone) }
    var editBusinessAddress by remember(currentAddress) { mutableStateOf(currentAddress) }
    var editBusinessHours by remember(currentHours) { mutableStateOf(currentHours) }
    var businessInfoSavedNotice by remember { mutableStateOf(false) }

    // Status Change Dialog
    selectedBookingForStatusChange?.let { b ->
        AlertDialog(
            onDismissRequest = { selectedBookingForStatusChange = null },
            title = {
                Text(
                    text = "स्थिति बदलें (ID: ${b.bookingId})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "नई स्थिति चुनें:", fontSize = 12.sp, color = Color.Gray)
                    BookingStatus.values().forEach { st ->
                        Surface(
                            onClick = { newStatusChoice = st },
                            shape = RoundedCornerShape(8.dp),
                            color = if (newStatusChoice == st) AgriGreenContainer else Color(0xFFF9FAF9),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = st.titleHindi,
                                    fontSize = 13.sp,
                                    fontWeight = if (newStatusChoice == st) FontWeight.Bold else FontWeight.Normal
                                )
                                RadioButton(
                                    selected = newStatusChoice == st,
                                    onClick = { newStatusChoice = st }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminNoteInput,
                        onValueChange = { adminNoteInput = it },
                        label = { Text("ऑपरेटर निर्देश / टिप्पणी") },
                        placeholder = { Text("उदा. ऑपरेटर रमेश रवाना") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateBookingStatus(b.bookingId, newStatusChoice, adminNoteInput)
                        selectedBookingForStatusChange = null
                        adminNoteInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("अपडेट करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedBookingForStatusChange = null }) {
                    Text("रद्द")
                }
            }
        )
    }

    // Payment Change Dialog
    selectedBookingForPaymentChange?.let { b ->
        AlertDialog(
            onDismissRequest = { selectedBookingForPaymentChange = null },
            title = {
                Text(
                    text = "भुगतान स्थिति बदलें (ID: ${b.bookingId})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "भुगतान स्थिति:", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaymentStatus.values().forEach { ps ->
                            FilterChip(
                                selected = newPaymentStatusChoice == ps,
                                onClick = { newPaymentStatusChoice = ps },
                                label = { Text(ps.titleHindi, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text(text = "भुगतान का माध्यम (Method):", fontSize = 12.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(PaymentMethod.CASH, PaymentMethod.ONLINE).forEach { pm ->
                            FilterChip(
                                selected = newPaymentMethodChoice == pm,
                                onClick = { newPaymentMethodChoice = pm },
                                label = { Text(pm.titleHindi, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newPaidAmountInput,
                        onValueChange = { newPaidAmountInput = it },
                        label = { Text("प्राप्त राशि (Paid Amount)") },
                        placeholder = { Text("उदा. ₹2,400") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = newPaymentDateInput,
                        onValueChange = { newPaymentDateInput = it },
                        label = { Text("भुगतान तिथि (Date)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateBookingPayment(
                            b.bookingId,
                            newPaymentStatusChoice,
                            newPaymentMethodChoice,
                            b.estimatedAmount,
                            newPaidAmountInput,
                            newPaymentDateInput
                        )
                        selectedBookingForPaymentChange = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedBookingForPaymentChange = null }) {
                    Text("रद्द")
                }
            }
        )
    }

    // Add Service Dialog
    if (showAddServiceDialog) {
        AlertDialog(
            onDismissRequest = { showAddServiceDialog = false },
            title = {
                Text(
                    text = "नया कृषि यंत्र जोड़ें (Add Service)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newServiceNameHindi,
                        onValueChange = { newServiceNameHindi = it },
                        label = { Text("यंत्र का नाम (हिंदी)") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = newServiceNameEnglish,
                        onValueChange = { newServiceNameEnglish = it },
                        label = { Text("Service Name (English)") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = newServiceDesc,
                        onValueChange = { newServiceDesc = it },
                        label = { Text("विवरण (Description)") },
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 2
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newServicePrice,
                            onValueChange = { newServicePrice = it },
                            label = { Text("किराया दर") },
                            placeholder = { Text("उदा. ₹700 / घंटा") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = newServiceHp,
                            onValueChange = { newServiceHp = it },
                            label = { Text("हॉर्सपावर") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newServiceNameHindi.isNotBlank() && newServicePrice.isNotBlank()) {
                            AgriRepository.addNewService(
                                newServiceNameHindi,
                                newServiceNameEnglish,
                                newServiceDesc,
                                newServicePrice,
                                newServiceUnit,
                                newServiceHp
                            )
                            showAddServiceDialog = false
                            newServiceNameHindi = ""
                            newServiceNameEnglish = ""
                            newServiceDesc = ""
                            newServicePrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("जोड़ें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddServiceDialog = false }) { Text("रद्द") }
            }
        )
    }

    // Edit Service Dialog
    serviceToEdit?.let { s ->
        AlertDialog(
            onDismissRequest = { serviceToEdit = null },
            title = {
                Text(
                    text = "यंत्र विवरण संपादित करें",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editNameHindi,
                        onValueChange = { editNameHindi = it },
                        label = { Text("नाम (हिंदी)") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("किराया दर") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editUnit,
                        onValueChange = { editUnit = it },
                        label = { Text("इकाई / दर विवरण") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editDesc,
                        onValueChange = { editDesc = it },
                        label = { Text("विवरण") },
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 2
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "सेवा सक्रिय / उपलब्ध रखें:", fontSize = 12.sp)
                        Switch(checked = editAvailable, onCheckedChange = { editAvailable = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateServiceDetails(
                            s.id,
                            editNameHindi,
                            editNameEnglish,
                            editDesc,
                            editPrice,
                            editUnit,
                            editHp,
                            editAvailable
                        )
                        serviceToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { serviceToEdit = null }) { Text("रद्द") }
            }
        )
    }

    // Filtered bookings
    val filteredBookings = remember(bookings, bookingSearchQuery, selectedStatusFilter) {
        bookings.filter { b ->
            val matchesQuery = bookingSearchQuery.isBlank() ||
                b.bookingId.contains(bookingSearchQuery, ignoreCase = true) ||
                b.customerName.contains(bookingSearchQuery, ignoreCase = true) ||
                b.mobileNumber.contains(bookingSearchQuery) ||
                b.village.contains(bookingSearchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "सभी" -> true
                "लंबित" -> b.status == BookingStatus.PENDING
                "पुष्टि" -> b.status == BookingStatus.CONFIRMED
                "चालू" -> b.status == BookingStatus.IN_PROGRESS
                "संपन्न" -> b.status == BookingStatus.COMPLETED
                "रद्द/अस्वीकृत" -> b.status == BookingStatus.CANCELLED || b.status == BookingStatus.REJECTED
                else -> true
            }
            matchesQuery && matchesStatus
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("admin_dashboard_screen"),
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
                        Column {
                            Text(
                                text = "पप्पू ट्रैक्टर व्यवस्थापक डैशबोर्ड (V5.0)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "सक्रिय व्यवस्थापक: ${currentAdmin?.name ?: "अधिकृत एडमिन"}",
                                fontSize = 11.sp,
                                color = Color(0xFFC8E6C9)
                            )
                        }

                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "लॉगआउट", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Agricultural Stats Row
                    Text(
                        text = "1. कृषि यंत्र सेवाएं (Agriculture):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD54F)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatTile(title = "कुल बुकिंग", value = "${bookings.size}", modifier = Modifier.weight(1f))
                        StatTile(
                            title = "लंबित",
                            value = "${bookings.count { it.status == BookingStatus.PENDING }}",
                            color = Color(0xFFFFB74D),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "पुष्टि",
                            value = "${bookings.count { it.status == BookingStatus.CONFIRMED }}",
                            color = Color(0xFF64B5F6),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "चालू",
                            value = "${bookings.count { it.status == BookingStatus.IN_PROGRESS }}",
                            color = Color(0xFF81C784),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "संपन्न",
                            value = "${bookings.count { it.status == BookingStatus.COMPLETED }}",
                            color = Color(0xFF80CBC4),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Ayurvedic Store Stats Row
                    Text(
                        text = "2. आयुर्वेदिक औषधालय (Ayurveda - Shop Pickup):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFF59D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatTile(
                            title = "कुल दवाएं",
                            value = "${ayurvedicProducts.size}",
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "स्टॉक में",
                            value = "${ayurvedicProducts.count { it.stockQuantity > 0 }}",
                            color = Color(0xFF81C784),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "ऑर्डर पूछताछ",
                            value = "${ayurvedicOrders.size}",
                            color = Color(0xFF90CAF9),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "पिकअप तैयार",
                            value = "${ayurvedicOrders.count { it.status == com.example.data.model.AyurvedicOrderStatus.READY_FOR_PICKUP }}",
                            color = Color(0xFFFFCC80),
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "प्राप्त (Collected)",
                            value = "${ayurvedicOrders.count { it.status == com.example.data.model.AyurvedicOrderStatus.COLLECTED }}",
                            color = Color(0xFFA5D6A7),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "लोकल डेटा मोड (Local Data Mode) • बैकएंड डेटाबेस क्लाउड इंटीग्रेशन हेतु तैयार",
                            fontSize = 10.sp,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Navigation Tabs Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminTab.values().forEach { tab ->
                    val isSelected = selectedAdminTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedAdminTab = tab },
                        label = {
                            Text(
                                text = tab.titleHindi,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // TAB 1: BOOKING MANAGEMENT
        if (selectedAdminTab == AdminTab.BOOKINGS) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        OutlinedTextField(
                            value = bookingSearchQuery,
                            onValueChange = { bookingSearchQuery = it },
                            placeholder = { Text("आईडी, नाम या मोबाइल द्वारा खोजें...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AgriGreenPrimary) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("सभी", "लंबित", "पुष्टि", "चालू", "संपन्न", "रद्द/अस्वीकृत").forEach { filter ->
                                val isSelected = selectedStatusFilter == filter
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedStatusFilter = filter },
                                    label = { Text(filter, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AgriGreenPrimary,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            if (filteredBookings.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Inbox, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("कोई बुकिंग नहीं मिली", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(filteredBookings) { booking ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = booking.bookingId,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AgriGreenDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "• ${booking.createdAtFormatted}", fontSize = 11.sp, color = Color.Gray)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (booking.status) {
                                        BookingStatus.PENDING -> Color(0xFFFFF3E0)
                                        BookingStatus.CONFIRMED -> Color(0xFFE3F2FD)
                                        BookingStatus.IN_PROGRESS -> Color(0xFFE8F5E9)
                                        BookingStatus.COMPLETED -> Color(0xFFE8F5E9)
                                        BookingStatus.REJECTED -> Color(0xFFFFEBEE)
                                        BookingStatus.CANCELLED -> Color(0xFFF5F5F5)
                                    }
                                ) {
                                    Text(
                                        text = booking.status.titleHindi,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (booking.status) {
                                            BookingStatus.PENDING -> Color(0xFFE65100)
                                            BookingStatus.CONFIRMED -> Color(0xFF1565C0)
                                            BookingStatus.IN_PROGRESS -> Color(0xFF2E7D32)
                                            BookingStatus.COMPLETED -> Color(0xFF1B5E20)
                                            BookingStatus.REJECTED -> Color(0xFFC62828)
                                            BookingStatus.CANCELLED -> Color(0xFF616161)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "किसान: ${booking.customerName} (${booking.mobileNumber})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (booking.fatherName.isNotBlank()) {
                                Text(text = "पिता: ${booking.fatherName}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Text(text = "गांव व पता: ${booking.village}, ${booking.fullAddress}", fontSize = 12.sp, color = Color(0xFF4B5563))
                            Text(text = "यंत्र: ${booking.serviceNameHindi} • मात्रा: ${booking.landArea} ${booking.landUnit}", fontSize = 12.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "तारीख: ${booking.bookingDate} (${booking.preferredTime})", fontSize = 11.sp, color = Color(0xFF6B7280))

                            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "भुगतान: ", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "${booking.paymentStatus.titleHindi} (${booking.paymentMethod.titleHindi}) - प्राप्त: ${booking.paidAmount}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (booking.paymentStatus == PaymentStatus.FULLY_PAID) AgriGreenPrimary else Color(0xFFD97706)
                                )
                            }

                            if (booking.adminNotes.isNotBlank()) {
                                Text(text = "नोट: ${booking.adminNotes}", fontSize = 11.sp, color = Color(0xFF4B5563), modifier = Modifier.padding(top = 2.dp))
                            }

                            Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        selectedBookingForStatusChange = booking
                                        newStatusChoice = booking.status
                                        adminNoteInput = booking.adminNotes
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("स्थिति बदलें", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        selectedBookingForPaymentChange = booking
                                        newPaymentStatusChoice = booking.paymentStatus
                                        newPaymentMethodChoice = booking.paymentMethod
                                        newPaidAmountInput = booking.paidAmount
                                        newPaymentDateInput = booking.paymentDate.ifBlank { "आज" }
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Text("भुगतान दर्ज", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { ContactUtils.dialPhoneNumber(context, booking.mobileNumber) },
                                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(6.dp)).background(CallBlue)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = "कॉल", tint = Color.White, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = {
                                        val msg = "नमस्ते ${booking.customerName} जी, मैं पप्पू ट्रैक्टर से बोल रहा हूँ। आपकी बुकिंग आईडी ${booking.bookingId} (${booking.serviceNameHindi}) की स्थिति: ${booking.status.titleHindi} है।"
                                        ContactUtils.openWhatsApp(context, booking.mobileNumber, msg)
                                    },
                                    modifier = Modifier.size(38.dp).clip(RoundedCornerShape(6.dp)).background(WhatsAppGreen)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 2: AYURVEDIC ORDERS MANAGEMENT (SHOP PICKUP)
        if (selectedAdminTab == AdminTab.AYURVEDIC_ORDERS) {
            item {
                AyurvedicOrdersAdminSection()
            }
        }

        // TAB 3: AYURVEDIC PRODUCTS & STOCK INVENTORY
        if (selectedAdminTab == AdminTab.AYURVEDIC_PRODUCTS) {
            item {
                AyurvedicProductsAdminSection()
            }
        }

        // TAB 4: AYURVEDIC MEDICINE CATEGORIES
        if (selectedAdminTab == AdminTab.AYURVEDIC_CATEGORIES) {
            item {
                AyurvedicCategoriesAdminSection()
            }
        }

        // TAB 5: PAYMENTS MANAGEMENT
        if (selectedAdminTab == AdminTab.PAYMENTS) {
            items(bookings) { b ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "${b.bookingId} - ${b.customerName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "यंत्र: ${b.serviceNameHindi}", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "अनुमानित: ${b.estimatedAmount} • प्राप्त: ${b.paidAmount}", fontSize = 12.sp, color = AgriGreenPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "माध्यम: ${b.paymentMethod.titleHindi} (${b.paymentDate})", fontSize = 11.sp, color = Color(0xFF6B7280))
                        }

                        Button(
                            onClick = {
                                selectedBookingForPaymentChange = b
                                newPaymentStatusChoice = b.paymentStatus
                                newPaymentMethodChoice = b.paymentMethod
                                newPaidAmountInput = b.paidAmount
                                newPaymentDateInput = b.paymentDate.ifBlank { "आज" }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("अपडेट", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // TAB 3: SERVICES MANAGEMENT
        if (selectedAdminTab == AdminTab.SERVICES) {
            item {
                Button(
                    onClick = { showAddServiceDialog = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGoldHarvest)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("नया कृषि यंत्र जोड़ें (Add New Machine)", fontWeight = FontWeight.Bold)
                }
            }

            items(services) { service ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = service.nameHindi, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                                Text(text = "${service.nameEnglish} • ${service.horsepower}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Switch(
                                checked = service.isAvailable,
                                onCheckedChange = { AgriRepository.toggleServiceAvailability(service.id) }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(shape = RoundedCornerShape(8.dp), color = AgriGreenContainer, modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "दर: ${service.priceRateHindi}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                                Text(text = service.priceUnit, fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                serviceToEdit = service
                                editNameHindi = service.nameHindi
                                editNameEnglish = service.nameEnglish
                                editDesc = service.shortDescHindi
                                editPrice = service.priceRateHindi
                                editUnit = service.priceUnit
                                editHp = service.horsepower
                                editAvailable = service.isAvailable
                            },
                            modifier = Modifier.fillMaxWidth().height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("यंत्र व दर संपादित करें", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // TAB 4: CUSTOMERS DIRECTORY LINK
        if (selectedAdminTab == AdminTab.CUSTOMERS) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "किसान डायरेक्टरी प्रबंधन (Customer Records)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AgriGreenDark)
                        Text(text = "कुल ${customers.size} किसान पंजीकृत हैं। आप यहां से संपूर्ण डायरेक्टरी देख सकते हैं, नए किसान जोड़ सकते हैं अथवा संपादित कर सकते हैं।", fontSize = 12.sp, color = Color(0xFF4B5563), lineHeight = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToCustomerDirectory,
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Icon(Icons.Default.People, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("किसान डायरेक्टरी खोलें (Open Directory)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 5: WHATSAPP CONFIGURATION & BUSINESS SETTINGS
        if (selectedAdminTab == AdminTab.WHATSAPP_CONFIG) {
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
                            text = "व्हाट्सएप व व्यापारिक सेटिंग्स (WhatsApp Settings)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )

                        Text(
                            text = "बुकिंग संदेश इसी नंबर के व्हाट्सएप पर प्राप्त होंगे:",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )

                        OutlinedTextField(
                            value = editBusinessWhatsApp,
                            onValueChange = { editBusinessWhatsApp = it },
                            label = { Text("मालिक का व्हाट्सएप नंबर *") },
                            placeholder = { Text("7388403923") },
                            leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editBusinessPhone,
                            onValueChange = { editBusinessPhone = it },
                            label = { Text("कॉलिंग फोन नंबर *") },
                            placeholder = { Text("7388403923") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CallBlue) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        OutlinedTextField(
                            value = editBusinessAddress,
                            onValueChange = { editBusinessAddress = it },
                            label = { Text("कार्यालय व कार्यशाला पता") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            maxLines = 3
                        )

                        OutlinedTextField(
                            value = editBusinessHours,
                            onValueChange = { editBusinessHours = it },
                            label = { Text("कार्य समय (Operating Hours)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        if (businessInfoSavedNotice) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AgriGreenContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AgriGreenPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "व्हाट्सएप व व्यापारिक जानकारी सफलतापूर्वक सहेज ली गई!",
                                        fontSize = 12.sp,
                                        color = AgriGreenDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                AgriRepository.updateBusinessInfo(
                                    name = "",
                                    phone = editBusinessPhone,
                                    whatsApp = editBusinessWhatsApp,
                                    address = editBusinessAddress,
                                    hours = editBusinessHours
                                )
                                businessInfoSavedNotice = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("सेटिंग्स सहेजें (Save WhatsApp Settings)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    color: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = title, fontSize = 9.sp, color = Color.White.copy(alpha = 0.85f))
        }
    }
}
