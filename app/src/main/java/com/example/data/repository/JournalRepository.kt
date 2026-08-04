package com.example.data.repository

import com.example.data.local.JournalDao
import com.example.data.local.JournalEntryEntity
import com.example.data.local.WellnessTipEntity
import com.example.data.remote.AiEngineService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalRepository(
    private val journalDao: JournalDao,
    private val aiEngineService: AiEngineService = AiEngineService()
) {
    val allEntries: Flow<List<JournalEntryEntity>> = journalDao.getAllEntries()
    val allTips: Flow<List<WellnessTipEntity>> = journalDao.getAllWellnessTips()
    val pendingSyncCount: Flow<Int> = journalDao.getPendingSyncCount()

    suspend fun seedSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingEntries = allEntries.first()
        if (existingEntries.isEmpty()) {
            val now = System.currentTimeMillis()
            val dayMs = 24 * 60 * 60 * 1000L
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

            val sampleEntries = listOf(
                JournalEntryEntity(
                    timestamp = now - (0 * dayMs),
                    dateString = dateFormat.format(Date(now)),
                    title = "Morning Clarity & Project Breakthrough",
                    content = "Started the day with 15 minutes of quiet meditation. Wrapped up the AI cloud architecture design session. Felt deeply engaged and clear-headed.",
                    mood = "JOYFUL",
                    moodScore = 9,
                    tags = "Work, Meditate, Focus",
                    sentimentLabel = "Positive",
                    aiTriggers = "Creative Flow, Mindfulness Practice",
                    aiSummaryTip = "Savor this state of momentum! Schedule a 10-minute restorative break before your evening session.",
                    syncStatus = "SYNCED"
                ),
                JournalEntryEntity(
                    timestamp = now - (1 * dayMs),
                    dateString = dateFormat.format(Date(now - (1 * dayMs))),
                    title = "Late Night Deadline Pressure",
                    content = "Struggled with fatigue around 4 PM. Took a brisk walk outside which helped release shoulder tightness, but stayed up late polishing slides.",
                    mood = "EXHAUSTED",
                    moodScore = 4,
                    tags = "Work, Sleep, Exercise",
                    sentimentLabel = "Challenging",
                    aiTriggers = "Screen Strain, Sleep Deficit",
                    aiSummaryTip = "Prioritize a digital sunset tonight at 9 PM. A warm bath or magnesium tea will accelerate nervous system recovery.",
                    syncStatus = "SYNCED"
                ),
                JournalEntryEntity(
                    timestamp = now - (2 * dayMs),
                    dateString = dateFormat.format(Date(now - (2 * dayMs))),
                    title = "Peaceful Sunset Walk & Gratitude",
                    content = "Disconnected from all notifications after 6 PM. Caught up with an old friend over dinner and reflected on long-term personal growth goals.",
                    mood = "CALM",
                    moodScore = 8,
                    tags = "Social, Outdoor, Reflection",
                    sentimentLabel = "Balanced",
                    aiTriggers = "Social Connection, Digital Detox",
                    aiSummaryTip = "Social connection is one of your strongest mood boosters. Maintain a weekly check-in habit with close friends.",
                    syncStatus = "SYNCED"
                ),
                JournalEntryEntity(
                    timestamp = now - (3 * dayMs),
                    dateString = dateFormat.format(Date(now - (3 * dayMs))),
                    title = "Deep Focus Coding Session",
                    content = "Built high-performance local database persistence and verified multi-tasking UI routes across tablet and mobile displays.",
                    mood = "FOCUSED",
                    moodScore = 9,
                    tags = "Coding, Architecture, Learning",
                    sentimentLabel = "Positive",
                    aiTriggers = "Task Completion, Autonomy",
                    aiSummaryTip = "Deep work sessions yield high cognitive satisfaction. Remember to stretch every hour to protect posture.",
                    syncStatus = "SYNCED"
                ),
                JournalEntryEntity(
                    timestamp = now - (4 * dayMs),
                    dateString = dateFormat.format(Date(now - (4 * dayMs))),
                    title = "Felt Mild Anxiety Over Roadmaps",
                    content = "Too many competing tasks in the morning backlog. Felt overwhelmed initially, but breaking them into 3 key priorities brought calm.",
                    mood = "ANXIOUS",
                    moodScore = 5,
                    tags = "Planning, Work, Stress",
                    sentimentLabel = "Reflective",
                    aiTriggers = "Over-commitment, Multitasking",
                    aiSummaryTip = "Task micro-chunking proved effective. Keep using the 3-Priority rule when overload begins.",
                    syncStatus = "SYNCED"
                )
            )

            for (entry in sampleEntries) {
                journalDao.insertEntry(entry)
            }

            val sampleTips = listOf(
                WellnessTipEntity(
                    category = "MINDFULNESS",
                    title = "Box Breathing Reset",
                    description = "Inhale for 4s, hold for 4s, exhale for 4s, hold for 4s. Repeat 4 cycles to recalibrate stress levels.",
                    targetMood = "ANXIOUS",
                    isCompleted = false,
                    priority = "HIGH"
                ),
                WellnessTipEntity(
                    category = "PHYSICAL",
                    title = "Hydration & Sunlight Walk",
                    description = "Drink 500ml water and spend 15 minutes outdoors within 1 hour of waking up.",
                    targetMood = "EXHAUSTED",
                    isCompleted = true,
                    priority = "HIGH"
                ),
                WellnessTipEntity(
                    category = "RECOVERY",
                    title = "Screen-Free Evening Ritual",
                    description = "Turn off phone and laptop screens 45 minutes before sleep. Read physical fiction or journal.",
                    targetMood = "EXHAUSTED",
                    isCompleted = false,
                    priority = "MEDIUM"
                ),
                WellnessTipEntity(
                    category = "COGNITIVE",
                    title = "Cognitive Re-framing",
                    description = "Identify one negative automatic thought today and rewrite it with three objective facts.",
                    targetMood = "OVERWHELMED",
                    isCompleted = false,
                    priority = "MEDIUM"
                )
            )

            journalDao.insertTips(sampleTips)
        }
    }

    suspend fun createJournalEntry(
        title: String,
        content: String,
        mood: String,
        moodScore: Int,
        tags: String
    ): JournalEntryEntity = withContext(Dispatchers.IO) {
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val aiResult = aiEngineService.analyzeJournalEntry(
            title = title,
            content = content,
            mood = mood,
            moodScore = moodScore,
            tags = tags
        )

        val entry = JournalEntryEntity(
            timestamp = System.currentTimeMillis(),
            dateString = dateStr,
            title = title.ifBlank { "Daily Reflections" },
            content = content,
            mood = mood,
            moodScore = moodScore,
            tags = tags,
            sentimentLabel = aiResult.sentimentLabel,
            aiTriggers = aiResult.triggers,
            aiSummaryTip = aiResult.summaryTip,
            syncStatus = "SYNCED"
        )

        val insertedId = journalDao.insertEntry(entry)

        // Add generated wellness tips
        val newTips = aiResult.recommendedTips.map { (tipTitle, tipDesc) ->
            WellnessTipEntity(
                category = when (mood) {
                    "ANXIOUS", "OVERWHELMED" -> "MINDFULNESS"
                    "EXHAUSTED" -> "RECOVERY"
                    "JOYFUL", "ENERGETIC" -> "PHYSICAL"
                    else -> "COGNITIVE"
                },
                title = tipTitle,
                description = tipDesc,
                targetMood = mood,
                isCompleted = false,
                priority = if (moodScore <= 5) "HIGH" else "MEDIUM"
            )
        }
        journalDao.insertTips(newTips)

        return@withContext entry.copy(id = insertedId)
    }

    suspend fun updateTipCompletion(tipId: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        val tips = allTips.first()
        val tip = tips.find { it.id == tipId }
        if (tip != null) {
            journalDao.updateTip(tip.copy(isCompleted = isCompleted))
        }
    }

    suspend fun deleteEntry(entry: JournalEntryEntity) = withContext(Dispatchers.IO) {
        journalDao.deleteEntry(entry)
    }

    suspend fun deleteTip(tipId: Long) = withContext(Dispatchers.IO) {
        journalDao.deleteTipById(tipId)
    }

    suspend fun syncPendingEntries() = withContext(Dispatchers.IO) {
        val pending = journalDao.getPendingSyncEntries()
        for (item in pending) {
            journalDao.updateEntry(item.copy(syncStatus = "SYNCED"))
        }
    }
}
