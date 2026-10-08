package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.B2BEntity
import com.example.data.model.JobEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.SectionHeader
import com.example.ui.components.StarRatingBar
import com.example.ui.components.VerificationBadge
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun LandingHomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val jobs by viewModel.allJobs.collectAsState()
    val products by viewModel.allProducts.collectAsState()
    val b2bListings by viewModel.allB2BListings.collectAsState()
    val services by viewModel.allServices.collectAsState()
    val users by viewModel.allUsers.collectAsState()

    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) DarkBackground else LightBackground)
            .testTag("landing_home_screen"),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {
        // 1. WELCOME / PROFILE GREETING
        item {
            Surface(
                color = if (isDark) DarkSurface else LightSurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (currentUser != null) {
                                when (lang) {
                                    LanguageCode.AMHARIC -> "ሰላም፣ ${currentUser?.fullName?.substringBefore(" ")}"
                                    LanguageCode.ENGLISH -> "Welcome, ${currentUser?.fullName?.substringBefore(" ")}"
                                    LanguageCode.OROMO -> "Baga nagaan dhufte, ${currentUser?.fullName?.substringBefore(" ")}"
                                    LanguageCode.TIGRINYA -> "ሰላም፣ ${currentUser?.fullName?.substringBefore(" ")}"
                                }
                            } else {
                                Strings.get("tagline", lang)
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = Strings.get("sub_tagline", lang),
                                fontSize = 12.sp,
                                color = LightTextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Role pill
                    Surface(
                        color = RoyalBlueSoft,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = currentUser?.primaryRole?.replace("_", " ") ?: "MEMBER",
                            color = RoyalBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. SMART SEARCH BAR (Prominent, High-Contrast, 4 Languages)
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(AppScreen.GLOBAL_SEARCH) }
                    .testTag("smart_search_bar"),
                color = if (isDark) DarkSurface else LightSurface,
                shadowElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) DarkBorder else LightBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RoyalBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = Strings.get("search", lang),
                            tint = RoyalBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = Strings.get("search_hint", lang),
                        fontSize = 13.sp,
                        color = LightTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.GLOBAL_SEARCH) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = "Filters",
                            tint = RoyalBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 3. HERO BRAND BANNER (Visual Richness with Platform Numbers)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = BrandNavy)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner),
                        contentDescription = "Hero Art",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        BrandNavy.copy(alpha = 0.40f),
                                        BrandNavy.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Badge at top
                        Surface(
                            color = AccentGoldBright,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "ዘመኑን ለዋጀ አገልግሎት • Modern Service",
                                color = BrandNavyDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Tagline
                        Column {
                            Text(
                                text = Strings.get("tagline", lang),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Strings.get("sub_tagline", lang),
                                fontSize = 12.sp,
                                color = AccentGoldLight
                            )
                        }
                    }
                }
            }
        }

        // 4. PLATFORM METRICS BAR (Community Strength Counters)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PlatformMetricPill(
                        number = "${users.size}+",
                        label = Strings.get("stat_members", lang),
                        icon = Icons.Filled.People,
                        tint = CatHireColor,
                        bg = CatHireBg
                    )
                    PlatformMetricPill(
                        number = "${jobs.size}+",
                        label = Strings.get("stat_jobs", lang),
                        icon = Icons.Filled.Work,
                        tint = CatJobColor,
                        bg = CatJobBg
                    )
                    PlatformMetricPill(
                        number = "${products.size}+",
                        label = Strings.get("stat_products", lang),
                        icon = Icons.Filled.Storefront,
                        tint = CatMarketColor,
                        bg = CatMarketBg
                    )
                    PlatformMetricPill(
                        number = "${b2bListings.size}+",
                        label = Strings.get("stat_deals", lang),
                        icon = Icons.Filled.LocalShipping,
                        tint = CatB2BColor,
                        bg = CatB2BBg
                    )
                }
            }
        }

        // 5. QUICK ACTION CARDS (5 Pillars: Jobs, Hire, Market, B2B, Services)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(
                title = if (lang == LanguageCode.AMHARIC) "ዋና ዋና አገልግሎቶች" else "Quick Actions & Portals"
            )
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top row: Find Job & Hire Job
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = Strings.get("find_job", lang),
                        subtitle = Strings.get("find_job_desc", lang),
                        count = "${jobs.size} Open",
                        icon = Icons.Filled.Work,
                        tint = CatJobColor,
                        bgColor = CatJobBg,
                        onClick = { viewModel.navigateTo(AppScreen.JOBS) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = Strings.get("post_job", lang),
                        subtitle = Strings.get("post_job_desc", lang),
                        count = "Post / Hire",
                        icon = Icons.Filled.PersonAdd,
                        tint = CatHireColor,
                        bgColor = CatHireBg,
                        onClick = { viewModel.navigateTo(AppScreen.JOBS) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Middle row: Marketplace & B2B Wholesale
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = Strings.get("shop_market", lang),
                        subtitle = Strings.get("shop_market_desc", lang),
                        count = "${products.size} Items",
                        icon = Icons.Filled.ShoppingBag,
                        tint = CatMarketColor,
                        bgColor = CatMarketBg,
                        onClick = { viewModel.navigateTo(AppScreen.MARKETPLACE) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = Strings.get("b2b_wholesale", lang),
                        subtitle = Strings.get("b2b_wholesale_desc", lang),
                        count = "${b2bListings.size} Importers",
                        icon = Icons.Filled.LocalShipping,
                        tint = CatB2BColor,
                        bgColor = CatB2BBg,
                        onClick = { viewModel.navigateTo(AppScreen.B2B) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Services Full-Width Banner Card
                Surface(
                    onClick = { viewModel.navigateTo(AppScreen.SERVICES) },
                    shape = RoundedCornerShape(16.dp),
                    color = CatServiceBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CatServiceColor.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CatServiceColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Handyman,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.get("pro_services", lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = BrandNavyDark
                            )
                            Text(
                                text = Strings.get("pro_services_desc", lang),
                                fontSize = 12.sp,
                                color = LightTextSecondary
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = CatServiceColor.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = CatServiceColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. MAIN CATEGORIES (Chips Carousel)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = Strings.get("all_categories", lang)
            )
        }

        item {
            val categories = listOf(
                Triple("ALL", Strings.get("all_categories", lang), Icons.Filled.Dashboard),
                Triple("TECH", Strings.get("cat_tech", lang), Icons.Filled.Computer),
                Triple("CONST", Strings.get("cat_construction", lang), Icons.Filled.Architecture),
                Triple("TEXTILE", Strings.get("cat_textiles", lang), Icons.Filled.Checkroom),
                Triple("AGRI", Strings.get("cat_agri", lang), Icons.Filled.Agriculture),
                Triple("FINANCE", Strings.get("cat_accounting", lang), Icons.Filled.AccountBalance),
                Triple("EDU", Strings.get("cat_education", lang), Icons.Filled.School),
                Triple("TRANS", Strings.get("cat_transport", lang), Icons.Filled.DirectionsCar)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { (id, label, icon) ->
                    CategoryChip(
                        label = label,
                        isSelected = selectedCategoryFilter == id,
                        icon = icon,
                        onClick = { selectedCategoryFilter = id }
                    )
                }
            }
        }

        // 7. FEATURED JOBS & FREELANCE GIGS (Upwork / Fiverr style cards)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = Strings.get("featured_jobs", lang),
                actionText = Strings.get("view_all", lang),
                onActionClick = { viewModel.navigateTo(AppScreen.JOBS) }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(jobs.take(5)) { job ->
                    FeaturedJobCard(
                        job = job,
                        lang = lang,
                        isDark = isDark,
                        onOpen = { viewModel.openJobDetail(job) }
                    )
                }
            }
        }

        // 8. FEATURED MARKETPLACE PRODUCTS (Clean, Image-First Cards)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = Strings.get("featured_products", lang),
                actionText = Strings.get("view_all", lang),
                onActionClick = { viewModel.navigateTo(AppScreen.MARKETPLACE) }
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(products.take(5)) { prod ->
                    FeaturedProductCard(
                        product = prod,
                        lang = lang,
                        isDark = isDark,
                        onOpen = { viewModel.openProductDetail(prod) }
                    )
                }
            }
        }

        // 9. POPULAR PROFESSIONAL SERVICES
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = Strings.get("popular_services", lang),
                actionText = Strings.get("view_all", lang),
                onActionClick = { viewModel.navigateTo(AppScreen.SERVICES) }
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                services.take(3).forEach { service ->
                    ServiceItemCard(
                        service = service,
                        lang = lang,
                        isDark = isDark,
                        onOpen = { viewModel.openServiceDetail(service) }
                    )
                }
            }
        }

        // 10. IMPORTER & DISTRIBUTOR OPPORTUNITIES (B2B Wholesale)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(
                title = Strings.get("importer_opps", lang),
                actionText = Strings.get("view_all", lang),
                onActionClick = { viewModel.navigateTo(AppScreen.B2B) }
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                b2bListings.take(2).forEach { listing ->
                    B2BSpotlightCard(
                        listing = listing,
                        lang = lang,
                        isDark = isDark,
                        onOpen = { viewModel.openB2BDetail(listing) }
                    )
                }

                // Explore B2B CTA button
                Button(
                    onClick = { viewModel.navigateTo(AppScreen.B2B) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CatB2BColor)
                ) {
                    Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Strings.get("explore_b2b", lang),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 11. VERIFIED TRUST & COMMUNITY RECOGNITION (Redesigned & Safe)
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else RoyalBlueSoft),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoyalBlue.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.get("trust_title", lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (isDark) DarkTextPrimary else BrandNavyDark
                            )
                            Text(
                                text = "Platform Verification System",
                                fontSize = 11.sp,
                                color = RoyalBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = Strings.get("trust_desc", lang),
                        fontSize = 12.sp,
                        color = if (isDark) DarkTextSecondary else LightTextSecondary,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Strings.get("get_verified", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-COMPONENTS FOR LANDING HOME
// -------------------------------------------------------------

@Composable
private fun PlatformMetricPill(
    number: String,
    label: String,
    icon: ImageVector,
    tint: Color,
    bg: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        }
        Text(text = number, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        Text(text = label, fontSize = 11.sp, color = LightTextSecondary)
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    count: String,
    icon: ImageVector,
    tint: Color,
    bgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, tint.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(tint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = count,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = BrandNavyDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = LightTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FeaturedJobCard(
    job: JobEntity,
    lang: LanguageCode,
    isDark: Boolean,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CatJobBg
                ) {
                    Text(
                        text = job.category.take(15),
                        color = CatJobColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
                VerificationBadge(status = "VERIFIED", lang = lang, compact = true)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (lang == LanguageCode.AMHARIC && job.amharicTitle.isNotBlank()) job.amharicTitle else job.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isDark) DarkTextPrimary else LightTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${job.employerName} • ${job.location}",
                fontSize = 12.sp,
                color = LightTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = if (isDark) DarkBorder else LightBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (job.budgetAmount > 0) "${job.budgetAmount.toInt()} ETB" else job.salaryRange,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = EmeraldSuccess
                    )
                    Text(
                        text = job.employmentType,
                        fontSize = 10.sp,
                        color = LightTextMuted
                    )
                }

                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(Strings.get("apply_now", lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FeaturedProductCard(
    product: ProductEntity,
    lang: LanguageCode,
    isDark: Boolean,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
    ) {
        Column {
            // Product image preview / visual icon box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(CatMarketBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = null,
                    tint = CatMarketColor,
                    modifier = Modifier.size(42.dp)
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CatMarketColor,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.category.take(12),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isDark) DarkTextPrimary else LightTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${product.price} ETB",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = CatMarketColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Store, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Text(
                        text = product.sellerName,
                        fontSize = 10.sp,
                        color = LightTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceItemCard(
    service: ServiceEntity,
    lang: LanguageCode,
    isDark: Boolean,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CatServiceBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Handyman,
                    contentDescription = null,
                    tint = CatServiceColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${service.providerName} • ${service.location}",
                    fontSize = 11.sp,
                    color = LightTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                StarRatingBar(rating = service.rating, reviewCount = service.reviewsCount)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = service.rateDescription,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = BrandNavyDark,
                    maxLines = 1
                )
                Text(
                    text = service.category,
                    fontSize = 10.sp,
                    color = LightTextMuted
                )
            }
        }
    }
}

@Composable
private fun B2BSpotlightCard(
    listing: B2BEntity,
    lang: LanguageCode,
    isDark: Boolean,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CatB2BBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (listing.b2bType == "IMPORTER") Icons.Filled.FlightLand else Icons.Filled.LocalShipping,
                    contentDescription = null,
                    tint = CatB2BColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = listing.b2bType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CatB2BColor
                    )
                    Text("•", fontSize = 10.sp, color = Color.Gray)
                    Text(listing.productOrigin, fontSize = 11.sp, color = LightTextSecondary)
                }
                Text(
                    text = listing.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${listing.companyName} • ${listing.location}",
                    fontSize = 11.sp,
                    color = LightTextSecondary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${listing.wholesalePrice} ETB",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandNavyDark
                )
                Text(
                    text = "MOQ: ${listing.minimumOrderQty.take(10)}",
                    fontSize = 10.sp,
                    color = LightTextMuted
                )
            }
        }
    }
}
