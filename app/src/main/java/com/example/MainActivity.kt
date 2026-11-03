package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PappuBottomNavBar
import com.example.ui.components.PappuTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BottomTab
import com.example.ui.viewmodel.PappuViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PappuTractorApp()
            }
        }
    }
}

@Composable
fun PappuTractorApp(
    viewModel: PappuViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isSubmittingBooking by viewModel.isSubmittingBooking.collectAsState()
    val bookingErrorMessage by viewModel.bookingErrorMessage.collectAsState()
    val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsState()
    val authenticatedAdmin by viewModel.authenticatedAdmin.collectAsState()
    val adminError by viewModel.adminError.collectAsState()
    val cart by viewModel.cart.collectAsState()

    val isSecondaryScreen = currentScreen !is Screen.Home && currentScreen !is Screen.Splash

    // System Back Button Navigation Logic
    BackHandler(enabled = isSecondaryScreen) {
        when (currentScreen) {
            is Screen.AdminDashboard -> viewModel.logoutAdmin()
            is Screen.AdminLogin -> viewModel.navigateTo(Screen.Home)
            is Screen.CustomerDirectory -> viewModel.navigateTo(Screen.Home)
            is Screen.Settings -> viewModel.navigateTo(Screen.Home)
            is Screen.Reviews -> viewModel.navigateTo(Screen.Home)
            is Screen.AboutUs -> viewModel.navigateTo(Screen.Home)
            is Screen.BookingSuccess -> viewModel.navigateTo(Screen.Home)
            is Screen.ServiceDetail -> viewModel.navigateTo(Screen.Services)
            is Screen.BookingForm -> viewModel.navigateTo(Screen.Services)
            is Screen.Services -> viewModel.navigateTo(Screen.Home)
            is Screen.TrackBooking -> viewModel.navigateTo(Screen.Home)
            is Screen.Contact -> viewModel.navigateTo(Screen.Home)
            is Screen.AyurvedicStore -> viewModel.navigateTo(Screen.Home)
            is Screen.AyurvedicProductDetail -> viewModel.navigateTo(Screen.AyurvedicStore)
            is Screen.AyurvedicCart -> viewModel.navigateTo(Screen.AyurvedicStore)
            is Screen.AyurvedicOrderSuccess -> viewModel.navigateTo(Screen.AyurvedicStore)
            else -> {}
        }
    }

    if (currentScreen is Screen.Splash) {
        SplashScreen(
            onTimeout = {
                viewModel.navigateTo(Screen.Home)
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AgriBackground),
            topBar = {
                PappuTopAppBar(
                    title = "पप्पू ट्रैक्टर",
                    subtitle = "उन्नत कृषि सेवाएं एवं आयुर्वेदिक स्टोर",
                    showBackButton = isSecondaryScreen,
                    onBackClick = {
                        when (currentScreen) {
                            is Screen.AdminDashboard -> viewModel.logoutAdmin()
                            is Screen.AdminLogin -> viewModel.navigateTo(Screen.Home)
                            is Screen.CustomerDirectory -> viewModel.navigateTo(Screen.Home)
                            is Screen.Settings -> viewModel.navigateTo(Screen.Home)
                            is Screen.Reviews -> viewModel.navigateTo(Screen.Home)
                            is Screen.AboutUs -> viewModel.navigateTo(Screen.Home)
                            is Screen.BookingSuccess -> viewModel.navigateTo(Screen.Home)
                            is Screen.ServiceDetail -> viewModel.navigateTo(Screen.Services)
                            is Screen.BookingForm -> viewModel.navigateTo(Screen.Services)
                            is Screen.Services -> viewModel.navigateTo(Screen.Home)
                            is Screen.TrackBooking -> viewModel.navigateTo(Screen.Home)
                            is Screen.Contact -> viewModel.navigateTo(Screen.Home)
                            is Screen.AyurvedicStore -> viewModel.navigateTo(Screen.Home)
                            is Screen.AyurvedicProductDetail -> viewModel.navigateTo(Screen.AyurvedicStore)
                            is Screen.AyurvedicCart -> viewModel.navigateTo(Screen.AyurvedicStore)
                            is Screen.AyurvedicOrderSuccess -> viewModel.navigateTo(Screen.AyurvedicStore)
                            else -> {}
                        }
                    },
                    onAboutClick = {
                        viewModel.navigateTo(Screen.AboutUs)
                    },
                    onReviewsClick = {
                        viewModel.navigateTo(Screen.Reviews)
                    },
                    onCustomerDirectoryClick = {
                        viewModel.navigateTo(Screen.CustomerDirectory)
                    },
                    onSettingsClick = {
                        viewModel.navigateTo(Screen.Settings)
                    },
                    onAyurvedicStoreClick = {
                        viewModel.navigateTo(Screen.AyurvedicStore)
                    },
                    onAdminClick = {
                        if (isAdminAuthenticated) {
                            viewModel.navigateTo(Screen.AdminDashboard)
                        } else {
                            viewModel.navigateTo(Screen.AdminLogin)
                        }
                    }
                )
            },
            bottomBar = {
                // Keep bottom navigation visible on customer tabs
                if (currentScreen !is Screen.AdminDashboard && currentScreen !is Screen.AdminLogin) {
                    PappuBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            viewModel.onTabSelected(tab)
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            onBookNowClick = { service ->
                                viewModel.startBooking(service?.id)
                            },
                            onViewDetailsClick = { service ->
                                viewModel.openServiceDetail(service.id)
                            },
                            onViewAllServicesClick = {
                                viewModel.onTabSelected(BottomTab.SERVICES)
                            },
                            onOpenAyurvedicStoreClick = {
                                viewModel.navigateTo(Screen.AyurvedicStore)
                            },
                            onAyurvedicProductClick = { product ->
                                viewModel.openAyurvedicProductDetail(product.id)
                            }
                        )
                    }

                    is Screen.Services -> {
                        ServicesScreen(
                            onBookNowClick = { service ->
                                viewModel.startBooking(service.id)
                            },
                            onViewDetailsClick = { service ->
                                viewModel.openServiceDetail(service.id)
                            }
                        )
                    }

                    is Screen.ServiceDetail -> {
                        ServiceDetailScreen(
                            serviceId = screen.serviceId,
                            onBackClick = {
                                viewModel.navigateTo(Screen.Services)
                            },
                            onBookNowClick = { service ->
                                viewModel.startBooking(service.id)
                            }
                        )
                    }

                    is Screen.BookingForm -> {
                        BookingFormScreen(
                            preselectedServiceId = screen.preselectedServiceId,
                            isSubmitting = isSubmittingBooking,
                            externalErrorMessage = bookingErrorMessage,
                            onSubmitBooking = { name, fatherName, mobile, village, address, serviceId, area, unit, date, time, notes ->
                                viewModel.submitBooking(
                                    customerName = name,
                                    fatherName = fatherName,
                                    mobileNumber = mobile,
                                    village = village,
                                    fullAddress = address,
                                    serviceId = serviceId,
                                    landArea = area,
                                    landUnit = unit,
                                    bookingDate = date,
                                    preferredTime = time,
                                    additionalNotes = notes,
                                    onSuccess = {}
                                )
                            }
                        )
                    }

                    is Screen.BookingSuccess -> {
                        BookingSuccessScreen(
                            booking = screen.booking,
                            onTrackBookingClick = { bookingId ->
                                viewModel.trackSpecificBooking(bookingId)
                            },
                            onBackToHomeClick = {
                                viewModel.navigateTo(Screen.Home)
                            }
                        )
                    }

                    is Screen.TrackBooking -> {
                        TrackBookingScreen(
                            initialQuery = screen.initialQuery
                        )
                    }

                    is Screen.Contact -> {
                        ContactScreen()
                    }

                    is Screen.AboutUs -> {
                        AboutUsScreen(
                            onBookNowClick = {
                                viewModel.startBooking(null)
                            }
                        )
                    }

                    is Screen.Reviews -> {
                        ReviewsScreen(
                            onBackClick = {
                                viewModel.navigateTo(Screen.Home)
                            },
                            onSubmitReview = { name, village, rating, serviceId, comment ->
                                viewModel.submitReview(name, village, rating, serviceId, comment)
                            }
                        )
                    }

                    is Screen.CustomerDirectory -> {
                        CustomerDirectoryScreen(
                            isAdmin = isAdminAuthenticated,
                            onBackClick = {
                                viewModel.navigateTo(Screen.Home)
                            },
                            onCustomerSelectedForBooking = { customer ->
                                viewModel.startBooking(null)
                            }
                        )
                    }

                    is Screen.Settings -> {
                        SettingsScreen(
                            isAdmin = isAdminAuthenticated,
                            currentAdmin = authenticatedAdmin,
                            onBackClick = {
                                viewModel.navigateTo(Screen.Home)
                            },
                            onAdminLoginClick = {
                                viewModel.navigateTo(Screen.AdminLogin)
                            },
                            onLogoutClick = {
                                viewModel.logoutAdmin()
                            },
                            onContactSupportClick = {
                                viewModel.navigateTo(Screen.Contact)
                            },
                            onAboutClick = {
                                viewModel.navigateTo(Screen.AboutUs)
                            }
                        )
                    }

                    is Screen.AdminLogin -> {
                        AdminLoginScreen(
                            adminError = adminError,
                            onLoginClick = { pin ->
                                viewModel.attemptAdminLogin(pin)
                            },
                            onBackClick = {
                                viewModel.navigateTo(Screen.Home)
                            }
                        )
                    }

                    is Screen.AdminDashboard -> {
                        AdminDashboardScreen(
                            currentAdmin = authenticatedAdmin,
                            onNavigateToCustomerDirectory = {
                                viewModel.navigateTo(Screen.CustomerDirectory)
                            },
                            onLogoutClick = {
                                viewModel.logoutAdmin()
                            }
                        )
                    }

                    is Screen.AyurvedicStore -> {
                        AyurvedicStoreScreen(
                            cart = cart,
                            onProductClick = { product ->
                                viewModel.openAyurvedicProductDetail(product.id)
                            },
                            onAddToCart = { product ->
                                viewModel.addToCart(product.id, 1)
                            },
                            onOpenCartClick = {
                                viewModel.navigateTo(Screen.AyurvedicCart)
                            },
                            onBackClick = {
                                viewModel.navigateTo(Screen.Home)
                            }
                        )
                    }

                    is Screen.AyurvedicProductDetail -> {
                        val quantityInCart = cart[screen.productId] ?: 0
                        AyurvedicProductDetailScreen(
                            productId = screen.productId,
                            quantityInCart = quantityInCart,
                            onAddToCart = { product, qty ->
                                viewModel.updateCartQuantity(product.id, qty)
                            },
                            onOpenCartClick = {
                                viewModel.navigateTo(Screen.AyurvedicCart)
                            },
                            onBackClick = {
                                viewModel.navigateTo(Screen.AyurvedicStore)
                            }
                        )
                    }

                    is Screen.AyurvedicCart -> {
                        AyurvedicCartScreen(
                            cart = cart,
                            onUpdateQuantity = { prodId, qty ->
                                viewModel.updateCartQuantity(prodId, qty)
                            },
                            onRemoveItem = { prodId ->
                                viewModel.removeFromCart(prodId)
                            },
                            onClearCart = {
                                viewModel.clearCart()
                            },
                            onSubmitOrder = { name, mobile, village, address, notes, onSuccess ->
                                viewModel.submitAyurvedicOrder(name, mobile, village, address, notes, onSuccess)
                            },
                            onBackClick = {
                                viewModel.navigateTo(Screen.AyurvedicStore)
                            }
                        )
                    }

                    is Screen.AyurvedicOrderSuccess -> {
                        AyurvedicOrderSuccessScreen(
                            order = screen.order,
                            onBackToStoreClick = {
                                viewModel.navigateTo(Screen.AyurvedicStore)
                            },
                            onBackToHomeClick = {
                                viewModel.navigateTo(Screen.Home)
                            }
                        )
                    }

                    is Screen.Splash -> {
                        // Handled above
                    }
                }
            }
        }
    }
}
