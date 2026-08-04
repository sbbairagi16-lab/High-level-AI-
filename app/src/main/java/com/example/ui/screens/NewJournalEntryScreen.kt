package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.MoodOption
import com.example.ui.components.MoodSelector
import com.example.ui.components.availableMoods
import com.example.ui.theme.DarkBorderColor
import com.example.ui.theme.DarkCardSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.NeonPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewJournalEntryScreen(
    isAnalyzing: Boolean,
    onBack: () -> Unit,
    onSaveEntry: (String, String, String, Int, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedMoodOption by remember { mutableStateOf(availableMoods[0]) }
    var moodScore by remember { mutableStateOf(selectedMoodOption.defaultScore) }
    var selectedTags by remember { mutableStateOf(setOf("Work", "Meditate")) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("new_journal_entry_screen")
    ) {
        // Back Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "New Daily Reflection",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AI will analyze sentiment & suggest wellness tips",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood & Score Selector Component
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 18.dp
        ) {
            MoodSelector(
                selectedMood = selectedMoodOption.key,
                onMoodSelected = { option ->
                    selectedMoodOption = option
                    moodScore = option.defaultScore
                },
                moodScore = moodScore,
                onScoreChanged = { moodScore = it },
                selectedTags = selectedTags,
                onTagToggled = { tag ->
                    selectedTags = if (selectedTags.contains(tag)) {
                        selectedTags - tag
                    } else {
                        selectedTags + tag
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Input Field
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("entry_title_input"),
            label = { Text("Entry Title", color = Color(0xFF94A3B8)) },
            placeholder = { Text("e.g., Morning Breakthrough or Late Night Fatigue", color = Color(0xFF64748B)) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkCardSurfaceElevated,
                unfocusedContainerColor = DarkCardSurfaceElevated,
                focusedBorderColor = ElectricIndigo,
                unfocusedBorderColor = DarkBorderColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // AI Writing Ideas / Prompts
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Reflection Prompts (Tap to insert)",
                style = MaterialTheme.typography.labelMedium,
                color = ElectricBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val reflectionPrompts = listOf(
                "What brought a smile to your face today?",
                "What caused unexpected tension or stress?",
                "3 things you feel grateful for right now"
            )

            reflectionPrompts.forEach { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkCardSurfaceElevated)
                        .clickable {
                            content = if (content.isBlank()) "$prompt\n\n" else "$content\n$prompt\n"
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = prompt, style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content Area Text Field
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .testTag("entry_content_input"),
            label = { Text("Journal Reflections & Notes", color = Color(0xFF94A3B8)) },
            placeholder = { Text("Express your thoughts freely. MindPulse AI will extract emotional themes and personalized tips...", color = Color(0xFF64748B)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkCardSurfaceElevated,
                unfocusedContainerColor = DarkCardSurfaceElevated,
                focusedBorderColor = ElectricIndigo,
                unfocusedBorderColor = DarkBorderColor,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save & Analyze Action Button
        Button(
            onClick = {
                val tagsStr = selectedTags.joinToString(", ")
                onSaveEntry(title, content, selectedMoodOption.key, moodScore, tagsStr)
            },
            enabled = !isAnalyzing && content.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_and_analyze_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricIndigo,
                disabledContainerColor = DarkCardSurfaceElevated
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Analyzing Entry with Gemini AI...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyze & Save Reflection",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
