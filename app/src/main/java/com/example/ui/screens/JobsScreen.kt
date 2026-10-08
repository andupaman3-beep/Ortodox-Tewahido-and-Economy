package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.CategoryChip
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val jobs by viewModel.allJobs.collectAsState()
    val userApplications by viewModel.userApplications.collectAsState()

    var selectedTab by remember { mutableStateOf("ALL") } // ALL, FULL_TIME, PART_TIME, CONTRACT, DAILY_RATE, REMOTE
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showMyApplicationsSheet by remember { mutableStateOf(false) }

    val categories = listOf(
        "All",
        "የሱቅና ሽያጭ (Retail)",
        "መኪና አሽከርካሪ (Drivers)",
        "እደ-ጥበብና ግንባታ (Trades)",
        "ምግብ ዝግጅት (Hospitality)",
        "ክብካቤ (Home & Care)",
        "ኤሌክትሪክና ቧንቧ (Electrical)",
        "ባህልና ጥበብ (Crafts)",
        "ፋይናንስና ሂሳብ (Accounting)",
        "ቴክኖሎጂ (Technology)"
    )

    val filteredJobs = jobs.filter { job ->
        val matchesTab = when (selectedTab) {
            "FULL_TIME" -> job.projectType == "FULL_TIME" || job.employmentType == EmploymentType.FULL_TIME.name
            "PART_TIME" -> job.projectType == "PART_TIME" || job.employmentType == EmploymentType.PART_TIME.name
            "CONTRACT" -> job.projectType == "CONTRACT" || job.employmentType == EmploymentType.CONTRACT.name
            "DAILY_RATE" -> job.projectType == "DAILY_RATE" || job.employmentType == EmploymentType.DAILY_RATE.name
            "REMOTE" -> job.projectType == "REMOTE" || job.employmentType == EmploymentType.REMOTE.name
            else -> true
        }
        val matchesCategory = (selectedCategory == "All") || job.category.contains(selectedCategory.substringBefore("(").trim(), ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                job.title.contains(searchQuery, ignoreCase = true) ||
                job.amharicTitle.contains(searchQuery, ignoreCase = true) ||
                job.location.contains(searchQuery, ignoreCase = true) ||
                job.employerName.contains(searchQuery, ignoreCase = true) ||
                job.skillsRequired.contains(searchQuery, ignoreCase = true)

        matchesTab && matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = modifier.testTag("jobs_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = RoyalBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null, tint = AccentGoldBright) },
                text = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የሥራ ማስታወቂያ ለጥፍ" else "Post a Job Vacancy",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("post_job_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Search Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "የሥራ ማስታወቂያ፣ የሙያ ዘርፍ ወይም ከተማ/አካባቢ ይፈልጉ..."
                            else
                                "Search jobs by title, skills, or location in Ethiopia...",
                            fontSize = 12.5.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = RoyalBlue) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("jobs_search_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Domestic Employment Type Selector (Full-time, Part-time, Contract, Daily Rate, Remote)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTab == "ALL",
                        onClick = { selectedTab = "ALL" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "ሁሉም ሥራ" else "All Jobs", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalBlue, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTab == "FULL_TIME",
                        onClick = { selectedTab = "FULL_TIME" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "📌 ቋሚ / ሙሉ ሰዓት" else "📌 Full-Time", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalBlue, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTab == "PART_TIME",
                        onClick = { selectedTab = "PART_TIME" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "⏱ የትርፍ ሰዓት" else "⏱ Part-Time", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF7C3AED), selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTab == "CONTRACT",
                        onClick = { selectedTab = "CONTRACT" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "📋 የውል / ኮንትራት" else "📋 Contract", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD97706), selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTab == "DAILY_RATE",
                        onClick = { selectedTab = "DAILY_RATE" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "☀️ የቀን ክፍያ" else "☀️ Daily Rate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldSuccess, selectedLabelColor = Color.White)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedTab == "REMOTE",
                        onClick = { selectedTab = "REMOTE" },
                        label = { Text(if (lang == LanguageCode.AMHARIC) "🌐 የርቀት ሥራ" else "🌐 Remote", fontSize = 11.5.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0284C7), selectedLabelColor = Color.White)
                    )
                }
            }

            // Categories horizontal scroller
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val cleanLabel = if (cat == "All") {
                        if (lang == LanguageCode.AMHARIC) "ሁሉም ዘርፍ" else "All Sectors"
                    } else cat

                    CategoryChip(
                        label = cleanLabel,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            // My Active Applications Banner
            if (userApplications.isNotEmpty()) {
                Surface(
                    color = RoyalBlueSoft,
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { showMyApplicationsSheet = true },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.WorkOutline, contentDescription = null, tint = RoyalBlue)
                            Text(
                                text = if (lang == LanguageCode.AMHARIC)
                                    "የእኔ ማመልከቻዎች (${userApplications.size})"
                                else
                                    "My Submitted Applications (${userApplications.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandNavyDark
                            )
                        }
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ሁኔታውን እይ >" else "Track Status >",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    }
                }
            }

            // List of Ethiopian Jobs and Vacancies
            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.WorkOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "ምንም የሥራ ማስታወቂያ አልተገኘም"
                            else
                                "No matching job vacancies found",
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "የፍለጋ ቃሉን ወይም የሥራ ዓይነቱን ቀይረው ይሞክሩ።"
                            else
                                "Try adjusting your filter or search keywords.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredJobs) { job ->
                        EthiopianJobCard(
                            job = job,
                            lang = lang,
                            onClick = { viewModel.openJobDetail(job) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateJobVacancyDialog(
            lang = lang,
            onDismiss = { showCreateDialog = false },
            onSubmit = { title, amTitle, category, location, budget, projType, days, skills, exp, desc, deadline, phone ->
                viewModel.createProjectOrGig(
                    title = title,
                    amharicTitle = amTitle,
                    category = category,
                    location = location,
                    budget = budget,
                    projectType = projType,
                    deliveryDays = days,
                    skills = skills,
                    experience = exp,
                    desc = desc,
                    deadline = deadline
                )
                showCreateDialog = false
            }
        )
    }

    if (showMyApplicationsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMyApplicationsSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (lang == LanguageCode.AMHARIC) "የእኔ የሥራ ማመልከቻዎች" else "My Job Applications",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandNavyDark
                )

                if (userApplications.isEmpty()) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "እስካሁን ያመለከቱት ሥራ የለም።" else "No applications submitted yet.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxHeight(0.6f)
                    ) {
                        items(userApplications) { app ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = app.jobTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Surface(
                                            color = RoyalBlueSoft,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = app.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalBlue,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "የተጠየቀ ክፍያ: ${app.bidAmount.toInt()} ETB • የማስረከቢያ ጊዜ: ${app.deliveryDays} ቀናት",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                    if (app.coverNote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "\"${app.coverNote.take(80)}...\"",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Authentic Ethiopian Domestic Job Card (Full-Time, Part-Time, Contract, Daily Rate)
 */
@Composable
fun EthiopianJobCard(
    job: JobEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val (badgeText, badgeBg, badgeTextColor) = when (job.projectType) {
        "FULL_TIME" -> Triple("📌 ሙሉ ሰዓት (ቋሚ)", RoyalBlueSoft, RoyalBlue)
        "PART_TIME" -> Triple("⏱ የትርፍ ሰዓት", Color(0xFFF3E8FF), Color(0xFF7C3AED))
        "CONTRACT" -> Triple("📋 የውል ስምምነት", AccentGoldLight, Color(0xFFB45309))
        "DAILY_RATE" -> Triple("☀️ የቀን ክፍያ", EmeraldSoft, EmeraldSuccess)
        "REMOTE" -> Triple("🌐 የርቀት ሥራ", Color(0xFFE0F2FE), Color(0xFF0284C7))
        else -> Triple("📌 ${job.employmentType}", RoyalBlueSoft, RoyalBlue)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("job_item_${job.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Employment Type Pill + Category + Verified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = job.category.take(18),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                if (job.employerVerified) {
                    VerificationBadge(status = "VERIFIED", lang = lang, compact = true)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Job Title
            Text(
                text = if (lang == LanguageCode.AMHARIC && job.amharicTitle.isNotBlank()) job.amharicTitle else job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Employer and Location
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = job.employerName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandNavyDark
                )
                Text(text = "•", fontSize = 12.sp, color = Color.Gray)
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(13.dp))
                Text(
                    text = job.location,
                    fontSize = 11.5.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description Snippet
            Text(
                text = job.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            // Skills Chips
            if (job.skillsRequired.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(job.skillsRequired.split(",")) { skill ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = skill.trim(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(10.dp))

            // Salary, Turnaround & Direct Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = job.salaryRange.ifBlank { "${job.budgetAmount.toInt()} ETB" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldSuccess
                    )
                    Text(
                        text = "የማመልከቻ ቀነ ገደብ: ${job.deadline.take(10)}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Employer Button
                    Surface(
                        onClick = {
                            val phone = job.employerPhone.ifBlank { "+251911223344" }
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(dialIntent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = RoyalBlueSoft,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = "Call", tint = RoyalBlue, modifier = Modifier.size(14.dp))
                            Text(
                                text = if (lang == LanguageCode.AMHARIC) "ደውል" else "Call",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalBlue
                            )
                        }
                    }

                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ዝርዝር እይ" else "View Details",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FreelanceJobCard(
    job: JobEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    EthiopianJobCard(job = job, lang = lang, onClick = onClick)
}

/**
 * Detailed View for Domestic Ethiopian Job Vacancies
 */
@Composable
fun JobDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val context = LocalContext.current
    val lang by viewModel.language.collectAsState()
    val job by viewModel.selectedJob.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    var showProposalDialog by remember { mutableStateOf(false) }

    if (job == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(if (lang == LanguageCode.AMHARIC) "የሥራው መረጃ አልተገኘም" else "Job not found")
        }
        return
    }

    val j = job!!
    val isFav = favorites.any { it.itemId == j.id }

    val (badgeText, badgeBg, badgeTextColor) = when (j.projectType) {
        "FULL_TIME" -> Triple("📌 ሙሉ ሰዓት (ቋሚ)", RoyalBlueSoft, RoyalBlue)
        "PART_TIME" -> Triple("⏱ የትርፍ ሰዓት", Color(0xFFF3E8FF), Color(0xFF7C3AED))
        "CONTRACT" -> Triple("📋 የውል ስምምነት", AccentGoldLight, Color(0xFFB45309))
        "DAILY_RATE" -> Triple("☀️ የቀን ክፍያ", EmeraldSoft, EmeraldSuccess)
        "REMOTE" -> Triple("🌐 የርቀት ሥራ", Color(0xFFE0F2FE), Color(0xFF0284C7))
        else -> Triple("📌 ${j.employmentType}", RoyalBlueSoft, RoyalBlue)
    }

    Scaffold(
        modifier = modifier.testTag("job_detail_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = RoyalBlue)
                    }
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የሥራው ዝርዝር መረጃ" else "Job Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BrandNavyDark
                    )
                    Row {
                        IconButton(
                            onClick = {
                                viewModel.toggleFavorite(j.id, "JOB", j.title, j.salaryRange)
                            }
                        ) {
                            Icon(
                                imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) StatusRed else Color.Gray
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Call Employer Button
                    OutlinedButton(
                        onClick = {
                            val phone = j.employerPhone.ifBlank { "+251911223344" }
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                            context.startActivity(dialIntent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = null, tint = RoyalBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == LanguageCode.AMHARIC) "በስልክ ደውል" else "Call Employer", color = RoyalBlue, fontWeight = FontWeight.Bold)
                    }

                    // Apply Button
                    Button(
                        onClick = { showProposalDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("apply_job_button")
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, tint = AccentGoldBright)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ማመልከቻ አስገባ" else "Apply Now",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeTextColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (j.employerVerified) {
                                VerificationBadge(status = "VERIFIED", lang = lang)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC && j.amharicTitle.isNotBlank()) j.amharicTitle else j.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandNavyDark,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.Business, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(text = j.employerName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                            Text(text = j.location, fontSize = 12.5.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Compensation & Timeline Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(if (lang == LanguageCode.AMHARIC) "ክፍያ / ደመወዝ" else "Compensation", fontSize = 11.sp, color = TextMuted)
                                Text(j.salaryRange.ifBlank { "${j.budgetAmount.toInt()} ETB" }, fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldSuccess)
                            }
                            Column {
                                Text(if (lang == LanguageCode.AMHARIC) "ቀነ ገደብ" else "Deadline", fontSize = 11.sp, color = TextMuted)
                                Text(j.deadline.take(10), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BrandNavyDark)
                            }
                            Column {
                                Text(if (lang == LanguageCode.AMHARIC) "አመልካቾች" else "Applicants", fontSize = 11.sp, color = TextMuted)
                                Text("${j.proposalsCount} አመልካቾች", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalBlue)
                            }
                        }
                    }
                }
            }

            // Employer Contact Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalBlueSoft)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የአሠሪው መረጃና የመገናኛ አድራሻ" else "Employer Contact Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = RoyalBlue
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = j.employerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = BrandNavyDark)
                                Text(text = "ስልክ፡ ${j.employerPhone.ifBlank { "+251 911 22 33 44" }}", fontSize = 12.sp, color = TextMuted)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        val phone = j.employerPhone.ifBlank { "+251911223344" }
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(dialIntent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.Call, contentDescription = null, tint = AccentGoldBright, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ደውል", fontSize = 11.sp, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openChatWith(j.employerId, j.employerName) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Filled.Chat, contentDescription = null, tint = RoyalBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ቻት", fontSize = 11.sp, color = RoyalBlue)
                                }
                            }
                        }
                    }
                }
            }

            // Job Description & Scope
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የሥራው ዝርዝር መግለጫ" else "Job Description & Scope",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandNavyDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = j.description,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Skills & Requirements
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የሚፈለጉ ክህሎቶችና ልምድ" else "Required Skills & Experience",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BrandNavyDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ልምድ: ${j.experienceLevel}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (j.skillsRequired.isNotBlank()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(j.skillsRequired.split(",")) { skill ->
                                    Surface(
                                        color = AccentGoldLight,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentGoldBright),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = skill.trim(),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BrandNavyDark,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProposalDialog) {
        SubmitApplicationDialog(
            jobTitle = if (lang == LanguageCode.AMHARIC && j.amharicTitle.isNotBlank()) j.amharicTitle else j.title,
            suggestedBudget = j.budgetAmount,
            suggestedDays = j.deliveryDays,
            lang = lang,
            onDismiss = { showProposalDialog = false },
            onSubmit = { bid, days, availability, cover ->
                viewModel.submitProposal(j, bid, days, availability, cover)
                showProposalDialog = false
            }
        )
    }
}

/**
 * Dialog for Employers in Ethiopia to post job vacancies
 */
@Composable
private fun CreateJobVacancyDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        amTitle: String,
        category: String,
        location: String,
        budget: Double,
        projectType: String,
        deliveryDays: Int,
        skills: String,
        experience: String,
        desc: String,
        deadline: String,
        phone: String
    ) -> Unit
) {
    var projectType by remember { mutableStateOf("FULL_TIME") } // FULL_TIME, PART_TIME, CONTRACT, DAILY_RATE, REMOTE
    var title by remember { mutableStateOf("") }
    var amTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("የሱቅና ሽያጭ (Retail)") }
    var location by remember { mutableStateOf("አዲስ አበባ፣ መርካቶ") }
    var salaryStr by remember { mutableStateOf("15000") }
    var phone by remember { mutableStateOf("+2519") }
    var skills by remember { mutableStateOf("ታማኝነት፣ ልምድ") }
    var experience by remember { mutableStateOf("1+ ዓመት ልምድ") }
    var desc by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("2026-12-30") }

    val categoriesList = listOf(
        "የሱቅና ሽያጭ (Retail)",
        "መኪና አሽከርካሪ (Drivers)",
        "እደ-ጥበብና ግንባታ (Trades)",
        "ምግብ ዝግጅት (Hospitality)",
        "ክብካቤ (Home & Care)",
        "ኤሌክትሪክና ቧንቧ (Electrical)",
        "ባህልና ጥበብ (Crafts)",
        "ፋይናንስና ሂሳብ (Accounting)",
        "ቴክኖሎጂ (Technology)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "አዲስ የሥራ ማስታወቂያ ይለጥፉ" else "Post a Domestic Job Vacancy",
                fontWeight = FontWeight.Bold,
                color = RoyalBlue
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የሥራውን ዓይነት ይምረጡ:" else "Select Job Type:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = projectType == "FULL_TIME",
                            onClick = { projectType = "FULL_TIME" },
                            label = { Text("ሙሉ ሰዓት", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = projectType == "PART_TIME",
                            onClick = { projectType = "PART_TIME" },
                            label = { Text("የትርፍ ሰዓት", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = projectType == "CONTRACT",
                            onClick = { projectType = "CONTRACT" },
                            label = { Text("ኮንትራት", fontSize = 10.sp) }
                        )
                        FilterChip(
                            selected = projectType == "DAILY_RATE",
                            onClick = { projectType = "DAILY_RATE" },
                            label = { Text("የቀን", fontSize = 10.sp) }
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("የሥራው መጠሪያ (Title in English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = amTitle,
                        onValueChange = { amTitle = it },
                        label = { Text("የሥራው መጠሪያ በአማርኛ (Title in Amharic)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("አድራሻ / ከተማና ክፍለ ከተማ (Location in Ethiopia)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = salaryStr,
                        onValueChange = { salaryStr = it },
                        label = { Text("ደመወዝ / ክፍያ በብር (Monthly Salary or Daily Rate in ETB)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("የአሠሪው ስልክ ቁጥር (Employer Phone Number)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = skills,
                        onValueChange = { skills = it },
                        label = { Text("የሚፈለጉ ክህሎቶች (Skills Required)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("ዝርዝር የሥራ መስፈርቶች (Requirements & Description)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val salary = salaryStr.toDoubleOrNull() ?: 15000.0
                    if ((title.isNotBlank() || amTitle.isNotBlank()) && desc.isNotBlank()) {
                        onSubmit(
                            title.ifBlank { amTitle },
                            amTitle.ifBlank { title },
                            category,
                            location,
                            salary,
                            projectType,
                            30,
                            skills,
                            experience,
                            desc,
                            deadline,
                            phone
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text(if (lang == LanguageCode.AMHARIC) "ማስታወቂያውን ለጥፍ" else "Publish Vacancy", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", lang))
            }
        }
    )
}

/**
 * Dialog for Job Seekers to submit their application or bid to the employer
 */
@Composable
private fun SubmitApplicationDialog(
    jobTitle: String,
    suggestedBudget: Double,
    suggestedDays: Int,
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (Double, Int, String, String) -> Unit
) {
    var bidStr by remember { mutableStateOf(suggestedBudget.toInt().toString()) }
    var availability by remember { mutableStateOf("ወዲያውኑ መጀመር እችላለሁ (Immediate Availability)") }
    var coverNote by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "የሥራ ማመልከቻ አስገባ" else "Submit Job Application",
                fontWeight = FontWeight.Bold,
                color = RoyalBlue
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                item {
                    Text(
                        text = "Position: $jobTitle",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = RoyalBlue
                    )
                }
                item {
                    OutlinedTextField(
                        value = bidStr,
                        onValueChange = { bidStr = it },
                        label = { Text("የሚፈልጉት ክፍያ ወይም ደመወዝ በብር (Desired Salary/Rate in ETB)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = availability,
                        onValueChange = { availability = it },
                        label = { Text("ሥራ መጀመር የሚችሉበት ጊዜ (Availability / Start Time)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = coverNote,
                        onValueChange = { coverNote = it },
                        label = { Text("ለአሠሪው አጭር ማስታወሻ / ስለ ልምድዎ (Message / Qualifications)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bid = bidStr.toDoubleOrNull() ?: suggestedBudget
                    onSubmit(bid, 30, availability, coverNote)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
            ) {
                Text(if (lang == LanguageCode.AMHARIC) "ማመልከቻውን ላክ" else "Send Application", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", lang))
            }
        }
    )
}
