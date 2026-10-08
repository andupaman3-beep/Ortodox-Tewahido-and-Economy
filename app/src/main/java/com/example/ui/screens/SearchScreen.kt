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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryChip
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun SearchScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val allB2B by viewModel.allB2BListings.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("ALL") }

    val tabs = listOf("ALL", "JOBS", "PRODUCTS", "B2B", "SERVICES", "PEOPLE")

    val matchingJobs = allJobs.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.amharicTitle.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    val matchingProducts = allProducts.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.amharicTitle.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true)
    }

    val matchingB2B = allB2B.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.companyName.contains(searchQuery, ignoreCase = true)
    }

    val matchingServices = allServices.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.amharicTitle.contains(searchQuery, ignoreCase = true)
    }

    val matchingUsers = allUsers.filter {
        searchQuery.isNotBlank() && (it.fullName.contains(searchQuery, ignoreCase = true) || it.profession.contains(searchQuery, ignoreCase = true) || it.businessName.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Search Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TewahidoNavy)
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(Strings.get("search_hint", lang), fontSize = 13.sp) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = null)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp)
                        .testTag("global_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        // Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tabs) { tab ->
                CategoryChip(
                    label = when (tab) {
                        "ALL" -> if (lang == LanguageCode.AMHARIC) "ሁሉም" else "All"
                        "JOBS" -> Strings.get("jobs", lang)
                        "PRODUCTS" -> Strings.get("marketplace", lang)
                        "B2B" -> Strings.get("b2b", lang)
                        "SERVICES" -> Strings.get("services", lang)
                        "PEOPLE" -> if (lang == LanguageCode.AMHARIC) "ሰዎችና ንግዶች" else "People & Businesses"
                        else -> tab
                    },
                    isSelected = selectedTab == tab,
                    onClick = { selectedTab = tab }
                )
            }
        }

        // Results
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Jobs Section
            if (selectedTab in listOf("ALL", "JOBS") && matchingJobs.isNotEmpty()) {
                item {
                    Text(
                        text = "💼 ${Strings.get("jobs", lang)} (${matchingJobs.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TewahidoNavy
                    )
                }
                items(matchingJobs.take(if (selectedTab == "ALL") 2 else 20)) { job ->
                    FreelanceJobCard(job = job, lang = lang, onClick = { viewModel.openJobDetail(job) })
                }
            }

            // Products Section
            if (selectedTab in listOf("ALL", "PRODUCTS") && matchingProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🛍️ ${Strings.get("marketplace", lang)} (${matchingProducts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TewahidoNavy
                    )
                }
                items(matchingProducts.take(if (selectedTab == "ALL") 2 else 20)) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openProductDetail(prod) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = prod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${prod.sellerName} • ${prod.location}", fontSize = 11.sp, color = TextMuted)
                            }
                            Text(text = "${prod.price.toInt()} ETB", fontWeight = FontWeight.Bold, color = TewahidoNavy)
                        }
                    }
                }
            }

            // B2B Section
            if (selectedTab in listOf("ALL", "B2B") && matchingB2B.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🚢 ${Strings.get("b2b", lang)} (${matchingB2B.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TewahidoNavy
                    )
                }
                items(matchingB2B.take(if (selectedTab == "ALL") 2 else 20)) { b2b ->
                    B2BItemCard(item = b2b, lang = lang, onClick = { viewModel.openB2BDetail(b2b) })
                }
            }

            // Services Section
            if (selectedTab in listOf("ALL", "SERVICES") && matchingServices.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🔧 ${Strings.get("services", lang)} (${matchingServices.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TewahidoNavy
                    )
                }
                items(matchingServices.take(if (selectedTab == "ALL") 2 else 20)) { svc ->
                    ServiceItemCard(service = svc, lang = lang, onClick = { viewModel.openServiceDetail(svc) })
                }
            }

            // Users Section
            if (selectedTab in listOf("ALL", "PEOPLE") && matchingUsers.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "👥 People & Businesses (${matchingUsers.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TewahidoNavy
                    )
                }
                items(matchingUsers) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openUserProfile(user) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "${user.profession} • ${user.businessName}", fontSize = 11.sp, color = TextMuted)
                            }
                            Text(text = user.primaryRole, fontSize = 10.sp, color = OrthodoxGoldDark)
                        }
                    }
                }
            }
        }
    }
}
