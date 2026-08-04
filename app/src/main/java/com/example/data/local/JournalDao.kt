package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries WHERE mood = :mood ORDER BY timestamp DESC")
    fun getEntriesByMood(mood: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: JournalEntryEntity)

    @Query("SELECT COUNT(*) FROM journal_entries WHERE syncStatus = 'PENDING_SYNC'")
    fun getPendingSyncCount(): Flow<Int>

    @Query("SELECT * FROM journal_entries WHERE syncStatus = 'PENDING_SYNC'")
    suspend fun getPendingSyncEntries(): List<JournalEntryEntity>

    // Wellness tips queries
    @Query("SELECT * FROM wellness_tips ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllWellnessTips(): Flow<List<WellnessTipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTips(tips: List<WellnessTipEntity>)

    @Update
    suspend fun updateTip(tip: WellnessTipEntity)

    @Delete
    suspend fun deleteTip(tip: WellnessTipEntity)

    @Query("DELETE FROM wellness_tips WHERE id = :id")
    suspend fun deleteTipById(id: Long)
}
