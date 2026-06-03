package com.emotionfriend.api.service

import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.LessonTopicRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LessonTopicServiceTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(
            LessonTopic(id = 1, title = "Mindfulness", description = "Bài học thở"),
            LessonTopic(id = 2, title = "Emotions", description = "Nhận diện cảm xúc"),
        )

        override suspend fun getById(id: Int): LessonTopic? = when (id) {
            1 -> LessonTopic(id = 1, title = "Mindfulness")
            else -> null
        }

        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = listOf(
            ScenarioLesson(
                id = 21,
                title = "Bài tập 1",
                situation = "Con vui khi...",
                options = listOf("HAPPY", "SAD"),
                correctEmotion = "HAPPY",
                explanation = "Giải thích.",
                sortOrder = 1,
                topicId = topicId,
            ),
        )

        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 3)

        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 1) topic.copy(id = 1) else null

        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = LessonTopicService(FakeTopicRepo())

    @Test
    fun `getAll returns all available lesson topics`() {
        val topics = service.getAll()

        assertEquals(2, topics.size)
        assertEquals("Mindfulness", topics.first().title)
    }

    @Test
    fun `getById throws when topic is missing`() {
        assertFailsWith<NoSuchElementException> { service.getById(999) }
    }

    @Test
    fun `getById returns expected topic`() {
        val topic = service.getById(1)

        assertEquals(1, topic.id)
        assertEquals("Mindfulness", topic.title)
    }

    @Test
    fun `getScenariosForTopic returns scenarios for matching topic`() {
        val scenarios = service.getScenariosForTopic(1)

        assertEquals(1, scenarios.size)
        assertEquals(21, scenarios.first().id)
    }

    @Test
    fun `create returns topic with generated id`() {
        val topic = service.create(LessonTopic(title = "New Topic"))

        assertEquals(3, topic.id)
        assertEquals("New Topic", topic.title)
    }

    @Test
    fun `delete returns false when topic does not exist`() {
        assertEquals(false, service.delete(999))
    }

    @Test
    fun `update returns existing topic when found`() {
        val updated = service.update(1, LessonTopic(id = 1, title = "Mindfulness Updated"))

        assertEquals(1, updated.id)
        assertEquals("Mindfulness Updated", updated.title)
    }

    @Test
    fun `update throws when topic does not exist`() {
        assertFailsWith<NoSuchElementException> { service.update(999, LessonTopic(title = "Ghost")) }
    }
}
