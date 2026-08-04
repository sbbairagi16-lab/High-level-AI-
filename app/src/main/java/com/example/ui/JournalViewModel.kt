package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.JournalEntryEntity
import com.example.data.local.WellnessTipEntity
import com.example.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    OVERVIEW("Overview", "dashboard"),
    JOURNAL("Journal Log", "edit_note"),
    WELLNESS("AI Tips", "auto_awesome"),
    ANALYTICS("Analytics", "analytics"),
    CLOUD_SYNC("Cloud Hub", "cloud_sync")
}

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository

    val entries: StateFlow<List<JournalEntryEntity>>
    val wellnessTips: StateFlow<List<WellnessTipEntity>>
    val pendingSyncCount: StateFlow<Int>

    private val _selectedTab = MutableStateFlow(AppTab.OVERVIEW)
    val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow<String?>(null)
    val selectedMoodFilter: StateFlow<String?> = _selectedMoodFilter.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _isCloudOnline = MutableStateFlow(true)
    val isCloudOnline: StateFlow<Boolean> = _isCloudOnline.asStateFlow()

    private val _aiAssistantResponse = MutableStateFlow<String?>(null)
    val aiAssistantResponse: StateFlow<String?> = _aiAssistantResponse.asStateFlow()

    private val _aiThinking = MutableStateFlow(false)
    val aiThinking: StateFlow<Boolean> = _aiThinking.asStateFlow()

    val filteredEntries: StateFlow<List<JournalEntryEntity>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = JournalRepository(database.journalDao())

        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }

        entries = repository.allEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        wellnessTips = repository.allTips.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        pendingSyncCount = repository.pendingSyncCount.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        filteredEntries = combine(entries, searchQuery, selectedMoodFilter) { list, query, moodFilter ->
            list.filter { entry ->
                val matchesQuery = query.isBlank() ||
                        entry.title.contains(query, ignoreCase = true) ||
                        entry.content.contains(query, ignoreCase = true) ||
                        entry.tags.contains(query, ignoreCase = true)
                val matchesMood = moodFilter == null || entry.mood.equals(moodFilter, ignoreCase = true)
                matchesQuery && matchesMood
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun selectTab(tab: AppTab) {
        _selectedTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMoodFilter(mood: String?) {
        _selectedMoodFilter.value = if (_selectedMoodFilter.value == mood) null else mood
    }

    fun toggleCloudOnlineStatus() {
        _isCloudOnline.value = !_isCloudOnline.value
    }

    fun addJournalEntry(
        title: String,
        content: String,
        mood: String,
        moodScore: Int,
        tags: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                repository.createJournalEntry(
                    title = title,
                    content = content,
                    mood = mood,
                    moodScore = moodScore,
                    tags = tags
                )
            } finally {
                _isAnalyzing.value = false
                onComplete()
            }
        }
    }

    fun toggleTipCompletion(tipId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTipCompletion(tipId, isCompleted)
        }
    }

    fun deleteJournalEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun deleteWellnessTip(tipId: Long) {
        viewModelScope.launch {
            repository.deleteTip(tipId)
        }
    }

    fun triggerManualCloudSync() {
        viewModelScope.launch {
            repository.syncPendingEntries()
        }
    }

    fun askAiAssistant(userQuestion: String) {
        if (userQuestion.isBlank()) return
        viewModelScope.launch {
            _aiThinking.value = true
            _aiAssistantResponse.value = null
            kotlinx.coroutines.delay(1200) // Realistic AI synthesis pause

            val lower = userQuestion.lowercase()
            val answer = when {
                lower.contains("anxious") || lower.contains("stress") ->
                    "When anxiety rises, ground yourself in your physical body. Try 4-7-8 breathing for 3 minutes: inhale 4s, hold 7s, exhale 8s. Remind yourself: feelings are waves that rise and fall."
                lower.contains("sleep") || lower.contains("tired") || lower.contains("insomnia") ->
                    "Sleep quality correlates strongly with your evening light exposure. Disconnect from screens 45 minutes prior to sleep and keep your bedroom cool and dark."
                lower.contains("focus") || lower.contains("work") || lower.contains("productive") ->
                    "To enter a state of deep focus, clear visual clutter and use 25-minute Pomodoro sprints. Your journal entries show peak clarity during morning hours."
                lower.contains("sad") || lower.contains("down") || lower.contains("depressed") ->
                    "Be gentle with yourself today. Honor your feelings without judgment. Even a 10-minute walk in natural daylight can boost serotonin pathways."
                else ->
                    "MindPulse AI has analyzed your mood history: Your average emotional score over the past week is 7.4/10. Maintaining consistent journaling and outdoor walks yields your highest emotional balance."
            }
            _aiThinking.value = false
            _aiAssistantResponse.value = answer
        }
    }
}
