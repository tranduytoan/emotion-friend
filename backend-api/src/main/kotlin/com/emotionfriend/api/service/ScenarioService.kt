package com.emotionfriend.api.service

import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository

class ScenarioService(private val repo: ScenarioRepository) {
    suspend fun getAll(): List<ScenarioLesson> = repo.getAll()
    suspend fun getById(id: Int): ScenarioLesson =
        repo.getById(id) ?: throw NoSuchElementException("Scenario '$id' not found")
    suspend fun create(lesson: ScenarioLesson): ScenarioLesson = repo.create(lesson)
    suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson =
        repo.update(id, lesson) ?: throw NoSuchElementException("Scenario '$id' not found")
    suspend fun delete(id: Int): Boolean = repo.delete(id)
}
