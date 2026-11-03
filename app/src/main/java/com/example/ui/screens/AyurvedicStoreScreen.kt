package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AyurvedicCategory
import com.example.data.model.AyurvedicProduct
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyurvedicStoreScreen(
    cart: Map<String, Int>,
    onProductClick: (AyurvedicProduct) -> Unit,
    onAddToCart: (AyurvedicProduct) -> Unit,
    onOpenCartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val storeName by AgriRepository.ayurvedicStoreName.collectAsState()
    val doctorName by AgriRepository.ayurvedicDoctorName.collectAsState()
    val storeDesc by AgriRepository.ayurvedicStoreDesc.collectAsState()
    val shopHours by AgriRepository.ayurvedicShopHours.collectAsState()
    val shopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val categories by AgriRepository.ayurvedicCategories.collectAsState()
    val allProducts by AgriRepository.ayurvedicProducts.collectAsState()
    val businessPhone by AgriRepository.businessPhone.collectAsState()
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("cat_all") }

    val filteredProducts = remember(searchQuery, selectedCategoryId, allProducts) {
        val published = allProducts.filter { it.isPublished }
        val categoryFiltered = if (selectedCategoryId == "cat_all") published else published.filter { it.categoryId == selectedCategoryId }
        if (searchQuery.isBlank()) categoryFiltered
        else {
            val q = searchQuery.trim().lowercase()
            categoryFiltered.filter {
                it.nameHindi.lowercase().contains(q) ||
                it.nameEnglish.lowercase().contains(q) ||
                it.shortDescHindi.lowercase().contains(q) ||
                it.verifiedIngredientsHindi.lowercase().contains(q) ||
                it.manufacturer.lowercase().contains(q)
            }
        }
    }

    val totalCartCount = cart.values.sum()
    val totalCartAmount = cart.entries.sumOf { (prodId, qty) ->
        val p = allProducts.find { it.id == prodId }
        (p?.price ?: 0.0) * qty
    }

    Scaffold(
        topBar = {
            Surface(
                color = AyurvedaHerbGreen,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("ayurveda_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "पीछे जाएं",
                                tint = Color.White
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "आयुर्वेदिक स्टोर",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AyurvedaAmber
                                ) {
                                    Text(
                                        text = "दुकान से पिकअप",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = doctorName,
                                fontSize = 11.sp,
                                color = Color(0xFFD7EAD8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Contact Doctor / Shop
                    IconButton(
                        onClick = { ContactUtils.dialPhoneNumber(context, businessPhone) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("ayurveda_top_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "दुकान पर कॉल करें",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (totalCartCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onOpenCartClick,
                    containerColor = WhatsAppGreen,
                    contentColor = Color.White,
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) {
                                    Text(totalCartCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "कार्ट"
                            )
                        }
                    },
                    text = {
                        Text(
                            text = "ऑर्डर देखें (₹${totalCartAmount.toInt()})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("ayurveda_cart_fab")
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .padding(innerPadding)
                .testTag("ayurvedic_store_screen"),
            contentPadding = PaddingValues(bottom = if (totalCartCount > 0) 88.dp else 24.dp)
        ) {
            // Hero Dispensary Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ayurveda_hero_store),
                        contentDescription = "Ayurvedic Dispensary",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AyurvedaHerbGreen.copy(alpha = 0.88f),
                                        Color(0xFF1E3A1F).copy(alpha = 0.82f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFD54F)
                                ) {
                                    Text(
                                        text = "प्रामाणिक वनौषधि",
                                        color = Color(0xFF263238),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "दुकान से स्वयं प्राप्त करें",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = storeName,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = storeDesc,
                                fontSize = 12.sp,
                                color = Color(0xFFE8F5E9),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Store address and hours bar
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = shopAddress,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = shopHours,
                                    color = Color(0xFFFFF9C4),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // CRITICAL MEDICAL SAFETY DISCLAIMER (Requirement Section 7)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("medical_safety_disclaimer_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Healing,
                            contentDescription = "चिकित्सा सावधानी",
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "आयुर्वेदिक औषधालय – महत्वपूर्ण चिकित्सा सूचना:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "यह आयुर्वेदिक औषधालय है, कोई स्वचालित निदान (Diagnosis) या चमत्कारिक इलाज प्रणाली नहीं है। किसी भी रोग की स्थिति में योग्य आयुर्वेदिक चिकित्सक (वैद्य जी) से व्यक्तिगत परामर्श अवश्य लें। दवाएं केवल दुकान से स्वयं आकर प्राप्त की जा सकती हैं।",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Search Bar for Medicines
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "दवा खोजें (त्रिफला, अश्वगंधा, तेल, तुलसी...)",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "खोजें",
                                tint = AyurvedaHerbGreen
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "हटाएं",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ayurveda_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AyurvedaHerbGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            // Categories Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategoryId == category.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryId = category.id },
                            label = {
                                Text(
                                    text = category.nameHindi,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AyurvedaHerbGreen,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = Color(0xFF2E4032)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) AyurvedaHerbGreen else Color(0xFFDCE7DC)
                            )
                        )
                    }
                }
            }

            // Section Header: Available Verified Medicines
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "उपलब्ध औषधियां (${filteredProducts.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B381E)
                    )
                    Text(
                        text = "दुकान से संग्रह (Shop Pickup)",
                        fontSize = 11.sp,
                        color = Color(0xFF4A5E4F),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Empty state if search finds nothing
            if (filteredProducts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "कोई औषधि नहीं मिली",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "कृपया अन्य नाम से खोजें या दुकान पर सीधे फोन करके जानकारी लें।",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Product Cards Listing
                items(filteredProducts) { product ->
                    val quantityInCart = cart[product.id] ?: 0
                    AyurvedicProductCard(
                        product = product,
                        quantityInCart = quantityInCart,
                        onCardClick = { onProductClick(product) },
                        onAddToCart = { onAddToCart(product) }
                    )
                }
            }

            // Shop Pickup & Doctor Consultation Info Footer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F2))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = AyurvedaHerbGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "दुकान से संग्रह प्रक्रिया (Pickup Process)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. ऐप में औषधि चुनें और 'ऑर्डर देखें' पर जाएं।\n2. अपना नाम व गांव दर्ज कर WhatsApp पर ऑर्डर भेजें।\n3. दुकान से उपलब्धता की पुष्टि होने पर दुकान पहुंचकर दवा प्राप्त करें और वहीं भुगतान करें।",
                            fontSize = 11.sp,
                            color = Color(0xFF263238),
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { ContactUtils.dialPhoneNumber(context, businessPhone) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("कॉल करें", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    ContactUtils.openWhatsApp(
                                        context,
                                        whatsAppNumber,
                                        "नमस्ते वैद्य जी, मुझे आयुर्वेदिक औषधियों के विषय में जानकारी चाहिए।"
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("व्हाट्सएप", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AyurvedicProductCard(
    product: AyurvedicProduct,
    quantityInCart: Int,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onCardClick)
            .testTag("ayurvedic_product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image Thumbnail
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F5F5))
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.nameHindi,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (!product.isAvailable || product.stockQuantity <= 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "स्टॉक समाप्त",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Category & Pack size badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = product.categoryNameHindi,
                            color = AyurvedaHerbGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = product.packSize,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = product.nameHindi,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B381E),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.shortDescHindi,
                    fontSize = 11.sp,
                    color = Color(0xFF4A5E4F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price and Add to Cart Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = product.priceFormatted,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AyurvedaAmber
                        )
                        Text(
                            text = if (product.stockQuantity > 0) "उपलब्ध (${product.stockQuantity} नग)" else "उपलब्ध नहीं",
                            fontSize = 10.sp,
                            color = if (product.stockQuantity > 0) Color(0xFF2E7D32) else Color.Red
                        )
                    }

                    if (product.isAvailable && product.stockQuantity > 0) {
                        Button(
                            onClick = onAddToCart,
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("add_to_cart_btn_${product.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (quantityInCart > 0) AyurvedaHerbGreen else AgriGreenPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            if (quantityInCart > 0) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "जोड़ा गया ($quantityInCart)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "जोड़ें",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "दुकान पर पूछें",
                                color = Color(0xFFC62828),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
