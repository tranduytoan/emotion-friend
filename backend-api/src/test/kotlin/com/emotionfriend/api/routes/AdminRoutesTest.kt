package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.LessonTopicRequest
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.MusicTrackRequest
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.ScenarioLessonRequest
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.model.StoryRequest
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class AdminRoutesTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(LessonTopic(id = 1, title = "Demo Topic"))
        override suspend fun getById(id: Int): LessonTopic? = if (id == 1) LessonTopic(id = 1, title = "Demo Topic") else null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = listOf(
            ScenarioLesson(
                id = 10,
                title = "Demo Scenario",
                situation = "Tình huống",
                options = listOf("HAPPY", "SAD"),
                correctEmotion = "HAPPY",
                explanation = "Ok",
                topicId = topicId,
            ),
        )
        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 2)
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 1) topic.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(
            ScenarioLesson(id = 1, title = "Demo", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok"),
        )
        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 1) ScenarioLesson(id = 1, title = "Demo", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok") else null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 2)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 1) lesson.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(Story(id = 5, title = "Story", content = "Once", category = "Cat"))
        override suspend fun getById(id: Int): Story? = if (id == 5) Story(id = 5, title = "Story", content = "Once", category = "Cat") else null
        override suspend fun create(story: Story): Story = story.copy(id = 6)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 5) story.copy(id = 5) else null
        override suspend fun delete(id: Int): Boolean = id == 5
    }

    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = listOf(MusicTrack(id = 1, title = "Track", artist = "Artist", filename = "track.mp3"))
        override suspend fun getById(id: Int): MusicTrack? = if (id == 1) MusicTrack(id = 1, title = "Track", artist = "Artist", filename = "track.mp3") else null
        override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 2)
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = if (id == 1) track.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val storyService = StoryService(FakeStoryRepo())
    private val musicService = MusicService(FakeMusicRepo())

    @Test
    fun `admin endpoint rejects missing authorization header`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("Invalid or missing admin token", result.error)
    }

    @Test
    fun `admin topics list returns authorized payload`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<List<LessonTopic>>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.size)
    }

    @Test
    fun `admin post topic creates a new topic`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.post("/admin/topics") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"title": "New Topic", "description": "Desc", "difficulty": 2, "sortOrder": 5}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val result = response.body<ApiResponse<LessonTopic>>()
        assertEquals(true, result.success)
        assertEquals(2, result.data?.id)
        assertEquals("New Topic", result.data?.title)
    }

    @Test
    fun `admin get topic by missing id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics/abc") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }

    @Test
    fun `admin delete topic not found returns 404`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.delete("/admin/topics/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("Not found", result.error)
    }

    @Test
    fun `admin get music with invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/music/abc") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("id must be an integer", result.error)
    }

    @Test
    fun `admin create story rejects invalid request body`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.post("/admin/stories") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{ invalid json }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
        assertEquals("Invalid request body", result.error)
    }
}
