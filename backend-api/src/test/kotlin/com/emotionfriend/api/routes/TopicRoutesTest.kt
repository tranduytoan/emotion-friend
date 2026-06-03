package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.service.LessonTopicService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class TopicRoutesTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(
            LessonTopic(id = 1, title = "Tự nhận thức", description = "Hiểu cảm xúc"),
        )

        override suspend fun getById(id: Int): LessonTopic? = if (id == 1) {
            LessonTopic(id = 1, title = "Tự nhận thức")
        } else {
            null
        }

        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = listOf(
            ScenarioLesson(
                id = 101,
                title = "Tình huống",
                situation = "Khi con cảm thấy buồn",
                options = listOf("SAD", "HAPPY"),
                correctEmotion = "SAD",
                explanation = "Giải thích",
                topicId = topicId,
            ),
        )

        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 2)

        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 1) topic.copy(id = 1) else null

        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = LessonTopicService(FakeTopicRepo())

    @Test
    fun `get all topics returns collection of topics`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(service) }
        }

        val response = client.get("/api/topics")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<LessonTopic>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
        assertEquals("Tự nhận thức", result.data?.first()?.title)
    }

    @Test
    fun `get topic by invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(service) }
        }

        val response = client.get("/api/topics/invalid")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }

    @Test
    fun `get topic scenarios returns list for valid topic id`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(service) }
        }

        val response = client.get("/api/topics/1/scenarios")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<ScenarioLesson>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
        assertEquals(101, result.data?.first()?.id)
    }
}
