package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()
    val unreadNotifs = notifications.count { !it.isRead }
    val allUsers by viewModel.allUsers.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showPersonaMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_top_bar"),
        color = if (isDark) DarkSurface else ElectricBlueHeader,
        shadowElevation = 5.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isDark) {
                        Brush.verticalGradient(listOf(DarkSurface, DarkBackground))
                    } else {
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF1E3A8A), // Deep Royal Blue
                                Color(0xFF1D4ED8), // Vivid Sapphire Blue
                                Color(0xFF2563EB)  // Bright Vibrant Blue
                            )
                        )
                    }
                )
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            // Main Top Bar Row: Brand Header & Main Utilities
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Official Circular Emblem & Full Title (Expands flexibly, never truncated)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.LANDING_HOME) }
                        .weight(1f)
                ) {
                    // Official Circular Emblem from User's uploaded badge
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(2.dp, AccentGoldBright),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Orthodoxy and Economy Official Emblem",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = Strings.get("app_name", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 18.sp,
                            maxLines = 2,
                            softWrap = true
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Strings.get("tagline", lang).substringBefore("—").trim(),
                            fontSize = 10.5.sp,
                            color = AccentGoldLight,
                            lineHeight = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Right: Action Icons (Theme, Notification, Profile Avatar)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    // Dark/Light Mode Toggle
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("theme_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = Strings.get("switch_theme", lang),
                            tint = if (isDark) AccentGoldBright else Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Notifications Bell with Badge
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("notifications_top_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifs > 0) {
                                    Badge(
                                        containerColor = StatusWarning,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifs", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = Strings.get("notifications", lang),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // User Avatar / Persona Switcher
                    Box {
                        Surface(
                            onClick = { showPersonaMenu = true },
                            shape = CircleShape,
                            color = AccentGoldLight,
                            border = androidx.compose.foundation.BorderStroke(2.dp, AccentGoldBright),
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("profile_avatar_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentUser?.fullName?.take(1) ?: "U",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BrandNavyDark
                                )
                            }
                        }

                        // Persona Dropdown Menu
                        DropdownMenu(
                            expanded = showPersonaMenu,
                            onDismissRequest = { showPersonaMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = currentUser?.fullName ?: "Guest",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = currentUser?.primaryRole ?: "Member",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = BrandNavy)
                                },
                                onClick = {
                                    showPersonaMenu = false
                                    viewModel.navigateTo(AppScreen.PROFILE)
                                }
                            )

                            Divider()

                            allUsers.take(5).forEach { user ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${user.fullName} (${user.primaryRole})",
                                            fontSize = 12.sp,
                                            fontWeight = if (user.id == currentUser?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = if (user.id == currentUser?.id) Icons.Filled.CheckCircle else Icons.Outlined.Person,
                                            contentDescription = null,
                                            tint = if (user.id == currentUser?.id) EmeraldSuccess else Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        viewModel.switchUser(user)
                                        showPersonaMenu = false
                                    }
                                )
                            }

                            Divider()

                            DropdownMenuItem(
                                text = { Text(Strings.get("admin", lang), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = AccentGold) },
                                onClick = {
                                    showPersonaMenu = false
                                    viewModel.navigateTo(AppScreen.ADMIN)
                                }
                            )
                        }
                    }
                }
            }

            // Sub-Row: Location Selector Chip, Full Language Pill, and AI Assistant
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location badge
                Surface(
                    onClick = { showLocationDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier.height(28.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = null,
                            tint = AccentGoldBright,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = currentLocation.substringBefore(","),
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Full Language Switch Pill
                Surface(
                    onClick = { showLanguageDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("lang_selector_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = lang.flag, fontSize = 12.sp)
                        Text(
                            text = when (lang) {
                                LanguageCode.AMHARIC -> "አማርኛ"
                                LanguageCode.ENGLISH -> "English"
                                LanguageCode.OROMO -> "Oromoo"
                                LanguageCode.TIGRINYA -> "ትግርኛ"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = AccentGoldLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                // Tewahido Chat Quick Pill
                Surface(
                    onClick = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                    shape = RoundedCornerShape(12.dp),
                    color = AccentGoldBright.copy(alpha = 0.22f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("tewahido_chat_quick_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Forum,
                            contentDescription = null,
                            tint = AccentGoldBright,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "ተዋሕዶ Chat",
                            color = AccentGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Language, contentDescription = null, tint = BrandNavy)
                    Text(
                        text = Strings.get("select_language", lang),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageCode.values().forEach { langOption ->
                        val isSelected = langOption == lang
                        Surface(
                            onClick = {
                                viewModel.setLanguage(langOption)
                                showLanguageDialog = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) RoyalBlueSoft else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, RoyalBlue) else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(text = langOption.flag, fontSize = 22.sp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = langOption.nativeName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) RoyalBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = langOption.displayName,
                                        fontSize = 12.sp,
                                        color = LightTextSecondary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = RoyalBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(Strings.get("cancel", lang), color = RoyalBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Location Selector Dialog
    if (showLocationDialog) {
        val locations = listOf(
            "Addis Ababa, Ethiopia",
            "Bahir Dar, Amhara",
            "Hawassa, Sidama",
            "Dire Dawa, Ethiopia",
            "Gondar, Amhara",
            "Mekelle, Tigray",
            "Adama (Nazret), Oromia",
            "Jimma, Oromia",
            "Bishoftu (Debre Zeyit), Oromia",
            "Dessie, Amhara",
            "Global Diaspora (US / Europe)"
        )
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = StatusWarning)
                    Text(
                        text = Strings.get("select_location", lang),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    locations.take(6).forEach { loc ->
                        val isSelected = loc == currentLocation
                        Surface(
                            onClick = {
                                viewModel.setLocation(loc)
                                showLocationDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) AccentGoldLight.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationCity,
                                    contentDescription = null,
                                    tint = if (isSelected) AccentGold else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = loc,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text(Strings.get("cancel", lang), color = BrandNavy, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
