package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyurvedicOrder
import com.example.data.model.Customer
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyurvedicCartScreen(
    cart: Map<String, Int>,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onClearCart: () -> Unit,
    onSubmitOrder: (
        name: String,
        mobile: String,
        village: String,
        address: String,
        notes: String,
        onSuccess: (AyurvedicOrder) -> Unit
    ) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val allProducts by AgriRepository.ayurvedicProducts.collectAsState()
    val customers by AgriRepository.customers.collectAsState()
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()
    val shopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val shopHours by AgriRepository.ayurvedicShopHours.collectAsState()

    var customerName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var village by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var customerNotes by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf<String?>(null) }
    var showWhatsAppConfirmationDialog by remember { mutableStateOf(false) }
    var pendingPreparedOrder by remember { mutableStateOf<AyurvedicOrder?>(null) }
    var showConsentDialogForCustomer by remember { mutableStateOf<Customer?>(null) }

    // Autocomplete matching customers
    val matchingCustomers = remember(mobileNumber, customerName, customers) {
        if (mobileNumber.length >= 3) {
            customers.filter { it.mobileNumber.contains(mobileNumber.trim()) }
        } else if (customerName.length >= 2) {
            customers.filter { it.name.lowercase().contains(customerName.trim().lowercase()) }
        } else {
            emptyList()
        }
    }

    val cartItemsWithProducts = remember(cart, allProducts) {
        cart.mapNotNull { (prodId, qty) ->
            val p = allProducts.find { it.id == prodId }
            if (p != null) Pair(p, qty) else null
        }
    }

    val totalAmount = remember(cartItemsWithProducts) {
        cartItemsWithProducts.sumOf { (product, qty) -> product.price * qty }
    }

    // Consent autofill dialog
    if (showConsentDialogForCustomer != null) {
        val cust = showConsentDialogForCustomer!!
        AlertDialog(
            onDismissRequest = { showConsentDialogForCustomer = null },
            title = {
                Text(
                    text = "किसान विवरण स्वतः भरें?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text("क्या आप पूर्व में दर्ज किसान विवरण का उपयोग करना चाहते हैं?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• नाम: ${cust.name}", fontWeight = FontWeight.SemiBold)
                    Text("• मोबाइल: ${cust.mobileNumber}")
                    Text("• गांव: ${cust.village}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        customerName = cust.name
                        mobileNumber = cust.mobileNumber
                        village = cust.village
                        fullAddress = cust.fullAddress
                        showConsentDialogForCustomer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Text("स्वीकार व स्वतः भरें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConsentDialogForCustomer = null }) {
                    Text("रद्द करें")
                }
            }
        )
    }

    // Pre-dispatch Confirmation Dialog (Requirement Section 5)
    if (showWhatsAppConfirmationDialog && pendingPreparedOrder != null) {
        val order = pendingPreparedOrder!!
        AlertDialog(
            onDismissRequest = { showWhatsAppConfirmationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WhatsApp पर ऑर्डर भेजें?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "ऑर्डर संदर्भ: ${order.orderId}",
                        fontWeight = FontWeight.Bold,
                        color = AyurvedaHerbGreen
                    )
                    Text("ग्राहक: ${order.customerName} (${order.village})")
                    Text("कुल अनुमानित राशि: ${order.totalAmountFormatted}", fontWeight = FontWeight.Bold)
                    Text("पिकअप: दुकान से स्वयं प्राप्त करेंगे (Shop Pickup)")
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = "\"आपका ऑर्डर WhatsApp पर भेजने के बाद दुकान से पुष्टि प्राप्त करें। दवा लेने आने से पहले उपलब्धता और कीमत की पुष्टि कर लें।\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(10.dp),
                            lineHeight = 17.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "व्हाट्सएप खुलने पर संदेश भेजने के लिए Send बटन दबाएं।",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWhatsAppConfirmationDialog = false
                        val orderMsg = ContactUtils.createWhatsAppAyurvedicOrderMessage(order)
                        ContactUtils.openWhatsApp(context, whatsAppNumber, orderMsg)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    modifier = Modifier.testTag("confirm_open_whatsapp_order_btn")
                ) {
                    Text("WhatsApp खोलें व भेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showWhatsAppConfirmationDialog = false }) {
                    Text("वापस बदलें")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "आयुर्वेदिक ऑर्डर सारांश",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "पीछे", tint = Color.White)
                    }
                },
                actions = {
                    if (cart.isNotEmpty()) {
                        TextButton(
                            onClick = onClearCart,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) {
                            Text("खाली करें", fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AyurvedaHerbGreen)
            )
        }
    ) { innerPadding ->
        if (cartItemsWithProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(AgriBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "आपकी दवा सूची खाली है",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "आयुर्वेदिक स्टोर से अपनी आवश्यकतानुसार औषधियां चुनें।",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("दवाएं देखें")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AgriBackground)
                    .padding(innerPadding)
                    .testTag("ayurvedic_cart_screen"),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // SHOP PICKUP MANDATORY NOTICE BANNER
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "दुकान से संग्रह (Shop Pickup Only):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "यह ऑर्डर सीधे दुकान से प्राप्त करने हेतु है। होम डिलीवरी उपलब्ध नहीं है।\nदुकान: $shopAddress\nसमय: $shopHours",
                                    fontSize = 11.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Selected Products List
                item {
                    Text(
                        text = "चयनित औषधियां (${cartItemsWithProducts.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B381E),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                items(cartItemsWithProducts) { (product, qty) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = product.imageRes),
                                contentDescription = product.nameHindi,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.nameHindi,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B381E)
                                )
                                Text(
                                    text = "${product.packSize} • ${product.priceFormatted} प्रति नग",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "योग: ₹${(product.price * qty).toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AyurvedaAmber
                                )
                            }

                            // Quantity controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onUpdateQuantity(product.id, qty - 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (qty == 1) Icons.Default.Delete else Icons.Default.Remove,
                                        contentDescription = "घटाएं",
                                        tint = if (qty == 1) Color.Red else Color.DarkGray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = qty.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { onUpdateQuantity(product.id, qty + 1) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "बढ़ाएं", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Total Summary Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F2))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "कुल अनुमानित राशि",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E4032)
                                )
                                Text(
                                    text = "(दुकान पर भुगतान करें)",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "₹${totalAmount.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = AyurvedaAmber
                            )
                        }
                    }
                }

                // Customer Details Form (Requirement Section 5)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ग्राहक विवरण (Customer Details)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B381E)
                            )
                            Text(
                                text = "दुकानदार से संपर्क हेतु अपनी सही जानकारी भरें।",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )

                            // Error prompt
                            if (formError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = formError!!,
                                        color = Color(0xFFC62828),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Customer Name
                            OutlinedTextField(
                                value = customerName,
                                onValueChange = {
                                    customerName = it
                                    formError = null
                                },
                                label = { Text("ग्राहक का नाम *") },
                                placeholder = { Text("उदा. रामप्रसाद यादव") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ayurveda_customer_name_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mobile Number
                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = {
                                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                        mobileNumber = it
                                        formError = null
                                    }
                                },
                                label = { Text("मोबाइल नंबर (10 अंक) *") },
                                placeholder = { Text("7388403923") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ayurveda_customer_mobile_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Autocomplete suggestion chip if customer found
                            if (matchingCustomers.isNotEmpty() && (customerName.isBlank() || village.isBlank())) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    matchingCustomers.take(3).forEach { cust ->
                                        SuggestionChip(
                                            onClick = { showConsentDialogForCustomer = cust },
                                            label = {
                                                Text(
                                                    text = "${cust.name} (${cust.village})",
                                                    fontSize = 11.sp
                                                )
                                            },
                                            icon = {
                                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Village
                            OutlinedTextField(
                                value = village,
                                onValueChange = {
                                    village = it
                                    formError = null
                                },
                                label = { Text("गांव / कस्बा का नाम *") },
                                placeholder = { Text("उदा. कल्याणपुर") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ayurveda_customer_village_input"),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Notes / Timing
                            OutlinedTextField(
                                value = customerNotes,
                                onValueChange = { customerNotes = it },
                                label = { Text("दुकान आने का संभावित समय या निर्देश (वैकल्पिक)") },
                                placeholder = { Text("उदा. शाम को 5 बजे दुकान से ले लूंगा") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Delivery option indicator (LOCKED to Shop Pickup)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE8F5E9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = true,
                                        onClick = null,
                                        colors = RadioButtonDefaults.colors(selectedColor = AyurvedaHerbGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "दुकान से स्वयं प्राप्त करेंगे (Shop Pickup)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AyurvedaHerbGreen
                                        )
                                        Text(
                                            text = "होम डिलीवरी उपलब्ध नहीं है। दुकान काउंटर पर भुगतान करें।",
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Required Statutory Disclaimer Notice
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF9C4),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "\"आपका ऑर्डर WhatsApp पर भेजने के बाद दुकान से पुष्टि प्राप्त करें। दवा लेने आने से पहले उपलब्धता और कीमत की पुष्टि कर लें।\"",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF5D4037),
                                    modifier = Modifier.padding(10.dp),
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // ORDER ON WHATSAPP BUTTON
                            Button(
                                onClick = {
                                    if (customerName.isBlank()) {
                                        formError = "कृपया अपना नाम दर्ज करें।"
                                        return@Button
                                    }
                                    if (mobileNumber.length != 10) {
                                        formError = "कृपया सही 10 अंकों का मोबाइल नंबर दर्ज करें।"
                                        return@Button
                                    }
                                    if (village.isBlank()) {
                                        formError = "कृपया अपने गांव का नाम दर्ज करें।"
                                        return@Button
                                    }

                                    formError = null
                                    onSubmitOrder(
                                        customerName,
                                        mobileNumber,
                                        village,
                                        fullAddress,
                                        customerNotes
                                    ) { createdOrder ->
                                        pendingPreparedOrder = createdOrder
                                        showWhatsAppConfirmationDialog = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_ayurvedic_order_whatsapp_btn"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "व्हाट्सएप पर ऑर्डर भेजें (₹${totalAmount.toInt()})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
