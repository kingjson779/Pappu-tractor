package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Category for Ayurvedic Store.
 * Authorized admin can create, edit and remove categories.
 */
data class AyurvedicCategory(
    val id: String,
    val nameHindi: String,
    val nameEnglish: String,
    val descriptionHindi: String = ""
)

/**
 * Verified Ayurvedic product / medicine.
 * Strict medical compliance: no invented cures, real verified ingredients only.
 */
data class AyurvedicProduct(
    val id: String,
    val nameHindi: String,
    val nameEnglish: String,
    val categoryId: String,
    val categoryNameHindi: String,
    val manufacturer: String,
    val shortDescHindi: String,
    val fullDescHindi: String,
    val verifiedIngredientsHindi: String,
    val packSize: String,
    val price: Double,
    val priceFormatted: String,
    val stockQuantity: Int,
    val isAvailable: Boolean = true,
    val isPublished: Boolean = true,
    @DrawableRes val imageRes: Int = R.drawable.ayurveda_churna,
    val applicableWarningsHindi: String = "योग्य आयुर्वेदिक चिकित्सक (वैद्य जी) के परामर्श अनुसार सेवन करें।",
    val dosageDisclaimerHindi: String = "मात्रा व अनुपान हेतु चिकित्सक से परामर्श लें। स्व-चिकित्सा से बचें।",
    val requiresPrescription: Boolean = false,
    val licenseInfo: String = "आयुष विभाग पंजीकृत / स्वीकृत निर्माण",
    val expiryDuration: String = "निर्माण तिथि से 24 माह"
)

data class AyurvedicOrderItem(
    val productId: String,
    val productNameHindi: String,
    val packSize: String,
    val unitPrice: Double,
    val quantity: Int,
    val totalPrice: Double
)

enum class AyurvedicOrderStatus(val titleHindi: String, val badgeColorHex: Long) {
    PENDING_VERIFICATION("दुकान पुष्टि प्रतीक्षित (Pending)", 0xFFE65100),
    READY_FOR_PICKUP("दुकान से पिकअप हेतु तैयार (Ready)", 0xFF1976D2),
    COLLECTED("दुकान से प्राप्त (Collected)", 0xFF2E7D32),
    CANCELLED("रद्द (Cancelled)", 0xFFD32F2F)
}

/**
 * Ayurvedic Order model.
 * Important: STRICTLY SHOP PICKUP ONLY (दुकान से स्वयं प्राप्त करें).
 * Delivery is not available.
 */
data class AyurvedicOrder(
    val orderId: String,
    val customerName: String,
    val mobileNumber: String,
    val village: String,
    val fullAddress: String = "",
    val items: List<AyurvedicOrderItem>,
    val totalAmount: Double,
    val totalAmountFormatted: String,
    val pickupType: String = "दुकान से स्वयं प्राप्त करेंगे (Shop Pickup Only)",
    val status: AyurvedicOrderStatus = AyurvedicOrderStatus.PENDING_VERIFICATION,
    val createdAtFormatted: String,
    val customerNotes: String = "",
    val adminNotes: String = ""
)
