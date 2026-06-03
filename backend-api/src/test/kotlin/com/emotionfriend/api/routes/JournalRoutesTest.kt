package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.CreateJournalEntryRequest
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.service.JournalService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class JournalRoutesTest {
    private class FakeJournalRepo : JournalRepository {
        override suspend fun create(entry: JournalEntry): JournalEntry = entry.copy(id = 100, createdAt = "2026-06-03T10:00:00Z")
        override suspend fun getAllByChildId(childId: String): List<JournalEntry> = listOf(
            JournalEntry(id = 100, childId = childId, emotionType = EmotionType.SAD, note = "Ghi chú", createdAt = "2026-06-03T10:00:00Z"),
        )
    }

    private val service = JournalService(FakeJournalRepo())

    @Test
    fun `post journal entry succeeds with valid payload`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { journalRoutes(service) }
        }

        val response = client.post("/api/journal-entries") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"childId": "child-1", "emotionType": "SAD", "note": "Test note"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.body<ApiResponse<JournalEntry>>()
        assertEquals(true, result.success)
        assertEquals(100, result.data?.id)
    }

    @Test
    fun `get journal entries returns list by childId`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { journalRoutes(service) }
        }

        val response = client.get("/api/journal-entries/child-1")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<JournalEntry>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `post journal entry rejects blank childId`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { journalRoutes(service) }
        }

        val response = client.post("/api/journal-entries") {
            contentType(ContentType.Application.Json)
            setBody("{\"childId\": \"\", \"emotionType\": \"HAPPY\", \"note\": \"Ok\"}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("childId must not be blank", result.error)
    }
}
