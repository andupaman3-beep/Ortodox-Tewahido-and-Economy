package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.ui.components.StarRatingBar
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

private enum class ProfileTab {
    OVERVIEW,
    EDIT_PROFILE
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val userReviews by viewModel.currentSelectedUserReviews.collectAsState()
    val isAuthLoading by viewModel.isAuthLoading.collectAsState()
    val firebaseUser by viewModel.firebaseUser.collectAsState()

    val profileUser = selectedUser ?: currentUser
    val isMyProfile = selectedUser == null || selectedUser?.id == currentUser?.id

    var activeTab by remember { mutableStateOf(ProfileTab.OVERVIEW) }
    var showVerifyDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    if (selectedUser != null) {
        BackHandler {
            viewModel.selectedUser.value = null
            viewModel.navigateBack()
        }
    }

    if (profileUser == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(RoyalBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የመገለጫ አካውንት አልተገኘም" else "No Profile Selected",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                    )
                    Text(
                        text = if (lang == LanguageCode.AMHARIC)
                            "እባክዎ መለያዎን ይፍጠሩ ወይም በ Google አካውንት ይግቡ።"
                        else
                            "Please create an account or sign in with Google to view and manage your profile.",
                        fontSize = 13.sp,
                        color = if (isDark) DarkTextSecondary else LightTextSecondary,
                        lineHeight = 18.sp
                    )
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Login, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In with Google / Register", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    val u = profileUser

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back Navigation Bar when viewing another user
        if (!isMyProfile) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectedUser.value = null
                            viewModel.navigateBack()
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = RoyalBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ወደ ቀዳሚው ተመለስ" else "Back to Listings",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RoyalBlue
                    )
                }
            }
        }

        // 1. HERO PROFILE CARD (Royal Blue gradient & Gold emblem border)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card"),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    // Header Banner with Royal Blue gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF1E3A8A), // Deep Royal Navy
                                        Color(0xFF2563EB), // Royal Blue
                                        Color(0xFF3B82F6)  // Light Vibrant Blue
                                    )
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Verified,
                                        contentDescription = null,
                                        tint = AccentGoldBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = u.primaryRole,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            if (isMyProfile) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.clickable {
                                        viewModel.refreshProfileFromFirestore()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Sync from Firestore",
                                        tint = AccentGoldBright,
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // User Identity Row (Avatar overlapping banner)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-44).dp)
                        ) {
                            // Avatar with Radiant Gold Border (#FFD700)
                            Surface(
                                shape = CircleShape,
                                color = RoyalBlue,
                                border = androidx.compose.foundation.BorderStroke(3.dp, AccentGoldBright),
                                shadowElevation = 6.dp,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = u.fullName.take(1).uppercase(),
                                        color = AccentGoldBright,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = u.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextPrimary else BrandNavyDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "@${u.username}",
                                    fontSize = 12.sp,
                                    color = if (isDark) DarkTextSecondary else LightTextSecondary
                                )
                                if (u.profession.isNotBlank()) {
                                    Text(
                                        text = u.profession,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoyalBlue
                                    )
                                }
                            }
                        }

                        // Adjustment for negative offset
                        Spacer(modifier = Modifier.height((-28).dp))

                        // Business, Verification & Rating
                        if (u.businessName.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Storefront,
                                    contentDescription = null,
                                    tint = AccentGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = u.businessName,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isDark) DarkTextPrimary else LightTextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerificationBadge(status = u.verificationStatus, lang = lang)
                            StarRatingBar(rating = u.rating, reviewCount = u.reviewCount)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Metadata Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "አካባቢ" else "Location",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = u.location.take(14),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextPrimary else BrandNavyDark
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "የተቀላቀሉት" else "Joined",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = u.joinedDate,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextPrimary else BrandNavyDark
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Firestore Status",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CloudDone,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "Cloud Sync",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. TAB SWITCHER (For own profile: Overview vs Edit Profile)
        if (isMyProfile) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDark) DarkSurface else RoyalBlueSoft,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Surface(
                            onClick = { activeTab = ProfileTab.OVERVIEW },
                            shape = RoundedCornerShape(9.dp),
                            color = if (activeTab == ProfileTab.OVERVIEW) RoyalBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_overview")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = if (activeTab == ProfileTab.OVERVIEW) AccentGoldBright else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "አጠቃላይ መገለጫ" else "Profile View",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (activeTab == ProfileTab.OVERVIEW) Color.White else (if (isDark) DarkTextSecondary else LightTextSecondary)
                                )
                            }
                        }

                        Surface(
                            onClick = { activeTab = ProfileTab.EDIT_PROFILE },
                            shape = RoundedCornerShape(9.dp),
                            color = if (activeTab == ProfileTab.EDIT_PROFILE) RoyalBlue else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tab_edit_profile")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = if (activeTab == ProfileTab.EDIT_PROFILE) AccentGoldBright else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "መገለጫ አድስ (Firestore)" else "Edit Profile (Firestore)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (activeTab == ProfileTab.EDIT_PROFILE) Color.White else (if (isDark) DarkTextSecondary else LightTextSecondary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // CONTENT DISPLAY BASED ON ACTIVE TAB
        // ==========================================
        if (!isMyProfile || activeTab == ProfileTab.OVERVIEW) {
            // 3A. BIO SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_bio_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FormatQuote,
                                contentDescription = null,
                                tint = RoyalBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (lang == LanguageCode.AMHARIC) "የሙያና የሕይወት መግለጫ (Bio)" else "Professional Bio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isDark) DarkTextPrimary else BrandNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (u.bio.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isDark) DarkBackground else RoyalBlueSoft.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = u.bio,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp,
                                    color = if (isDark) DarkTextPrimary else LightTextPrimary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        } else {
                            Text(
                                text = if (lang == LanguageCode.AMHARIC)
                                    "ምንም መግለጫ አልተጻፈም። መገለጫዎን በማደስ ስለ ሙያዎ እና ስለስራ ልምድዎ ይግለጹ።"
                                else
                                    "No biography written yet. Click 'Edit Profile' to add your bio and highlight your professional background.",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 3B. SKILLS & COMPETENCIES SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_skills_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Stars,
                                    contentDescription = null,
                                    tint = AccentGoldBright,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "የሙያ ክህሎቶችና አገልግሎቶች" else "Skills & Core Competencies",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isDark) DarkTextPrimary else BrandNavyDark
                                )
                            }

                            if (isMyProfile) {
                                TextButton(onClick = { activeTab = ProfileTab.EDIT_PROFILE }) {
                                    Text("Add / Edit", fontSize = 11.5.sp, color = RoyalBlue, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val skillsList = u.skills.split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }

                        if (skillsList.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                skillsList.forEach { skill ->
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = AccentGoldLight,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright),
                                        shadowElevation = 1.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFFB45309), // Warm Gold-Amber
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = skill,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = BrandNavyDark
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = if (lang == LanguageCode.AMHARIC)
                                    "እስካሁን ምንም ክህሎት አልተመዘገበም። በ'መገለጫ አድስ' ክፍል ውስጥ ክህሎቶችን ይጨምሩ።"
                                else
                                    "No skills added yet. Use the 'Edit Profile' tab to list your specialties and technical competencies.",
                                fontSize = 12.5.sp,
                                color = TextMuted,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // 3C. CONTACT INFORMATION SECTION
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_contact_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContactPhone,
                                contentDescription = null,
                                tint = RoyalBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (lang == LanguageCode.AMHARIC) "የመገናኛ አድራሻዎች" else "Contact Information",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isDark) DarkTextPrimary else BrandNavyDark
                            )
                        }

                        // Phone item
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) DarkBackground else RoyalBlueSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Phone,
                                        contentDescription = null,
                                        tint = AccentGoldBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == LanguageCode.AMHARIC) "ስልክ ቁጥር" else "Phone Number",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = u.phone.ifBlank { "Not provided" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                                    )
                                }
                            }
                        }

                        // Email item
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) DarkBackground else RoyalBlueSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = null,
                                        tint = AccentGoldBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == LanguageCode.AMHARIC) "ኢሜይል" else "Email Address",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = u.email.ifBlank { "Google authenticated" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                                    )
                                }
                            }
                        }

                        // Telegram item
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) DarkBackground else RoyalBlueSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Send,
                                        contentDescription = null,
                                        tint = AccentGoldBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Telegram",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = if (u.telegramHandle.isNotBlank()) "@${u.telegramHandle}" else "Not provided",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                                    )
                                }
                            }
                        }

                        // Parish & Location item
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) DarkBackground else RoyalBlueSoft,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(RoyalBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = AccentGoldBright,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == LanguageCode.AMHARIC) "አካባቢ / ደብር" else "City & Parish Location",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = u.location.ifBlank { "Addis Ababa" },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3D. ACTION BUTTONS & SHORTCUTS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (isMyProfile) {
                            // Verification Request Button if not yet verified
                            if (u.verificationStatus != VerificationStatus.VERIFIED.name) {
                                Button(
                                    onClick = { showVerifyDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGoldBright),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("request_verification_btn")
                                ) {
                                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = BrandNavyDark)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (lang == LanguageCode.AMHARIC)
                                            "የተረጋገጠ አባልነት ባጅ ጠይቅ (Request Badge)"
                                        else
                                            "Request Orthodox Verification Badge",
                                        color = BrandNavyDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Hub Shortcuts Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    onClick = { viewModel.navigateTo(AppScreen.CV_BUILDER) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = CatJobBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CatJobColor.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Description, contentDescription = null, tint = CatJobColor, modifier = Modifier.size(18.dp))
                                        Text("CV / Resume", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandNavyDark)
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.navigateTo(AppScreen.DOCUMENTS) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = CatServiceBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CatServiceColor.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Folder, contentDescription = null, tint = CatServiceColor, modifier = Modifier.size(18.dp))
                                        Text("Documents", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandNavyDark)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    onClick = { viewModel.navigateTo(AppScreen.FAVORITES) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = CatMarketBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CatMarketColor.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = CatMarketColor, modifier = Modifier.size(18.dp))
                                        Text("Saved Items", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandNavyDark)
                                    }
                                }

                                Surface(
                                    onClick = { viewModel.navigateTo(AppScreen.SELLER_DASHBOARD) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = CatB2BBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CatB2BColor.copy(alpha = 0.3f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Storefront, contentDescription = null, tint = CatB2BColor, modifier = Modifier.size(18.dp))
                                        Text("Seller Hub", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = BrandNavyDark)
                                    }
                                }
                            }

                            // Firebase Auth Card
                            if (firebaseUser != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AccentGoldLight,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.CloudDone,
                                                contentDescription = null,
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Column {
                                                Text(
                                                    text = "Firestore Cloud Active",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = BrandNavyDark
                                                )
                                                Text(
                                                    text = firebaseUser?.email ?: "",
                                                    fontSize = 11.sp,
                                                    color = LightTextSecondary
                                                )
                                            }
                                        }
                                        TextButton(onClick = { viewModel.signOutFirebase() }) {
                                            Text("Sign Out", fontSize = 11.sp, color = StatusError, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(AppScreen.AUTH) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Filled.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (lang == LanguageCode.AMHARIC) "በ Google መለያ ግባ / ቀይር" else "Google Sign-In / Account Switch",
                                    fontSize = 12.sp
                                )
                            }
                        } else {
                            // Viewing someone else
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.openChatWith(u.id, u.fullName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = null, tint = AccentGoldBright)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send Message", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { showReviewDialog = true },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = AccentGoldBright, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Review", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 3E. REVIEWS LIST
            item {
                Text(
                    text = if (lang == LanguageCode.AMHARIC) "የተሰጡ አስተያየቶችና ደረጃዎች" else "Reviews & Ratings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isDark) DarkTextPrimary else BrandNavyDark
                )
            }

            if (userReviews.isEmpty()) {
                item {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "እስካሁን ምንም አስተያየት አልተሰጠም።" else "No reviews recorded yet.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            } else {
                items(userReviews) { rev ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = rev.authorName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                StarRatingBar(rating = rev.rating.toFloat())
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = rev.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // EDIT PROFILE TAB (LIVE FIRESTORE EDITOR)
            // ==========================================
            item {
                EditProfileFormCard(
                    user = u,
                    lang = lang,
                    isDark = isDark,
                    isAuthLoading = isAuthLoading,
                    onSave = { bio, skills, phone, loc, tele, prof, name ->
                        viewModel.updateUserProfile(
                            bio = bio,
                            skills = skills,
                            phone = phone,
                            location = loc,
                            telegramHandle = tele,
                            profession = prof,
                            fullName = name
                        ) {
                            activeTab = ProfileTab.OVERVIEW
                        }
                    },
                    onRefreshCloud = {
                        viewModel.refreshProfileFromFirestore()
                    },
                    onCancel = {
                        activeTab = ProfileTab.OVERVIEW
                    }
                )
            }
        }
    }

    if (showVerifyDialog) {
        VerificationRequestDialog(
            lang = lang,
            onDismiss = { showVerifyDialog = false },
            onSubmit = { note ->
                viewModel.submitVerificationRequest(note)
                showVerifyDialog = false
            }
        )
    }

    if (showReviewDialog) {
        AddReviewDialog(
            targetUserName = u.fullName,
            lang = lang,
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.submitReview(u.id, rating, comment)
                showReviewDialog = false
            }
        )
    }

    if (showRegisterDialog) {
        RegisterAccountDialog(
            lang = lang,
            onDismiss = { showRegisterDialog = false },
            onSubmit = { name, email, username, role, secRoles, prof, bName, loc, phone, bio ->
                viewModel.registerUser(name, email, username, role, secRoles, prof, bName, loc, phone, bio)
                showRegisterDialog = false
            }
        )
    }
}

/**
 * Interactive Profile Editor Form with Firestore Data Model integration,
 * Dynamic Skills Tag Manager, Bio Editor, and Contact Fields.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditProfileFormCard(
    user: UserEntity,
    lang: LanguageCode,
    isDark: Boolean,
    isAuthLoading: Boolean,
    onSave: (bio: String, skills: String, phone: String, loc: String, tele: String, prof: String, name: String) -> Unit,
    onRefreshCloud: () -> Unit,
    onCancel: () -> Unit
) {
    var fullName by remember(user) { mutableStateOf(user.fullName) }
    var profession by remember(user) { mutableStateOf(user.profession) }
    var bio by remember(user) { mutableStateOf(user.bio) }
    var phone by remember(user) { mutableStateOf(user.phone) }
    var location by remember(user) { mutableStateOf(user.location) }
    var telegramHandle by remember(user) { mutableStateOf(user.telegramHandle) }

    // Skills dynamic chip management
    var currentSkills by remember(user) {
        mutableStateOf(
            user.skills.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toMutableList()
        )
    }
    var newSkillInput by remember { mutableStateOf("") }

    val suggestedSkills = listOf(
        "Carpentry & Woodwork",
        "Electrical Installation",
        "Accounting & Auditing",
        "Android & Web Dev",
        "Church Art & Painting",
        "Architecture & CAD",
        "Translation (Ge'ez/Amharic/En)",
        "Tailoring & Vestments",
        "Healthcare & Nursing",
        "Agri-Business"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("edit_profile_form_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(RoyalBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = null,
                        tint = AccentGoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የመገለጫ መረጃ ማሻሻያ (Firestore)" else "Update Profile (Firestore)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                    )
                    Text(
                        text = if (lang == LanguageCode.AMHARIC)
                            "ለውጦች በቀጥታ ወደ Firestore ደመና ዳታቤዝ ይቀመጣሉ።"
                        else
                            "Updates sync immediately to Cloud Firestore and your local device.",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // 1. Basic Info
            Text(
                text = "1. Personal & Professional Identity",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalBlue
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name / ሙሉ ስም") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("name_input")
            )

            OutlinedTextField(
                value = profession,
                onValueChange = { profession = it },
                label = { Text("Profession / Title (e.g. Senior Carpenter, Accountant)") },
                leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profession_input")
            )

            // 2. Bio Field
            Text(
                text = "2. Bio / የህይወትና የሙያ ታሪክ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalBlue
            )

            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 1000) bio = it },
                label = { Text("Professional Bio (Faith, Mission, Experience)") },
                placeholder = {
                    Text(
                        text = "Describe your expertise, work ethic, and dedication to delivering faithful, quality services...",
                        fontSize = 12.sp
                    )
                },
                supportingText = {
                    Text(
                        text = "${bio.length} / 1000 characters",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )
                },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bio_input")
            )

            // 3. Dynamic Skills Chip Management
            Text(
                text = "3. Skills & Competencies / ሙያዎችና ክህሎቶች",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalBlue
            )

            // Display current skill chips with remove buttons
            if (currentSkills.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentSkills.forEach { s ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AccentGoldLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright)
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = s,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandNavyDark
                                )
                                IconButton(
                                    onClick = {
                                        currentSkills = currentSkills.toMutableList().apply { remove(s) }
                                    },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove skill",
                                        tint = Color(0xFFB45309),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No skills added yet. Type below or tap a suggestion.",
                    fontSize = 11.5.sp,
                    color = TextMuted
                )
            }

            // Input to add a new skill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newSkillInput,
                    onValueChange = { newSkillInput = it },
                    label = { Text("Add New Skill") },
                    placeholder = { Text("e.g. Carpentry, Python, Tax filing") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("skills_input")
                )

                Button(
                    onClick = {
                        val trimmed = newSkillInput.trim()
                        if (trimmed.isNotBlank() && !currentSkills.contains(trimmed)) {
                            currentSkills = currentSkills.toMutableList().apply { add(trimmed) }
                            newSkillInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    modifier = Modifier.testTag("add_skill_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = AccentGoldBright)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Quick Suggestions
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Quick suggestions (tap to add):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(suggestedSkills.filter { !currentSkills.contains(it) }) { sugg ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                currentSkills = currentSkills.toMutableList().apply { add(sugg) }
                            },
                            label = { Text(sugg, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = null, modifier = Modifier.size(12.dp))
                            }
                        )
                    }
                }
            }

            // 4. Contact Information Fields
            Text(
                text = "4. Contact & Location Information",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalBlue
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number / ስልክ ቁጥር") },
                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("phone_input")
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location / Parish (e.g. Addis Ababa / Bole Medhanealem)") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_input")
            )

            OutlinedTextField(
                value = telegramHandle,
                onValueChange = { telegramHandle = it.removePrefix("@") },
                label = { Text("Telegram Username (e.g. orthodox_pro)") },
                prefix = { Text("@", color = RoyalBlue, fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Filled.Send, contentDescription = null, tint = RoyalBlue) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("telegram_input")
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Action Buttons: Save to Firestore vs Cancel
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val joinedSkills = currentSkills.joinToString(", ")
                        onSave(bio, joinedSkills, phone, location, telegramHandle, profession, fullName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_button"),
                    enabled = !isAuthLoading
                ) {
                    if (isAuthLoading) {
                        CircularProgressIndicator(
                            color = AccentGoldBright,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing to Firestore...", color = Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Filled.Save, contentDescription = null, tint = AccentGoldBright)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "መረጃ አስቀምጥ & ወደ Firestore ላክ"
                            else
                                "Save & Sync to Cloud Firestore",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefreshCloud,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("refresh_firestore_button")
                    ) {
                        Icon(Icons.Filled.CloudDownload, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pull from Cloud", fontSize = 11.5.sp)
                    }

                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (lang == LanguageCode.AMHARIC) "ሰርዝ" else "Cancel", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun VerificationRequestDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var parishName by remember { mutableStateOf("") }
    var priestName by remember { mutableStateOf("") }
    var licenseNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "የኦርቶዶክስ አባልነት ማረጋገጫ ጥያቄ" else "Request Orthodox Verification",
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (lang == LanguageCode.AMHARIC)
                        "በመድረካችን ላይ የታመነ የክርስቲያን አባልነት ባጅ ለማግኘት የደብርዎን ስም እና የንስሐ አባትዎን ያሳውቁን።"
                    else
                        "To obtain the Golden Verified Badge, submit your local Parish and Father Confessor details for review.",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                OutlinedTextField(
                    value = parishName,
                    onValueChange = { parishName = it },
                    label = { Text("Parish / የደብር ወይም የገዳም ስም") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priestName,
                    onValueChange = { priestName = it },
                    label = { Text("Father Confessor / የንስሐ አባት ስም") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = licenseNote,
                    onValueChange = { licenseNote = it },
                    label = { Text("Trade License TIN or ID note (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fullNote = "Parish: $parishName | Priest: $priestName | Note: $licenseNote"
                    onSubmit(fullNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGoldBright)
            ) {
                Text(Strings.get("submit", lang), color = BrandNavyDark, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.get("cancel", lang)) }
        }
    )
}

@Composable
private fun AddReviewDialog(
    targetUserName: String,
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate & Review $targetUserName", fontWeight = FontWeight.Bold, color = BrandNavy) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = AccentGoldBright,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Write your honest review") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (comment.isNotBlank()) onSubmit(rating, comment) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("Submit Review", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun RegisterAccountDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, UserRole, List<UserRole>, String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.JOB_SEEKER) }
    var profession by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Addis Ababa") }
    var phone by remember { mutableStateOf("+2519") }
    var bio by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "አዲስ የተጠቃሚ መለያ መዝግብ" else "Register Account",
                fontWeight = FontWeight.Bold,
                color = BrandNavy
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }) }
                item { OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }) }
                item { OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }) }
                item {
                    Text("Select Primary Role:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(UserRole.JOB_SEEKER, UserRole.EMPLOYER, UserRole.SELLER, UserRole.SERVICE_PROVIDER).forEach { r ->
                            FilterChip(
                                selected = selectedRole == r,
                                onClick = { selectedRole = r },
                                label = { Text(r.displayNameEn.take(9), fontSize = 10.sp) }
                            )
                        }
                    }
                }
                item { OutlinedTextField(value = profession, onValueChange = { profession = it }, label = { Text("Profession / Skill") }) }
                item { OutlinedTextField(value = businessName, onValueChange = { businessName = it }, label = { Text("Business Name (if any)") }) }
                item { OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }) }
                item { OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }) }
                item { OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Short Bio") }, minLines = 2) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onSubmit(name, email, username, selectedRole, emptyList(), profession, businessName, location, phone, bio)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text("Register", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
