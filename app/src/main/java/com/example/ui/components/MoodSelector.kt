package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkBorderColor
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.MoodAnxious
import com.example.ui.theme.MoodCalm
import com.example.ui.theme.MoodEnergetic
import com.example.ui.theme.MoodExhausted
import com.example.ui.theme.MoodFocused
import com.example.ui.theme.MoodJoyful
import com.example.ui.theme.MoodOverwhelmed
import com.example.ui.theme.MoodReflective

data class MoodOption(
    val key: String,
    val label: String,
    val emoji: String,
    val color: Color,
    val defaultScore: Int
)

val availableMoods = listOf(
    MoodOption("JOYFUL", "Joyful", "😊", MoodJoyful, 9),
    MoodOption("CALM", "Calm", "😌", MoodCalm, 8),
    MoodOption("FOCUSED", "Focused", "🎯", MoodFocused, 9),
    MoodOption("ENERGETIC", "Energetic", "⚡", MoodEnergetic, 9),
    MoodOption("ANXIOUS", "Anxious", "😰", MoodAnxious, 4),
    MoodOption("EXHAUSTED", "Exhausted", "😴", MoodExhausted, 3),
    MoodOption("REFLECTIVE", "Reflective", "🧘", MoodReflective, 7),
    MoodOption("OVERWHELMED", "Overwhelmed", "🌊", MoodOverwhelmed, 4)
)

val defaultTagOptions = listOf(
    "Work", "Sleep", "Exercise", "Social", "Meditate", "Nutrition", "Reading", "Family", "Travel"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodSelector(
    selectedMood: String,
    onMoodSelected: (MoodOption) -> Unit,
    moodScore: Int,
    onScoreChanged: (Int) -> Unit,
    selectedTags: Set<String>,
    onTagToggled: (String) -> Unit
) {
    val activeOption = availableMoods.find { it.key == selectedMood } ?: availableMoods[0]

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Primary Mood",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Mood Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(availableMoods) { item ->
                val isSelected = item.key == selectedMood
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) item.color.copy(alpha = 0.2f) else DarkCardSurfaceElevated
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) item.color else DarkBorderColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onMoodSelected(item) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("mood_chip_${item.key.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intensity Score Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mood Intensity Score",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF94A3B8)
            )
            Text(
                text = "$moodScore / 10",
                style = MaterialTheme.typography.titleMedium,
                color = activeOption.color,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = moodScore.toFloat(),
            onValueChange = { onScoreChanged(it.toInt().coerceIn(1, 10)) },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = activeOption.color,
                activeTrackColor = activeOption.color,
                inactiveTrackColor = DarkCardSurfaceElevated
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mood_score_slider")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Context Tag Chips
        Text(
            text = "Context Tags",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            defaultTagOptions.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) ElectricIndigo.copy(alpha = 0.25f) else DarkCardSurfaceElevated
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) ElectricIndigo else DarkBorderColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onTagToggled(tag) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("tag_chip_${tag.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ElectricIndigo,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) Color.White else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
