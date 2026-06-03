package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.service.ScenarioService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ScenarioRoutesTest {
    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(
            ScenarioLesson(id = 5, title = "Tình huống", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok"),
        )

        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 5) {
            ScenarioLesson(id = 5, title = "Tình huống", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok")
        } else null

        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 6)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 5) lesson.copy(id = 5) else null
        override suspend fun delete(id: Int): Boolean = id == 5
    }

    private val service = ScenarioService(FakeScenarioRepo())

    @Test
    fun `get scenarios returns list`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(service) }
        }

        val response = client.get("/api/scenarios")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<ScenarioLesson>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `get scenario by missing id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(service) }
        }

        val response = client.get("/api/scenarios/xyz")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }
}
