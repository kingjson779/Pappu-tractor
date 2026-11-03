package com.example.data.model

enum class ReviewModerationStatus(val titleHindi: String) {
    APPROVED("सत्यापित (Approved)"),
    PENDING_MODERATION("समीक्षाधीन (Under Review)")
}

data class CustomerReview(
    val id: String,
    val customerName: String,
    val village: String,
    val rating: Int, // 1 to 5
    val serviceId: String,
    val serviceNameHindi: String,
    val commentHindi: String,
    val date: String,
    val moderationStatus: ReviewModerationStatus = ReviewModerationStatus.APPROVED
)
