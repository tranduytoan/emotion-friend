package com.emotionfriend.api.service

import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.repository.PracticeRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class PracticeServiceTest {
    private class FakePracticeRepo : PracticeRepository {
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 42)
        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = listOf(
            PracticeAttempt(id = 42, childId = childId, scenarioId = 11, isCorrect = false, promptEmotion = "SAD"),
        )
    }

    private val service = PracticeService(FakePracticeRepo())

    @Test
    fun `create returns attempt with id`() {
        val attempt = service.create(PracticeAttempt(childId = "child-1", scenarioId = 11, isCorrect = true, promptEmotion = "HAPPY"))
        assertEquals(42, attempt.id)
        assertEquals("child-1", attempt.childId)
    }

    @Test
    fun `getAllByChildId returns history list`() {
        val results = service.getAllByChildId("child-1")
        assertEquals(1, results.size)
        assertEquals("SAD", results.first().promptEmotion)
    }
}
