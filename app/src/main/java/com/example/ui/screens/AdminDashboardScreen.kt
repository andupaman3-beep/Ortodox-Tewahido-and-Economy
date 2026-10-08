package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.VerificationStatus
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val allB2B by viewModel.allB2BListings.collectAsState()
    val allServices by viewModel.allServices.collectAsState()
    val allReports by viewModel.allReports.collectAsState()

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Verification Queue, 2: Reports & Moderation, 3: Users

    val pendingVerifications = allUsers.filter { it.verificationStatus == VerificationStatus.PENDING.name }
    val pendingReports = allReports.filter { it.status == "PENDING" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Top banner
        Surface(
            color = TewahidoNavy,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Column {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "የተዋሕዶ መረብ አስተዳዳሪ ዳሽቦርድ" else "Platform Administration Console",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Central Security, Verification & Moderation",
                            color = OrthodoxGoldLight,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Tabs
        PrimaryTabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = TewahidoNavyDark,
            contentColor = OrthodoxGold
        ) {
            Tab(
                selected = selectedAdminTab == 0,
                onClick = { selectedAdminTab = 0 },
                text = { Text("Overview", fontSize = 11.sp, color = if (selectedAdminTab == 0) OrthodoxGold else Color.LightGray) }
            )
            Tab(
                selected = selectedAdminTab == 1,
                onClick = { selectedAdminTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Verifications", fontSize = 11.sp, color = if (selectedAdminTab == 1) OrthodoxGold else Color.LightGray)
                        if (pendingVerifications.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(containerColor = OrthodoxGold) { Text("${pendingVerifications.size}", color = Color.Black) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedAdminTab == 2,
                onClick = { selectedAdminTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Reports", fontSize = 11.sp, color = if (selectedAdminTab == 2) OrthodoxGold else Color.LightGray)
                        if (pendingReports.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(containerColor = StatusRed) { Text("${pendingReports.size}", color = Color.White) }
                        }
                    }
                }
            )
            Tab(
                selected = selectedAdminTab == 3,
                onClick = { selectedAdminTab = 3 },
                text = { Text("Users", fontSize = 11.sp, color = if (selectedAdminTab == 3) OrthodoxGold else Color.LightGray) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedAdminTab) {
                0 -> {
                    // Overview stats
                    item {
                        Text(
                            text = "Platform Statistics & Vital Health",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TewahidoNavy
                        )
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AdminStatCard(title = "Total Users", count = "${allUsers.size}", icon = Icons.Filled.People, color = TewahidoNavy, modifier = Modifier.weight(1f))
                            AdminStatCard(title = "Active Jobs", count = "${allJobs.size}", icon = Icons.Filled.Work, color = OrthodoxGoldDark, modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AdminStatCard(title = "Market Items", count = "${allProducts.size}", icon = Icons.Filled.Storefront, color = OrthodoxAmber, modifier = Modifier.weight(1f))
                            AdminStatCard(title = "B2B Deals", count = "${allB2B.size}", icon = Icons.Filled.LocalShipping, color = OrthodoxTeal, modifier = Modifier.weight(1f))
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AdminStatCard(title = "Services Listed", count = "${allServices.size}", icon = Icons.Filled.Handyman, color = TewahidoNavyLight, modifier = Modifier.weight(1f))
                            AdminStatCard(title = "Pending Reports", count = "${pendingReports.size}", icon = Icons.Filled.Flag, color = StatusRed, modifier = Modifier.weight(1f))
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Platform Security & Integrity Rules", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Verification badges are strictly gated behind Administrator review.", fontSize = 12.sp, color = TextMuted)
                                Text("• Zero anonymous write access enabled; authenticated role auditing active.", fontSize = 12.sp, color = TextMuted)
                                Text("• Automated scam-keyword telemetry continuously scanning postings.", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }
                }

                1 -> {
                    // Verification Queue
                    item {
                        Text(
                            text = "Orthodox Community Verification Requests (${pendingVerifications.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TewahidoNavy
                        )
                    }

                    if (pendingVerifications.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("No pending verification requests at this moment.", color = TextMuted)
                                }
                            }
                        }
                    } else {
                        items(pendingVerifications) { user ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${user.primaryRole} • ${user.email}", fontSize = 12.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        color = Color(0xFFFFF9E6),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Documents Note: ${user.verificationDocNote.ifBlank { "Parish and ID submitted." }}",
                                            fontSize = 11.sp,
                                            color = OrthodoxGoldDark,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.adminApproveVerification(user.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve & Verify", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.adminRejectVerification(user.id) },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Decline", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Reports & Moderation
                    item {
                        Text(
                            text = "Moderation Queue & Flagged Content (${allReports.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TewahidoNavy
                        )
                    }

                    if (allReports.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Zero abuse or scam reports! Platform is clean.", color = StatusGreen)
                                }
                            }
                        }
                    } else {
                        items(allReports) { rep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Reported ${rep.targetType}: ${rep.targetTitle}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Surface(
                                            color = if (rep.status == "PENDING") StatusRed.copy(alpha = 0.2f) else StatusGreen.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(text = rep.status, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Reason: ${rep.reason} • Reporter: ${rep.reporterName}", fontSize = 11.sp, color = TextMuted)
                                    Text(text = rep.details, fontSize = 12.sp)

                                    if (rep.status == "PENDING") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.adminResolveReport(rep.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Resolve & Remove", fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.adminDismissReport(rep.id) },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Dismiss", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // Users list
                    item {
                        Text(
                            text = "Registered Members & Businesses (${allUsers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TewahidoNavy
                        )
                    }

                    items(allUsers) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "${user.primaryRole} • ${user.email}", fontSize = 11.sp, color = TextMuted)
                                    Text(text = "Status: ${user.verificationStatus}", fontSize = 10.sp, color = OrthodoxGoldDark)
                                }
                                TextButton(
                                    onClick = { viewModel.adminToggleUserSuspension(user.id, !user.isSuspended) },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = if (user.isSuspended) StatusGreen else StatusRed
                                    )
                                ) {
                                    Text(if (user.isSuspended) "Restore" else "Suspend", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    title: String,
    count: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
