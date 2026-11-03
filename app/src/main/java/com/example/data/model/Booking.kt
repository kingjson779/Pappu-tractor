package com.example.data.model

enum class BookingStatus(
    val titleHindi: String,
    val stepIndex: Int,
    val descriptionHindi: String,
    val isTerminal: Boolean = false
) {
    PENDING("लंबित (Pending)", 1, "आपकी बुकिंग प्राप्त हो गई है। हमारी टीम जल्द संपर्क करेगी।"),
    CONFIRMED("पुष्टि हुई (Confirmed)", 2, "बुकिंग कन्फर्म हो गई है और ऑपरेटर व मशीन निर्धारित कर दी गई है।"),
    IN_PROGRESS("काम जारी (In Progress)", 3, "मशीन आपके खेत/गांव के लिए रवाना हो चुकी है या काम चालू है।"),
    COMPLETED("कार्य संपन्न (Completed)", 4, "कार्य सफलतापूर्वक संपन्न हो गया है। पप्पू ट्रैक्टर चुनने के लिए धन्यवाद!", true),
    REJECTED("अस्वीकृत (Rejected)", -1, "खेद है, मशीन व्यस्त होने या अनुपलब्ध होने से बुकिंग स्वीकार नहीं हो सकी।", true),
    CANCELLED("रद्द की गई (Cancelled)", 0, "यह बुकिंग रद्द कर दी गई है।", true)
}

enum class PaymentStatus(val titleHindi: String) {
    UNPAID("भुगतान बाकी (Pending)"),
    ADVANCE_PAID("अग्रिम भुगतान (Advance Paid)"),
    FULLY_PAID("पूर्ण भुगतान (Completed)")
}

enum class PaymentMethod(val titleHindi: String) {
    CASH("नकद (Cash)"),
    ONLINE("ऑनलाइन / यूपीआई (Online/UPI)"),
    NOT_SELECTED("अनिर्धारित (Pending)")
}

data class Booking(
    val bookingId: String,
    val customerName: String,
    val fatherName: String = "",
    val mobileNumber: String,
    val village: String,
    val fullAddress: String,
    val serviceId: String,
    val serviceNameHindi: String,
    val landArea: String,
    val landUnit: String = "बीघा",
    val bookingDate: String,
    val preferredTime: String,
    val additionalNotes: String = "",
    val status: BookingStatus = BookingStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentMethod: PaymentMethod = PaymentMethod.NOT_SELECTED,
    val estimatedAmount: String = "दर अनुसार",
    val paidAmount: String = "₹0",
    val paymentDate: String = "",
    val adminNotes: String = "",
    val createdAtFormatted: String = "आज"
)
