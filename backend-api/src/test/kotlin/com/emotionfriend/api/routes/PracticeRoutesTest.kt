package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.ExpressionPracticeResult
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.service.PracticeService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class PracticeRoutesTest {
    private class FakePracticeRepo : PracticeRepository {
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 99)

        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = listOf(
            PracticeAttempt(
                id = 99,
                childId = childId,
                scenarioId = 21,
                isCorrect = true,
                promptEmotion = "HAPPY",
            ),
        )
    }

    private val service = PracticeService(FakePracticeRepo())

    @Test
    fun `practice attempt post returns created attempt when childId is valid`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(service) }
        }

        val response = client.post("/api/practice-attempts") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {
                  "childId": "child-1",
                  "scenarioId": 21,
                  "isCorrect": true,
                  "promptEmotion": "HAPPY"
                }
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val result = response.body<ApiResponse<PracticeAttempt>>()
        assertEquals(true, result.success)
        assertEquals(99, result.data?.id)
        assertEquals("child-1", result.data?.childId)
    }

    @Test
    fun `practice attempt post rejects blank childId`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(service) }
        }

        val response = client.post("/api/practice-attempts") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"childId": "", "isCorrect": false}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("childId must not be blank", result.error)
    }

    @Test
    fun `expression practice result returns deterministic positive feedback`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(service) }
        }

        val response = client.post("/api/expression-practice/result") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"childId": "child-1", "promptedEmotion": "SAD"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<ExpressionPracticeResult>>()
        assertEquals(true, result.success)
        assertEquals("SAD", result.data?.promptedEmotion)
        assertEquals(true, result.data?.matched)
    }

    @Test
    fun `expression practice result returns fallback feedback for unknown emotion`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(service) }
        }

        val response = client.post("/api/expression-practice/result") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"childId": "child-1", "promptedEmotion": "EXCITED"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<ExpressionPracticeResult>>()
        assertEquals(true, result.success)
        assertEquals("EXCITED", result.data?.promptedEmotion)
        assertEquals(true, result.data?.matched)
        assertEquals("Con làm tốt lắm! 🌟 Hãy tiếp tục luyện tập nhé!", result.data?.feedback)
    }

    @Test
    fun `practice attempt get returns history for child id`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(service) }
        }

        val response = client.get("/api/practice-attempts/child-1")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<PracticeAttempt>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
        assertEquals("child-1", result.data?.first()?.childId)
    }
}
