package com.emotionfriend.api.repository

import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson

interface LessonTopicRepository {
    suspend fun getAll(): List<LessonTopic>
    suspend fun getById(id: Int): LessonTopic?
    suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson>
    suspend fun create(topic: LessonTopic): LessonTopic
    suspend fun update(id: Int, topic: LessonTopic): LessonTopic?
    suspend fun delete(id: Int): Boolean
}
