package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CategoryChip
import com.example.ui.state.*
import com.example.ui.theme.*

@Composable
fun AiAssistantScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val aiResult by viewModel.aiResultOutput.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var selectedMode by remember { mutableStateOf("JOB_MATCH") } // JOB_MATCH, ENHANCE_DESCRIPTION, FRAUD_DETECTOR
    var promptInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_assistant_screen")
    ) {
        // Gorgeous Rich Royal Blue Gradient Header
        Surface(
            color = if (isDark) DarkSurface else ElectricBlueHeader,
            shadowElevation = 5.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(listOf(DarkSurface, DarkBackground))
                        } else {
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E3A8A), // Deep Royal Blue
                                    Color(0xFF1D4ED8), // Vivid Sapphire Blue
                                    Color(0xFF2563EB)  // Bright Vibrant Blue
                                )
                            )
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Forum, contentDescription = null, tint = AccentGoldBright, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ተዋሕዶ Chat (ረዳትና አማካሪ)" else "Tewahido Chat",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Orthodox Smart Matcher, Listing Copilot & Sentinel",
                            color = AccentGoldLight,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }

        // Content Area with Stunning Purple/Lavender Background
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1A102F), // Dark midnight purple
                                Color(0xFF0F081D)  // Deep velvet purple
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFF3E8FF), // Light soft lavender purple
                                Color(0xFFEDE9FE), // Modern lilac tint
                                Color(0xFFFAF5FF)  // Clean soft purple fade
                            )
                        )
                    }
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode selection chips
            item {
                Text(
                    text = if (lang == LanguageCode.AMHARIC) "የውይይትና የረዳት አገልግሎት ምረጥ" else "Select Tewahido Chat Service",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF4C1D95)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        label = if (lang == LanguageCode.AMHARIC) "የሥራ አዛማጅ" else "Job Matcher",
                        isSelected = selectedMode == "JOB_MATCH",
                        onClick = {
                            selectedMode = "JOB_MATCH"
                            promptInput = "Android, Kotlin, Architecture, Accounting"
                        },
                        icon = Icons.Filled.Work
                    )
                    CategoryChip(
                        label = if (lang == LanguageCode.AMHARIC) "ማስታወቂያ አሻሽል" else "Listing Enhancer",
                        isSelected = selectedMode == "ENHANCE_DESCRIPTION",
                        onClick = {
                            selectedMode = "ENHANCE_DESCRIPTION"
                            promptInput = "የቤተክርስቲያን አልባሳትና የጥበብ እቃዎች በታማኝነት እንሸጣለን።"
                        },
                        icon = Icons.Filled.EditNote
                    )
                    CategoryChip(
                        label = if (lang == LanguageCode.AMHARIC) "ማጭበርበር ጠቋሚ" else "Fraud Sentinel",
                        isSelected = selectedMode == "FRAUD_DETECTOR",
                        onClick = {
                            selectedMode = "FRAUD_DETECTOR"
                            promptInput = "Invest 1000 ETB get instant 5000 ETB quick cash lottery gift"
                        },
                        icon = Icons.Filled.Security
                    )
                }
            }

            // Description info card with purple styling
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF281A45) else Color(0xFFFFFFFF).copy(alpha = 0.90f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF581C87).copy(alpha = 0.6f) else Color(0xFFDDD6FE)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = when (selectedMode) {
                                "JOB_MATCH" -> "Matches your verified skills and qualifications with live posted employer positions."
                                "ENHANCE_DESCRIPTION" -> "Polishes product and service descriptions with dignified Christian ethos, professional clarity, and multilingual appeal."
                                else -> "Scans suspicious ads, spam, and advance-fee schemes to safeguard our Orthodox community from scams."
                            },
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF4C1D95),
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Input card with purple accents
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF281A45) else Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF581C87) else Color(0xFFDDD6FE)
                    ),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = when (selectedMode) {
                                "JOB_MATCH" -> "Enter your skills or target role:"
                                "ENHANCE_DESCRIPTION" -> "Draft your raw listing text:"
                                else -> "Enter text or listing to evaluate:"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color(0xFFF3E8FF) else Color(0xFF3B0764)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("ተዋሕዶ Chat ጥያቄዎን ወይም ማስታወቂያዎን እዚህ ይፃፉ...", fontSize = 12.5.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_prompt_field"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF7C3AED),
                                unfocusedBorderColor = if (isDark) Color(0xFF581C87) else Color(0xFFDDD6FE)
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    viewModel.runAiFeature(selectedMode, promptInput)
                                }
                            },
                            enabled = !isAiLoading && promptInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7C3AED), // Vibrant Royal Violet
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("ai_run_btn")
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ተዋሕዶ Chat በማስላት ላይ...", color = Color.White, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Filled.Forum, contentDescription = null, tint = AccentGoldBright)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "ተዋሕዶ Chat ተንትንና አዛምድ" else "Ask Tewahido Chat",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Output section in rich purple styled card
            if (aiResult != null) {
                item {
                    Text(
                        text = "ተዋሕዶ Chat ውጤትና ምክር (Chat Response)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF4C1D95)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_result_card"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) Color(0xFF2E1065) else Color(0xFFFAF5FF)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF7C3AED).copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Forum, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Tewahido Chat Assistant",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF7C3AED)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = aiResult!!,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp,
                                color = if (isDark) Color(0xFFF3E8FF) else Color(0xFF1E1B4B)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = if (isDark) Color.Black.copy(alpha = 0.3f) else Color(0xFFEDE9FE).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "⚠️ Disclaimer: ተዋሕዶ Chat ጥቆማና ምክር ይሰጣል። የሥራና የንግድ ውሳኔዎች በራስዎ ፍላጎትና ስምምነት የሚከናወኑ ናቸው።",
                                    fontSize = 10.5.sp,
                                    color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF6B21A8),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
