package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.JournalEntryEntity
import com.example.ui.components.GlassCard
import com.example.ui.components.MetricStatCard
import com.example.ui.components.MoodDistributionProgressCard
import com.example.ui.components.MoodLineChartCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.WarmAmber

@Composable
fun AnalyticsScreen(
    entries: List<JournalEntryEntity>
) {
    val avgScore = if (entries.isEmpty()) 8.4f else entries.map { it.moodScore }.average().toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("analytics_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Emotional Analytics & Intelligence",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Data-driven correlation between lifestyle habits & mood index",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricStatCard(
                    title = "Mood Index",
                    value = String.format("%.1f", avgScore),
                    change = "↑ Optimal Level",
                    icon = Icons.Default.Favorite,
                    iconBgColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )

                MetricStatCard(
                    title = "Total Logs",
                    value = "${entries.size}",
                    change = "Consistent Habit",
                    icon = Icons.Default.TrendingUp,
                    iconBgColor = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            MoodLineChartCard(entries = entries)
        }

        item {
            MoodDistributionProgressCard(entries = entries)
        }

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp
            ) {
                Column {
                    Text(
                        text = "AI Key Behavioral Insights",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    InsightBulletPoint("Sleep Quality", "7+ hours of sleep increases your next-day mood score by +2.4 points.")
                    Spacer(modifier = Modifier.height(8.dp))
                    InsightBulletPoint("Outdoor Exercise", "Walking or running yields an average mood rating of 8.8/10.")
                    Spacer(modifier = Modifier.height(8.dp))
                    InsightBulletPoint("Late Screen Time", "Friction and anxiety entries most frequently occur between 10 PM - 12 AM.")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InsightBulletPoint(topic: String, description: String) {
    Column {
        Text(text = "• $topic", style = MaterialTheme.typography.labelMedium, color = ElectricIndigo, fontWeight = FontWeight.Bold)
        Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
    }
}
