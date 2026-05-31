package com.emotionfriend.api.service

import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.LessonTopicRepository

class LessonTopicService(private val repo: LessonTopicRepository) {
    suspend fun getAll(): List<LessonTopic> = repo.getAll()
    suspend fun getById(id: Int): LessonTopic =
        repo.getById(id) ?: throw NoSuchElementException("Topic '$id' not found")
    suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> =
        repo.getScenariosForTopic(topicId)
    suspend fun create(topic: LessonTopic): LessonTopic = repo.create(topic)
    suspend fun update(id: Int, topic: LessonTopic): LessonTopic =
        repo.update(id, topic) ?: throw NoSuchElementException("Topic '$id' not found")
    suspend fun delete(id: Int): Boolean = repo.delete(id)
}
