package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.repository.AgriRepository
import com.example.ui.theme.AgriGoldHarvest
import com.example.ui.theme.AgriGreenDark
import com.example.util.ContactUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PappuTopAppBar(
    title: String = "पप्पू ट्रैक्टर",
    subtitle: String = "उन्नत कृषि सेवाएं | Agriculture Services",
    onAboutClick: () -> Unit = {},
    onReviewsClick: () -> Unit = {},
    onCustomerDirectoryClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    onAyurvedicStoreClick: () -> Unit = {},
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val phoneToDial by AgriRepository.businessPhone.collectAsState()

    Surface(
        color = AgriGreenDark,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBackButton) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "पीछे जाएं",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(2.dp))
                }

                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pappu_tractor),
                        contentDescription = "Pappu Tractor Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp))
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AgriGoldHarvest
                        ) {
                            Text(
                                text = "V5.0",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        color = Color(0xFFC8E6C9),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }

            // Quick Actions Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Call Quick Action
                IconButton(
                    onClick = { ContactUtils.dialPhoneNumber(context, phoneToDial) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E7D32))
                        .testTag("top_app_bar_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "कॉल करें",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Ayurvedic Store Quick Button
                IconButton(
                    onClick = onAyurvedicStoreClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF388E3C))
                        .testTag("top_app_bar_ayurveda_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalPharmacy,
                        contentDescription = "आयुर्वेदिक स्टोर",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Customer Directory Button
                IconButton(
                    onClick = onCustomerDirectoryClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("top_app_bar_customers_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "किसान निर्देशिका",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Settings Button
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("top_app_bar_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "सेटिंग्स",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Admin Portal Icon
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("top_app_bar_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "व्यवस्थापक पोर्टल",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Reviews Button
                IconButton(
                    onClick = onReviewsClick,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .testTag("top_app_bar_reviews_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "समीक्षाएं",
                        tint = Color(0xFFFFD54F),
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
        }
    }
}
