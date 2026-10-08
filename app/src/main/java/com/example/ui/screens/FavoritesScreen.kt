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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmptyStateView
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val favorites by viewModel.userFavorites.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = favorites.filter {
        selectedFilter == "ALL" || it.itemType == selectedFilter
    }

    Scaffold(
        modifier = modifier.testTag("favorites_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.get("favorites_title", lang),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkSurface else LightSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isDark) DarkBackground else LightBackground)
        ) {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL", "JOB", "PRODUCT", "SERVICE").forEach { type ->
                    val isSel = selectedFilter == type
                    Surface(
                        onClick = { selectedFilter = type },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) RoyalBlue else (if (isDark) DarkSurface else LightSurface),
                        border = if (!isSel) androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder) else null
                    ) {
                        Text(
                            text = when (type) {
                                "ALL" -> "All (${favorites.size})"
                                "JOB" -> "Jobs"
                                "PRODUCT" -> "Marketplace"
                                else -> "Services"
                            },
                            color = if (isSel) Color.White else (if (isDark) DarkTextPrimary else LightTextPrimary),
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = Strings.get("no_favorites", lang),
                    subtitle = "Tap the heart or save icon on any job, marketplace product, or service to keep track here.",
                    icon = Icons.Outlined.FavoriteBorder,
                    actionText = "Explore Platform",
                    onAction = { viewModel.navigateTo(AppScreen.LANDING_HOME) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredList) { fav ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (fav.itemType) {
                                        "JOB" -> viewModel.navigateTo(AppScreen.JOBS)
                                        "PRODUCT" -> viewModel.navigateTo(AppScreen.MARKETPLACE)
                                        "SERVICE" -> viewModel.navigateTo(AppScreen.SERVICES)
                                        else -> viewModel.navigateTo(AppScreen.B2B)
                                    }
                                },
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
                                Icon(
                                    imageVector = when (fav.itemType) {
                                        "JOB" -> Icons.Filled.Work
                                        "PRODUCT" -> Icons.Filled.ShoppingBag
                                        "SERVICE" -> Icons.Filled.Handyman
                                        else -> Icons.Filled.LocalShipping
                                    },
                                    contentDescription = null,
                                    tint = when (fav.itemType) {
                                        "JOB" -> CatJobColor
                                        "PRODUCT" -> CatMarketColor
                                        "SERVICE" -> CatServiceColor
                                        else -> CatB2BColor
                                    },
                                    modifier = Modifier.size(28.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fav.itemTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${fav.itemType} • ${fav.itemSubtitle}",
                                        fontSize = 12.sp,
                                        color = LightTextSecondary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.toggleFavorite(fav.itemId, fav.itemType, fav.itemTitle, fav.itemSubtitle)
                                    }
                                ) {
                                    Icon(Icons.Filled.Favorite, contentDescription = "Remove", tint = StatusError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
