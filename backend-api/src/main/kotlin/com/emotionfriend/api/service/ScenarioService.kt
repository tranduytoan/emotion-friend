package com.emotionfriend.api.service

import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository

class ScenarioService(private val repo: ScenarioRepository) {
    suspend fun getAll(): List<ScenarioLesson> = repo.getAll()
    suspend fun getById(id: String): ScenarioLesson =
        repo.getById(id) ?: throw NoSuchElementException("Scenario '$id' not found")
    suspend fun create(lesson: ScenarioLesson): ScenarioLesson = repo.create(lesson)
    suspend fun update(id: String, lesson: ScenarioLesson): ScenarioLesson =
        repo.update(id, lesson) ?: throw NoSuchElementException("Scenario '$id' not found")
    suspend fun delete(id: String): Boolean = repo.delete(id)
}
