package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.StarRatingBar
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun ServicesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val services by viewModel.allServices.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Architecture & Engineering", "Finance & Legal", "Technology", "Craft & Art")

    val filteredServices = services.filter { svc ->
        val matchesCategory = (selectedCategory == "All") || svc.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                svc.title.contains(searchQuery, ignoreCase = true) ||
                svc.amharicTitle.contains(searchQuery, ignoreCase = true) ||
                svc.providerName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = modifier.testTag("services_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TewahidoNavy,
                contentColor = OrthodoxGold,
                icon = { Icon(Icons.Filled.Handyman, contentDescription = null) },
                text = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የሙያ አገልግሎት መዝግብ" else "Offer Service",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("offer_service_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የሙያ አገልግሎት ወይም ባለሙያ ፈልግ..." else "Search professional services...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = OrthodoxGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrthodoxGold,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    CategoryChip(
                        label = if (cat == "All" && lang == LanguageCode.AMHARIC) "ሁሉም" else cat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            // List
            if (filteredServices.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ምንም አገልግሎት አልተገኘም" else "No services found",
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredServices) { svc ->
                        ServiceItemCard(
                            service = svc,
                            lang = lang,
                            onClick = { viewModel.openServiceDetail(svc) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddServiceDialog(
            lang = lang,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, amTitle, cat, rate, loc, desc, exp, phone ->
                viewModel.createService(title, amTitle, cat, rate, loc, desc, exp, phone)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ServiceItemCard(
    service: ServiceEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("service_item_${service.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) service.amharicTitle else service.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = service.providerName, fontSize = 12.sp, color = TextMuted)
                }
                if (service.isVerified) {
                    VerificationBadge(status = "VERIFIED", lang = lang, compact = true)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StarRatingBar(rating = service.rating, reviewCount = service.reviewsCount)
                Text(
                    text = "${service.experienceYears}+ Yrs Exp",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrthodoxGoldDark
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0EFEA))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.rateDescription.take(22),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TewahidoNavy
                )
                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "አገልግሎት ጠይቅ" else "Request Service",
                        fontSize = 11.sp,
                        color = OrthodoxGoldLight
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val service by viewModel.selectedService.collectAsState()

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Service details unavailable.")
        }
        return
    }

    val s = service!!
    val isDark by viewModel.isDarkMode.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()
    val isFav = favorites.any { it.itemId == s.id }

    Scaffold(
        modifier = modifier.testTag("service_detail_screen"),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የአገልግሎት ዝርዝር" else "Service Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(s.id, "SERVICE", s.title, s.rateDescription)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) StatusRed else (if (isDark) DarkTextPrimary else LightTextPrimary)
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.snackbarMessage.value = "Link copied for: ${s.title}"
                        }
                    ) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkSurface else LightSurface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.openChatWith(s.providerId, s.providerName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, tint = OrthodoxGoldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ባለሙያውን አነጋግር / ዋጋ ጠይቅ" else "Contact & Request Quote",
                            fontWeight = FontWeight.Bold,
                            color = OrthodoxGoldLight
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.navigateBack() }
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TewahidoNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ወደ አገልግሎቶች ተመለስ" else "Back to Services",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TewahidoNavy
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(s.category, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OrthodoxGoldDark)
                            if (s.isVerified) {
                                VerificationBadge(status = "VERIFIED", lang = lang)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) s.amharicTitle else s.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = s.providerName, fontSize = 13.sp, color = TextMuted)

                        Spacer(modifier = Modifier.height(10.dp))
                        StarRatingBar(rating = s.rating, reviewCount = s.reviewsCount)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Specs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Experience", fontSize = 11.sp, color = TextMuted)
                                Text("${s.experienceYears} Years", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Rate Estimate", fontSize = 11.sp, color = TextMuted)
                                Text(s.rateDescription, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TewahidoNavy)
                            }
                            Column {
                                Text("Location", fontSize = 11.sp, color = TextMuted)
                                Text(s.location, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የአገልግሎቱ ዝርዝር" else "Service Description",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TewahidoNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = s.description, fontSize = 12.sp, lineHeight = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "📞 Phone: ${s.phone}", fontSize = 12.sp, color = OrthodoxGoldDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddServiceDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, String, String, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technology") }
    var rate by remember { mutableStateOf("በስምምነት") }
    var location by remember { mutableStateOf("Addis Ababa") }
    var desc by remember { mutableStateOf("") }
    var expStr by remember { mutableStateOf("5") }
    var phone by remember { mutableStateOf("+251911000000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "የሙያ አገልግሎት መዝግብ" else "Register Professional Service",
                fontWeight = FontWeight.Bold,
                color = TewahidoNavy
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Service Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = amTitle,
                        onValueChange = { amTitle = it },
                        label = { Text("የአገልግሎቱ መጠሪያ (አማርኛ)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = rate,
                        onValueChange = { rate = it },
                        label = { Text("Rate Description (e.g. 15,000 ETB / Project)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description & Expertise") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = expStr,
                        onValueChange = { expStr = it },
                        label = { Text("Years of Experience") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val exp = expStr.toIntOrNull() ?: 1
                    if (title.isNotBlank() && desc.isNotBlank()) {
                        onSubmit(title, amTitle, category, rate, location, desc, exp, phone)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy)
            ) {
                Text(Strings.get("submit", lang), color = OrthodoxGoldLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.get("cancel", lang)) }
        }
    )
}
