package com.emotionfriend.api.service

import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ScenarioServiceTest {
    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(
            ScenarioLesson(
                id = 1,
                title = "Tình huống 1",
                situation = "Khi con ...",
                options = listOf("HAPPY", "SAD"),
                correctEmotion = "HAPPY",
                explanation = "Giải thích.",
            ),
        )

        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 1) {
            ScenarioLesson(
                id = 1,
                title = "Tình huống 1",
                situation = "Khi con ...",
                options = listOf("HAPPY", "SAD"),
                correctEmotion = "HAPPY",
                explanation = "Giải thích.",
            )
        } else null

        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 2)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 1) lesson.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = ScenarioService(FakeScenarioRepo())

    @Test
    fun `getAll returns all scenarios`() {
        assertEquals(1, service.getAll().size)
    }

    @Test
    fun `getById throws when scenario missing`() {
        assertFailsWith<NoSuchElementException> { service.getById(99) }
    }

    @Test
    fun `create returns new scenario with id assigned`() {
        val created = service.create(
            ScenarioLesson(
                title = "Mới",
                situation = "Mới",
                options = listOf("HAPPY"),
                correctEmotion = "HAPPY",
                explanation = "Ok",
            ),
        )
        assertEquals(2, created.id)
    }

    @Test
    fun `update returns scenario when scenario exists`() {
        val updated = service.update(1, ScenarioLesson(
            id = 1,
            title = "Updated",
            situation = "Mới",
            options = listOf("HAPPY"),
            correctEmotion = "HAPPY",
            explanation = "Ok",
        ))
        assertEquals(1, updated.id)
        assertEquals("Updated", updated.title)
    }

    @Test
    fun `delete returns true for existing scenario`() {
        assertEquals(true, service.delete(1))
    }

    @Test
    fun `update throws when scenario missing`() {
        assertFailsWith<NoSuchElementException> {
            service.update(99, ScenarioLesson(
                title = "Mới",
                situation = "Mới",
                options = listOf("HAPPY"),
                correctEmotion = "HAPPY",
                explanation = "Ok",
            ))
        }
    }
}
