package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AgriRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface Screen {
    object Splash : Screen
    object Home : Screen
    object Services : Screen
    data class ServiceDetail(val serviceId: String) : Screen
    data class BookingForm(val preselectedServiceId: String? = null) : Screen
    data class BookingSuccess(val booking: Booking) : Screen
    data class TrackBooking(val initialQuery: String? = null) : Screen
    object Contact : Screen
    object AboutUs : Screen
    object Reviews : Screen
    object CustomerDirectory : Screen
    object Settings : Screen
    object AdminLogin : Screen
    object AdminDashboard : Screen
    object AyurvedicStore : Screen
    data class AyurvedicProductDetail(val productId: String) : Screen
    object AyurvedicCart : Screen
    data class AyurvedicOrderSuccess(val order: AyurvedicOrder) : Screen
}

enum class BottomTab(val titleHindi: String, val routeName: String) {
    HOME("होम", "home"),
    SERVICES("सेवाएं", "services"),
    BOOKING("बुकिंग", "booking"),
    TRACK("ट्रैक करें", "track"),
    CONTACT("संपर्क", "contact")
}

class PappuViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedTab = MutableStateFlow(BottomTab.HOME)
    val selectedTab: StateFlow<BottomTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Tracking state
    private val _trackingInput = MutableStateFlow("")
    val trackingInput: StateFlow<String> = _trackingInput.asStateFlow()

    private val _trackedBooking = MutableStateFlow<Booking?>(null)
    val trackedBooking: StateFlow<Booking?> = _trackedBooking.asStateFlow()

    private val _trackingSearched = MutableStateFlow(false)
    val trackingSearched: StateFlow<Boolean> = _trackingSearched.asStateFlow()

    // Booking Submission state
    private val _isSubmittingBooking = MutableStateFlow(false)
    val isSubmittingBooking: StateFlow<Boolean> = _isSubmittingBooking.asStateFlow()

    private val _bookingErrorMessage = MutableStateFlow<String?>(null)
    val bookingErrorMessage: StateFlow<String?> = _bookingErrorMessage.asStateFlow()

    // Admin state
    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _authenticatedAdmin = MutableStateFlow<AdminAccount?>(null)
    val authenticatedAdmin: StateFlow<AdminAccount?> = _authenticatedAdmin.asStateFlow()

    private val _adminPinInput = MutableStateFlow("")
    val adminPinInput: StateFlow<String> = _adminPinInput.asStateFlow()

    private val _adminError = MutableStateFlow<String?>(null)
    val adminError: StateFlow<String?> = _adminError.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        when (screen) {
            is Screen.Home -> _selectedTab.value = BottomTab.HOME
            is Screen.Services -> _selectedTab.value = BottomTab.SERVICES
            is Screen.BookingForm -> _selectedTab.value = BottomTab.BOOKING
            is Screen.TrackBooking -> _selectedTab.value = BottomTab.TRACK
            is Screen.Contact -> _selectedTab.value = BottomTab.CONTACT
            else -> {}
        }
    }

    fun onTabSelected(tab: BottomTab) {
        _selectedTab.value = tab
        when (tab) {
            BottomTab.HOME -> _currentScreen.value = Screen.Home
            BottomTab.SERVICES -> _currentScreen.value = Screen.Services
            BottomTab.BOOKING -> _currentScreen.value = Screen.BookingForm(null)
            BottomTab.TRACK -> _currentScreen.value = Screen.TrackBooking(null)
            BottomTab.CONTACT -> _currentScreen.value = Screen.Contact
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openServiceDetail(serviceId: String) {
        navigateTo(Screen.ServiceDetail(serviceId))
    }

    fun startBooking(serviceId: String? = null) {
        _bookingErrorMessage.value = null
        navigateTo(Screen.BookingForm(serviceId))
    }

    fun submitBooking(
        customerName: String,
        fatherName: String,
        mobileNumber: String,
        village: String,
        fullAddress: String,
        serviceId: String,
        landArea: String,
        landUnit: String,
        bookingDate: String,
        preferredTime: String,
        additionalNotes: String,
        onSuccess: (Booking) -> Unit
    ) {
        if (_isSubmittingBooking.value) return
        _bookingErrorMessage.value = null

        // Check for accidental duplicate submission
        if (AgriRepository.isDuplicateBooking(customerName, mobileNumber, serviceId, bookingDate)) {
            _bookingErrorMessage.value = "इस नाम, मोबाइल और तारीख पर पहले से बुकिंग दर्ज है। कृपया अपनी बुकिंग स्थिति ट्रैक करें।"
            return
        }

        viewModelScope.launch {
            _isSubmittingBooking.value = true
            delay(400)
            val booking = AgriRepository.createBooking(
                customerName = customerName,
                fatherName = fatherName,
                mobileNumber = mobileNumber,
                village = village,
                fullAddress = fullAddress,
                serviceId = serviceId,
                landArea = landArea,
                landUnit = landUnit,
                bookingDate = bookingDate,
                preferredTime = preferredTime,
                additionalNotes = additionalNotes
            )
            _isSubmittingBooking.value = false
            _trackingInput.value = booking.bookingId
            _trackedBooking.value = booking
            _trackingSearched.value = true
            navigateTo(Screen.BookingSuccess(booking))
            onSuccess(booking)
        }
    }

    fun setTrackingInput(input: String) {
        _trackingInput.value = input
    }

    fun searchTracking() {
        _trackingSearched.value = true
        _trackedBooking.value = AgriRepository.findBooking(_trackingInput.value)
    }

    fun trackSpecificBooking(bookingId: String) {
        _trackingInput.value = bookingId
        _trackingSearched.value = true
        _trackedBooking.value = AgriRepository.findBooking(bookingId)
        navigateTo(Screen.TrackBooking(bookingId))
    }

    // Customer Reviews
    fun submitReview(
        name: String,
        village: String,
        rating: Int,
        serviceId: String,
        comment: String
    ) {
        AgriRepository.submitReview(name, village, rating, serviceId, comment)
    }

    // Admin Authentication supporting 2 authorized accounts
    fun setAdminPin(pin: String) {
        _adminPinInput.value = pin
        _adminError.value = null
    }

    fun attemptAdminLogin(pin: String) {
        val verifiedAdmin = AgriRepository.verifyAdminPin(pin)
        if (verifiedAdmin != null) {
            _isAdminAuthenticated.value = true
            _authenticatedAdmin.value = verifiedAdmin
            _adminError.value = null
            navigateTo(Screen.AdminDashboard)
        } else {
            _adminError.value = "गलत सुरक्षा पिन दर्ज किया गया। कृपया अधिकृत पिन दर्ज करें।"
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        _authenticatedAdmin.value = null
        _adminPinInput.value = ""
        navigateTo(Screen.Home)
    }

    // Admin Actions
    fun adminUpdateBookingStatus(bookingId: String, status: BookingStatus, note: String = "") {
        AgriRepository.updateBookingStatus(bookingId, status, note)
    }

    fun adminUpdatePayment(
        bookingId: String,
        paymentStatus: PaymentStatus,
        paymentMethod: PaymentMethod,
        estimatedAmount: String,
        paidAmount: String,
        paymentDate: String
    ) {
        AgriRepository.updateBookingPayment(bookingId, paymentStatus, paymentMethod, estimatedAmount, paidAmount, paymentDate)
    }

    fun adminUpdatePrice(serviceId: String, price: String, unit: String) {
        AgriRepository.updateServicePrice(serviceId, price, unit)
    }

    fun adminToggleAvailability(serviceId: String) {
        AgriRepository.toggleServiceAvailability(serviceId)
    }

    fun adminUpdateBusinessInfo(name: String, phone: String, whatsApp: String, address: String, hours: String) {
        AgriRepository.updateBusinessInfo(name, phone, whatsApp, address, hours)
    }

    // Customer Directory management
    fun registerNewCustomer(name: String, fatherName: String, mobile: String, village: String, address: String) {
        AgriRepository.registerCustomer(name, fatherName, mobile, village, address)
    }

    fun editCustomer(id: String, name: String, fatherName: String, mobile: String, village: String, address: String) {
        AgriRepository.updateCustomer(id, name, fatherName, mobile, village, address)
    }

    fun removeCustomer(id: String) {
        AgriRepository.deleteCustomer(id)
    }

    // =========================================================================
    // Ayurvedic Store Cart & Order Management (SHOP PICKUP ONLY)
    // =========================================================================
    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    val cart: StateFlow<Map<String, Int>> = _cart.asStateFlow()

    fun addToCart(productId: String, quantity: Int = 1) {
        val current = _cart.value.toMutableMap()
        val currentQty = current[productId] ?: 0
        current[productId] = currentQty + quantity
        _cart.value = current
    }

    fun updateCartQuantity(productId: String, newQuantity: Int) {
        val current = _cart.value.toMutableMap()
        if (newQuantity <= 0) {
            current.remove(productId)
        } else {
            current[productId] = newQuantity
        }
        _cart.value = current
    }

    fun removeFromCart(productId: String) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun submitAyurvedicOrder(
        customerName: String,
        mobileNumber: String,
        village: String,
        fullAddress: String,
        notes: String,
        onSuccess: (AyurvedicOrder) -> Unit
    ) {
        val cartMap = _cart.value
        if (cartMap.isEmpty()) return

        val items = cartMap.mapNotNull { (prodId, qty) ->
            val product = AgriRepository.getAyurvedicProductById(prodId) ?: return@mapNotNull null
            AyurvedicOrderItem(
                productId = prodId,
                productNameHindi = product.nameHindi,
                packSize = product.packSize,
                unitPrice = product.price,
                quantity = qty,
                totalPrice = product.price * qty
            )
        }

        if (items.isEmpty()) return

        val order = AgriRepository.createAyurvedicOrder(
            customerName = customerName,
            mobileNumber = mobileNumber,
            village = village,
            fullAddress = fullAddress,
            items = items,
            customerNotes = notes
        )

        clearCart()
        navigateTo(Screen.AyurvedicOrderSuccess(order))
        onSuccess(order)
    }

    fun openAyurvedicProductDetail(productId: String) {
        navigateTo(Screen.AyurvedicProductDetail(productId))
    }
}
