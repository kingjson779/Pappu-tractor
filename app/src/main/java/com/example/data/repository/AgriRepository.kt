package com.example.data.repository

import com.example.R
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object AgriRepository {

    // Default business numbers (Owner: 7388403923)
    private val _businessName = MutableStateFlow("PAPPU TRACTOR (पप्पू ट्रैक्टर)")
    val businessName: StateFlow<String> = _businessName.asStateFlow()

    private val _businessPhone = MutableStateFlow("+917388403923")
    val businessPhone: StateFlow<String> = _businessPhone.asStateFlow()

    private val _businessPhoneDisplay = MutableStateFlow("+91 73884 03923")
    val businessPhoneDisplay: StateFlow<String> = _businessPhoneDisplay.asStateFlow()

    private val _businessWhatsApp = MutableStateFlow("917388403923")
    val businessWhatsApp: StateFlow<String> = _businessWhatsApp.asStateFlow()

    private val _businessEmail = MutableStateFlow("pappubind11q@gmail.com")
    val businessEmail: StateFlow<String> = _businessEmail.asStateFlow()

    private val _businessAddressHindi = MutableStateFlow(
        "पप्पू ट्रैक्टर कृषि सेवा केंद्र, किसान मंडी रोड, रामपुर - गाजीपुर, उत्तर प्रदेश (पिन: 233001)"
    )
    val businessAddressHindi: StateFlow<String> = _businessAddressHindi.asStateFlow()

    private val _workingHoursHindi = MutableStateFlow(
        "प्रातः 06:00 बजे से रात्रि 09:00 बजे तक (सप्ताह के सातों दिन)"
    )
    val workingHoursHindi: StateFlow<String> = _workingHoursHindi.asStateFlow()

    // App Preferences Settings
    private val _appLanguage = MutableStateFlow("hi") // "hi" for Hindi, "en" for English
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _appThemeMode = MutableStateFlow("light") // "light", "dark", "system"
    val appThemeMode: StateFlow<String> = _appThemeMode.asStateFlow()

    private val _bookingNotificationsEnabled = MutableStateFlow(true)
    val bookingNotificationsEnabled: StateFlow<Boolean> = _bookingNotificationsEnabled.asStateFlow()

    private val _promotionalAlertsEnabled = MutableStateFlow(false)
    val promotionalAlertsEnabled: StateFlow<Boolean> = _promotionalAlertsEnabled.asStateFlow()

    fun updateAppSettings(language: String, theme: String, bookingNotifs: Boolean, promoNotifs: Boolean) {
        _appLanguage.value = language
        _appThemeMode.value = theme
        _bookingNotificationsEnabled.value = bookingNotifs
        _promotionalAlertsEnabled.value = promoNotifs
    }

    fun updateBusinessInfo(
        name: String,
        phone: String,
        whatsApp: String,
        address: String,
        hours: String
    ) {
        if (name.isNotBlank()) _businessName.value = name.trim()
        val cleanPhone = phone.trim()
        _businessPhone.value = if (cleanPhone.startsWith("+")) cleanPhone else "+91$cleanPhone"
        _businessPhoneDisplay.value = cleanPhone
        _businessWhatsApp.value = whatsApp.replace(Regex("[^0-9]"), "")
        if (address.isNotBlank()) _businessAddressHindi.value = address.trim()
        if (hours.isNotBlank()) _workingHoursHindi.value = hours.trim()
    }

    // Two Authorized Admin Accounts
    private val _adminAccounts = MutableStateFlow<List<AdminAccount>>(
        listOf(
            AdminAccount(
                id = "admin_owner",
                name = "पप्पू बिंद (Pappu Bind)",
                roleHindi = "मुख्य स्वामी व संचालक (Owner & Chief Operator)",
                phone = "7388403923",
                pin = "7388"
            ),
            AdminAccount(
                id = "admin_family",
                name = "पारिवारिक व्यवस्थापक (Family Administrator)",
                roleHindi = "परिचालन व्यवस्थापक (Family Operations Admin)",
                phone = "9123456789",
                pin = "1234"
            )
        )
    )
    val adminAccounts: StateFlow<List<AdminAccount>> = _adminAccounts.asStateFlow()

    fun verifyAdminPin(pin: String): AdminAccount? {
        val cleanPin = pin.trim()
        return _adminAccounts.value.find { it.pin == cleanPin }
    }

    fun updateAdminPin(adminId: String, oldPin: String, newPin: String): Boolean {
        val current = _adminAccounts.value.find { it.id == adminId } ?: return false
        if (current.pin != oldPin.trim()) return false
        _adminAccounts.value = _adminAccounts.value.map {
            if (it.id == adminId) it.copy(pin = newPin.trim()) else it
        }
        return true
    }

    // Smart Customer Directory (Consent based, local to Pappu Tractor)
    private val _customers = MutableStateFlow<List<Customer>>(
        listOf(
            Customer(
                id = "CUST-101",
                name = "रामप्रसाद यादव",
                fatherName = "स्व. रामसेवक यादव",
                mobileNumber = "9876543210",
                village = "बहादुरपुर",
                fullAddress = "बहादुरपुर, प्राथमिक विद्यालय के पास, खेत संख्या 14",
                registeredDate = "15 मार्च 2026"
            ),
            Customer(
                id = "CUST-102",
                name = "शिव कुमार वर्मा",
                fatherName = "दयाराम वर्मा",
                mobileNumber = "7388403923",
                village = "कल्याणपुर",
                fullAddress = "कल्याणपुर नहर पुलिया के पास, खेत सं. 22",
                registeredDate = "22 मार्च 2026"
            ),
            Customer(
                id = "CUST-103",
                name = "सुरेश चंद्र बिंद",
                fatherName = "केदारनाथ बिंद",
                mobileNumber = "9450012345",
                village = "जंगीपुर",
                fullAddress = "जंगीपुर मेन रोड, बरगद के पेड़ के सामने",
                registeredDate = "01 अप्रैल 2026"
            ),
            Customer(
                id = "CUST-104",
                name = "दिनेश मौर्य",
                fatherName = "छोटेलाल मौर्य",
                mobileNumber = "9123456789",
                village = "सैदपुर",
                fullAddress = "सैदपुर पूर्वी टोला, नलकूप के पास",
                registeredDate = "10 अप्रैल 2026"
            ),
            Customer(
                id = "CUST-105",
                name = "महेंद्र प्रताप सिंह",
                fatherName = "ठाकुर भानुप्रताप सिंह",
                mobileNumber = "9839001122",
                village = "रामपुर",
                fullAddress = "रामपुर पुरानी बस्ती, मंदिर के पीछे",
                registeredDate = "18 अप्रैल 2026"
            )
        )
    )
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    fun searchCustomers(query: String): List<Customer> {
        if (query.isBlank()) return _customers.value
        val q = query.trim().lowercase()
        return _customers.value.filter {
            it.name.lowercase().contains(q) ||
            it.fatherName.lowercase().contains(q) ||
            it.mobileNumber.contains(q) ||
            it.village.lowercase().contains(q)
        }
    }

    fun registerCustomer(
        name: String,
        fatherName: String,
        mobileNumber: String,
        village: String,
        fullAddress: String
    ): Customer {
        val existing = _customers.value.find { it.mobileNumber == mobileNumber.trim() }
        if (existing != null) {
            // Update existing record
            val updated = existing.copy(
                name = name.trim(),
                fatherName = fatherName.trim(),
                village = village.trim(),
                fullAddress = fullAddress.trim()
            )
            _customers.value = _customers.value.map { if (it.id == existing.id) updated else it }
            return updated
        }

        val newCustomer = Customer(
            id = "CUST-${Random.nextInt(106, 999)}",
            name = name.trim(),
            fatherName = fatherName.trim(),
            mobileNumber = mobileNumber.trim(),
            village = village.trim(),
            fullAddress = fullAddress.trim(),
            registeredDate = "आज"
        )
        _customers.value = listOf(newCustomer) + _customers.value
        return newCustomer
    }

    fun updateCustomer(
        id: String,
        name: String,
        fatherName: String,
        mobileNumber: String,
        village: String,
        fullAddress: String
    ) {
        _customers.value = _customers.value.map {
            if (it.id == id) {
                it.copy(
                    name = name.trim(),
                    fatherName = fatherName.trim(),
                    mobileNumber = mobileNumber.trim(),
                    village = village.trim(),
                    fullAddress = fullAddress.trim()
                )
            } else it
        }
    }

    fun deleteCustomer(id: String) {
        _customers.value = _customers.value.filter { it.id != id }
    }

    // Editable Services Catalog
    private val _services = MutableStateFlow<List<AgriService>>(
        listOf(
            AgriService(
                id = "new_holland_3032",
                nameHindi = "न्यू हॉलैंड 3032 ट्रैक्टर",
                nameEnglish = "New Holland 3032 Tractor",
                shortDescHindi = "35 HP शक्तिशाली इंजन, 3-सिलेंडर, सभी कृषि कार्यों व ढुलाई के लिए सर्वश्रेष्ठ।",
                fullDescHindi = "न्यू हॉलैंड 3032 ट्रैक्टर अत्यधिक टिकाऊ और ईंधन की बचत करने वाला विश्वसनीय ट्रैक्टर है। यह खेत की गहरी जुताई, कल्टीवेटर, रोटावेटर चलाने तथा ट्रॉली ढुलाई के लिए सबसे उपयुक्त है। हमारे प्रशिक्षित और अनुभवी चालक द्वारा समय पर कार्य सम्पन्न किया जाता है।",
                priceRateHindi = "₹600 / घंटा",
                priceUnit = "₹1,200 / बीघा से शुरू (डीजल व चालक सहित)",
                imageRes = R.drawable.hero_tractor_field,
                galleryImages = listOf(
                    R.drawable.hero_tractor_field,
                    R.drawable.machine_rotavator,
                    R.drawable.ic_pappu_tractor
                ),
                horsepower = "35 HP (पावर स्टीयरिंग)",
                workDetails = listOf(
                    "इंजन व क्षमता" to "3 सिलेंडर, 35 हॉर्सपावर, उच्च टॉर्क",
                    "कार्य गति" to "1 बीघा जुताई लगभग 30-40 मिनट में",
                    "डीजल खपत" to "किराए में डीजल और ड्राइवर का खर्च शामिल",
                    "संबद्ध यंत्र" to "कल्टीवेटर, 9-टाइन हैरो, लेवलर पाटा, हेवी ट्रॉली",
                    "ऑपरेटर व्यवस्था" to "10+ वर्ष अनुभवी स्थानीय ट्रैक्टर चालक"
                ),
                suitableFor = listOf("गहरी जुताई", "कल्टीवेटर", "ट्रॉली ढुलाई", "लेवलर"),
                highlights = listOf("कम डीजल खपत", "अनुभवी ड्राइवर", "समय पर खेत में उपस्थिति", "मजबूत हाइड्रोलिक्स"),
                badge = "सबसे लोकप्रिय",
                isAvailable = true
            ),
            AgriService(
                id = "deluxe_thresher_50hp",
                nameHindi = "डीलक्स थ्रेशर 50 HP",
                nameEnglish = "Deluxe Thresher 50 HP",
                shortDescHindi = "गेहूं, सरसों, चना व बाजरा की सुपरफास्ट दाना निकासी और बारीक भूसा निर्माण।",
                fullDescHindi = "डीलक्स 50 HP मल्टीक्रॉप थ्रेशर बिना दाना तोड़े 100% साफ अनाज अलग करता है। यह मशीन पशुओं के लिए बेहतरीन मुलायम भूसा बनाती है। बड़े खेतों और भारी पैदावार के लिए सबसे भरोसेमंद मशीन।",
                priceRateHindi = "₹800 / घंटा",
                priceUnit = "प्रति घंटा / प्रति ट्रॉली दर",
                imageRes = R.drawable.machine_thresher,
                galleryImages = listOf(
                    R.drawable.machine_thresher,
                    R.drawable.hero_tractor_field,
                    R.drawable.machine_harvester
                ),
                horsepower = "50 HP क्षमता",
                workDetails = listOf(
                    "उपयुक्त फसलें" to "गेहूं, सरसों, चना, बाजरा, सोयाबीन",
                    "निकासी क्षमता" to "15-20 क्विंटल प्रति घंटा स्वच्छ अनाज",
                    "भूसा गुणवत्ता" to "डबल ब्लोअर द्वारा बारीक व मुलायम भूसा",
                    "सुरक्षा उपकरण" to "रिवर्स गियर सुरक्षा प्रणाली व आपातकालीन कटऑफ",
                    "सहायक दल" to "मशीन के साथ अनुभवी ऑपरेटर व सहायक"
                ),
                suitableFor = listOf("गेहूं", "सरसों", "चना", "सोयाबीन"),
                highlights = listOf("साफ सुथरा अनाज", "बारीक मुलायम भूसा", "दाना नहीं टूटता", "डबल ब्लोअर फैन"),
                badge = "तेज गहाई",
                isAvailable = true
            ),
            AgriService(
                id = "nipha_rotavator",
                nameHindi = "निफा रोटावेटर",
                nameEnglish = "Nipha Rotavator",
                shortDescHindi = "खेत की एक ही बार में गहरी जुताई व मिट्टी को बारीक भुरभुरा बनाने वाला रोटावेटर।",
                fullDescHindi = "निफा रोटावेटर उच्च कोटि के बोरोन स्टील एल-ब्लेड्स से सुसज्जित है। यह पुरानी फसल के ठूंठ और खरपतवार को मिट्टी में दबाकर जैविक खाद बनाता है तथा एक ही चक्कर में खेत को बुवाई के लिए तैयार करता है।",
                priceRateHindi = "₹750 / बीघा",
                priceUnit = "प्रति बीघा दर (समतल खेत)",
                imageRes = R.drawable.machine_rotavator,
                galleryImages = listOf(
                    R.drawable.machine_rotavator,
                    R.drawable.hero_tractor_field,
                    R.drawable.ic_pappu_tractor
                ),
                horsepower = "हेवी ड्यूटी मल्टीस्पीड गियरबॉक्स",
                workDetails = listOf(
                    "ब्लेड प्रकार" to "48 बोरोन स्टील एल-आकार के कठोर ब्लेड",
                    "कार्य गहराई" to "6 से 8 इंच गहरी बारीक मिट्टी तैयारी",
                    "दक्षता" to "एक ही राउंड में खेत बुवाई हेतु उत्तम तैयार",
                    "खरपतवार नियंत्रण" to "जड़ों को काटकर मिट्टी में दबाने की क्षमता"
                ),
                suitableFor = listOf("खेत तैयारी", "मिट्टी भुरभुरी करना", "फसल अवशेष मिलाना"),
                highlights = listOf("बोरोन स्टील ब्लेड", "एक चक्कर में खेत तैयार", "नमी बरकरार", "तेल सील गियरबॉक्स"),
                badge = "बेहतरीन जुताई",
                isAvailable = true
            ),
            AgriService(
                id = "small_rice_mill",
                nameHindi = "स्मॉल राइस मिल (धान कुटाई)",
                nameEnglish = "Small Rice Mill",
                shortDescHindi = "धान से बिना टूटे चमकदार सफेद चावल निकालने वाली आधुनिक मिनी राइस मिल।",
                fullDescHindi = "हमारी आधुनिक स्मॉल राइस मिल धान की कुटाई करके उच्च गुणवत्ता का चावल और बढ़िया पॉलिश प्रदान करती है। इसमें भूसी और चावल पूरी तरह अलग होते हैं तथा किसान भाइयों को खेत या घर के पास ही कुटाई की सुविधा मिलती है।",
                priceRateHindi = "₹150 / क्विंटल",
                priceUnit = "प्रति क्विंटल कुटाई दर",
                imageRes = R.drawable.machine_rice_mill,
                galleryImages = listOf(
                    R.drawable.machine_rice_mill,
                    R.drawable.hero_tractor_field
                ),
                horsepower = "आधुनिक इलेक्ट्रिक व डीजल मोटर",
                workDetails = listOf(
                    "कुटाई क्षमता" to "4 से 6 क्विंटल धान प्रति घंटा",
                    "चावल रिकवरी" to "68% से अधिक साबुत दाना रिकवरी",
                    "पॉलिशर" to "डबल जेट कोल्ड पॉलिशर तकनीक",
                    "स्थान" to "खेत अथवा घर के दरवाजे पर ले जाने योग्य"
                ),
                suitableFor = listOf("बासमती व मोटा धान", "चावल पॉलिशिंग", "भूसी निष्कासन"),
                highlights = listOf("चावल की कम टूट-फूट", "चमकदार पॉलिश", "घर/गांव पर सुविधा", "स्वच्छ दाना"),
                badge = "घर पहुंच सेवा",
                isAvailable = true
            ),
            AgriService(
                id = "wheat_cutting_machine",
                nameHindi = "गेहूं कटाई मशीन (रीपर / कटर)",
                nameEnglish = "Wheat Cutting Machine",
                shortDescHindi = "गेहूं फसल की जमीन के पास से तेज व साफ कटाई, मजदूरी और समय की 80% बचत।",
                fullDescHindi = "गेहूं कटाई मशीन (रीपर) पकी हुई गेहूं, जौ व जई की फसल को बिल्कुल जमीन की सतह से काटकर कतार में बिछाती है, जिससे भूसा भी पूरा मिलता है और दाने का नुकसान भी नहीं होता। कटाई के मौसम में तत्काल बुकिंग उपलब्ध।",
                priceRateHindi = "₹900 / बीघा",
                priceUnit = "प्रति बीघा कटाई दर",
                imageRes = R.drawable.machine_harvester,
                galleryImages = listOf(
                    R.drawable.machine_harvester,
                    R.drawable.hero_tractor_field
                ),
                horsepower = "हाई-स्पीड 7 फीट कटर बार",
                workDetails = listOf(
                    "कटाई ऊंचाई" to "जमीन से मात्र 1.5 से 2 इंच ऊपर से कटाई",
                    "कटाई गति" to "1 बीघा केवल 20 से 25 मिनट में",
                    "कन्वेयर बेल्ट" to "फसल को एक समान कतार में बिछाने की व्यवस्था",
                    "फसल सुरक्षा" to "शून्य दाना नुकसान और पूरा भूसा सुरक्षित"
                ),
                suitableFor = listOf("गेहूं कटाई", "जौ कटाई", "जई कटाई"),
                highlights = listOf("जमीन से सटाकर कटाई", "शून्य दाना नुकसान", "1 बीघा केवल 20 मिनट में", "मजदूरी की बचत"),
                badge = "सीजनल स्पेशल",
                isAvailable = true
            )
        )
    )
    val services: StateFlow<List<AgriService>> = _services.asStateFlow()

    fun updateServicePrice(id: String, newPrice: String, newUnit: String) {
        _services.value = _services.value.map {
            if (it.id == id) {
                it.copy(priceRateHindi = newPrice.trim(), priceUnit = newUnit.trim())
            } else it
        }
    }

    fun updateServiceDetails(
        id: String,
        nameHindi: String,
        nameEnglish: String,
        descHindi: String,
        priceRate: String,
        priceUnit: String,
        horsepower: String,
        isAvailable: Boolean
    ) {
        _services.value = _services.value.map {
            if (it.id == id) {
                it.copy(
                    nameHindi = nameHindi.trim(),
                    nameEnglish = nameEnglish.trim(),
                    shortDescHindi = descHindi.trim(),
                    fullDescHindi = descHindi.trim(),
                    priceRateHindi = priceRate.trim(),
                    priceUnit = priceUnit.trim(),
                    horsepower = horsepower.trim(),
                    isAvailable = isAvailable
                )
            } else it
        }
    }

    fun addNewService(
        nameHindi: String,
        nameEnglish: String,
        descHindi: String,
        priceRate: String,
        priceUnit: String,
        horsepower: String
    ): AgriService {
        val newId = "service_${System.currentTimeMillis()}"
        val newService = AgriService(
            id = newId,
            nameHindi = nameHindi.trim(),
            nameEnglish = nameEnglish.trim(),
            shortDescHindi = descHindi.trim(),
            fullDescHindi = descHindi.trim(),
            priceRateHindi = priceRate.trim(),
            priceUnit = priceUnit.trim(),
            imageRes = R.drawable.hero_tractor_field,
            galleryImages = listOf(R.drawable.hero_tractor_field),
            horsepower = horsepower.trim(),
            workDetails = listOf("क्षमता" to horsepower.trim()),
            suitableFor = listOf("सामान्य कृषि कार्य"),
            highlights = listOf("आधुनिक तकनीक", "कुशल चालक"),
            badge = "नया यंत्र",
            isAvailable = true
        )
        _services.value = _services.value + newService
        return newService
    }

    fun toggleServiceAvailability(id: String) {
        _services.value = _services.value.map {
            if (it.id == id) {
                it.copy(isAvailable = !it.isAvailable)
            } else it
        }
    }

    // Pre-seeded Demo Bookings
    private val _bookings = MutableStateFlow<List<Booking>>(
        listOf(
            Booking(
                bookingId = "PT-8024",
                customerName = "रामप्रसाद यादव",
                fatherName = "स्व. रामसेवक यादव",
                mobileNumber = "9876543210",
                village = "बहादुरपुर",
                fullAddress = "बहादुरपुर, प्राथमिक विद्यालय के पास, खेत संख्या 14",
                serviceId = "new_holland_3032",
                serviceNameHindi = "न्यू हॉलैंड 3032 ट्रैक्टर",
                landArea = "4",
                landUnit = "बीघा",
                bookingDate = "कल सुबह",
                preferredTime = "07:00 AM (प्रातः)",
                additionalNotes = "रोटावेटर से 2 चक्कर जुताई करवानी है।",
                status = BookingStatus.CONFIRMED,
                paymentStatus = PaymentStatus.ADVANCE_PAID,
                paymentMethod = PaymentMethod.ONLINE,
                estimatedAmount = "₹2,400",
                paidAmount = "₹500",
                paymentDate = "कल शाम 06:30 PM",
                adminNotes = "चालक रमेश को नियुक्त किया गया।",
                createdAtFormatted = "कल"
            ),
            Booking(
                bookingId = "PT-7910",
                customerName = "शिव कुमार वर्मा",
                fatherName = "दयाराम वर्मा",
                mobileNumber = "7388403923",
                village = "कल्याणपुर",
                fullAddress = "कल्याणपुर नहर पुलिया के पास, खेत सं. 22",
                serviceId = "deluxe_thresher_50hp",
                serviceNameHindi = "डीलक्स थ्रेशर 50 HP",
                landArea = "25",
                landUnit = "क्विंटल",
                bookingDate = "आज",
                preferredTime = "11:00 AM",
                additionalNotes = "थ्रेशर के साथ 2 सहायक भी चाहिए।",
                status = BookingStatus.IN_PROGRESS,
                paymentStatus = PaymentStatus.UNPAID,
                paymentMethod = PaymentMethod.CASH,
                estimatedAmount = "₹3,200",
                paidAmount = "₹0",
                adminNotes = "मशीन खेत की ओर रवाना हो चुकी है।",
                createdAtFormatted = "आज प्रातः"
            ),
            Booking(
                bookingId = "PT-6582",
                customerName = "सुरेश चंद्र बिंद",
                fatherName = "केदारनाथ बिंद",
                mobileNumber = "9450012345",
                village = "जंगीपुर",
                fullAddress = "जंगीपुर मेन रोड, बरगद के पेड़ के सामने",
                serviceId = "wheat_cutting_machine",
                serviceNameHindi = "गेहूं कटाई मशीन (रीपर / कटर)",
                landArea = "6",
                landUnit = "बीघा",
                bookingDate = "2 दिन पहले",
                preferredTime = "08:00 AM",
                additionalNotes = "काम पूरा हुआ।",
                status = BookingStatus.COMPLETED,
                paymentStatus = PaymentStatus.FULLY_PAID,
                paymentMethod = PaymentMethod.CASH,
                estimatedAmount = "₹5,400",
                paidAmount = "₹5,400",
                paymentDate = "2 दिन पहले",
                adminNotes = "संतुष्ट ग्राहक, पूरा भुगतान प्राप्त।",
                createdAtFormatted = "3 दिन पहले"
            ),
            Booking(
                bookingId = "PT-9140",
                customerName = "दिनेश मौर्य",
                fatherName = "छोटेलाल मौर्य",
                mobileNumber = "9123456789",
                village = "सैदपुर",
                fullAddress = "सैदपुर पूर्वी टोला, नलकूप के पास",
                serviceId = "nipha_rotavator",
                serviceNameHindi = "निफा रोटावेटर",
                landArea = "3",
                landUnit = "बीघा",
                bookingDate = "परसों",
                preferredTime = "09:00 AM",
                additionalNotes = "खेत में नमी कम है, गहरी जुताई करनी है।",
                status = BookingStatus.PENDING,
                paymentStatus = PaymentStatus.UNPAID,
                paymentMethod = PaymentMethod.NOT_SELECTED,
                estimatedAmount = "₹2,250",
                paidAmount = "₹0",
                adminNotes = "सत्यापन बाकी है।",
                createdAtFormatted = "आज दोपहर"
            ),
            Booking(
                bookingId = "PT-5421",
                customerName = "महेंद्र प्रताप सिंह",
                fatherName = "ठाकुर भानुप्रताप सिंह",
                mobileNumber = "9839001122",
                village = "रामपुर",
                fullAddress = "रामपुर पुरानी बस्ती",
                serviceId = "small_rice_mill",
                serviceNameHindi = "स्मॉल राइस मिल (धान कुटाई)",
                landArea = "12",
                landUnit = "क्विंटल",
                bookingDate = "गत सप्ताह",
                preferredTime = "02:00 PM",
                additionalNotes = "समय पर बारिश के कारण रद्द किया गया।",
                status = BookingStatus.CANCELLED,
                paymentStatus = PaymentStatus.UNPAID,
                paymentMethod = PaymentMethod.NOT_SELECTED,
                estimatedAmount = "₹1,800",
                paidAmount = "₹0",
                adminNotes = "मौसम खराब होने के कारण ग्राहक के अनुरोध पर रद्द।",
                createdAtFormatted = "गत सप्ताह"
            )
        )
    )
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    fun getServiceById(id: String): AgriService? {
        return _services.value.find { it.id == id }
    }

    fun searchServices(query: String): List<AgriService> {
        if (query.isBlank()) return _services.value
        val trimmed = query.trim().lowercase()
        return _services.value.filter {
            it.nameHindi.lowercase().contains(trimmed) ||
            it.nameEnglish.lowercase().contains(trimmed) ||
            it.shortDescHindi.lowercase().contains(trimmed) ||
            it.suitableFor.any { tag -> tag.lowercase().contains(trimmed) }
        }
    }

    fun isDuplicateBooking(name: String, mobile: String, serviceId: String, date: String): Boolean {
        val cleanMobile = mobile.trim()
        val cleanName = name.trim().lowercase()
        val cleanDate = date.trim().lowercase()
        return _bookings.value.any {
            it.mobileNumber == cleanMobile &&
            it.customerName.lowercase() == cleanName &&
            it.serviceId == serviceId &&
            it.bookingDate.lowercase() == cleanDate &&
            it.status != BookingStatus.CANCELLED &&
            it.status != BookingStatus.REJECTED
        }
    }

    fun createBooking(
        customerName: String,
        fatherName: String = "",
        mobileNumber: String,
        village: String,
        fullAddress: String,
        serviceId: String,
        landArea: String,
        landUnit: String,
        bookingDate: String,
        preferredTime: String,
        additionalNotes: String
    ): Booking {
        val service = getServiceById(serviceId)
        val serviceName = service?.nameHindi ?: "कृषि यंत्र सेवा"
        val randomNum = Random.nextInt(1000, 9999)
        val generatedId = "PT-$randomNum"

        val newBooking = Booking(
            bookingId = generatedId,
            customerName = customerName.trim(),
            fatherName = fatherName.trim(),
            mobileNumber = mobileNumber.trim(),
            village = village.trim(),
            fullAddress = fullAddress.trim(),
            serviceId = serviceId,
            serviceNameHindi = serviceName,
            landArea = landArea.trim(),
            landUnit = landUnit.trim(),
            bookingDate = bookingDate.trim(),
            preferredTime = preferredTime.trim(),
            additionalNotes = additionalNotes.trim(),
            status = BookingStatus.PENDING,
            paymentStatus = PaymentStatus.UNPAID,
            paymentMethod = PaymentMethod.NOT_SELECTED,
            estimatedAmount = service?.priceRateHindi ?: "दर अनुसार",
            paidAmount = "₹0",
            paymentDate = "",
            adminNotes = "व्हाट्सएप बुकिंग प्रणाली द्वारा दर्ज",
            createdAtFormatted = "अभी (आज)"
        )

        _bookings.value = listOf(newBooking) + _bookings.value

        // Also add or update customer directory with consent
        registerCustomer(customerName, fatherName, mobileNumber, village, fullAddress)

        return newBooking
    }

    fun updateBookingStatus(bookingId: String, newStatus: BookingStatus, adminNote: String = "") {
        _bookings.value = _bookings.value.map {
            if (it.bookingId.equals(bookingId, ignoreCase = true)) {
                it.copy(
                    status = newStatus,
                    adminNotes = if (adminNote.isNotBlank()) adminNote else it.adminNotes
                )
            } else it
        }
    }

    fun updateBookingPayment(
        bookingId: String,
        paymentStatus: PaymentStatus,
        paymentMethod: PaymentMethod,
        estimatedAmount: String = "",
        paidAmount: String = "",
        paymentDate: String = ""
    ) {
        _bookings.value = _bookings.value.map {
            if (it.bookingId.equals(bookingId, ignoreCase = true)) {
                it.copy(
                    paymentStatus = paymentStatus,
                    paymentMethod = paymentMethod,
                    estimatedAmount = if (estimatedAmount.isNotBlank()) estimatedAmount else it.estimatedAmount,
                    paidAmount = if (paidAmount.isNotBlank()) paidAmount else it.paidAmount,
                    paymentDate = if (paymentDate.isNotBlank()) paymentDate else it.paymentDate
                )
            } else it
        }
    }

    fun findBooking(bookingIdOrMobile: String): Booking? {
        val query = bookingIdOrMobile.trim().uppercase()
        if (query.isBlank()) return null
        return _bookings.value.find {
            it.bookingId.uppercase() == query ||
            it.mobileNumber == query ||
            (query.length >= 4 && it.bookingId.endsWith(query))
        }
    }

    // Customer Reviews
    private val _reviews = MutableStateFlow<List<CustomerReview>>(
        listOf(
            CustomerReview(
                id = "REV-101",
                customerName = "रामप्रसाद यादव",
                village = "बहादुरपुर",
                rating = 5,
                serviceId = "new_holland_3032",
                serviceNameHindi = "न्यू हॉलैंड 3032 ट्रैक्टर",
                commentHindi = "पप्पू ट्रैक्टर की सेवा बहुत शानदार है। समय पर खेत पर पहुंच गया। जुताई बहुत गहरी और साफ हुई।",
                date = "3 दिन पहले",
                moderationStatus = ReviewModerationStatus.APPROVED
            ),
            CustomerReview(
                id = "REV-102",
                customerName = "सुरेश चंद्र बिंद",
                village = "जंगीपुर",
                rating = 5,
                serviceId = "wheat_cutting_machine",
                serviceNameHindi = "गेहूं कटाई मशीन (रीपर)",
                commentHindi = "गेहूं कटाई मशीन से 6 बीघा खेत सिर्फ 2 घंटे में कट गया! जमीन से सटकर कटाई हुई जिससे भूसा भी भरपूर मिला।",
                date = "1 सप्ताह पहले",
                moderationStatus = ReviewModerationStatus.APPROVED
            ),
            CustomerReview(
                id = "REV-103",
                customerName = "महेंद्र सिंह",
                village = "कल्याणपुर",
                rating = 4,
                serviceId = "deluxe_thresher_50hp",
                serviceNameHindi = "डीलक्स थ्रेशर 50 HP",
                commentHindi = "थ्रेशर से गेहूं बिल्कुल साफ निकला, एक भी दाना टूटा नहीं। भूसा भी बहुत मुलायम बना।",
                date = "2 सप्ताह पहले",
                moderationStatus = ReviewModerationStatus.APPROVED
            )
        )
    )
    val reviews: StateFlow<List<CustomerReview>> = _reviews.asStateFlow()

    fun submitReview(
        customerName: String,
        village: String,
        rating: Int,
        serviceId: String,
        commentHindi: String
    ): CustomerReview {
        val service = getServiceById(serviceId)
        val serviceName = service?.nameHindi ?: "कृषि यंत्र"
        val review = CustomerReview(
            id = "REV-${Random.nextInt(200, 999)}",
            customerName = customerName.trim(),
            village = village.trim(),
            rating = rating.coerceIn(1, 5),
            serviceId = serviceId,
            serviceNameHindi = serviceName,
            commentHindi = commentHindi.trim(),
            date = "आज",
            moderationStatus = ReviewModerationStatus.APPROVED
        )
        _reviews.value = listOf(review) + _reviews.value
        return review
    }

    // =========================================================================
    // SECTION B: AYURVEDIC STORE (आयुर्वेदिक औषधालय / स्टोर)
    // Managed by business owner's father (Ayurvedic Doctor / वैद्य जी)
    // STRICT MEDICAL SAFETY & SHOP PICKUP ONLY
    // =========================================================================

    private val _ayurvedicStoreName = MutableStateFlow("पप्पू आयुर्वेदिक औषधालय (Pappu Ayurvedic Store)")
    val ayurvedicStoreName: StateFlow<String> = _ayurvedicStoreName.asStateFlow()

    private val _ayurvedicDoctorName = MutableStateFlow("वैद्य जी / डॉ. बिंद (वरिष्ठ आयुर्वेदिक चिकित्सक)")
    val ayurvedicDoctorName: StateFlow<String> = _ayurvedicDoctorName.asStateFlow()

    private val _ayurvedicStoreDesc = MutableStateFlow("शुद्ध व प्रामाणिक जड़ी-बूटियों एवं शास्त्रीय योगों से निर्मित औषधियां। दुकान से स्वयं आकर प्राप्त करें (Shop Pickup Only)।")
    val ayurvedicStoreDesc: StateFlow<String> = _ayurvedicStoreDesc.asStateFlow()

    private val _ayurvedicShopAddress = MutableStateFlow("पप्पू ट्रैक्टर एवं आयुर्वेदिक स्टोर, मुख्य बाजार चौराहा, गाजीपुर-वाराणसी मार्ग")
    val ayurvedicShopAddress: StateFlow<String> = _ayurvedicShopAddress.asStateFlow()

    private val _ayurvedicShopHours = MutableStateFlow("प्रातः 08:00 AM से सायं 07:30 PM (सोमवार - रविवार)")
    val ayurvedicShopHours: StateFlow<String> = _ayurvedicShopHours.asStateFlow()

    fun updateAyurvedicStoreSettings(name: String, doctor: String, desc: String, address: String, hours: String) {
        if (name.isNotBlank()) _ayurvedicStoreName.value = name.trim()
        if (doctor.isNotBlank()) _ayurvedicDoctorName.value = doctor.trim()
        if (desc.isNotBlank()) _ayurvedicStoreDesc.value = desc.trim()
        if (address.isNotBlank()) _ayurvedicShopAddress.value = address.trim()
        if (hours.isNotBlank()) _ayurvedicShopHours.value = hours.trim()
    }

    // Categories (Admin can add/edit/remove)
    private val _ayurvedicCategories = MutableStateFlow<List<AyurvedicCategory>>(
        listOf(
            AyurvedicCategory(
                id = "cat_all",
                nameHindi = "सभी औषधियां",
                nameEnglish = "All Medicines",
                descriptionHindi = "समस्त उपलब्ध आयुर्वेदिक औषधियां"
            ),
            AyurvedicCategory(
                id = "cat_herbal",
                nameHindi = "हर्बल एवं चूर्ण",
                nameEnglish = "Herbal & Churna",
                descriptionHindi = "शुद्ध प्राकृतिक जड़ी-बूटियों से निर्मित पारंपरिक चूर्ण व भस्म"
            ),
            AyurvedicCategory(
                id = "cat_classical",
                nameHindi = "शास्त्रीय तेल व आसव",
                nameEnglish = "Classical Oils & Asava",
                descriptionHindi = "प्राचीन संहिताओं के अनुसार सिद्ध वातनाशक तेल व काढ़े"
            ),
            AyurvedicCategory(
                id = "cat_immunity",
                nameHindi = "रोग प्रतिरोधक व पोषण",
                nameEnglish = "Immunity & Nutrition",
                descriptionHindi = "शरीर की जीवनी शक्ति एवं पोषण संवर्धन योग"
            ),
            AyurvedicCategory(
                id = "cat_personal_care",
                nameHindi = "हर्बल पर्सनल केयर",
                nameEnglish = "Herbal Personal Care",
                descriptionHindi = "त्वचा एवं केश की शुद्धि हेतु प्राकृतिक उत्पाद"
            )
        )
    )
    val ayurvedicCategories: StateFlow<List<AyurvedicCategory>> = _ayurvedicCategories.asStateFlow()

    fun addAyurvedicCategory(category: AyurvedicCategory) {
        _ayurvedicCategories.value = _ayurvedicCategories.value + category
    }

    fun updateAyurvedicCategory(category: AyurvedicCategory) {
        _ayurvedicCategories.value = _ayurvedicCategories.value.map {
            if (it.id == category.id) category else it
        }
    }

    fun deleteAyurvedicCategory(categoryId: String) {
        _ayurvedicCategories.value = _ayurvedicCategories.value.filter { it.id != categoryId }
    }

    // Selected Verified Products added by Admin
    private val _ayurvedicProducts = MutableStateFlow<List<AyurvedicProduct>>(
        listOf(
            AyurvedicProduct(
                id = "ayur_triphala",
                nameHindi = "त्रिफला चूर्ण (Triphala Churna)",
                nameEnglish = "Triphala Churna",
                categoryId = "cat_herbal",
                categoryNameHindi = "हर्बल एवं चूर्ण",
                manufacturer = "प्रामाणिक आयुर्वेदिक फार्मेसी",
                shortDescHindi = "आंवला, हरड़ और बहेड़ा का शुद्ध योग। पाचन व उदर शुद्धि हेतु उत्तम।",
                fullDescHindi = "त्रिफला चूर्ण त्रिदोष (वात, पित्त, कफ) शामक है। यह पारंपरिक रूप से पाचन विकार, कब्ज एवं नेत्र स्वास्थ्य को बनाए रखने के लिए प्रयोग किया जाता है। किसी भी रोग की स्थिति में चिकित्सक परामर्श अवश्य लें।",
                verifiedIngredientsHindi = "आमलकी (Emblica officinalis), हरीतकी (Terminalia chebula), विभीतकी (Terminalia bellirica) समान अनुपात में",
                packSize = "100 ग्राम",
                price = 65.0,
                priceFormatted = "₹65",
                stockQuantity = 25,
                isAvailable = true,
                isPublished = true,
                imageRes = R.drawable.ayurveda_churna,
                applicableWarningsHindi = "गर्भावस्था में बिना चिकित्सक सलाह सेवन न करें।",
                dosageDisclaimerHindi = "मात्रा व अनुपान हेतु योग्य आयुर्वेदिक चिकित्सक से परामर्श लें।"
            ),
            AyurvedicProduct(
                id = "ayur_ashwagandha",
                nameHindi = "अश्वगंधा चूर्ण (Ashwagandha Churna)",
                nameEnglish = "Ashwagandha Churna",
                categoryId = "cat_herbal",
                categoryNameHindi = "हर्बल एवं चूर्ण",
                manufacturer = "शुद्ध वनौषधि केंद्र",
                shortDescHindi = "शारीरिक बल, ऊर्जा एवं तनाव मुक्ति हेतु शुद्ध अश्वगंधा जड़ का चूर्ण।",
                fullDescHindi = "अश्वगंधा एक रसायन वनौषधि है जो शरीर की सामान्य दुर्बलता, थकान एवं मानसिक तनाव को कम करने में पारंपरिक रूप से सहायक मानी जाती है।",
                verifiedIngredientsHindi = "अश्वगंधा मूल चूर्ण (Withania somnifera) 100% शुद्ध",
                packSize = "100 ग्राम",
                price = 120.0,
                priceFormatted = "₹120",
                stockQuantity = 18,
                isAvailable = true,
                isPublished = true,
                imageRes = R.drawable.ayurveda_churna,
                applicableWarningsHindi = "उच्च रक्तचाप या गंभीर बीमारी में चिकित्सक से पूछें।",
                dosageDisclaimerHindi = "मात्रा व अनुपान हेतु योग्य चिकित्सक का परामर्श आवश्यक है।"
            ),
            AyurvedicProduct(
                id = "ayur_pain_oil",
                nameHindi = "महाविषगर्भ तेल (Maha Vishgarbha Taila)",
                nameEnglish = "Maha Vishgarbha Taila (Massage Oil)",
                categoryId = "cat_classical",
                categoryNameHindi = "शास्त्रीय तेल व आसव",
                manufacturer = "शास्त्रीय निर्माणशाला",
                shortDescHindi = "जोड़ों के दर्द, मांसपेशियों की जकड़न एवं वात रोगों में बाह्य मालिश हेतु सिद्ध तेल।",
                fullDescHindi = "आयुर्वेदिक ग्रंथों के विधान अनुसार निर्मित। घुटने का दर्द, कमर दर्द, मोच एवं मांसपेशियों की अकड़न में बाह्य मालिश करने पर सुखद राहत मिलती है।",
                verifiedIngredientsHindi = "तिल तेल बेस, धतूरा पत्र, निर्गुण्डी, अर्क पत्र, प्रसारिणी, कपूर, पुदीना सत्व",
                packSize = "100 मिली",
                price = 160.0,
                priceFormatted = "₹160",
                stockQuantity = 14,
                isAvailable = true,
                isPublished = true,
                imageRes = R.drawable.ayurveda_oil,
                applicableWarningsHindi = "केवल बाह्य मालिश हेतु (For External Application Only)। कटी त्वचा एवं आंखों से दूर रखें।",
                dosageDisclaimerHindi = "प्रभावित स्थान पर हल्के हाथ से 5-10 मिनट मालिश करें।"
            ),
            AyurvedicProduct(
                id = "ayur_tulsi_drops",
                nameHindi = "पंच तुलसी अर्क (Panch Tulsi Ark Drops)",
                nameEnglish = "Panch Tulsi Ark Drops",
                categoryId = "cat_immunity",
                categoryNameHindi = "रोग प्रतिरोधक व पोषण",
                manufacturer = "हर्बल अमृत प्रोडक्ट्स",
                shortDescHindi = "5 प्रकार की शुद्ध तुलसी का अर्क। मौसमी खांसी, सर्दी एवं रोग प्रतिरोधकता में लाभकारी।",
                fullDescHindi = "प्राकृतिक एंटी-ऑक्सीडेंट एवं एंटी-बैक्टीरियल गुणों से युक्त। शरीर की रोग प्रतिरोधक क्षमता को बनाए रखने और गले की खराश में उपयोगी।",
                verifiedIngredientsHindi = "श्याम तुलसी, राम तुलसी, श्वेत तुलसी, वन तुलसी, बर्बेरी तुलसी सत्व",
                packSize = "30 मिली",
                price = 140.0,
                priceFormatted = "₹140",
                stockQuantity = 32,
                isAvailable = true,
                isPublished = true,
                imageRes = R.drawable.ayurveda_oil,
                applicableWarningsHindi = "सीधे आंखों में न डालें। बच्चों की पहुंच से दूर रखें।",
                dosageDisclaimerHindi = "गुनगुने जल या चाय में 1-2 बूंद डालकर लें।"
            ),
            AyurvedicProduct(
                id = "ayur_neem_soap",
                nameHindi = "नीम व एलोवेरा हर्बल साबुन",
                nameEnglish = "Neem & Aloe Vera Herbal Soap",
                categoryId = "cat_personal_care",
                categoryNameHindi = "हर्बल पर्सनल केयर",
                manufacturer = "प्राकृतिक कुटीर उद्योग",
                shortDescHindi = "त्वचा की सुरक्षा, मुंहासे एवं खुजली से बचाव हेतु शुद्ध नीम व घृतकुमारी युक्त।",
                fullDescHindi = "हानिकारक रसायनों और पैराबेंस से मुक्त। त्वचा को स्वच्छ, कोमल व प्राकृतिक रूप से चमकदार बनाए रखने में सहायक।",
                verifiedIngredientsHindi = "नीम तेल, एलोवेरा सत्व, नारियल तेल, शुद्ध ग्लिसरीन",
                packSize = "100 ग्राम",
                price = 50.0,
                priceFormatted = "₹50",
                stockQuantity = 45,
                isAvailable = true,
                isPublished = true,
                imageRes = R.drawable.ayurveda_churna,
                applicableWarningsHindi = "केवल बाह्य स्नान हेतु।",
                dosageDisclaimerHindi = "दैनिक स्नान में सामान्य साबुन की भांति प्रयोग करें।"
            )
        )
    )
    val ayurvedicProducts: StateFlow<List<AyurvedicProduct>> = _ayurvedicProducts.asStateFlow()

    fun searchAyurvedicProducts(query: String, categoryId: String? = null): List<AyurvedicProduct> {
        val all = _ayurvedicProducts.value.filter { it.isPublished }
        val categoryFiltered = if (categoryId == null || categoryId == "cat_all") all else all.filter { it.categoryId == categoryId }
        if (query.isBlank()) return categoryFiltered
        val q = query.trim().lowercase()
        return categoryFiltered.filter {
            it.nameHindi.lowercase().contains(q) ||
            it.nameEnglish.lowercase().contains(q) ||
            it.shortDescHindi.lowercase().contains(q) ||
            it.verifiedIngredientsHindi.lowercase().contains(q) ||
            it.manufacturer.lowercase().contains(q)
        }
    }

    fun getAyurvedicProductById(id: String): AyurvedicProduct? {
        return _ayurvedicProducts.value.find { it.id == id }
    }

    fun addAyurvedicProduct(product: AyurvedicProduct) {
        _ayurvedicProducts.value = listOf(product) + _ayurvedicProducts.value
    }

    fun updateAyurvedicProduct(product: AyurvedicProduct) {
        _ayurvedicProducts.value = _ayurvedicProducts.value.map {
            if (it.id == product.id) product else it
        }
    }

    fun deleteAyurvedicProduct(productId: String) {
        _ayurvedicProducts.value = _ayurvedicProducts.value.filter { it.id != productId }
    }

    fun toggleAyurvedicProductAvailability(productId: String) {
        _ayurvedicProducts.value = _ayurvedicProducts.value.map {
            if (it.id == productId) it.copy(isAvailable = !it.isAvailable) else it
        }
    }

    fun toggleAyurvedicProductPublish(productId: String) {
        _ayurvedicProducts.value = _ayurvedicProducts.value.map {
            if (it.id == productId) it.copy(isPublished = !it.isPublished) else it
        }
    }

    fun updateProductStock(productId: String, newStock: Int) {
        _ayurvedicProducts.value = _ayurvedicProducts.value.map {
            if (it.id == productId) it.copy(stockQuantity = newStock, isAvailable = newStock > 0) else it
        }
    }

    // Ayurvedic Orders (SHOP PICKUP ONLY)
    private val _ayurvedicOrders = MutableStateFlow<List<AyurvedicOrder>>(
        listOf(
            AyurvedicOrder(
                orderId = "AYUR-101",
                customerName = "शिव कुमार वर्मा",
                mobileNumber = "7388403923",
                village = "कल्याणपुर",
                fullAddress = "कल्याणपुर नहर के पास",
                items = listOf(
                    AyurvedicOrderItem("ayur_triphala", "त्रिफला चूर्ण (Triphala Churna)", "100 ग्राम", 65.0, 2, 130.0),
                    AyurvedicOrderItem("ayur_pain_oil", "महाविषगर्भ तेल", "100 मिली", 160.0, 1, 160.0)
                ),
                totalAmount = 290.0,
                totalAmountFormatted = "₹290",
                pickupType = "दुकान से स्वयं प्राप्त करेंगे (Shop Pickup Only)",
                status = AyurvedicOrderStatus.READY_FOR_PICKUP,
                createdAtFormatted = "आज दोपहर 12:30 PM",
                customerNotes = "शाम 5 बजे दुकान से ले लूंगा।",
                adminNotes = "सामग्री पैक कर दुकान काउंटर पर रखी गई है।"
            ),
            AyurvedicOrder(
                orderId = "AYUR-102",
                customerName = "रामप्रसाद यादव",
                mobileNumber = "9876543210",
                village = "बहादुरपुर",
                fullAddress = "प्राथमिक विद्यालय के पास",
                items = listOf(
                    AyurvedicOrderItem("ayur_ashwagandha", "अश्वगंधा चूर्ण", "100 ग्राम", 120.0, 1, 120.0),
                    AyurvedicOrderItem("ayur_tulsi_drops", "पंच तुलसी अर्क", "30 मिली", 140.0, 1, 140.0)
                ),
                totalAmount = 260.0,
                totalAmountFormatted = "₹260",
                pickupType = "दुकान से स्वयं प्राप्त करेंगे (Shop Pickup Only)",
                status = AyurvedicOrderStatus.PENDING_VERIFICATION,
                createdAtFormatted = "आज प्रातः 09:15 AM",
                customerNotes = "कल सुबह आऊंगा।",
                adminNotes = "ग्राहक से व्हाट्सएप पर पुष्टि प्रतीक्षित है।"
            )
        )
    )
    val ayurvedicOrders: StateFlow<List<AyurvedicOrder>> = _ayurvedicOrders.asStateFlow()

    fun createAyurvedicOrder(
        customerName: String,
        mobileNumber: String,
        village: String,
        fullAddress: String,
        items: List<AyurvedicOrderItem>,
        customerNotes: String = ""
    ): AyurvedicOrder {
        val totalAmount = items.sumOf { it.totalPrice }
        val randomNum = Random.nextInt(100, 999)
        val orderId = "AYUR-$randomNum"

        val newOrder = AyurvedicOrder(
            orderId = orderId,
            customerName = customerName.trim(),
            mobileNumber = mobileNumber.trim(),
            village = village.trim(),
            fullAddress = fullAddress.trim(),
            items = items,
            totalAmount = totalAmount,
            totalAmountFormatted = "₹${totalAmount.toInt()}",
            pickupType = "दुकान से स्वयं प्राप्त करेंगे (Shop Pickup Only)",
            status = AyurvedicOrderStatus.PENDING_VERIFICATION,
            createdAtFormatted = "अभी (आज)",
            customerNotes = customerNotes.trim(),
            adminNotes = "व्हाट्सएप ऑर्डर पूछताछ प्राप्त"
        )

        _ayurvedicOrders.value = listOf(newOrder) + _ayurvedicOrders.value

        // Automatically record to Customer Directory with consent
        registerCustomer(customerName, "", mobileNumber, village, fullAddress)

        return newOrder
    }

    fun updateAyurvedicOrderStatus(orderId: String, newStatus: AyurvedicOrderStatus, adminNotes: String = "") {
        _ayurvedicOrders.value = _ayurvedicOrders.value.map {
            if (it.orderId.equals(orderId, ignoreCase = true)) {
                it.copy(
                    status = newStatus,
                    adminNotes = if (adminNotes.isNotBlank()) adminNotes else it.adminNotes
                )
            } else it
        }
    }

    fun findAyurvedicOrder(query: String): AyurvedicOrder? {
        val q = query.trim().lowercase()
        return _ayurvedicOrders.value.find {
            it.orderId.lowercase() == q || it.mobileNumber.contains(q)
        }
    }
}

