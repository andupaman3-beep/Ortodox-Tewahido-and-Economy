package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.state.AppScreen
import com.example.ui.state.AppViewModel
import com.example.ui.state.LanguageCode
import com.example.ui.state.Strings
import com.example.ui.theme.*

data class NavItemSpec(
    val screen: AppScreen,
    val relatedScreens: List<AppScreen>,
    val labelKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

val MainNavItems = listOf(
    NavItemSpec(
        screen = AppScreen.LANDING_HOME,
        relatedScreens = listOf(AppScreen.LANDING_HOME),
        labelKey = "home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home_tab"
    ),
    NavItemSpec(
        screen = AppScreen.JOBS,
        relatedScreens = listOf(AppScreen.JOBS, AppScreen.JOB_DETAIL),
        labelKey = "jobs",
        selectedIcon = Icons.Filled.Work,
        unselectedIcon = Icons.Outlined.WorkOutline,
        testTag = "nav_jobs_tab"
    ),
    NavItemSpec(
        screen = AppScreen.MARKETPLACE,
        relatedScreens = listOf(AppScreen.MARKETPLACE, AppScreen.PRODUCT_DETAIL),
        labelKey = "marketplace",
        selectedIcon = Icons.Filled.Storefront,
        unselectedIcon = Icons.Outlined.Storefront,
        testTag = "nav_market_tab"
    ),
    NavItemSpec(
        screen = AppScreen.SERVICES,
        relatedScreens = listOf(AppScreen.SERVICES, AppScreen.SERVICE_DETAIL),
        labelKey = "services",
        selectedIcon = Icons.Filled.Handyman,
        unselectedIcon = Icons.Outlined.Handyman,
        testTag = "nav_services_tab"
    ),
    NavItemSpec(
        screen = AppScreen.MESSAGES,
        relatedScreens = listOf(AppScreen.MESSAGES, AppScreen.CHAT),
        labelKey = "messages",
        selectedIcon = Icons.Filled.Chat,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline,
        testTag = "nav_messages_tab"
    )
)

/**
 * Modern, responsive BottomNavigation component establishing a consistent
 * color palette: Deep Navy, Royal Blue, and Gold accents.
 */
@Composable
fun AppBottomNav(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val conversations by viewModel.userConversations.collectAsState()
    val hasUnreadMsg = conversations.any { it.unreadCountUser2 > 0 }

    NavigationBar(
        containerColor = if (isDark) DeepPurpleDark else FooterLightPurple,
        contentColor = if (isDark) Color.White else FooterDeepPurple,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars,
        modifier = modifier.testTag("app_bottom_nav")
    ) {
        MainNavItems.forEach { item ->
            val isSelected = currentScreen in item.relatedScreens
            val isMessagesTab = item.screen == AppScreen.MESSAGES

            NavigationBarItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(item.screen) },
                icon = {
                    if (isMessagesTab && hasUnreadMsg) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AccentGoldBright, // Gold #FFD700 accent for badge
                                    contentColor = FooterDeepPurple
                                ) {
                                    Text("!", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = Strings.get(item.labelKey, lang),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = Strings.get(item.labelKey, lang),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = Strings.get(item.labelKey, lang),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    // Professional Royal Blue (#4169E1), Deep Purple (#4B0082) and Gold (#FFD700) palette
                    selectedIconColor = if (isDark) AccentGoldBright else FooterDeepPurple,
                    selectedTextColor = if (isDark) AccentGoldBright else FooterDeepPurple,
                    indicatorColor = if (isDark) Color(0xFF581C87) else Color(0xFFDDD6FE), // Soft Lavender Indicator
                    unselectedIconColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF6B7280),
                    unselectedTextColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF6B7280)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

/**
 * Modern, responsive NavigationRail for wide/tablet displays,
 * providing the canonical side navigation layout.
 */
@Composable
fun AppNavigationRail(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val conversations by viewModel.userConversations.collectAsState()
    val hasUnreadMsg = conversations.any { it.unreadCountUser2 > 0 }

    NavigationRail(
        containerColor = if (isDark) DarkSurface else BrandNavy,
        contentColor = Color.White,
        header = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AccentGoldBright),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = "Emblem",
                        tint = BrandNavyDark,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        modifier = modifier.testTag("app_navigation_rail")
    ) {
        Spacer(modifier = Modifier.weight(1f))
        MainNavItems.forEach { item ->
            val isSelected = currentScreen in item.relatedScreens
            val isMessagesTab = item.screen == AppScreen.MESSAGES

            NavigationRailItem(
                selected = isSelected,
                onClick = { viewModel.navigateTo(item.screen) },
                icon = {
                    if (isMessagesTab && hasUnreadMsg) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = AccentGoldBright,
                                    contentColor = BrandNavyDark
                                ) {
                                    Text("!", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = Strings.get(item.labelKey, lang),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = Strings.get(item.labelKey, lang),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = Strings.get(item.labelKey, lang),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = BrandNavyDark,
                    selectedTextColor = AccentGoldLight,
                    indicatorColor = AccentGoldBright,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
