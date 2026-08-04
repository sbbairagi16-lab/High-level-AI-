package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String,
    val title: String,
    val content: String,
    val mood: String, // e.g. JOYFUL, CALM, FOCUSED, ENERGETIC, ANXIOUS, EXHAUSTED, REFLECTIVE, OVERWHELMED
    val moodScore: Int, // 1 to 10
    val tags: String, // comma separated tags e.g. "Work, Exercise"
    val sentimentLabel: String, // "Positive", "Balanced", "Challenging", "Reflective"
    val aiTriggers: String, // AI identified triggers or themes
    val aiSummaryTip: String, // AI generated wellness tip specific to entry
    val syncStatus: String = "SYNCED" // SYNCED, PENDING_SYNC, LOCAL_ONLY
)

@Entity(tableName = "wellness_tips")
data class WellnessTipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // MINDFULNESS, PHYSICAL, RECOVERY, COGNITIVE
    val title: String,
    val description: String,
    val targetMood: String,
    val isCompleted: Boolean = false,
    val priority: String = "MEDIUM", // HIGH, MEDIUM, ROUTINE
    val createdAt: Long = System.currentTimeMillis()
)
