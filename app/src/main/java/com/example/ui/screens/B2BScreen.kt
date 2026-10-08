package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.B2BEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun B2BScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val listings by viewModel.allB2BListings.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, IMPORTER, DISTRIBUTOR
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredListings = listings.filter { item ->
        val matchesType = (selectedFilter == "ALL") || item.b2bType == selectedFilter
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.companyName.contains(searchQuery, ignoreCase = true) ||
                item.productOrigin.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
    }

    Scaffold(
        modifier = modifier.testTag("b2b_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TewahidoNavy,
                contentColor = OrthodoxGold,
                icon = { Icon(Icons.Filled.AddBusiness, contentDescription = null) },
                text = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ጅምላ አቅርቦት መዝግብ" else "Post B2B Offer",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("post_b2b_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header card
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
                            text = if (lang == LanguageCode.AMHARIC) "አስመጪዎች፣ አከፋፋዮች፣ የጅምላ እቃ ፈልግ..." else "Search importers, distributors, products...",
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

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip(
                    label = if (lang == LanguageCode.AMHARIC) "ሁሉም B2B" else "All B2B",
                    isSelected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" }
                )
                CategoryChip(
                    label = if (lang == LanguageCode.AMHARIC) "አስመጪዎች (Importers)" else "Importers",
                    isSelected = selectedFilter == "IMPORTER",
                    onClick = { selectedFilter = "IMPORTER" },
                    icon = Icons.Filled.FlightLand
                )
                CategoryChip(
                    label = if (lang == LanguageCode.AMHARIC) "አከፋፋዮች (Distributors)" else "Distributors",
                    isSelected = selectedFilter == "DISTRIBUTOR",
                    onClick = { selectedFilter = "DISTRIBUTOR" },
                    icon = Icons.Filled.LocalShipping
                )
            }

            // Listings
            if (filteredListings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ምንም የጅምላ አቅርቦት አልተገኘም" else "No B2B wholesale listings found",
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
                    items(filteredListings) { item ->
                        B2BItemCard(
                            item = item,
                            lang = lang,
                            onClick = { viewModel.openB2BDetail(item) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddB2BDialog(
            lang = lang,
            onDismiss = { showAddDialog = false },
            onSubmit = { type, title, origin, price, minQty, totalQty, loc, deliv, desc, phone, email ->
                viewModel.createB2BListing(type, title, origin, price, minQty, totalQty, loc, deliv, desc, phone, email)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun B2BItemCard(
    item: B2BEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("b2b_item_${item.id}"),
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
                Surface(
                    color = if (item.b2bType == "IMPORTER") TewahidoNavy.copy(alpha = 0.1f) else OrthodoxTeal.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (item.b2bType == "IMPORTER") Icons.Filled.FlightLand else Icons.Filled.LocalShipping,
                            contentDescription = null,
                            tint = if (item.b2bType == "IMPORTER") TewahidoNavy else OrthodoxTeal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = item.b2bType,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.b2bType == "IMPORTER") TewahidoNavy else OrthodoxTeal
                        )
                    }
                }
                if (item.isVerified) {
                    VerificationBadge(status = "VERIFIED", lang = lang, compact = true)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.companyName} • Origin: ${item.productOrigin}",
                fontSize = 11.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0EFEA))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wholesale: ${item.wholesalePrice} ${item.currency}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TewahidoNavy
                    )
                    Text(
                        text = "Min Order: ${item.minimumOrderQty}",
                        fontSize = 10.sp,
                        color = OrthodoxGoldDark
                    )
                }
                Button(
                    onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ዝርዝር / አነጋግር" else "Details & Contact",
                        fontSize = 11.sp,
                        color = OrthodoxGoldLight
                    )
                }
            }
        }
    }
}

@Composable
fun B2BDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val listing by viewModel.selectedB2B.collectAsState()

    if (listing == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Listing details unavailable.")
        }
        return
    }

    val b = listing!!
    val isDark by viewModel.isDarkMode.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()
    val isFav = favorites.any { it.itemId == b.id }

    Scaffold(
        modifier = modifier.testTag("b2b_detail_screen"),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የጅምላ ዕድል ዝርዝር" else "B2B Wholesale Details",
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
                            viewModel.toggleFavorite(b.id, "B2B", b.title, "${b.wholesalePrice.toInt()} ETB")
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
                            viewModel.snackbarMessage.value = "Link copied for: ${b.title}"
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
                            viewModel.openChatWith(b.userId, b.companyName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, tint = OrthodoxGoldLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የጅምላ ጥያቄ ላክ (RFQ)" else "Contact for Wholesale / RFQ",
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
                        text = if (lang == LanguageCode.AMHARIC) "ወደ አስመጪና አከፋፋይ ተመለስ" else "Back to B2B",
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
                            Text(
                                text = "B2B • ${b.b2bType}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrthodoxGoldDark
                            )
                            if (b.isVerified) {
                                VerificationBadge(status = "VERIFIED", lang = lang)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = b.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = b.companyName, fontSize = 13.sp, color = TextMuted)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Specs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Wholesale Price", fontSize = 11.sp, color = TextMuted)
                                Text("${b.wholesalePrice} ${b.currency}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TewahidoNavy)
                            }
                            Column {
                                Text("Min Order (MOQ)", fontSize = 11.sp, color = TextMuted)
                                Text(b.minimumOrderQty, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Product Origin", fontSize = 11.sp, color = TextMuted)
                                Text(b.productOrigin, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            text = if (lang == LanguageCode.AMHARIC) "የአቅርቦትና የማድረስ ዝርዝር" else "Supply & Delivery Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TewahidoNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = b.description, fontSize = 12.sp, lineHeight = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "📍 Warehouse: ${b.location}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "🚚 Delivery Terms: ${b.deliveryTerms}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "📞 Direct Contact: ${b.contactPhone} | ${b.contactEmail}", fontSize = 12.sp, color = OrthodoxGoldDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun AddB2BDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Double, String, String, String, String, String, String, String) -> Unit
) {
    var type by remember { mutableStateOf("IMPORTER") }
    var title by remember { mutableStateOf("") }
    var origin by remember { mutableStateOf("Turkey / Local") }
    var priceStr by remember { mutableStateOf("150") }
    var minQty by remember { mutableStateOf("100 Units") }
    var totalQty by remember { mutableStateOf("5000 Units Available") }
    var location by remember { mutableStateOf("Addis Ababa, Kality") }
    var delivery by remember { mutableStateOf("Ex-Warehouse Delivery") }
    var desc by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+251911000000") }
    var email by remember { mutableStateOf("info@company.et") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "የጅምላ አቅርቦት መዝግብ" else "Post B2B Wholesale Listing",
                fontWeight = FontWeight.Bold,
                color = TewahidoNavy
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = type == "IMPORTER",
                            onClick = { type = "IMPORTER" },
                            label = { Text("Importer (አስመጪ)") }
                        )
                        FilterChip(
                            selected = type == "DISTRIBUTOR",
                            onClick = { type = "DISTRIBUTOR" },
                            label = { Text("Distributor (አከፋፋይ)") }
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product / Listing Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = origin,
                        onValueChange = { origin = it },
                        label = { Text("Product Origin (e.g. Dubai, Turkey, Local)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Wholesale Price (ETB)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = minQty,
                        onValueChange = { minQty = it },
                        label = { Text("Minimum Order Quantity (MOQ)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description & Specifications") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
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
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && desc.isNotBlank()) {
                        onSubmit(type, title, origin, price, minQty, totalQty, location, delivery, desc, phone, email)
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
