package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.CallBlue
import com.example.ui.theme.WhatsAppGreen
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDirectoryScreen(
    isAdmin: Boolean = false,
    onBackClick: () -> Unit,
    onCustomerSelectedForBooking: ((Customer) -> Unit)? = null
) {
    val context = LocalContext.current
    val customers by AgriRepository.customers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    // Form fields for add/edit
    var formName by remember { mutableStateOf("") }
    var formFatherName by remember { mutableStateOf("") }
    var formMobile by remember { mutableStateOf("") }
    var formVillage by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formConsentChecked by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    val filteredCustomers = remember(searchQuery, customers) {
        if (searchQuery.isBlank()) customers
        else AgriRepository.searchCustomers(searchQuery)
    }

    // Add / Edit Dialog
    if (showAddDialog || customerToEdit != null) {
        val isEditing = customerToEdit != null
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                customerToEdit = null
            },
            title = {
                Text(
                    text = if (isEditing) "किसान विवरण संपादित करें" else "नया किसान पंजीकरण (New Registration)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it; formError = null },
                        label = { Text("किसान का पूरा नाम *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = formFatherName,
                        onValueChange = { formFatherName = it },
                        label = { Text("पिता का नाम (Father's Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = formMobile,
                        onValueChange = {
                            if (it.length <= 10 && it.all { c -> c.isDigit() }) {
                                formMobile = it
                                formError = null
                            }
                        },
                        label = { Text("मोबाइल नंबर (10 अंक) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = formVillage,
                        onValueChange = { formVillage = it; formError = null },
                        label = { Text("गांव का नाम *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = formAddress,
                        onValueChange = { formAddress = it },
                        label = { Text("खेत / घर का पता व पहचान चिह्न") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 2
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = formConsentChecked,
                            onCheckedChange = { formConsentChecked = it; formError = null }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "किसान की सहमति से विवरण सहेजा जा रहा है (Privacy Protected)।",
                            fontSize = 11.sp,
                            color = Color(0xFF4B5563)
                        )
                    }

                    if (formError != null) {
                        Text(
                            text = formError ?: "",
                            color = Color(0xFFD32F2F),
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        when {
                            formName.isBlank() -> formError = "कृपया किसान का नाम दर्ज करें।"
                            formMobile.length != 10 -> formError = "कृपया 10 अंकों का मोबाइल नंबर दर्ज करें।"
                            formVillage.isBlank() -> formError = "कृपया गांव का नाम दर्ज करें।"
                            !formConsentChecked -> formError = "कृपया किसान की सहमति चेकबॉक्स पर टिक करें।"
                            else -> {
                                if (isEditing) {
                                    customerToEdit?.let {
                                        AgriRepository.updateCustomer(
                                            it.id,
                                            formName,
                                            formFatherName,
                                            formMobile,
                                            formVillage,
                                            formAddress
                                        )
                                    }
                                } else {
                                    AgriRepository.registerCustomer(
                                        formName,
                                        formFatherName,
                                        formMobile,
                                        formVillage,
                                        formAddress
                                    )
                                }
                                showAddDialog = false
                                customerToEdit = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text(if (isEditing) "सहेजें" else "पंजीकृत करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddDialog = false
                    customerToEdit = null
                }) {
                    Text("रद्द")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    customerToDelete?.let { c ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("रिकॉर्ड हटाएं?", fontWeight = FontWeight.Bold) },
            text = { Text("क्या आप ${c.name} (${c.village}) का रिकॉर्ड हटाना चाहते हैं?") },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.deleteCustomer(c.id)
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("हटाएं")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { customerToDelete = null }) { Text("रद्द") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("customer_directory_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
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
                            Column {
                                Text(
                                    text = "स्मार्ट किसान डायरेक्टरी",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "कुल ${customers.size} पंजीकृत अन्नदाता",
                                    fontSize = 11.sp,
                                    color = Color(0xFFC8E6C9)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                formName = ""
                                formFatherName = ""
                                formMobile = ""
                                formVillage = ""
                                formAddress = ""
                                formConsentChecked = false
                                formError = null
                                showAddDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGoldHarvest),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("नया किसान", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Privacy Assurance Notice
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "गोपनीयता नीति: केवल किसान की सहमति से स्वयं प्रदान किया गया विवरण सहेजा जाता है।",
                                fontSize = 10.sp,
                                color = Color.White,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Search Input
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("किसान का नाम, पिता का नाम या मोबाइल खोजें...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AgriGreenPrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // List of Customers
        if (filteredCustomers.isEmpty()) {
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
                        Icon(Icons.Default.PersonSearch, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("कोई किसान रिकॉर्ड नहीं मिला", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredCustomers) { customer ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
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
                                    text = customer.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenDark
                                )
                                if (customer.fatherName.isNotBlank()) {
                                    Text(
                                        text = "पिता: ${customer.fatherName}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AgriGreenContainer
                            ) {
                                Text(
                                    text = customer.village,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "मोबाइल: +91 ${customer.mobileNumber}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937)
                        )

                        if (customer.fullAddress.isNotBlank()) {
                            Text(
                                text = "पता: ${customer.fullAddress}",
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563)
                            )
                        }

                        Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Autofill for booking action if provided
                            if (onCustomerSelectedForBooking != null) {
                                Button(
                                    onClick = { onCustomerSelectedForBooking(customer) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(2.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("बुकिंग फॉर्म भरें", fontSize = 11.sp)
                                }
                            }

                            // Quick Call
                            IconButton(
                                onClick = { ContactUtils.dialPhoneNumber(context, customer.mobileNumber) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CallBlue)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            // Quick WhatsApp
                            IconButton(
                                onClick = {
                                    ContactUtils.openWhatsApp(
                                        context,
                                        customer.mobileNumber,
                                        "नमस्ते ${customer.name} जी, मैं पप्पू ट्रैक्टर कृषि सेवा केंद्र से संपर्क कर रहा हूँ।"
                                    )
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(WhatsAppGreen)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(16.dp))
                            }

                            // Admin Edit / Delete Actions
                            if (isAdmin) {
                                IconButton(
                                    onClick = {
                                        customerToEdit = customer
                                        formName = customer.name
                                        formFatherName = customer.fatherName
                                        formMobile = customer.mobileNumber
                                        formVillage = customer.village
                                        formAddress = customer.fullAddress
                                        formConsentChecked = true
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFEEEEEE))
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF424242), modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { customerToDelete = customer },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFEBEE))
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
