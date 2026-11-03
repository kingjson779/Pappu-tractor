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
import com.example.data.model.AgriService
import com.example.data.model.AyurvedicProduct
import com.example.data.repository.AgriRepository
import com.example.ui.components.ServiceCard
import com.example.ui.components.WhatsAppCallActionRow
import com.example.ui.theme.*
import com.example.util.ContactUtils

enum class HomeSectionTab(val titleHindi: String, val subtitleHindi: String) {
    AGRICULTURE("🚜 कृषि यंत्र सेवाएं", "ट्रैक्टर • थ्रेशर • रोटावेटर • कटाई"),
    AYURVEDA("🌿 आयुर्वेदिक स्टोर", "प्रामाणिक वनौषधि • दुकान से पिकअप")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookNowClick: (AgriService?) -> Unit,
    onViewDetailsClick: (AgriService) -> Unit,
    onViewAllServicesClick: () -> Unit,
    onOpenAyurvedicStoreClick: () -> Unit = {},
    onAyurvedicProductClick: (AyurvedicProduct) -> Unit = {}
) {
    val context = LocalContext.current
    val allServices by AgriRepository.services.collectAsState()
    val ayurvedicProducts by AgriRepository.ayurvedicProducts.collectAsState()
    val whatsAppNumber by AgriRepository.businessWhatsApp.collectAsState()
    val businessPhone by AgriRepository.businessPhone.collectAsState()
    val ayurvedicDoctorName by AgriRepository.ayurvedicDoctorName.collectAsState()
    val ayurvedicShopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val ayurvedicShopHours by AgriRepository.ayurvedicShopHours.collectAsState()

    var activeSection by remember { mutableStateOf(HomeSectionTab.AGRICULTURE) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredServices = remember(searchQuery, allServices) {
        if (searchQuery.isBlank()) allServices
        else {
            val trimmed = searchQuery.trim().lowercase()
            allServices.filter {
                it.nameHindi.lowercase().contains(trimmed) ||
                it.nameEnglish.lowercase().contains(trimmed) ||
                it.shortDescHindi.lowercase().contains(trimmed) ||
                it.suitableFor.any { tag -> tag.lowercase().contains(trimmed) }
            }
        }
    }

    val filteredAyurveda = remember(searchQuery, ayurvedicProducts) {
        val published = ayurvedicProducts.filter { it.isPublished }
        if (searchQuery.isBlank()) published
        else {
            val trimmed = searchQuery.trim().lowercase()
            published.filter {
                it.nameHindi.lowercase().contains(trimmed) ||
                it.nameEnglish.lowercase().contains(trimmed) ||
                it.shortDescHindi.lowercase().contains(trimmed) ||
                it.verifiedIngredientsHindi.lowercase().contains(trimmed)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Two Major Business Section Switcher (Requirement Section 2)
        item {
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                        text = "व्यवसाय अनुभाग चुनें (Select Business Section):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HomeSectionTab.values().forEach { tab ->
                            val isSelected = activeSection == tab
                            val containerColor = if (isSelected) {
                                if (tab == HomeSectionTab.AGRICULTURE) AgriGreenPrimary else AyurvedaHerbGreen
                            } else Color(0xFFF1F8F2)
                            val textColor = if (isSelected) Color.White else Color(0xFF1B381E)

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = containerColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeSection = tab }
                                    .testTag("home_tab_${tab.name.lowercase()}"),
                                shadowElevation = if (isSelected) 2.dp else 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = tab.titleHindi,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = tab.subtitleHindi,
                                        fontSize = 10.sp,
                                        color = if (isSelected) Color(0xFFE8F5E9) else Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =====================================================================
        // SECTION A: AGRICULTURE SERVICES
        // =====================================================================
        if (activeSection == HomeSectionTab.AGRICULTURE) {
            // Hero Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_tractor_field),
                        contentDescription = "Pappu Tractor in Action",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark Green Gradient Overlay for readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AgriGreenDark.copy(alpha = 0.85f),
                                        AgriGreenPrimary.copy(alpha = 0.75f)
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
                        // Welcome & Business Name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_pappu_tractor),
                                contentDescription = "Pappu Tractor Logo",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PAPPU TRACTOR",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "किसान भाइयों का स्वागत है!",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFD54F),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Value Proposition Tagline
                        Column {
                            Text(
                                text = "आधुनिक कृषि यंत्र अब आपके खेत पर",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )
                            Text(
                                text = "ट्रैक्टर जुताई • थ्रेशर गहाई • रोटावेटर • राइस मिल • कटाई",
                                fontSize = 12.sp,
                                color = Color(0xFFE8F5E9)
                            )
                        }

                        // Direct Quick Book Now & WhatsApp CTA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onBookNowClick(null) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("hero_book_now_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AgriGoldHarvest,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "अभी बुक करें",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    ContactUtils.openWhatsApp(
                                        context,
                                        whatsAppNumber,
                                        "नमस्ते पप्पू ट्रैक्टर! मुझे कृषि मशीनरी किराए पर चाहिए।"
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("hero_whatsapp_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = WhatsAppGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "व्हाट्सएप",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Cross-Promotion Banner for Ayurvedic Store
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { onOpenAyurvedicStoreClick() }
                        .testTag("home_ayurveda_promo_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7EE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AyurvedaHerbGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalPharmacy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "पप्पू आयुर्वेदिक औषधालय (दुकान से संग्रह)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen
                            )
                            Text(
                                text = "पिताजी (वैद्य जी) द्वारा प्रामाणिक जड़ी-बूटियां एवं चूर्ण",
                                fontSize = 11.sp,
                                color = Color(0xFF2E4032)
                            )
                        }
                        TextButton(
                            onClick = onOpenAyurvedicStoreClick,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("स्टोर देखें", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AyurvedaHerbGreen)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Search Bar Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "कृषि सेवा खोजें (ट्रैक्टर, थ्रेशर, रोटावेटर, कटाई...)",
                                fontSize = 13.sp,
                                color = Color(0xFF9E9E9E)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "खोजें",
                                tint = AgriGreenPrimary
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
                            .testTag("home_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AgriGreenPrimary,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            // Quick Highlights Strip
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HighlightBadge(icon = Icons.Default.Verified, text = "उचित दर", modifier = Modifier.weight(1f))
                    HighlightBadge(icon = Icons.Default.Schedule, text = "समय पर सेवा", modifier = Modifier.weight(1f))
                    HighlightBadge(icon = Icons.Default.Engineering, text = "कुशल चालक", modifier = Modifier.weight(1f))
                }
            }

            // WhatsApp & Call Direct Action Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriGreenContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "सीधी पूछताछ व तत्काल बुकिंग:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "फोन या व्हाट्सएप पर संपर्क करें, हम तुरंत हाजिर होंगे।",
                            fontSize = 12.sp,
                            color = Color(0xFF2E4032)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        WhatsAppCallActionRow(
                            customMessage = "नमस्ते पप्पू ट्रैक्टर, मुझे तत्काल कृषि यंत्र सेवा की आवश्यकता है।"
                        )
                    }
                }
            }

            // Section Title: Featured Agricultural Services
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "प्रमुख कृषि सेवाएं (Featured Services)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                        Text(
                            text = "आधुनिक मशीनरी द्वारा उत्तम कार्य",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    TextButton(
                        onClick = onViewAllServicesClick,
                        colors = ButtonDefaults.textButtonColors(contentColor = AgriGreenPrimary)
                    ) {
                        Text(
                            text = "सभी देखें",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            // Services List
            items(filteredServices) { service ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ServiceCard(
                        service = service,
                        onViewDetailsClick = onViewDetailsClick,
                        onBookNowClick = { onBookNowClick(it) }
                    )
                }
            }
        }

        // =====================================================================
        // SECTION B: AYURVEDIC STORE PREVIEW & PRODUCTS
        // =====================================================================
        if (activeSection == HomeSectionTab.AYURVEDA) {
            // Hero Ayurvedic Dispensary Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ayurveda_hero_store),
                        contentDescription = "Ayurvedic Dispensary",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        AyurvedaHerbGreen.copy(alpha = 0.88f),
                                        Color(0xFF1B381E).copy(alpha = 0.82f)
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
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFD54F)
                            ) {
                                Text(
                                    text = "आयुर्वेदिक औषधालय • दुकान से पिकअप",
                                    color = Color(0xFF263238),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "पप्पू आयुर्वेदिक स्टोर",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "संचालक: $ayurvedicDoctorName",
                                fontSize = 12.sp,
                                color = Color(0xFFE8F5E9)
                            )
                        }

                        Button(
                            onClick = onOpenAyurvedicStoreClick,
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("संपूर्ण आयुर्वेदिक स्टोर खोलें", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Critical Medical Safety Notice
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Healing, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "सावधानी: यह आयुर्वेदिक औषधालय है। किसी भी बीमारी में योग्य चिकित्सक (वैद्य जी) से परामर्श लें। औषधियां दुकान से स्वयं आकर प्राप्त करें (Shop Pickup Only)।",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Products Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "उपलब्ध औषधियां (${filteredAyurveda.size})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B381E)
                    )
                    TextButton(onClick = onOpenAyurvedicStoreClick) {
                        Text("पूरा स्टोर देखें", fontSize = 12.sp, color = AyurvedaHerbGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Ayurvedic Products Listing
            items(filteredAyurveda) { product ->
                AyurvedicProductCard(
                    product = product,
                    quantityInCart = 0,
                    onCardClick = { onAyurvedicProductClick(product) },
                    onAddToCart = { onOpenAyurvedicStoreClick() }
                )
            }
        }
    }
}

@Composable
private fun HighlightBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AgriGreenPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AgriGreenDark
            )
        }
    }
}
