package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
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
import com.example.data.model.AdminAccount
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AyurvedaHerbGreen
import com.example.ui.theme.AyurvedaWarmCream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isAdmin: Boolean = false,
    currentAdmin: AdminAccount? = null,
    onBackClick: () -> Unit,
    onAdminLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val businessName by AgriRepository.businessName.collectAsState()
    val businessPhone by AgriRepository.businessPhone.collectAsState()
    val businessWhatsApp by AgriRepository.businessWhatsApp.collectAsState()
    val businessAddress by AgriRepository.businessAddressHindi.collectAsState()
    val workingHours by AgriRepository.workingHoursHindi.collectAsState()

    val appLanguage by AgriRepository.appLanguage.collectAsState()
    val appTheme by AgriRepository.appThemeMode.collectAsState()
    val bookingNotifs by AgriRepository.bookingNotificationsEnabled.collectAsState()
    val promoNotifs by AgriRepository.promotionalAlertsEnabled.collectAsState()
    val adminAccounts by AgriRepository.adminAccounts.collectAsState()

    val ayurvedicStoreName by AgriRepository.ayurvedicStoreName.collectAsState()
    val ayurvedicDoctorName by AgriRepository.ayurvedicDoctorName.collectAsState()
    val ayurvedicStoreDesc by AgriRepository.ayurvedicStoreDesc.collectAsState()
    val ayurvedicShopAddress by AgriRepository.ayurvedicShopAddress.collectAsState()
    val ayurvedicShopHours by AgriRepository.ayurvedicShopHours.collectAsState()

    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var showEditBusinessDialog by remember { mutableStateOf(false) }
    var showEditAyurvedicDialog by remember { mutableStateOf(false) }

    // PIN change state
    var oldPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var pinChangeError by remember { mutableStateOf<String?>(null) }
    var pinChangeSuccess by remember { mutableStateOf(false) }

    // Business edit state
    var editName by remember(businessName) { mutableStateOf(businessName) }
    var editPhone by remember(businessPhone) { mutableStateOf(businessPhone) }
    var editWhatsApp by remember(businessWhatsApp) { mutableStateOf(businessWhatsApp) }
    var editAddress by remember(businessAddress) { mutableStateOf(businessAddress) }
    var editHours by remember(workingHours) { mutableStateOf(workingHours) }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    text = "गोपनीयता नीति (Privacy Policy)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "पप्पू ट्रैक्टर कृषि सेवा केंद्र किसानों की निजता और व्यक्तिगत डेटा की सुरक्षा के प्रति पूर्णतः वचनबद्ध है:\n\n" +
                                "1. केवल वही जानकारी सहेजी जाती है जो किसान स्वयं बुकिंग या डायरेक्टरी हेतु सहमति से प्रदान करते हैं।\n" +
                                "2. किसी भी सरकारी पहचान पत्र, वोटर लिस्ट या आधार डेटा का उपयोग नहीं किया जाता है।\n" +
                                "3. किसान का डेटा किसी तीसरे पक्ष या विज्ञापन एजेंसी को साझा या बेचा नहीं जाता।\n" +
                                "4. किसान जब चाहें अपने विवरण में संशोधन अथवा हटाने का अनुरोध कर सकते हैं।",
                        fontSize = 12.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("समझ गया (OK)")
                }
            }
        )
    }

    // Terms & Conditions Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "नियम एवं शर्तें (Terms & Conditions)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "1. मशीन बुकिंग तय तारीख और समय पर मौसम तथा क्षेत्र की उपलब्धता के अधीन है।\n" +
                                "2. किराए में प्रशिक्षित ऑपरेटर व मानक परिचालन शामिल है।\n" +
                                "3. कार्य समाप्त होने पर नकद अथवा ऑनलाइन/यूपीआई द्वारा भुगतान देय है।\n" +
                                "4. आपातकालीन मौसम या खराबी की स्थिति में ग्राहक को तुरंत सूचना दी जाएगी।",
                        fontSize = 12.sp,
                        color = Color(0xFF374151),
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }) {
                    Text("स्वीकार है")
                }
            }
        )
    }

    // Change PIN Dialog
    if (showChangePinDialog && currentAdmin != null) {
        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = {
                Text(
                    text = "सुरक्षा पिन बदलें (${currentAdmin.name})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPinInput,
                        onValueChange = { oldPinInput = it; pinChangeError = null },
                        label = { Text("पुराना पिन") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { newPinInput = it; pinChangeError = null },
                        label = { Text("नया 4-अंकीय पिन") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    if (pinChangeError != null) {
                        Text(text = pinChangeError ?: "", color = Color(0xFFD32F2F), fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPinInput.isBlank() || newPinInput.isBlank()) {
                            pinChangeError = "कृपया दोनों पिन दर्ज करें।"
                        } else {
                            val success = AgriRepository.updateAdminPin(currentAdmin.id, oldPinInput, newPinInput)
                            if (success) {
                                showChangePinDialog = false
                                oldPinInput = ""
                                newPinInput = ""
                                pinChangeSuccess = true
                            } else {
                                pinChangeError = "पुराना पिन गलत है।"
                            }
                        }
                    }
                ) {
                    Text("पिन अपडेट करें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showChangePinDialog = false }) {
                    Text("रद्द")
                }
            }
        )
    }

    // Edit Business Settings Dialog (Admin Only)
    if (showEditBusinessDialog && isAdmin) {
        AlertDialog(
            onDismissRequest = { showEditBusinessDialog = false },
            title = {
                Text(
                    text = "व्यापारिक जानकारी संपादन",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("व्यवसाय का नाम") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("हेल्पलाइन फोन नंबर") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editWhatsApp,
                        onValueChange = { editWhatsApp = it },
                        label = { Text("व्हाट्सएप नंबर (डिफ़ॉल्ट: 7388403923)") },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = editAddress,
                        onValueChange = { editAddress = it },
                        label = { Text("कार्यशाला व सेवा केंद्र पता") },
                        shape = RoundedCornerShape(8.dp),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        AgriRepository.updateBusinessInfo(editName, editPhone, editWhatsApp, editAddress, editHours)
                        showEditBusinessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary)
                ) {
                    Text("सहेजें")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditBusinessDialog = false }) {
                    Text("रद्द")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("settings_screen"),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "सेटिंग्स व नियंत्रण (Settings)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "PAPPU TRACTOR V5.0",
                            fontSize = 11.sp,
                            color = Color(0xFFC8E6C9)
                        )
                    }
                }
            }
        }

        // 1. BUSINESS SETTINGS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = AgriGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "व्यापारिक जानकारी (Business Settings)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                        }

                        if (isAdmin) {
                            TextButton(onClick = { showEditBusinessDialog = true }) {
                                Text("संपादित करें", fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = "प्रशासनिक केवल",
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    SettingDetailRow(label = "व्यवसाय का नाम:", value = businessName)
                    SettingDetailRow(label = "हेल्पलाइन फोन:", value = businessPhone)
                    SettingDetailRow(label = "व्हाट्सएप नंबर:", value = "+91 $businessWhatsApp (क्लिक-टू-चैट)")
                    SettingDetailRow(label = "सेवा केंद्र पता:", value = businessAddress)
                }
            }
        }

        // 2. AYURVEDIC STORE SETTINGS (Shop Pickup Only)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalPharmacy, contentDescription = null, tint = AyurvedaHerbGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "आयुर्वेदिक स्टोर सेटिंग्स (Ayurvedic Store)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AyurvedaHerbGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    SettingDetailRow(label = "स्टोर का नाम:", value = ayurvedicStoreName)
                    SettingDetailRow(label = "चिकित्सक / संचालक:", value = "$ayurvedicDoctorName (स्वामी के पिताजी)")
                    SettingDetailRow(label = "स्टोर का परिचय:", value = ayurvedicStoreDesc)
                    SettingDetailRow(label = "दुकान का पता:", value = ayurvedicShopAddress)
                    SettingDetailRow(label = "खुलने का समय:", value = ayurvedicShopHours)
                    SettingDetailRow(label = "ऑर्डर वितरण नियम:", value = "दुकान से संग्रह (Shop Pickup Only) - होम डिलीवरी उपलब्ध नहीं है।")

                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFFBEB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "सूचना: \"आपका ऑर्डर WhatsApp पर भेजने के बाद दुकान से पुष्टि प्राप्त करें। दवा लेने आने से पहले उपलब्धता और कीमत की पुष्टि कर लें।\"",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(8.dp),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 3. APP SETTINGS (Language, Theme, Notifications)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ऐप सेटिंग्स (App Settings)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "भाषा (Language):", fontSize = 13.sp, color = Color(0xFF1F2937))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = appLanguage == "hi",
                                onClick = { AgriRepository.updateAppSettings("hi", appTheme, bookingNotifs, promoNotifs) },
                                label = { Text("हिंदी", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = appLanguage == "en",
                                onClick = { AgriRepository.updateAppSettings("en", appTheme, bookingNotifs, promoNotifs) },
                                label = { Text("English", fontSize = 11.sp) }
                            )
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                    // Theme Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "थीम (Theme):", fontSize = 13.sp, color = Color(0xFF1F2937))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = appTheme == "light",
                                onClick = { AgriRepository.updateAppSettings(appLanguage, "light", bookingNotifs, promoNotifs) },
                                label = { Text("लाइट (Light)", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = appTheme == "dark",
                                onClick = { AgriRepository.updateAppSettings(appLanguage, "dark", bookingNotifs, promoNotifs) },
                                label = { Text("डार्क (Dark)", fontSize = 11.sp) }
                            )
                        }
                    }

                    Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                    // Notification Preferences
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "बुकिंग स्टेटस अलर्ट्स", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "मशीन रवाना होने पर सूचना", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = bookingNotifs,
                            onCheckedChange = { AgriRepository.updateAppSettings(appLanguage, appTheme, it, promoNotifs) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "सीजनल फसल व छूट अलर्ट्स", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(text = "कटाई व जुताई मौसम संदेश", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = promoNotifs,
                            onCheckedChange = { AgriRepository.updateAppSettings(appLanguage, appTheme, bookingNotifs, it) }
                        )
                    }

                    Divider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                    // Version
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ऐप संस्करण (App Version):", fontSize = 12.sp, color = Color.Gray)
                        Text(text = "V5.0 Production Ready", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                    }
                }
            }
        }

        // 3. ADMIN SETTINGS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AgriGreenPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "व्यवस्थापक प्रबंधन (Admin Settings)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenDark
                            )
                        }

                        if (!isAdmin) {
                            Button(
                                onClick = onAdminLoginClick,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("लॉगिन करें", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isAdmin && currentAdmin != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AgriGreenContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "सक्रिय व्यवस्थापक: ${currentAdmin.name}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AgriGreenDark
                                )
                                Text(
                                    text = "पद: ${currentAdmin.roleHindi}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2E4032)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showChangePinDialog = true },
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("पिन बदलें", fontSize = 12.sp)
                            }

                            Button(
                                onClick = onLogoutClick,
                                modifier = Modifier.weight(1f).height(40.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                            ) {
                                Text("लॉगआउट", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "2 अधिकृत व्यवस्थापक खाते कॉन्फ़िगर हैं:\n1. मुख्य स्वामी (पप्पू बिंद)\n2. पारिवारिक व्यवस्थापक",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // 4. OTHER SETTINGS (Legal & Support)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "अन्य सहायता व कानूनी जानकारी (Other Settings):",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreenDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SettingNavRow(title = "गोपनीयता नीति (Privacy Policy)", onClick = { showPrivacyPolicyDialog = true })
                    Divider(color = Color(0xFFEEEEEE))
                    SettingNavRow(title = "नियम एवं शर्तें (Terms & Conditions)", onClick = { showTermsDialog = true })
                    Divider(color = Color(0xFFEEEEEE))
                    SettingNavRow(title = "पप्पू ट्रैक्टर के बारे में (About Us)", onClick = onAboutClick)
                    Divider(color = Color(0xFFEEEEEE))
                    SettingNavRow(title = "ग्राहक सहायता (Contact Support)", onClick = onContactSupportClick)
                }
            }
        }
    }
}

@Composable
private fun SettingDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF6B7280))
        Text(text = value, fontSize = 12.sp, color = Color(0xFF1F2937), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingNavRow(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = Color(0xFF1F2937), fontWeight = FontWeight.Medium)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
    }
}
