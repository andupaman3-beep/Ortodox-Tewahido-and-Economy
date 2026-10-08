package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val conversations by viewModel.userConversations.collectAsState()

    val myProducts = allProducts.filter { it.sellerId == currentUser?.id || currentUser?.primaryRole == "ADMIN" }
    val totalInventoryValue = myProducts.sumOf { it.price * it.quantity }

    Scaffold(
        modifier = modifier.testTag("seller_dashboard_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.get("seller_dashboard", lang),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.MARKETPLACE) }) {
                        Icon(Icons.Filled.AddShoppingCart, contentDescription = "Add Item", tint = RoyalBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkSurface else LightSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isDark) DarkBackground else LightBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Seller Merchant Status Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = currentUser?.businessName?.ifBlank { currentUser?.fullName } ?: "Orthodox Merchant",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Merchant ID: ${currentUser?.id ?: "usr_001"}",
                                    fontSize = 11.sp,
                                    color = AccentGoldLight
                                )
                            }
                            VerificationBadge(
                                status = currentUser?.verificationStatus ?: "VERIFIED",
                                lang = lang,
                                compact = false
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Real Inventory Value Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Inventory Value", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(
                                    text = "${String.format("%,.0f", totalInventoryValue)} ETB",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AccentGoldBright
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Store Rating", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(
                                    text = "★ ${currentUser?.rating ?: 5.0} (${currentUser?.reviewCount ?: 0} reviews)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Metric Cards Grid
            item {
                Text(
                    text = Strings.get("seller_analytics", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MerchantStatCard(
                        title = "Active Listings",
                        value = "${myProducts.size}",
                        icon = Icons.Filled.Storefront,
                        tint = CatMarketColor,
                        bg = CatMarketBg,
                        modifier = Modifier.weight(1f)
                    )
                    MerchantStatCard(
                        title = "Inquiries",
                        value = "${conversations.size}",
                        icon = Icons.Filled.Chat,
                        tint = RoyalBlue,
                        bg = RoyalBlueSoft,
                        modifier = Modifier.weight(1f)
                    )
                    MerchantStatCard(
                        title = "Total Units",
                        value = "${myProducts.sumOf { it.quantity }}",
                        icon = Icons.Filled.Inventory,
                        tint = EmeraldSuccess,
                        bg = EmeraldSoft,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Active Listings Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("active_listings", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.MARKETPLACE) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (myProducts.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No active products listed",
                        subtitle = "Publish your religious vestments, books, agricultural commodities, or products to sell to thousands of Orthodox buyers.",
                        icon = Icons.Outlined.Storefront,
                        actionText = "Post First Product",
                        onAction = { viewModel.navigateTo(AppScreen.MARKETPLACE) }
                    )
                }
            } else {
                items(myProducts) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CatMarketBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.ShoppingBag, contentDescription = null, tint = CatMarketColor)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prod.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${prod.price} ETB • ${prod.quantity} in stock • ${prod.condition}",
                                    fontSize = 12.sp,
                                    color = LightTextSecondary
                                )
                                Text(
                                    text = "Category: ${prod.category}",
                                    fontSize = 11.sp,
                                    color = CatMarketColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(onClick = { viewModel.deleteProduct(prod.id) }) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = StatusError)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MerchantStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bg,
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BrandNavyDark)
            Text(text = title, fontSize = 10.sp, color = LightTextSecondary)
        }
    }
}
