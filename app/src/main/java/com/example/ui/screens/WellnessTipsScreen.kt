package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WellnessTipEntity
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RadiantRose
import com.example.ui.theme.WarmAmber

@Composable
fun WellnessTipsScreen(
    tips: List<WellnessTipEntity>,
    onToggleTipCompletion: (Long, Boolean) -> Unit,
    onDeleteTip: (Long) -> Unit
) {
    val completedCount = tips.count { it.isCompleted }
    val totalCount = tips.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("wellness_tips_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Header Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowingBorder = true,
                cornerRadius = 20.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AI Personalized Wellness Tips",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tailored recommendations based on mood history",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElectricIndigo.copy(alpha = 0.2f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$completedCount / $totalCount Done",
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricIndigo,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkCardSurfaceElevated)
                    ) {
                        val pct = if (totalCount == 0) 0f else completedCount.toFloat() / totalCount
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(EmeraldGreen)
                        )
                    }
                }
            }
        }

        items(tips) { tip ->
            WellnessTipItemCard(
                tip = tip,
                onToggleCompletion = { onToggleTipCompletion(tip.id, !tip.isCompleted) },
                onDelete = { onDeleteTip(tip.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun WellnessTipItemCard(
    tip: WellnessTipEntity,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    val categoryIcon: ImageVector = when (tip.category.uppercase()) {
        "MINDFULNESS" -> Icons.Default.SelfImprovement
        "PHYSICAL" -> Icons.Default.FitnessCenter
        "RECOVERY" -> Icons.Default.Nightlight
        else -> Icons.Default.Psychology
    }

    val categoryColor: Color = when (tip.category.uppercase()) {
        "MINDFULNESS" -> NeonCyan
        "PHYSICAL" -> EmeraldGreen
        "RECOVERY" -> NeonPurple
        else -> ElectricBlue
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        onClick = onToggleCompletion
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox icon
            IconButton(
                onClick = onToggleCompletion,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("tip_checkbox_${tip.id}")
            ) {
                Icon(
                    imageVector = if (tip.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (tip.isCompleted) EmeraldGreen else Color(0xFF64748B),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tip.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (tip.priority == "HIGH") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WarmAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "High Priority",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (tip.isCompleted) Color(0xFF64748B) else Color.White,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (tip.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (tip.isCompleted) Color(0xFF475569) else Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Tip",
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
