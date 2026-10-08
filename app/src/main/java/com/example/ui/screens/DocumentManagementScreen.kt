package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentManagementScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val documents by viewModel.userDocuments.collectAsState()

    var showUploadModal by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    // Upload Form State
    var docTitle by remember { mutableStateOf("") }
    var docFileName by remember { mutableStateOf("") }
    var docType by remember { mutableStateOf("CERTIFICATE") }
    var isDocPublic by remember { mutableStateOf(false) }

    val filteredDocs = documents.filter { doc ->
        selectedCategoryFilter == "ALL" || doc.docType == selectedCategoryFilter
    }

    Scaffold(
        modifier = modifier.testTag("document_management_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.get("documents_title", lang),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadModal = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Upload Document", tint = RoyalBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDark) DarkSurface else LightSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showUploadModal = true },
                containerColor = RoyalBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.UploadFile, contentDescription = null) },
                text = { Text(Strings.get("upload_doc", lang), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(if (isDark) DarkBackground else LightBackground)
        ) {
            // Category Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("ALL", "All"),
                    Pair("CERTIFICATE", "Certificates"),
                    Pair("PARISH_RECOMMENDATION", "Parish Letters"),
                    Pair("BUSINESS_LICENSE", "Licenses")
                ).forEach { (id, label) ->
                    val isSel = selectedCategoryFilter == id
                    Surface(
                        onClick = { selectedCategoryFilter = id },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSel) RoyalBlue else (if (isDark) DarkSurface else LightSurface),
                        border = if (!isSel) androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder) else null
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) Color.White else (if (isDark) DarkTextPrimary else LightTextPrimary),
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (filteredDocs.isEmpty()) {
                EmptyStateView(
                    title = "No documents found",
                    subtitle = "Upload your professional licenses, certificates, and parish recommendation letters securely.",
                    icon = Icons.Outlined.Folder,
                    actionText = Strings.get("upload_doc", lang),
                    onAction = { showUploadModal = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDocs) { doc ->
                        DocumentItemCard(
                            doc = doc,
                            isDark = isDark,
                            onDownload = {
                                viewModel.snackbarMessage.value = "Downloaded ${doc.fileName}."
                            },
                            onDelete = {
                                viewModel.deleteDocument(doc.id)
                            }
                        )
                    }
                }
            }
        }
    }

    // Upload Document Modal
    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.UploadFile, contentDescription = null, tint = RoyalBlue)
                    Text("Upload Professional Document", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = docTitle,
                        onValueChange = { docTitle = it },
                        label = { Text("Document Title (የሰነድ ርዕስ)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = docFileName,
                        onValueChange = { docFileName = it },
                        label = { Text("File Name (e.g. License_2026.pdf)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Document Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf(
                        Pair("CERTIFICATE", "Academic / Professional Certificate"),
                        Pair("PARISH_RECOMMENDATION", "Parish Recommendation Letter"),
                        Pair("BUSINESS_LICENSE", "Trade License (TIN / VAT)"),
                        Pair("PORTFOLIO", "Work Portfolio / Architectural Drawing")
                    ).forEach { (type, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            RadioButton(
                                selected = docType == type,
                                onClick = { docType = type },
                                colors = RadioButtonDefaults.colors(selectedColor = RoyalBlue)
                            )
                            Text(label, fontSize = 12.sp)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Make document publicly visible on profile", fontSize = 12.sp)
                        Switch(
                            checked = isDocPublic,
                            onCheckedChange = { isDocPublic = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = RoyalBlue)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (docTitle.isBlank() || docFileName.isBlank()) {
                            viewModel.snackbarMessage.value = "Please enter document title and filename."
                            return@Button
                        }
                        viewModel.uploadDocument(
                            title = docTitle,
                            docType = docType,
                            fileName = docFileName,
                            sizeKb = 480,
                            isPublic = isDocPublic
                        )
                        showUploadModal = false
                        docTitle = ""
                        docFileName = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("Upload", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text(Strings.get("cancel", lang))
                }
            }
        )
    }
}

@Composable
private fun DocumentItemCard(
    doc: DocumentEntity,
    isDark: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (doc.docType) {
                            "PARISH_RECOMMENDATION" -> AccentGoldLight
                            "BUSINESS_LICENSE" -> CatB2BBg
                            else -> RoyalBlueSoft
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (doc.docType) {
                        "PARISH_RECOMMENDATION" -> Icons.Filled.Church
                        "BUSINESS_LICENSE" -> Icons.Filled.VerifiedUser
                        else -> Icons.Filled.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = when (doc.docType) {
                        "PARISH_RECOMMENDATION" -> AccentGold
                        "BUSINESS_LICENSE" -> CatB2BColor
                        else -> RoyalBlue
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${doc.fileName} • ${doc.fileSizeKb} KB",
                    fontSize = 11.sp,
                    color = LightTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (doc.isPublic) EmeraldSoft else LightSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = if (doc.isPublic) "PUBLIC ON PROFILE" else "PRIVATE & SECURED",
                        color = if (doc.isPublic) EmeraldSuccess else LightTextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onDownload) {
                    Icon(Icons.Filled.Download, contentDescription = "Download", tint = RoyalBlue)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = StatusError)
                }
            }
        }
    }
}
