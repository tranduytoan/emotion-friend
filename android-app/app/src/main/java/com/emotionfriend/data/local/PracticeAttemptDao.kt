package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeAttemptDao {

    @Query("SELECT * FROM practice_attempts WHERE childId = :childId ORDER BY createdAt DESC")
    fun getByChildId(childId: String): Flow<List<PracticeAttemptEntity>>

    @Query("SELECT * FROM practice_attempts WHERE childId = :childId ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentByChildId(childId: String, limit: Int): Flow<List<PracticeAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: PracticeAttemptEntity)

    /** Returns attempts not yet pushed to backend. */
    @Query("SELECT * FROM practice_attempts WHERE syncStatus = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPending(): List<PracticeAttemptEntity>

    @Query("UPDATE practice_attempts SET syncStatus = :status, lastModifiedAt = :ts WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: SyncStatus, ts: Long = System.currentTimeMillis())
}
