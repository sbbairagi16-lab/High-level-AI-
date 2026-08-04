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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.JournalEntryEntity
import com.example.ui.components.AiOrbCard
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.MoodLineChartCard
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MoodAnxious
import com.example.ui.theme.MoodCalm
import com.example.ui.theme.MoodExhausted
import com.example.ui.theme.MoodJoyful
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.WarmAmber

@Composable
fun DashboardScreen(
    entries: List<JournalEntryEntity>,
    aiResponse: String?,
    isThinking: Boolean,
    onAskQuestion: (String) -> Unit,
    onOpenNewEntry: () -> Unit,
    onViewAllEntries: () -> Unit,
    onViewEntryDetails: (JournalEntryEntity) -> Unit
) {
    val avgScore = if (entries.isEmpty()) 8.4f else entries.map { it.moodScore }.average().toFloat()
    val totalEntries = entries.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Welcome Header & New Entry Action (Matching top bar in prompt photo)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome back, Alex 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Explore, analyze, and elevate your emotional wellbeing.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF94A3B8)
                    )
                }

                Button(
                    onClick = onOpenNewEntry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricIndigo
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("new_journal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Entry",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "New Entry", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4 Key Metric Cards (Matching 4-card grid layout in prompt photo)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Avg Mood Index",
                    value = String.format("%.1f", avgScore),
                    change = "↑ 12% from last week",
                    icon = Icons.Default.Favorite,
                    iconBgColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Journal Entries",
                    value = "$totalEntries",
                    change = "↑ 18% this month",
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "AI Insights",
                    value = "${totalEntries * 3}",
                    change = "↑ 5% generated",
                    icon = Icons.Default.Psychology,
                    iconBgColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Wellness Streak",
                    value = "8 Days",
                    change = "Personal Best",
                    icon = Icons.Default.ElectricBolt,
                    iconBgColor = WarmAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // AI Glowing Orb Assistant Card (Matches prompt photo AI Chat center widget)
        item {
            AiOrbCard(
                aiResponse = aiResponse,
                isThinking = isThinking,
                onAskQuestion = onAskQuestion
            )
        }

        // Mood Line Chart Card
        item {
            MoodLineChartCard(entries = entries)
        }

        // Recent Entries Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Mood Journals",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricIndigo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onViewAllEntries() }
                        .testTag("view_all_entries_button")
                )
            }
        }

        // Recent Entries List
        items(entries.take(3)) { entry ->
            JournalItemCard(
                entry = entry,
                onClick = { onViewEntryDetails(entry) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun JournalItemCard(
    entry: JournalEntryEntity,
    onClick: () -> Unit
) {
    val moodColor = when (entry.mood.uppercase()) {
        "JOYFUL" -> MoodJoyful
        "CALM" -> MoodCalm
        "ANXIOUS" -> MoodAnxious
        "EXHAUSTED" -> MoodExhausted
        else -> ElectricIndigo
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(moodColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(moodColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${entry.mood} (${entry.moodScore}/10)",
                        style = MaterialTheme.typography.labelSmall,
                        color = moodColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Tip Preview snippet
            if (entry.aiSummaryTip.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardSurfaceElevated)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricIndigo,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = entry.aiSummaryTip,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCBD5E1),
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )

                Text(
                    text = "Tags: ${entry.tags}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
