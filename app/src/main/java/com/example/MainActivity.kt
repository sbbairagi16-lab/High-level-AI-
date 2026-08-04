package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.JournalEntryEntity
import com.example.ui.AppTab
import com.example.ui.JournalViewModel
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppNavigationSidebar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.CloudSyncScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.JournalListScreen
import com.example.ui.screens.NewJournalEntryScreen
import com.example.ui.screens.WellnessTipsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MindPulseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindPulseTheme {
                MindPulseApp()
            }
        }
    }
}

@Composable
fun MindPulseApp(
    viewModel: JournalViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val allEntries by viewModel.entries.collectAsStateWithLifecycle()
    val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
    val wellnessTips by viewModel.wellnessTips.collectAsStateWithLifecycle()
    val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val moodFilter by viewModel.selectedMoodFilter.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val isCloudOnline by viewModel.isCloudOnline.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiAssistantResponse.collectAsStateWithLifecycle()
    val isThinking by viewModel.aiThinking.collectAsStateWithLifecycle()

    var isWritingNewEntry by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Desktop / Tablet Multi-Pane Layout with Side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                AppNavigationSidebar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        isWritingNewEntry = false
                        viewModel.selectTab(tab)
                    },
                    pendingSyncCount = pendingSyncCount
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(top = 16.dp)
                ) {
                    if (isWritingNewEntry) {
                        NewJournalEntryScreen(
                            isAnalyzing = isAnalyzing,
                            onBack = { isWritingNewEntry = false },
                            onSaveEntry = { title, content, mood, score, tags ->
                                viewModel.addJournalEntry(title, content, mood, score, tags) {
                                    isWritingNewEntry = false
                                }
                            }
                        )
                    } else {
                        MainScreenContent(
                            selectedTab = selectedTab,
                            entries = filteredEntries,
                            allEntries = allEntries,
                            wellnessTips = wellnessTips,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedMoodFilter = moodFilter,
                            onMoodFilterSelected = { viewModel.setMoodFilter(it) },
                            aiResponse = aiResponse,
                            isThinking = isThinking,
                            isCloudOnline = isCloudOnline,
                            pendingSyncCount = pendingSyncCount,
                            onAskQuestion = { viewModel.askAiAssistant(it) },
                            onOpenNewEntry = { isWritingNewEntry = true },
                            onViewAllEntries = { viewModel.selectTab(AppTab.JOURNAL) },
                            onToggleTipCompletion = { id, done -> viewModel.toggleTipCompletion(id, done) },
                            onDeleteTip = { viewModel.deleteWellnessTip(it) },
                            onDeleteEntry = { viewModel.deleteJournalEntry(it) },
                            onToggleOnlineStatus = { viewModel.toggleCloudOnlineStatus() },
                            onTriggerSync = { viewModel.triggerManualCloudSync() }
                        )
                    }
                }
            }
        } else {
            // Mobile Compact Layout with Edge-to-Edge Bottom Navigation
            Scaffold(
                containerColor = DarkBackground,
                bottomBar = {
                    if (!isWritingNewEntry) {
                        AppBottomNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            pendingSyncCount = pendingSyncCount
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWritingNewEntry) {
                        NewJournalEntryScreen(
                            isAnalyzing = isAnalyzing,
                            onBack = { isWritingNewEntry = false },
                            onSaveEntry = { title, content, mood, score, tags ->
                                viewModel.addJournalEntry(title, content, mood, score, tags) {
                                    isWritingNewEntry = false
                                }
                            }
                        )
                    } else {
                        MainScreenContent(
                            selectedTab = selectedTab,
                            entries = filteredEntries,
                            allEntries = allEntries,
                            wellnessTips = wellnessTips,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedMoodFilter = moodFilter,
                            onMoodFilterSelected = { viewModel.setMoodFilter(it) },
                            aiResponse = aiResponse,
                            isThinking = isThinking,
                            isCloudOnline = isCloudOnline,
                            pendingSyncCount = pendingSyncCount,
                            onAskQuestion = { viewModel.askAiAssistant(it) },
                            onOpenNewEntry = { isWritingNewEntry = true },
                            onViewAllEntries = { viewModel.selectTab(AppTab.JOURNAL) },
                            onToggleTipCompletion = { id, done -> viewModel.toggleTipCompletion(id, done) },
                            onDeleteTip = { viewModel.deleteWellnessTip(it) },
                            onDeleteEntry = { viewModel.deleteJournalEntry(it) },
                            onToggleOnlineStatus = { viewModel.toggleCloudOnlineStatus() },
                            onTriggerSync = { viewModel.triggerManualCloudSync() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreenContent(
    selectedTab: AppTab,
    entries: List<JournalEntryEntity>,
    allEntries: List<JournalEntryEntity>,
    wellnessTips: List<com.example.data.local.WellnessTipEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedMoodFilter: String?,
    onMoodFilterSelected: (String?) -> Unit,
    aiResponse: String?,
    isThinking: Boolean,
    isCloudOnline: Boolean,
    pendingSyncCount: Int,
    onAskQuestion: (String) -> Unit,
    onOpenNewEntry: () -> Unit,
    onViewAllEntries: () -> Unit,
    onToggleTipCompletion: (Long, Boolean) -> Unit,
    onDeleteTip: (Long) -> Unit,
    onDeleteEntry: (JournalEntryEntity) -> Unit,
    onToggleOnlineStatus: () -> Unit,
    onTriggerSync: () -> Unit
) {
    when (selectedTab) {
        AppTab.OVERVIEW -> DashboardScreen(
            entries = allEntries,
            aiResponse = aiResponse,
            isThinking = isThinking,
            onAskQuestion = onAskQuestion,
            onOpenNewEntry = onOpenNewEntry,
            onViewAllEntries = onViewAllEntries,
            onViewEntryDetails = {}
        )
        AppTab.JOURNAL -> JournalListScreen(
            entries = entries,
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            selectedMoodFilter = selectedMoodFilter,
            onMoodFilterSelected = onMoodFilterSelected,
            onOpenNewEntry = onOpenNewEntry,
            onDeleteEntry = onDeleteEntry
        )
        AppTab.WELLNESS -> WellnessTipsScreen(
            tips = wellnessTips,
            onToggleTipCompletion = onToggleTipCompletion,
            onDeleteTip = onDeleteTip
        )
        AppTab.ANALYTICS -> AnalyticsScreen(
            entries = allEntries
        )
        AppTab.CLOUD_SYNC -> CloudSyncScreen(
            isOnline = isCloudOnline,
            pendingSyncCount = pendingSyncCount,
            onToggleOnlineStatus = onToggleOnlineStatus,
            onTriggerSync = onTriggerSync
        )
    }
}
