package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

/**
 * Ayurvedic Order Management Section for Admin Dashboard
 * Supports status update: Pending Verification -> Ready for Shop Pickup -> Collected / Cancelled
 */
@Composable
fun AyurvedicOrdersAdminSection() {
    val context = LocalContext.current
    val orders by AgriRepository.ayurvedicOrders.collectAsState()

    var statusFilter by remember { mutableStateOf("सभी") }
    var selectedOrderForStatus by remember { mutableStateOf<AyurvedicOrder?>(null) }
    var newStatusChoice by remember { mutableStateOf(AyurvedicOrderStatus.READY_FOR_PICKUP) }
    var adminNoteInput by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, statusFilter) {
        when (statusFilter) {
            "सभी" -> orders
            "प्रतीक्षित" -> orders.filter { it.status == AyurvedicOrderStatus.PENDING_VERIFICATION }
            "पिकअप तैयार" -> orders.filter { it.status == AyurvedicOrderStatus.READY_FOR_PICKUP }
            "प्राप्त (Collected)" -> orders.filter { it.status == AyurvedicOrderStatus.COLLECTED }
            "रद्द" -> orders.filter { it.status == AyurvedicOrderStatus.CANCELLED }
            else -> orders
        }
    }

    // Status Change Dialog
    if (selectedOrderForStatus != null) {
        val o = selectedOrderForStatus!!
        AlertDialog(
            onDismissRequest = { selectedOrderForStatus = null },
            title = {
                Text(
                    text = "ऑर्डर स्थिति बदलें (${o.orderId})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AyurvedaHerbGreen
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("ग्राहक: ${o.customerName} (${o.mobileNumber})", fontSize = 12.sp)
                    Text("कुल राशि: ${o.totalAmountFormatted}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("दवा स्थिति चुनें:", fontSize = 12.sp, color = Color.Gray)

                    AyurvedicOrderStatus.values().forEach { st ->
                        Surface(
                            onClick = { newStatusChoice = st },
                            shape = RoundedCornerShape(8.dp),
                            color = if (newStatusChoice == st) Color(0xFFE8F5E9) else Color(0xFFF9FAF9),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(st.titleHindi, fontSize = 12.sp, fontWeight = if (newStatusChoice == st) FontWeight.Bold else FontWeight.Normal)
                                if (newStatusChoice == st) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AyurvedaHerbGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = adminNoteInput,
                        onValueChange = { adminNoteInput = it },
                        label = { Text("व्यवस्थापक टिप्पणी (Admin Note)") },
                        placeholder = { Text("उदा. दवा काउंटर पर तैयार रखी है") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateAyurvedicOrderStatus(o.orderId, newStatusChoice, adminNoteInput)
                        selectedOrderForStatus = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedOrderForStatus = null }) {
                    Text("रद्द")
                }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "आयुर्वेदिक दवा ऑर्डर प्रबंधन (Shop Pickup Enquiries)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AyurvedaHerbGreen
                )
                Text(
                    text = "ग्राहक दुकान आकर दवाएं प्राप्त करेंगे। स्थिति अपडेट करें:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("सभी", "प्रतीक्षित", "पिकअप तैयार", "प्राप्त (Collected)", "रद्द").forEach { flt ->
                        val isSel = statusFilter == flt
                        FilterChip(
                            selected = isSel,
                            onClick = { statusFilter = flt },
                            label = { Text(flt, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AyurvedaHerbGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        if (filteredOrders.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("कोई ऑर्डर नहीं मिला", color = Color.Gray)
                }
            }
        } else {
            filteredOrders.forEach { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_ayur_order_${order.orderId}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ऑर्डर ID: ${order.orderId}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(order.status.badgeColorHex).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = order.status.titleHindi,
                                    color = Color(order.status.badgeColorHex),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ग्राहक: ${order.customerName} (${order.mobileNumber})", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text("गांव: ${order.village} • समय: ${order.createdAtFormatted}", fontSize = 11.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("दवाएं:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        order.items.forEach { itm ->
                            Text("• ${itm.productNameHindi} (${itm.packSize}) x ${itm.quantity} = ₹${itm.totalPrice.toInt()}", fontSize = 11.sp, color = Color.DarkGray)
                        }

                        if (order.customerNotes.isNotBlank()) {
                            Text("ग्राहक नोट: \"${order.customerNotes}\"", fontSize = 11.sp, color = Color(0xFF6B7280))
                        }
                        if (order.adminNotes.isNotBlank()) {
                            Text("एडमिन नोट: ${order.adminNotes}", fontSize = 11.sp, color = AyurvedaHerbGreen)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "कुल: ${order.totalAmountFormatted}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = AyurvedaAmber
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { ContactUtils.dialPhoneNumber(context, order.mobileNumber) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("कॉल", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        selectedOrderForStatus = order
                                        newStatusChoice = order.status
                                        adminNoteInput = order.adminNotes
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text("स्थिति बदलें", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ayurvedic Medicine Product Management Section for Admin Dashboard
 * Add medicine, edit price, update stock, toggle availability, hide/publish
 */
@Composable
fun AyurvedicProductsAdminSection() {
    val products by AgriRepository.ayurvedicProducts.collectAsState()
    val categories by AgriRepository.ayurvedicCategories.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<AyurvedicProduct?>(null) }
    var productToDelete by remember { mutableStateOf<AyurvedicProduct?>(null) }

    // Add Form State
    var addNameHindi by remember { mutableStateOf("") }
    var addNameEnglish by remember { mutableStateOf("") }
    var addCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id ?: "cat_herbal") }
    var addManufacturer by remember { mutableStateOf("") }
    var addPackSize by remember { mutableStateOf("100 ग्राम") }
    var addPrice by remember { mutableStateOf("") }
    var addStock by remember { mutableStateOf("20") }
    var addIngredients by remember { mutableStateOf("") }
    var addDesc by remember { mutableStateOf("") }
    var addWarnings by remember { mutableStateOf("योग्य चिकित्सक (वैद्य जी) के परामर्श अनुसार लें।") }

    // Edit Product Dialog (Price & Stock)
    if (productToEdit != null) {
        val p = productToEdit!!
        var editPriceInput by remember { mutableStateOf(p.price.toInt().toString()) }
        var editStockInput by remember { mutableStateOf(p.stockQuantity.toString()) }
        var editAvailable by remember { mutableStateOf(p.isAvailable) }
        var editPublished by remember { mutableStateOf(p.isPublished) }

        AlertDialog(
            onDismissRequest = { productToEdit = null },
            title = {
                Text("दवा विवरण संशोधित करें: ${p.nameHindi}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editPriceInput,
                        onValueChange = { editPriceInput = it },
                        label = { Text("मूल्य (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editStockInput,
                        onValueChange = { editStockInput = it },
                        label = { Text("स्टॉक संख्या (Stock Quantity)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("उपलब्ध (Available):", fontSize = 12.sp)
                        Switch(checked = editAvailable, onCheckedChange = { editAvailable = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ऐप में प्रदर्शित करें (Published):", fontSize = 12.sp)
                        Switch(checked = editPublished, onCheckedChange = { editPublished = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = editPriceInput.toDoubleOrNull() ?: p.price
                        val newStock = editStockInput.toIntOrNull() ?: p.stockQuantity
                        AgriRepository.updateAyurvedicProduct(
                            p.copy(
                                price = newPrice,
                                priceFormatted = "₹${newPrice.toInt()}",
                                stockQuantity = newStock,
                                isAvailable = editAvailable && newStock > 0,
                                isPublished = editPublished
                            )
                        )
                        productToEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { productToEdit = null }) { Text("रद्द") }
            }
        )
    }

    // Add Product Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text("नई आयुर्वेदिक औषधि जोड़ें", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AyurvedaHerbGreen)
            },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = addNameHindi,
                        onValueChange = { addNameHindi = it },
                        label = { Text("औषधि का नाम (हिंदी) *") },
                        placeholder = { Text("उदा. त्रिफला चूर्ण") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addNameEnglish,
                        onValueChange = { addNameEnglish = it },
                        label = { Text("Medicine Name (English)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addManufacturer,
                        onValueChange = { addManufacturer = it },
                        label = { Text("निर्माता / फार्मेसी *") },
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = addPackSize,
                            onValueChange = { addPackSize = it },
                            label = { Text("पैक साइज") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = addPrice,
                            onValueChange = { addPrice = it },
                            label = { Text("मूल्य (₹) *") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                    OutlinedTextField(
                        value = addStock,
                        onValueChange = { addStock = it },
                        label = { Text("स्टॉक संख्या *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = addIngredients,
                        onValueChange = { addIngredients = it },
                        label = { Text("सत्यापित घटक (Ingredients) *") },
                        placeholder = { Text("उदा. आंवला, हरड़, बहेड़ा") }
                    )
                    OutlinedTextField(
                        value = addDesc,
                        onValueChange = { addDesc = it },
                        label = { Text("विवरण व उपयोग") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addNameHindi.isNotBlank() && addPrice.isNotBlank()) {
                            val priceVal = addPrice.toDoubleOrNull() ?: 100.0
                            val stockVal = addStock.toIntOrNull() ?: 10
                            val selectedCat = categories.find { it.id == addCategoryId } ?: categories.first()

                            val newProd = AyurvedicProduct(
                                id = "ayur_custom_${System.currentTimeMillis() % 10000}",
                                nameHindi = addNameHindi.trim(),
                                nameEnglish = if (addNameEnglish.isNotBlank()) addNameEnglish.trim() else addNameHindi.trim(),
                                categoryId = selectedCat.id,
                                categoryNameHindi = selectedCat.nameHindi,
                                manufacturer = if (addManufacturer.isNotBlank()) addManufacturer.trim() else "प्रामाणिक फार्मेसी",
                                shortDescHindi = if (addDesc.isNotBlank()) addDesc.trim() else "आयुर्वेदिक औषधि",
                                fullDescHindi = if (addDesc.isNotBlank()) addDesc.trim() else "आयुर्वेदिक औषधि",
                                verifiedIngredientsHindi = if (addIngredients.isNotBlank()) addIngredients.trim() else "पारंपरिक जड़ी-बूटी योग",
                                packSize = addPackSize.trim(),
                                price = priceVal,
                                priceFormatted = "₹${priceVal.toInt()}",
                                stockQuantity = stockVal,
                                isAvailable = stockVal > 0,
                                isPublished = true,
                                imageRes = R.drawable.ayurveda_churna,
                                applicableWarningsHindi = addWarnings.trim(),
                                dosageDisclaimerHindi = "मात्रा व अनुपान हेतु योग्य आयुर्वेदिक चिकित्सक से परामर्श लें।"
                            )
                            AgriRepository.addAyurvedicProduct(newProd)
                            showAddDialog = false
                            addNameHindi = ""
                            addPrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Text("जोड़ें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) { Text("रद्द") }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "दवा सूची एवं स्टॉक प्रबंधन",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AyurvedaHerbGreen
                    )
                    Text(
                        text = "केवल अधिकृत एडमिन द्वारा चुनी गई दवाएं प्रदर्शित",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen),
                    modifier = Modifier.testTag("admin_add_ayur_product_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("नई दवा", fontSize = 12.sp)
                }
            }
        }

        // Product items
        products.forEach { prod ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = prod.imageRes),
                        contentDescription = prod.nameHindi,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp))
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.nameHindi, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("${prod.categoryNameHindi} • ${prod.packSize}", fontSize = 11.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(prod.priceFormatted, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AyurvedaAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "स्टॉक: ${prod.stockQuantity}",
                                fontSize = 11.sp,
                                color = if (prod.stockQuantity > 0) Color(0xFF2E7D32) else Color.Red
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { productToEdit = prod },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "संशोधित करें", tint = AyurvedaHerbGreen, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = { AgriRepository.deleteAyurvedicProduct(prod.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Ayurvedic Category Management Section for Admin Dashboard
 */
@Composable
fun AyurvedicCategoriesAdminSection() {
    val categories by AgriRepository.ayurvedicCategories.collectAsState()

    var showAddCatDialog by remember { mutableStateOf(false) }
    var catNameHindi by remember { mutableStateOf("") }
    var catNameEnglish by remember { mutableStateOf("") }
    var catDescHindi by remember { mutableStateOf("") }

    if (showAddCatDialog) {
        AlertDialog(
            onDismissRequest = { showAddCatDialog = false },
            title = { Text("नई दवा श्रेणी जोड़ें", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = catNameHindi,
                        onValueChange = { catNameHindi = it },
                        label = { Text("श्रेणी का नाम (हिंदी) *") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = catNameEnglish,
                        onValueChange = { catNameEnglish = it },
                        label = { Text("Category Name (English)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = catDescHindi,
                        onValueChange = { catDescHindi = it },
                        label = { Text("विवरण") },
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catNameHindi.isNotBlank()) {
                            val newCat = AyurvedicCategory(
                                id = "cat_custom_${System.currentTimeMillis() % 10000}",
                                nameHindi = catNameHindi.trim(),
                                nameEnglish = if (catNameEnglish.isNotBlank()) catNameEnglish.trim() else catNameHindi.trim(),
                                descriptionHindi = catDescHindi.trim()
                            )
                            AgriRepository.addAyurvedicCategory(newCat)
                            showAddCatDialog = false
                            catNameHindi = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddCatDialog = false }) { Text("रद्द") }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "दवा श्रेणियां (Medicine Categories)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AyurvedaHerbGreen
                    )
                    Text("श्रेणियों को जोड़ें या हटाएं", fontSize = 11.sp, color = Color.Gray)
                }

                Button(
                    onClick = { showAddCatDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("नई श्रेणी", fontSize = 12.sp)
                }
            }
        }

        categories.filter { it.id != "cat_all" }.forEach { cat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cat.nameHindi, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(cat.nameEnglish, fontSize = 11.sp, color = Color.Gray)
                        if (cat.descriptionHindi.isNotBlank()) {
                            Text(cat.descriptionHindi, fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }

                    IconButton(
                        onClick = { AgriRepository.deleteAyurvedicCategory(cat.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "हटाएं", tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
