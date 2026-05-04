package com.emotionfriend.data.local

import androidx.room.Dao
import androidx.room.Query
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

    @Query("SELECT COUNT(*) FROM scenario_lessons")
    suspend fun count(): Int
}
