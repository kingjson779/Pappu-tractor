package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyurvedicProduct
import com.example.data.repository.AgriRepository
import com.example.ui.theme.*
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyurvedicProductDetailScreen(
    productId: String,
    quantityInCart: Int,
    onAddToCart: (AyurvedicProduct, Int) -> Unit,
    onOpenCartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val product = remember(productId) { AgriRepository.getAyurvedicProductById(productId) }
    val businessPhone by AgriRepository.businessPhone.collectAsState()
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()
    val shopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val shopHours by AgriRepository.ayurvedicShopHours.collectAsState()

    var selectedQuantity by remember { mutableStateOf(if (quantityInCart > 0) quantityInCart else 1) }

    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("उत्पाद विवरण उपलब्ध नहीं है")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBackClick) { Text("वापस जाएं") }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = product.nameHindi,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("product_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "पीछे जाएं",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenCartClick,
                        modifier = Modifier.testTag("detail_cart_action_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (quantityInCart > 0) {
                                    Badge { Text(quantityInCart.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "कार्ट देखें",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AyurvedaHerbGreen)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "कुल मूल्य:",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "₹${(product.price * selectedQuantity).toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = AyurvedaAmber
                        )
                    }

                    if (product.isAvailable && product.stockQuantity > 0) {
                        Button(
                            onClick = {
                                onAddToCart(product, selectedQuantity)
                            },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("detail_add_to_cart_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AyurvedaHerbGreen)
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "कार्ट में जोड़ें ($selectedQuantity)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                ContactUtils.dialPhoneNumber(context, businessPhone)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("स्टॉक हेतु कॉल करें")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground)
                .padding(innerPadding)
                .testTag("ayurvedic_product_detail_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Product Image Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Color(0xFFF9F7F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = product.imageRes),
                        contentDescription = product.nameHindi,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = if (product.isAvailable) "स्टॉक में उपलब्ध (${product.stockQuantity})" else "अनुपलब्ध",
                            color = if (product.isAvailable) Color(0xFF2E7D32) else Color.Red,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Title, Price & Category Card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = product.categoryNameHindi,
                                    color = AyurvedaHerbGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "पैक: ${product.packSize}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = product.nameHindi,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B381E)
                        )

                        Text(
                            text = product.nameEnglish,
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = product.priceFormatted,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AyurvedaAmber
                                )
                                Text(
                                    text = "निर्माता: ${product.manufacturer}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4A5E4F)
                                )
                            }

                            // Quantity Selector
                            if (product.isAvailable && product.stockQuantity > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F8F2),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { if (selectedQuantity > 1) selectedQuantity-- },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "मात्रा घटाएं", modifier = Modifier.size(16.dp))
                                        }

                                        Text(
                                            text = selectedQuantity.toString(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        IconButton(
                                            onClick = { if (selectedQuantity < product.stockQuantity) selectedQuantity++ },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "मात्रा बढ़ाएं", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SHOP PICKUP MANDATORY NOTICE
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5EE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = AyurvedaHerbGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "दुकान से संग्रह (Shop Pickup Only)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen
                            )
                            Text(
                                text = "औषधियां केवल दुकान पर आकर प्राप्त की जा सकती हैं। होम डिलीवरी उपलब्ध नहीं है। दुकान का पता: $shopAddress ($shopHours)",
                                fontSize = 11.sp,
                                color = Color(0xFF263238),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Verified Ingredients Section (NO INVENTED CURES)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Nature,
                                contentDescription = null,
                                tint = AyurvedaHerbGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "प्रामाणिक घटक (Verified Ingredients)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B381E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.verifiedIngredientsHindi,
                            fontSize = 12.sp,
                            color = Color(0xFF2E4032),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Description & Traditional Indications
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "विवरण एवं पारंपरिक उपयोग",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B381E)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = product.fullDescHindi,
                            fontSize = 12.sp,
                            color = Color(0xFF4A5E4F),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Medical Safety, Warning & Doctor Advice
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "सावधानी व चिकित्सक परामर्श सूचना",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• चेतावनी: ${product.applicableWarningsHindi}",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• सेवन विधि व अनुपान: ${product.dosageDisclaimerHindi}",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Compliance & Licensing Information
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "उत्पाद एवं निर्माण विवरण",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B381E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("लाइसेंस स्थिति:", fontSize = 11.sp, color = Color.Gray)
                            Text(product.licenseInfo, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("वैधता / एक्सपायरी:", fontSize = 11.sp, color = Color.Gray)
                            Text(product.expiryDuration, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
