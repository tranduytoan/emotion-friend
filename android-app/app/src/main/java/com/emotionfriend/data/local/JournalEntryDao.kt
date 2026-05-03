package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {

    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun getAll(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE childId = :childId ORDER BY createdAt DESC")
    fun getByChildId(childId: String): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteById(id: String)
}
