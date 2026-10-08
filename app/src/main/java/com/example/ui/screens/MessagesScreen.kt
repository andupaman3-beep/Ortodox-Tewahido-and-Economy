package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.ui.state.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val conversations by viewModel.userConversations.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("messages_screen")
    ) {
        // Beautiful Vibrant Blue Gradient Header
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
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Forum, contentDescription = null, tint = AccentGoldBright, modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = Strings.get("messages", lang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Surface(
                        color = Color.White.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${conversations.size} ${if (lang == LanguageCode.AMHARIC) "ውይይቶች" else "Chats"}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Body with Gorgeous Purple Background
        Box(
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
                )
        ) {
            if (conversations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Forum,
                                contentDescription = null,
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ምንም መልእክት የለም" else "No conversations yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF4C1D95)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "ከቀጣሪዎች፣ ሻጮችና አስመጪዎች ጋር ውይይት ሲጀምሩ እዚህ ይታያሉ።"
                            else
                                "When you contact employers, sellers or importers, chats appear here.",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF6B21A8)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(conversations) { conv ->
                        ConversationItemCard(
                            conv = conv,
                            currentUserId = currentUser?.id ?: "",
                            onClick = {
                                val partnerId = if (conv.participant1Id == currentUser?.id) conv.participant2Id else conv.participant1Id
                                val partnerName = if (conv.participant1Id == currentUser?.id) conv.participant2Name else conv.participant1Name
                                viewModel.openChatWith(partnerId, partnerName)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItemCard(
    conv: ConversationEntity,
    currentUserId: String,
    onClick: () -> Unit
) {
    val partnerName = if (conv.participant1Id == currentUserId) conv.participant2Name else conv.participant1Name

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("conv_item_${conv.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C3AED)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = partnerName.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = partnerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.5.sp,
                    color = Color(0xFF1E1B4B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = conv.lastMessage,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(conv.lastMessageTimestamp))
                Text(text = timeStr, fontSize = 10.5.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ChatScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val messages by viewModel.activeChatMessages.collectAsState()
    val partner by viewModel.activeChatRecipient.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var inputMessage by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }

    val partnerName = partner?.second ?: "User"
    val partnerId = partner?.first ?: ""

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .testTag("chat_screen"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
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
                        .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.navigateBack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(AccentGoldBright),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = partnerName.take(1),
                                    color = BrandNavyDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = partnerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                                Text(
                                    text = if (lang == LanguageCode.AMHARIC) "ንቁ ውይይት (መስመር ላይ)" else "Active Conversation (Online)",
                                    color = AccentGoldLight,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        IconButton(onClick = { showReportDialog = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Options", tint = Color.White)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = if (isDark) DarkSurface else Color.White,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = {
                            Text(
                                text = if (lang == LanguageCode.AMHARIC) "መልእክትዎን እዚህ ይፃፉ..." else "Type message...",
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF7C3AED),
                            unfocusedBorderColor = if (isDark) Color(0xFF581C87) else Color(0xFFDDD6FE)
                        )
                    )
                    IconButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendChatMessage(inputMessage)
                                inputMessage = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7C3AED))
                            .testTag("chat_send_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        color = if (isDark) Color(0xFF281A45) else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7C3AED).copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC)
                                "🛡️ የተጠበቀና ክርስቲያናዊ ስነ-ምግባር ያለው ውይይት"
                            else
                                "🛡️ Secure & Ethical Communication",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFE9D5FF) else Color(0xFF5B21B6),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.id
                MessageBubble(message = msg, isMe = isMe)
            }
        }
    }

    if (showReportDialog) {
        ReportContentDialog(
            targetType = "USER",
            targetId = partnerId,
            targetTitle = partnerName,
            lang = lang,
            onDismiss = { showReportDialog = false },
            onSubmit = { reason, details ->
                viewModel.submitReport("USER", partnerId, partnerName, reason, details)
                showReportDialog = false
            }
        )
    }
}

@Composable
fun MessageBubble(
    message: MessageEntity,
    isMe: Boolean
) {
    val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) Color(0xFF6D28D9) else Color.White
            ),
            border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDD6FE)) else null,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 3.dp,
                bottomEnd = if (isMe) 3.dp else 16.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.widthIn(max = 285.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                Text(
                    text = message.content,
                    color = if (isMe) Color.White else Color(0xFF1E1B4B),
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = time,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isMe) Color(0xFFE9D5FF) else Color(0xFF7C3AED)
                    )
                }
            }
        }
    }
}
