package com.emotionfriend.api.repository

import com.emotionfriend.api.model.ScenarioLesson

interface ScenarioRepository {
    suspend fun getAll(): List<ScenarioLesson>
    suspend fun getById(id: Int): ScenarioLesson?
    suspend fun create(lesson: ScenarioLesson): ScenarioLesson
    suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson?
    suspend fun delete(id: Int): Boolean
}
