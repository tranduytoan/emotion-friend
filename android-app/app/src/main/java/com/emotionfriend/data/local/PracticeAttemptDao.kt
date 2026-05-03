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
}
