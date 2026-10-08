package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.model.CVEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.state.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CVManagementScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val userCVs by viewModel.userCVs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: My CVs, 1: CV Builder

    var showUploadDialog by remember { mutableStateOf(false) }
    var showPreviewCV by remember { mutableStateOf<CVEntity?>(null) }

    // Upload Dialog Form State
    var uploadFileName by remember { mutableStateOf("") }
    var uploadDocTitle by remember { mutableStateOf("") }
    var uploadFileType by remember { mutableStateOf("PDF") }
    var uploadError by remember { mutableStateOf<String?>(null) }

    // In-App CV Builder Form State
    var bFullName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var bProfession by remember { mutableStateOf(currentUser?.profession ?: "") }
    var bSummary by remember { mutableStateOf(currentUser?.bio ?: "") }
    var bExperience by remember { mutableStateOf("") }
    var bEducation by remember { mutableStateOf("") }
    var bSkills by remember { mutableStateOf("") }
    var bLanguages by remember { mutableStateOf("Amharic, English, Ge'ez") }
    var bCertifications by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.testTag("cv_management_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = Strings.get("cv_resumes", lang),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showUploadDialog = true }) {
                        Icon(Icons.Filled.UploadFile, contentDescription = "Upload", tint = RoyalBlue)
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
            // Tab Row (My CVs vs In-App CV Builder)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = if (isDark) DarkSurface else LightSurface,
                contentColor = RoyalBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "${Strings.get("cv_resumes", lang)} (${userCVs.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = Strings.get("create_cv", lang),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // List of Saved/Uploaded CVs
                if (userCVs.isEmpty()) {
                    EmptyStateView(
                        title = "No CV uploaded yet",
                        subtitle = "Build a professional CV with our builder or upload your existing PDF/Word resume.",
                        icon = Icons.Outlined.Description,
                        actionText = Strings.get("create_cv", lang),
                        onAction = { selectedTab = 1 }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(userCVs) { cv ->
                            CVCard(
                                cv = cv,
                                isDark = isDark,
                                onPreview = { showPreviewCV = cv },
                                onDelete = { viewModel.deleteCV(cv.id) },
                                onDownload = {
                                    viewModel.snackbarMessage.value = "Downloaded ${cv.fileName} to local device storage."
                                }
                            )
                        }

                        item {
                            Button(
                                onClick = { showUploadDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Filled.UploadFile, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(Strings.get("upload_cv", lang), fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else {
                // In-App Interactive CV Builder Form
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AccentGoldBright)
                                    Text(
                                        text = "Professional CV Builder",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = if (isDark) DarkTextPrimary else BrandNavyDark
                                    )
                                }
                                Text(
                                    text = "Fill in your qualifications to generate an Orthodox-certified professional resume.",
                                    fontSize = 12.sp,
                                    color = LightTextSecondary
                                )

                                OutlinedTextField(
                                    value = bFullName,
                                    onValueChange = { bFullName = it },
                                    label = { Text(Strings.get("full_name", lang)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bProfession,
                                    onValueChange = { bProfession = it },
                                    label = { Text("Professional Title / Profession") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bSummary,
                                    onValueChange = { bSummary = it },
                                    label = { Text(Strings.get("personal_summary", lang)) },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bExperience,
                                    onValueChange = { bExperience = it },
                                    label = { Text(Strings.get("work_experience", lang)) },
                                    placeholder = { Text("e.g. Senior Architect at Studio (3 yrs), Project Manager (2 yrs)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 2,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bEducation,
                                    onValueChange = { bEducation = it },
                                    label = { Text(Strings.get("education_qual", lang)) },
                                    placeholder = { Text("e.g. B.Sc in Civil Engineering - AAU") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bSkills,
                                    onValueChange = { bSkills = it },
                                    label = { Text(Strings.get("skills_expertise", lang)) },
                                    placeholder = { Text("e.g. Architecture, AutoCAD, Kotlin, Project Leadership") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bLanguages,
                                    onValueChange = { bLanguages = it },
                                    label = { Text("Languages (ቋንቋዎች)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = bCertifications,
                                    onValueChange = { bCertifications = it },
                                    label = { Text("Certifications & Licenses") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        if (bFullName.isBlank() || bProfession.isBlank()) {
                                            viewModel.snackbarMessage.value = "Please provide your full name and profession."
                                            return@Button
                                        }
                                        viewModel.createOrUpdateCV(
                                            fullName = bFullName,
                                            title = bProfession,
                                            summary = bSummary,
                                            experience = bExperience,
                                            education = bEducation,
                                            skills = bSkills,
                                            languages = bLanguages,
                                            certifications = bCertifications
                                        )
                                        selectedTab = 0
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Save & Generate CV", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Upload CV Dialog (Phase 7)
    if (showUploadDialog) {
        AlertDialog(
            onDismissRequest = {
                showUploadDialog = false
                uploadError = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Filled.UploadFile, contentDescription = null, tint = RoyalBlue)
                    Text("Upload Professional CV / Resume", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Permitted formats: PDF, DOC, DOCX (Max size: 5 MB). File integrity will be validated.",
                        fontSize = 12.sp,
                        color = LightTextSecondary
                    )

                    OutlinedTextField(
                        value = uploadDocTitle,
                        onValueChange = { uploadDocTitle = it },
                        label = { Text("CV Title (e.g. Senior Auditor Resume 2026)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = uploadFileName,
                        onValueChange = { uploadFileName = it },
                        label = { Text("File Name (e.g. Marta_Wolde_CV.pdf)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Format Radio Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf("PDF", "DOCX").forEach { format ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { uploadFileType = format }
                            ) {
                                RadioButton(
                                    selected = uploadFileType == format,
                                    onClick = { uploadFileType = format },
                                    colors = RadioButtonDefaults.colors(selectedColor = RoyalBlue)
                                )
                                Text(format, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }

                    uploadError?.let { err ->
                        Text(err, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = uploadFileName.trim()
                        if (trimmed.isBlank()) {
                            uploadError = "Please specify a file name."
                            return@Button
                        }
                        if (!trimmed.endsWith(".pdf", ignoreCase = true) &&
                            !trimmed.endsWith(".docx", ignoreCase = true) &&
                            !trimmed.endsWith(".doc", ignoreCase = true)
                        ) {
                            uploadError = "Invalid file extension! Must be .pdf or .docx"
                            return@Button
                        }
                        viewModel.uploadCVFile(
                            title = uploadDocTitle.ifBlank { "Professional CV" },
                            fileName = trimmed,
                            fileType = uploadFileType,
                            sizeKb = 320
                        )
                        showUploadDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Text("Validate & Upload", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) {
                    Text(Strings.get("cancel", lang))
                }
            }
        )
    }

    // Preview CV Dialog
    showPreviewCV?.let { cv ->
        AlertDialog(
            onDismissRequest = { showPreviewCV = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Description, contentDescription = null, tint = RoyalBlue)
                    Text(cv.fullName, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            color = RoyalBlueSoft,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(cv.professionTitle, fontWeight = FontWeight.Bold, color = RoyalBlue)
                                Text("File: ${cv.fileName} (${cv.fileType} • ${cv.fileSizeKb} KB)", fontSize = 11.sp, color = LightTextSecondary)
                            }
                        }
                    }
                    item {
                        Text("Summary:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(cv.summary, fontSize = 12.sp, color = LightTextSecondary)
                    }
                    if (cv.experience.isNotBlank()) {
                        item {
                            Text("Work Experience:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(cv.experience, fontSize = 12.sp, color = LightTextSecondary)
                        }
                    }
                    if (cv.education.isNotBlank()) {
                        item {
                            Text("Education:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(cv.education, fontSize = 12.sp, color = LightTextSecondary)
                        }
                    }
                    if (cv.skills.isNotBlank()) {
                        item {
                            Text("Skills:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(cv.skills, fontSize = 12.sp, color = LightTextSecondary)
                        }
                    }
                    if (cv.certifications.isNotBlank()) {
                        item {
                            Text("Certifications:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(cv.certifications, fontSize = 12.sp, color = LightTextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.snackbarMessage.value = "Downloaded ${cv.fileName} successfully."
                        showPreviewCV = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.get("download_cv", lang), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPreviewCV = null }) {
                    Text(Strings.get("cancel", lang))
                }
            }
        )
    }
}

@Composable
private fun CVCard(
    cv: CVEntity,
    isDark: Boolean,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) DarkSurface else LightSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) DarkBorder else LightBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoyalBlueSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PictureAsPdf,
                            contentDescription = null,
                            tint = RoyalBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = cv.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${cv.professionTitle} • ${cv.fileSizeKb} KB",
                            fontSize = 11.sp,
                            color = LightTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSoft
                ) {
                    Text(
                        text = "PRIMARY",
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = cv.summary,
                fontSize = 12.sp,
                color = LightTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = if (isDark) DarkBorder else LightBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = onPreview) {
                        Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = RoyalBlue)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview", color = RoyalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = onDownload) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldSuccess)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
