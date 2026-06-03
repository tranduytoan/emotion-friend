package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.ProgressRepository
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.service.ProgressService
import com.emotionfriend.api.service.PracticeService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressRoutesTest {
    private class FakeProgressRepo : ProgressRepository {
        override suspend fun getProgressSummary(childId: String): ProgressSummary = ProgressSummary(
            childId = childId,
            completedLessons = 8,
            accuracyRate = 0.92f,
            journalCount = 3,
            mostMistakenEmotion = EmotionType.ANGRY,
        )
    }

    private class FakePracticeRepo : PracticeRepository {
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 55)
        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = listOf(
            PracticeAttempt(id = 55, childId = childId, scenarioId = 1, isCorrect = true, promptEmotion = "HAPPY"),
        )
    }

    private val progressService = ProgressService(FakeProgressRepo())
    private val practiceService = PracticeService(FakePracticeRepo())

    @Test
    fun `get progress summary returns child summary`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val response = client.get("/api/progress/child-1")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<ProgressSummary>>()
        assertEquals(true, result.success)
        assertEquals(8, result.data?.completedLessons)
    }

    @Test
    fun `get history returns practice attempt list`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val response = client.get("/api/progress/child-1/history")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<PracticeAttempt>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `progress routes reject missing childId parameter`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val response = client.get("/api/progress/")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
