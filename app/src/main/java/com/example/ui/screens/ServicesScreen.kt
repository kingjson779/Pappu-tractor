package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgriService
import com.example.data.repository.AgriRepository
import com.example.ui.components.ServiceCard
import com.example.ui.theme.AgriBackground
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onBookNowClick: (AgriService) -> Unit,
    onViewDetailsClick: (AgriService) -> Unit
) {
    val allServices by AgriRepository.services.collectAsState()
    var selectedFilter by remember { mutableStateOf("सभी यंत्र") }
    val filters = listOf("सभी यंत्र", "ट्रैक्टर", "थ्रेशर", "रोटावेटर", "राइस मिल", "कटाई")

    val displayedServices = remember(selectedFilter, allServices) {
        if (selectedFilter == "सभी यंत्र") {
            allServices
        } else {
            allServices.filter {
                it.nameHindi.contains(selectedFilter) ||
                it.nameEnglish.contains(selectedFilter, ignoreCase = true) ||
                it.suitableFor.any { tag -> tag.contains(selectedFilter) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AgriBackground)
            .testTag("services_screen")
    ) {
        // Header banner
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "हमारी कृषि सेवाएं व मशीनरी",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AgriGreenDark
                )
                Text(
                    text = "अपनी आवश्यकता अनुसार मशीन चुनें और तुरंत खेत के लिए बुक करें।",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AgriGreenPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = AgriGreenContainer,
                                labelColor = AgriGreenDark
                            ),
                            border = null,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("filter_chip_$filter")
                        )
                    }
                }
            }
        }

        // Services List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF8E1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "किराए पर चालक (ड्राइवर) व डीजल सहायता शामिल है। अग्रिम बुकिंग उपलब्ध है।",
                            fontSize = 11.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            items(displayedServices) { service ->
                ServiceCard(
                    service = service,
                    onViewDetailsClick = onViewDetailsClick,
                    onBookNowClick = onBookNowClick
                )
            }
        }
    }
}
