package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ScenarioLessonDao {

    @Query("SELECT * FROM scenario_lessons ORDER BY title ASC")
    fun getAll(): Flow<List<ScenarioLessonEntity>>

    @Query("SELECT * FROM scenario_lessons WHERE id = :id")
    suspend fun getById(id: String): ScenarioLessonEntity?

    @Upsert
    suspend fun upsertAll(lessons: List<ScenarioLessonEntity>)

    @Query("DELETE FROM scenario_lessons")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(lessons: List<ScenarioLessonEntity>) {
        deleteAll()
        if (lessons.isNotEmpty()) {
            upsertAll(lessons)
        }
    }

    @Query("SELECT COUNT(*) FROM scenario_lessons")
    suspend fun count(): Int
}
