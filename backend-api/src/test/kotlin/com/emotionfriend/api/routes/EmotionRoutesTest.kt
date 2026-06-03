package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.service.EmotionService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class EmotionRoutesTest {
    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(
            EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả"),
        )

        override suspend fun getById(id: Int): EmotionCard? = if (id == 1) {
            EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả")
        } else null
    }

    private val service = EmotionService(FakeEmotionRepo())

    @Test
    fun `get all emotions returns list`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(service) }
        }

        val response = client.get("/api/emotions")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<EmotionCard>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `get emotion by invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(service) }
        }

        val response = client.get("/api/emotions/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }
}
