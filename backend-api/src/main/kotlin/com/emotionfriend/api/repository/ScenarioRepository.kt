package com.emotionfriend.api.repository

import com.emotionfriend.api.model.ScenarioLesson

interface ScenarioRepository {
    suspend fun getAll(): List<ScenarioLesson>
    suspend fun getById(id: String): ScenarioLesson?
    suspend fun create(lesson: ScenarioLesson): ScenarioLesson
    suspend fun update(id: String, lesson: ScenarioLesson): ScenarioLesson?
    suspend fun delete(id: String): Boolean
}
