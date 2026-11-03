package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.Booking

object ContactUtils {

    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "डायल नहीं हो सका: $phoneNumber", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Official WhatsApp Click-to-Chat integration:
     * International format without + or spaces or hyphens.
     */
    fun openWhatsApp(context: Context, phoneNumber: String, message: String): Boolean {
        return try {
            val cleanedNumber = phoneNumber.replace(Regex("[^0-9]"), "")
            val url = "https://api.whatsapp.com/send?phone=$cleanedNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "व्हाट्सएप उपलब्ध नहीं है या इंस्टॉल नहीं है। कृपया व्हाट्सएप इंस्टॉल करें।", Toast.LENGTH_LONG).show()
            false
        }
    }

    /**
     * Exact required WhatsApp Message format for PAPPU TRACTOR:
     */
    fun createWhatsAppBookingMessage(booking: Booking): String {
        return buildString {
            append("🚜 PAPPU TRACTOR – NEW BOOKING\n\n")
            append("Booking ID: ${booking.bookingId}\n\n")
            append("Customer Name: ${booking.customerName}\n")
            if (booking.fatherName.isNotBlank()) {
                append("Father's Name: ${booking.fatherName}\n")
            }
            append("Mobile Number: ${booking.mobileNumber}\n")
            append("Village: ${booking.village}\n")
            append("Full Address: ${booking.fullAddress}\n\n")
            append("Selected Service: ${booking.serviceNameHindi}\n")
            append("Land Area: ${booking.landArea} ${booking.landUnit}\n")
            append("Booking Date: ${booking.bookingDate}\n")
            append("Preferred Time: ${booking.preferredTime}\n\n")
            val notes = if (booking.additionalNotes.isNotBlank()) booking.additionalNotes else "कोई अतिरिक्त निर्देश नहीं"
            append("Additional Details: $notes\n\n")
            append("Please contact the customer to confirm availability.")
        }
    }

    fun createWhatsAppBookingSummary(booking: Booking): String = createWhatsAppBookingMessage(booking)

    /**
     * Exact required WhatsApp Message format for PAPPU AYURVEDIC STORE:
     * Strictly Shop Pickup Only.
     */
    fun createWhatsAppAyurvedicOrderMessage(order: com.example.data.model.AyurvedicOrder): String {
        return buildString {
            append("🌿 PAPPU AYURVEDIC STORE – NEW MEDICINE ENQUIRY / ORDER\n")
            append("दुकान से पिकअप (Shop Pickup Only)\n\n")
            append("Order ID: ${order.orderId}\n\n")
            append("Customer Name: ${order.customerName}\n")
            append("Mobile Number: ${order.mobileNumber}\n")
            append("Village: ${order.village}\n")
            if (order.fullAddress.isNotBlank()) {
                append("Address: ${order.fullAddress}\n")
            }
            append("\nSelected Medicines (चयनित औषधियां):\n")
            order.items.forEachIndexed { index, item ->
                append("${index + 1}. ${item.productNameHindi} (${item.packSize}) x ${item.quantity} = ₹${item.totalPrice.toInt()}\n")
            }
            append("\nTotal Estimated Amount: ${order.totalAmountFormatted}\n")
            append("Mode: STRICTLY SHOP PICKUP ONLY (दुकान से संग्रह)\n\n")
            if (order.customerNotes.isNotBlank()) {
                append("Customer Note: ${order.customerNotes}\n\n")
            }
            append("महत्वपूर्ण सूचना:\n")
            append("\"आपका ऑर्डर WhatsApp पर भेजने के बाद दुकान से पुष्टि प्राप्त करें। दवा लेने आने से पहले उपलब्धता और कीमत की पुष्टि कर लें।\"")
        }
    }

    fun openGoogleMaps(context: Context, address: String) {
        try {
            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(context, address, Toast.LENGTH_LONG).show()
        }
    }
}
