package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.AuthRepository
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.AuthService
import com.emotionfriend.api.service.EmotionService
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RouteFailureModesTest {
    private class FakeAuthRepo : AuthRepository {
        override suspend fun authenticate(email: String, password: String): AuthenticatedUser? =
            if (email == "test@example.com" && password == "ok123") AuthenticatedUser(id = 1, email = email, displayName = "Test User") else null

        override suspend fun findByEmail(email: String): AuthenticatedUser? = if (email == "test@example.com") AuthenticatedUser(id = 1, email = email, displayName = "Test User", isVerified = false) else null
        override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser = AuthenticatedUser(id = 2, email = email, displayName = displayName)
    }

    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = emptyList()
        override suspend fun getById(id: Int): LessonTopic? = null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = emptyList()
        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 1)
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = emptyList()
        override suspend fun getById(id: Int): ScenarioLesson? = null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 1)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = emptyList()
        override suspend fun getById(id: Int): Story? = null
        override suspend fun create(story: Story): Story = story.copy(id = 7)
        override suspend fun update(id: Int, story: Story): Story? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = emptyList()
        override suspend fun getById(id: Int): MusicTrack? = null
        override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 2)
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = emptyList()
        override suspend fun getById(id: Int): EmotionCard? = null
    }

    private val authService = AuthService(FakeAuthRepo())
    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val storyService = StoryService(FakeStoryRepo())
    private val musicService = MusicService(FakeMusicRepo())
    private val emotionService = EmotionService(FakeEmotionRepo())

    @Test
    fun `auth login rejects invalid credentials`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"email": "test@example.com", "password": "wrong-password"}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Invalid email or password.", payload.error)
    }

    @Test
    fun `auth login invalid json returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{ invalid json }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `auth forgot password invalid json returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody("{ invalid json }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `auth verify email invalid json returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/verify-email") {
            contentType(ContentType.Application.Json)
            setBody("{ invalid json }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `topic get by id returns not found when missing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Topic '99' not found", payload.error)
    }

    @Test
    fun `scenario get by id returns not found when missing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(scenarioService) }
        }

        val response = client.get("/api/scenarios/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Scenario '99' not found", payload.error)
    }

    @Test
    fun `story get by id returns not found when missing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(storyService) }
        }

        val response = client.get("/api/stories/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Story '99' not found", payload.error)
    }

    @Test
    fun `emotion get by id returns not found when missing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(emotionService) }
        }

        val response = client.get("/api/emotions/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Emotion '99' not found", payload.error)
    }

    @Test
    fun `admin route rejects invalid bearer token`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics") {
            header(HttpHeaders.Authorization, "Bearer invalid-token")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Invalid or missing admin token", payload.error)
    }
}
