package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgriGreenContainer
import com.example.ui.theme.AgriGreenDark
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.viewmodel.BottomTab

@Composable
fun PappuBottomNavBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(68.dp)
            .testTag("bottom_nav_bar"),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple(BottomTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
            Triple(BottomTab.SERVICES, Icons.Filled.Agriculture, Icons.Outlined.Agriculture),
            Triple(BottomTab.BOOKING, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
            Triple(BottomTab.TRACK, Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping),
            Triple(BottomTab.CONTACT, Icons.Filled.Call, Icons.Outlined.Call)
        )

        items.forEach { (tab, filledIcon, outlinedIcon) ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = tab.titleHindi
                    )
                },
                label = {
                    Text(
                        text = tab.titleHindi,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AgriGreenDark,
                    selectedTextColor = AgriGreenPrimary,
                    indicatorColor = AgriGreenContainer,
                    unselectedIconColor = Color(0xFF6B7280),
                    unselectedTextColor = Color(0xFF6B7280)
                ),
                modifier = Modifier.testTag("nav_item_${tab.routeName}")
            )
        }
    }
}
