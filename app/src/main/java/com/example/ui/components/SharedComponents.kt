package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VerificationStatus
import com.example.ui.state.LanguageCode
import com.example.ui.state.Strings
import com.example.ui.theme.*

@Composable
fun VerificationBadge(
    status: String,
    lang: LanguageCode,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val verStatus = try {
        VerificationStatus.valueOf(status)
    } catch (_: Exception) {
        VerificationStatus.UNVERIFIED
    }

    val (bg, fg, icon, label) = when (verStatus) {
        VerificationStatus.VERIFIED -> Quad(
            RoyalBlueSoft,
            RoyalBlue,
            Icons.Filled.Verified,
            Strings.get("verified_badge", lang)
        )
        VerificationStatus.PENDING -> Quad(
            StatusWarningSoft,
            StatusWarning,
            Icons.Filled.Schedule,
            Strings.get("pending_badge", lang)
        )
        VerificationStatus.REJECTED -> Quad(
            StatusErrorSoft,
            StatusError,
            Icons.Filled.Cancel,
            "Declined"
        )
        VerificationStatus.UNVERIFIED -> Quad(
            LightSurfaceVariant,
            LightTextSecondary,
            Icons.Outlined.Shield,
            Strings.get("unverified_badge", lang)
        )
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, fg.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 6.dp else 10.dp,
                vertical = if (compact) 3.dp else 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = fg,
                modifier = Modifier.size(if (compact) 14.dp else 16.dp)
            )
            if (!compact) {
                Text(
                    text = label,
                    color = fg,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun StarRatingBar(
    rating: Float,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val filledStars = rating.toInt().coerceIn(0, 5)
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= filledStars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = if (i <= filledStars) StarGold else Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = String.format("%.1f", rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )
        if (reviewCount != null && reviewCount > 0) {
            Text(
                text = "($reviewCount)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 13.sp,
                    color = RoyalBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = if (icon != null) {
            {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isSelected) Color.White else RoyalBlue
                )
            }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = RoyalBlue,
            selectedLabelColor = Color.White,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.height(36.dp)
    )
}

@Composable
fun EmptyStateView(
    title: String,
    subtitle: String? = null,
    icon: ImageVector = Icons.Outlined.SearchOff,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(RoyalBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RoyalBlue,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(actionText, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
