package com.example.data.model

import androidx.annotation.DrawableRes

data class AgriService(
    val id: String,
    val nameHindi: String,
    val nameEnglish: String,
    val shortDescHindi: String,
    val fullDescHindi: String,
    val priceRateHindi: String,
    val priceUnit: String,
    @DrawableRes val imageRes: Int,
    val galleryImages: List<Int> = emptyList(),
    val horsepower: String,
    val workDetails: List<Pair<String, String>> = emptyList(),
    val suitableFor: List<String> = emptyList(),
    val highlights: List<String> = emptyList(),
    val badge: String? = null,
    val isAvailable: Boolean = true
)
