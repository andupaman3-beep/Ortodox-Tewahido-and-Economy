package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.model.ProductEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val products by viewModel.allProducts.collectAsState()

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var showAddProductDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Religious & Cultural", "Food & Agriculture", "Electronics & Power", "Textiles")

    val filteredProducts = products.filter { prod ->
        val matchesCategory = (selectedCategory == "All") || prod.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                prod.title.contains(searchQuery, ignoreCase = true) ||
                prod.amharicTitle.contains(searchQuery, ignoreCase = true) ||
                prod.sellerName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = modifier.testTag("marketplace_screen"),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProductDialog = true },
                containerColor = TewahidoNavy,
                contentColor = OrthodoxGold,
                icon = { Icon(Icons.Filled.AddShoppingCart, contentDescription = null) },
                text = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "እቃ ሽጥ" else "Sell Item",
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.testTag("sell_product_fab")
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search field
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
                            text = if (lang == LanguageCode.AMHARIC) "የገበያ ሸቀጦችን ወይም ሻጮችን ፈልግ..." else "Search products or sellers...",
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("marketplace_search_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrthodoxGold,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Categories horizontal scroller
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

            // Product Grid
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ምንም እቃ አልተገኘም" else "No products found",
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts) { prod ->
                        ProductGridItem(
                            product = prod,
                            lang = lang,
                            onClick = { viewModel.openProductDetail(prod) }
                        )
                    }
                }
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            lang = lang,
            onDismiss = { showAddProductDialog = false },
            onSubmit = { title, amTitle, desc, cat, price, qty, loc, deliv, cond ->
                viewModel.createProduct(title, amTitle, desc, cat, price, qty, loc, deliv, cond)
                showAddProductDialog = false
            }
        )
    }
}

@Composable
fun ProductGridItem(
    product: ProductEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_item_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7F5EF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = null,
                    tint = OrthodoxGold,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (lang == LanguageCode.AMHARIC) product.amharicTitle else product.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = product.sellerName,
                fontSize = 10.sp,
                color = TextMuted,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${product.price.toInt()} ETB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TewahidoNavy
                )
                if (product.sellerVerified) {
                    VerificationBadge(status = "VERIFIED", lang = lang, compact = true)
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val product by viewModel.selectedProduct.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product details unavailable.")
        }
        return
    }

    val p = product!!
    val isDark by viewModel.isDarkMode.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()
    val isFav = favorites.any { it.itemId == p.id }

    Scaffold(
        modifier = modifier.testTag("product_detail_screen"),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "የእቃው ዝርዝር" else "Product Details",
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
                            viewModel.toggleFavorite(p.id, "PRODUCT", p.title, "${p.price.toInt()} ETB")
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
                            viewModel.snackbarMessage.value = "Link copied for: ${p.title}"
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
                    OutlinedButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Flag, contentDescription = null, tint = StatusRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == LanguageCode.AMHARIC) "ሪፖርት አድርግ" else "Report Item", fontSize = 11.sp, color = StatusRed)
                    }

                    Button(
                        onClick = {
                            viewModel.openChatWith(p.sellerId, p.sellerName)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy),
                        modifier = Modifier.weight(1.4f)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, tint = OrthodoxGoldLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ሻጩን አነጋግር" else "Message Seller",
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
                        text = if (lang == LanguageCode.AMHARIC) "ወደ ገበያ ተመለስ" else "Back to Marketplace",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TewahidoNavy
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF7F5EF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = OrthodoxGold,
                        modifier = Modifier.size(64.dp)
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
                                text = p.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrthodoxGoldDark
                            )
                            if (p.sellerVerified) {
                                VerificationBadge(status = "VERIFIED", lang = lang)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) p.amharicTitle else p.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${p.price.toInt()} ${p.currency}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TewahidoNavy
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Specs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Quantity / ብዛት", fontSize = 11.sp, color = TextMuted)
                                Text("${p.quantity} Units in Stock", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Condition / ሁኔታ", fontSize = 11.sp, color = TextMuted)
                                Text(p.condition, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Location / ቦታ", fontSize = 11.sp, color = TextMuted)
                                Text(p.location, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                            text = if (lang == LanguageCode.AMHARIC) "የእቃው ዝርዝር መረጃ" else "Product Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TewahidoNavy
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = p.description,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "🚚 Delivery: ${p.deliveryInfo}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OrthodoxTeal
                        )
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        ReportContentDialog(
            targetType = "PRODUCT",
            targetId = p.id,
            targetTitle = p.title,
            lang = lang,
            onDismiss = { showReportDialog = false },
            onSubmit = { reason, details ->
                viewModel.submitReport("PRODUCT", p.id, p.title, reason, details)
                showReportDialog = false
            }
        )
    }
}

@Composable
private fun AddProductDialog(
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String, Double, Int, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amTitle by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Religious & Cultural") }
    var priceStr by remember { mutableStateOf("4500") }
    var qtyStr by remember { mutableStateOf("10") }
    var location by remember { mutableStateOf("Addis Ababa") }
    var delivery by remember { mutableStateOf("በአዲስ አበባ በነፃ እናደርሳለን") }
    var condition by remember { mutableStateOf("አዲስ (Brand New)") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "እቃ ለገበያ አቅርብ" else "Add Marketplace Product",
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Product Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = amTitle,
                        onValueChange = { amTitle = it },
                        label = { Text("የእቃው ስም (አማርኛ)") },
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
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price (ETB)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = qtyStr,
                        onValueChange = { qtyStr = it },
                        label = { Text("Available Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Product Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = delivery,
                        onValueChange = { delivery = it },
                        label = { Text("Delivery Terms") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    val qty = qtyStr.toIntOrNull() ?: 1
                    if (title.isNotBlank() && desc.isNotBlank()) {
                        onSubmit(title, amTitle, desc, category, price, qty, location, delivery, condition)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TewahidoNavy)
            ) {
                Text(Strings.get("submit", lang), color = OrthodoxGoldLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", lang))
            }
        }
    )
}

@Composable
fun ReportContentDialog(
    targetType: String,
    targetId: String,
    targetTitle: String,
    lang: LanguageCode,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("SCAM") }
    var details by remember { mutableStateOf("") }

    val reasons = listOf("SCAM", "FAKE_ACCOUNT", "HARASSMENT", "INAPPROPRIATE_CONTENT", "SPAM", "OTHER")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (lang == LanguageCode.AMHARIC) "ለአስተዳዳሪው አሳውቅ / ሪፖርት" else "Report to Moderation",
                fontWeight = FontWeight.Bold,
                color = StatusRed
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "Subject: $targetTitle", fontSize = 12.sp, color = TextMuted)
                Text(text = "Reason for report:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                reasons.forEach { r ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = r }
                    ) {
                        RadioButton(selected = selectedReason == r, onClick = { selectedReason = r })
                        Text(text = r, fontSize = 12.sp)
                    }
                }
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional details for moderator") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedReason, details) },
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
            ) {
                Text("Submit Report", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
