package com.emotionfriend.data.repository

import com.emotionfriend.domain.model.ScenarioLesson
import kotlinx.coroutines.flow.Flow

interface ScenarioRepository {
    fun getAll(): Flow<List<ScenarioLesson>>
    suspend fun getById(id: String): ScenarioLesson?
    suspend fun upsertAll(lessons: List<ScenarioLesson>)
}
