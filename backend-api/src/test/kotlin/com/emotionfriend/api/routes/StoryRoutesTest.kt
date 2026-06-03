package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.StoryService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class StoryRoutesTest {
    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(
            Story(id = 1, title = "Story 1", content = "Once upon a time", category = "Moral"),
        )

        override suspend fun getById(id: Int): Story? = if (id == 1) {
            Story(id = 1, title = "Story 1", content = "Once upon a time", category = "Moral")
        } else null

        override suspend fun create(story: Story): Story = story.copy(id = 2)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 1) story.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = StoryService(FakeStoryRepo())

    @Test
    fun `list stories returns all stories`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(service) }
        }

        val response = client.get("/api/stories")
        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<Story>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `get story by missing id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(service) }
        }

        val response = client.get("/api/stories/not-a-number")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }

    @Test
    fun `get story by id not found returns 404`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(service) }
        }

        val response = client.get("/api/stories/99")
        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
    }
}
