package com.example

import com.example.data.model.BookingStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.repository.AgriRepository
import com.example.util.ContactUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun `verify agricultural services count and required equipment`() {
    val services = AgriRepository.services.value
    assertEquals(5, services.size)
    assertNotNull(AgriRepository.getServiceById("new_holland_3032"))
    assertNotNull(AgriRepository.getServiceById("deluxe_thresher_50hp"))
    assertNotNull(AgriRepository.getServiceById("nipha_rotavator"))
    assertNotNull(AgriRepository.getServiceById("small_rice_mill"))
    assertNotNull(AgriRepository.getServiceById("wheat_cutting_machine"))
  }

  @Test
  fun `verify user specified contact phone 7388403923`() {
    assertTrue(AgriRepository.businessPhone.value.contains("7388403923"))
    assertTrue(AgriRepository.businessWhatsApp.value.contains("7388403923"))
  }

  @Test
  fun `verify service searching works in Hindi and English`() {
    val tractorSearch = AgriRepository.searchServices("ट्रैक्टर")
    assertTrue(tractorSearch.isNotEmpty())

    val thresherSearch = AgriRepository.searchServices("Thresher")
    assertTrue(thresherSearch.isNotEmpty())

    val riceSearch = AgriRepository.searchServices("धान")
    assertTrue(riceSearch.isNotEmpty())
  }

  @Test
  fun `create booking and verify tracking`() {
    val booking = AgriRepository.createBooking(
      customerName = "राजेश पटेल",
      fatherName = "श्री रामनरेश पटेल",
      mobileNumber = "7388403923",
      village = "रामपुर",
      fullAddress = "मुख्य सड़क के पास",
      serviceId = "new_holland_3032",
      landArea = "5",
      landUnit = "बीघा",
      bookingDate = "कल",
      preferredTime = "07:00 AM",
      additionalNotes = "कल्टीवेटर से जुताई"
    )

    assertTrue(booking.bookingId.startsWith("PT-"))
    val found = AgriRepository.findBooking(booking.bookingId)
    assertNotNull(found)
    assertEquals("राजेश पटेल", found?.customerName)
    assertEquals("श्री रामनरेश पटेल", found?.fatherName)
    assertEquals(BookingStatus.PENDING, found?.status)
    assertEquals("बीघा", found?.landUnit)
  }

  @Test
  fun `verify duplicate booking detection`() {
    val isDup = AgriRepository.isDuplicateBooking("रामप्रसाद यादव", "9876543210", "new_holland_3032", "कल सुबह")
    assertTrue(isDup)
  }

  @Test
  fun `verify customer review submission`() {
    val initialCount = AgriRepository.reviews.value.size
    val review = AgriRepository.submitReview(
      customerName = "अनिल कुमार",
      village = "सैदपुर",
      rating = 5,
      serviceId = "nipha_rotavator",
      commentHindi = "रोटावेटर ने खेत बहुत अच्छा बनाया।"
    )
    assertEquals(initialCount + 1, AgriRepository.reviews.value.size)
    assertEquals(5, review.rating)
  }

  @Test
  fun `verify multi admin authentication`() {
    val owner = AgriRepository.verifyAdminPin("7388")
    assertNotNull(owner)
    assertEquals("admin_owner", owner?.id)

    val familyAdmin = AgriRepository.verifyAdminPin("1234")
    assertNotNull(familyAdmin)
    assertEquals("admin_family", familyAdmin?.id)

    val invalid = AgriRepository.verifyAdminPin("9999")
    assertEquals(null, invalid)
  }

  @Test
  fun `verify customer directory search and registration`() {
    val initialList = AgriRepository.customers.value
    assertTrue(initialList.isNotEmpty())

    val customer = AgriRepository.registerCustomer(
      name = "सुरेश चंद्र",
      fatherName = "श्री मातादीन",
      mobileNumber = "9988776655",
      village = "चौबेपुर",
      fullAddress = "मंदिर के पीछे"
    )
    assertNotNull(customer)
    assertEquals("सुरेश चंद्र", customer.name)

    val searchResults = AgriRepository.searchCustomers("सुरेश")
    assertTrue(searchResults.any { it.name.contains("सुरेश") })
  }

  @Test
  fun `verify WhatsApp message format adheres strictly to specification`() {
    val booking = AgriRepository.createBooking(
      customerName = "दिनेश सिंह",
      fatherName = "श्री राजबली सिंह",
      mobileNumber = "7388403923",
      village = "बीरभानपुर",
      fullAddress = "पोस्ट बीरभानपुर",
      serviceId = "wheat_cutting_machine",
      landArea = "3",
      landUnit = "एकड़",
      bookingDate = "25 मार्च",
      preferredTime = "08:00 AM",
      additionalNotes = "कटा हुआ गेहूं भूसा के पास रखना है"
    )

    val formattedMsg = ContactUtils.createWhatsAppBookingMessage(booking)
    assertTrue(formattedMsg.contains("🚜 PAPPU TRACTOR – NEW BOOKING"))
    assertTrue(formattedMsg.contains("Booking ID: ${booking.bookingId}"))
    assertTrue(formattedMsg.contains("Customer Name: दिनेश सिंह"))
    assertTrue(formattedMsg.contains("Mobile Number: 7388403923"))
    assertTrue(formattedMsg.contains("Selected Service: गेहूं कटाई मशीन"))
    assertTrue(formattedMsg.contains("Please contact the customer to confirm availability."))
  }

  @Test
  fun `verify payment update and status`() {
    val bookings = AgriRepository.bookings.value
    val target = bookings.first()
    AgriRepository.updateBookingPayment(
      bookingId = target.bookingId,
      paymentStatus = PaymentStatus.FULLY_PAID,
      paymentMethod = PaymentMethod.CASH,
      paidAmount = "₹1,500",
      paymentDate = "02-11-2026"
    )

    val updated = AgriRepository.findBooking(target.bookingId)
    assertEquals(PaymentStatus.FULLY_PAID, updated?.paymentStatus)
    assertEquals(PaymentMethod.CASH, updated?.paymentMethod)
    assertEquals("₹1,500", updated?.paidAmount)
  }

  @Test
  fun `verify ayurvedic store information and default verified products`() {
    val storeName = AgriRepository.ayurvedicStoreName.value
    assertTrue(storeName.contains("पप्पू आयुर्वेदिक") || storeName.contains("आयुर्वेद"))
    val doctorName = AgriRepository.ayurvedicDoctorName.value
    assertTrue(doctorName.contains("वैद्य"))

    val products = AgriRepository.ayurvedicProducts.value
    assertTrue("At least 5 verified products present", products.size >= 5)
    assertTrue("Has Triphala", products.any { it.nameHindi.contains("त्रिफला") })
    assertTrue("Has Ashwagandha", products.any { it.nameHindi.contains("अश्वगंधा") })
    assertTrue("Has Maha Vishgarbha Taila", products.any { it.nameHindi.contains("महाविषगर्भ") })
    assertTrue("Has Panch Tulsi", products.any { it.nameHindi.contains("तुलसी") })

    // Verify all products have medical warnings, dosage and verified ingredients
    products.forEach { p ->
      assertTrue("Product must have dosage instructions", p.dosageDisclaimerHindi.isNotBlank())
      assertTrue("Product must have safety warning", p.applicableWarningsHindi.isNotBlank())
      assertTrue("Product must have verified ingredients", p.verifiedIngredientsHindi.isNotBlank())
    }
  }

  @Test
  fun `verify ayurvedic order creation strictly enforces shop pickup`() {
    val triphala = AgriRepository.ayurvedicProducts.value.first { it.id == "ayur_triphala" }
    val ashwagandha = AgriRepository.ayurvedicProducts.value.first { it.id == "ayur_ashwagandha" }

    val order = AgriRepository.createAyurvedicOrder(
      customerName = "रामसेवक शर्मा",
      mobileNumber = "7388403923",
      village = "कछवां",
      fullAddress = "कछवां बाज़ार",
      items = listOf(
        com.example.data.model.AyurvedicOrderItem(
          productId = triphala.id,
          productNameHindi = triphala.nameHindi,
          packSize = triphala.packSize,
          unitPrice = triphala.price,
          quantity = 2,
          totalPrice = triphala.price * 2
        ),
        com.example.data.model.AyurvedicOrderItem(
          productId = ashwagandha.id,
          productNameHindi = ashwagandha.nameHindi,
          packSize = ashwagandha.packSize,
          unitPrice = ashwagandha.price,
          quantity = 1,
          totalPrice = ashwagandha.price * 1
        )
      ),
      customerNotes = "कृपया सुबह 10 बजे तैयार रखें"
    )

    assertTrue("Order ID must have AYUR- prefix", order.orderId.startsWith("AYUR-"))
    assertEquals(2, order.items.size)
    assertEquals(triphala.price * 2 + ashwagandha.price, order.totalAmount, 0.01)
    assertEquals(com.example.data.model.AyurvedicOrderStatus.PENDING_VERIFICATION, order.status)
    // Verify pickup note is present
    assertTrue(order.pickupType.contains("दुकान से स्वयं प्राप्त करेंगे") || order.pickupType.contains("Shop Pickup"))

    val found = AgriRepository.findAyurvedicOrder(order.orderId)
    assertNotNull(found)
    assertEquals(order.orderId, found?.orderId)
  }

  @Test
  fun `verify ayurvedic WhatsApp order message format and mandatory statutory notice`() {
    val items = listOf(
      com.example.data.model.AyurvedicOrderItem(
        productId = "p1",
        productNameHindi = "त्रिफला चूर्ण",
        packSize = "100 ग्राम",
        unitPrice = 110.0,
        quantity = 2,
        totalPrice = 220.0
      )
    )
    val order = com.example.data.model.AyurvedicOrder(
      orderId = "AYUR-2026-9999",
      customerName = "हरिओम सिंह",
      mobileNumber = "7388403923",
      village = "जंसा",
      fullAddress = "जंसा चौराहा",
      items = items,
      totalAmount = 220.0,
      totalAmountFormatted = "₹220",
      createdAtFormatted = "आज दोपहर",
      customerNotes = "दुकान आकर लेंगे"
    )

    val msg = ContactUtils.createWhatsAppAyurvedicOrderMessage(order)
    assertTrue("Header check", msg.contains("🌿 PAPPU AYURVEDIC STORE – NEW MEDICINE ENQUIRY / ORDER"))
    assertTrue("Order ID check", msg.contains("Order ID: AYUR-2026-9999"))
    assertTrue("Customer check", msg.contains("Customer Name: हरिओम सिंह"))
    assertTrue("Mobile check", msg.contains("Mobile Number: 7388403923"))
    assertTrue("Item check", msg.contains("1. त्रिफला चूर्ण (100 ग्राम) x 2 = ₹220"))
    assertTrue("Total check", msg.contains("Total Estimated Amount: ₹220"))
    assertTrue("Strict Shop Pickup only notice", msg.contains("Mode: STRICTLY SHOP PICKUP ONLY (दुकान से संग्रह)"))
    assertTrue("Mandatory disclaimer notice", msg.contains("दवा लेने आने से पहले उपलब्धता और कीमत की पुष्टि कर लें।"))
  }

  @Test
  fun `verify ayurvedic admin status updates and category product CRUD`() {
    val order = AgriRepository.createAyurvedicOrder(
      customerName = "संजय वर्मा",
      mobileNumber = "9988112233",
      village = "मिर्ज़ापुर",
      fullAddress = "कटरा बाज़ार",
      items = listOf(
        com.example.data.model.AyurvedicOrderItem(
          productId = "ayur_triphala",
          productNameHindi = "त्रिफला चूर्ण",
          packSize = "100 ग्राम",
          unitPrice = 65.0,
          quantity = 1,
          totalPrice = 65.0
        )
      )
    )

    // Update status to READY_FOR_PICKUP
    AgriRepository.updateAyurvedicOrderStatus(order.orderId, com.example.data.model.AyurvedicOrderStatus.READY_FOR_PICKUP)
    var updated = AgriRepository.findAyurvedicOrder(order.orderId)
    assertEquals(com.example.data.model.AyurvedicOrderStatus.READY_FOR_PICKUP, updated?.status)

    // Update status to COLLECTED
    AgriRepository.updateAyurvedicOrderStatus(order.orderId, com.example.data.model.AyurvedicOrderStatus.COLLECTED)
    updated = AgriRepository.findAyurvedicOrder(order.orderId)
    assertEquals(com.example.data.model.AyurvedicOrderStatus.COLLECTED, updated?.status)

    // Test product stock updates
    AgriRepository.updateProductStock("ayur_triphala", 45)
    val triphala = AgriRepository.getAyurvedicProductById("ayur_triphala")
    assertEquals(45, triphala?.stockQuantity)
  }
}
