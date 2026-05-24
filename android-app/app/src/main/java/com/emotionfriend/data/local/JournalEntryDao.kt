package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE childId = :childId ORDER BY createdAt DESC")
    fun getByChildId(childId: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity)

    @Update
    suspend fun update(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    /** Returns entries not yet pushed to backend. */
    @Query("SELECT * FROM journal_entries WHERE syncStatus = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<JournalEntryEntity>

    @Query("UPDATE journal_entries SET syncStatus = :status, lastModifiedAt = :ts WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, ts: Long = System.currentTimeMillis())
}
