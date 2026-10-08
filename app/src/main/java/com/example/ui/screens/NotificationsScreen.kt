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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.state.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler { viewModel.navigateBack() }

    val lang by viewModel.language.collectAsState()
    val notifications by viewModel.userNotifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("notifications_screen")
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TewahidoNavy)
                    }
                    Text(
                        text = Strings.get("notifications", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TewahidoNavy
                    )
                }

                if (notifications.isNotEmpty()) {
                    TextButton(onClick = { viewModel.markAllNotificationsAsRead() }) {
                        Text(
                            text = if (lang == LanguageCode.AMHARIC) "ሁሉንም አንብቤያለሁ" else "Mark All Read",
                            fontSize = 12.sp,
                            color = OrthodoxGoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsNone,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) "ምንም አዲስ ማሳወቂያ የለም" else "No notifications yet",
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notif ->
                    NotificationCard(
                        notif = notif,
                        lang = lang,
                        onClick = { viewModel.markNotificationAsRead(notif.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notif: NotificationEntity,
    lang: LanguageCode,
    onClick: () -> Unit
) {
    val time = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault()).format(Date(notif.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${notif.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (notif.isRead) MaterialTheme.colorScheme.surface else Color(0xFFFFFDF5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notif.isRead) 1.dp else 2.dp),
        border = if (!notif.isRead) androidx.compose.foundation.BorderStroke(1.dp, OrthodoxGold.copy(alpha = 0.5f)) else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (notif.type) {
                            "VERIFICATION" -> OrthodoxGold
                            "JOB_APPLICATION" -> TewahidoNavy
                            "STATUS_CHANGE" -> StatusGreen
                            else -> OrthodoxTeal
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notif.type) {
                        "VERIFICATION" -> Icons.Filled.Verified
                        "JOB_APPLICATION" -> Icons.Filled.Work
                        "STATUS_CHANGE" -> Icons.Filled.CheckCircle
                        else -> Icons.Filled.Notifications
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == LanguageCode.AMHARIC) notif.amharicTitle else notif.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(text = time, fontSize = 10.sp, color = TextMuted)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notif.message,
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
