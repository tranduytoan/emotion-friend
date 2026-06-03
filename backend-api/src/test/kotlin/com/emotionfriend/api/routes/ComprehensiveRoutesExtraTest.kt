package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.AuthForgotPasswordRequest
import com.emotionfriend.api.dto.AuthLoginRequest
import com.emotionfriend.api.dto.AuthRegisterRequest
import com.emotionfriend.api.dto.AuthResponseDto
import com.emotionfriend.api.dto.AuthVerifyEmailRequest
import com.emotionfriend.api.dto.CreatePracticeAttemptRequest
import com.emotionfriend.api.dto.ExpressionPracticeRequest
import com.emotionfriend.api.dto.ExpressionPracticeResult
import com.emotionfriend.api.dto.CreateJournalEntryRequest
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.EmotionService
import com.emotionfriend.api.service.JournalService
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.PracticeService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.io.File

class ComprehensiveRoutesExtraTest {
    private class FakeAuthRepo : com.emotionfriend.api.repository.AuthRepository {
        override suspend fun authenticate(email: String, password: String): AuthenticatedUser? {
            return if (email == "route-test@example.com" && password == "ok") AuthenticatedUser(id = 1, email = email, displayName = "Route Test") else null
        }

        override suspend fun findByEmail(email: String): AuthenticatedUser? {
            return if (email == "route-test@example.com") AuthenticatedUser(id = 1, email = email, displayName = "Route Test", isVerified = false) else null
        }

        override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser {
            return AuthenticatedUser(id = 2, email = email, displayName = displayName)
        }
    }

    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(LessonTopic(id = 7, title = "Topic Seven", description = "Topic for route tests"))
        override suspend fun getById(id: Int): LessonTopic? = if (id == 7) LessonTopic(id = 7, title = "Topic Seven", description = "Topic for route tests") else null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = if (topicId == 7) listOf(
            ScenarioLesson(id = 77, title = "Scenario Seven", situation = "Route test", options = listOf("HAPPY", "SAD"), correctEmotion = "SAD", explanation = "Explanation", topicId = 7),
        ) else emptyList()
        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 8)
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 7) topic.copy(id = 7) else null
        override suspend fun delete(id: Int): Boolean = id == 7
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(ScenarioLesson(id = 5, title = "Scenario A", situation = "Sample", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok"))
        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 5) ScenarioLesson(id = 5, title = "Scenario A", situation = "Sample", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok") else null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 6)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 5) lesson.copy(id = 5) else null
        override suspend fun delete(id: Int): Boolean = id == 5
    }

    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Cảm xúc vui"))
        override suspend fun getById(id: Int): EmotionCard? = if (id == 1) EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Cảm xúc vui") else null
    }

    private class FakePracticeRepo : PracticeRepository {
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 33)
        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = listOf(PracticeAttempt(id = 33, childId = childId, scenarioId = 5, isCorrect = true, promptEmotion = "HAPPY"))
    }

    private class FakeJournalRepo : JournalRepository {
        override suspend fun create(entry: JournalEntry): JournalEntry = entry.copy(id = 44, createdAt = "2026-06-03T14:00:00Z")
        override suspend fun getAllByChildId(childId: String): List<JournalEntry> = listOf(JournalEntry(id = 44, childId = childId, emotionType = EmotionType.CALM, note = "Test journal", createdAt = "2026-06-03T14:00:00Z"))
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(Story(id = 1, title = "Story One", content = "Story content", category = "A"))
        override suspend fun getById(id: Int): Story? = if (id == 1) Story(id = 1, title = "Story One", content = "Story content", category = "A") else null
        override suspend fun create(story: Story): Story = story.copy(id = 2)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 1) story.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val authService = com.emotionfriend.api.service.AuthService(FakeAuthRepo())
    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val emotionService = EmotionService(FakeEmotionRepo())
    private val practiceService = PracticeService(FakePracticeRepo())
    private val journalService = JournalService(FakeJournalRepo())
    private val storyService = StoryService(FakeStoryRepo())

    @Test
    fun `auth login with valid credentials returns OK`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"route-test@example.com\", \"password\": \"ok\"}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<AuthResponseDto>>()
        assertEquals(true, result.success)
        assertEquals("Đăng nhập thành công.", result.data?.message)
    }

    @Test
    fun `auth login returns bad request when password blank`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"route-test@example.com\", \"password\": \"\"}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Mật khẩu không được để trống.", payload.error)
    }

    @Test
    fun `auth register returns created when valid`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"new-user@example.com\", \"password\": \"secret\", \"displayName\": \"New user\"}")
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val payload = response.body<ApiResponse<AuthResponseDto>>()
        assertTrue(payload.success)
        assertEquals("Đăng ký thành công.", payload.data?.message)
    }

    @Test
    fun `auth forgot-password returns OK for known email`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"route-test@example.com\"}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<String>>()
        assertTrue(payload.success)
        assertEquals("Yêu cầu đặt lại mật khẩu đã được ghi nhận cho route-test@example.com.", payload.data)
    }

    @Test
    fun `auth verify-email rejects wrong code`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { authRoutes(authService) }
        }

        val response = client.post("/api/auth/verify-email") {
            contentType(ContentType.Application.Json)
            setBody("{\"email\": \"route-test@example.com\", \"code\": \"000000\"}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Mã xác thực không đúng.", payload.error)
    }

    @Test
    fun `topic routes returns topic by id and scenarios for topic`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val topicResponse = client.get("/api/topics/7")
        assertEquals(HttpStatusCode.OK, topicResponse.status)
        val topicPayload = topicResponse.body<ApiResponse<LessonTopic>>()
        assertTrue(topicPayload.success)
        assertEquals(7, topicPayload.data?.id)

        val scenarioResponse = client.get("/api/topics/7/scenarios")
        assertEquals(HttpStatusCode.OK, scenarioResponse.status)
        val scenarioPayload = scenarioResponse.body<ApiResponse<List<ScenarioLesson>>>()
        assertTrue(scenarioPayload.success)
        assertEquals(1, scenarioPayload.data?.size)
        assertEquals(77, scenarioPayload.data?.first()?.id)
    }

    @Test
    fun `topic routes returns bad request for invalid topic id`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/invalid")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("id must be an integer", payload.error)
    }

    @Test
    fun `scenario routes returns all scenarios and scenario details`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(scenarioService) }
        }

        val listResponse = client.get("/api/scenarios")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val listPayload = listResponse.body<ApiResponse<List<ScenarioLesson>>>()
        assertTrue(listPayload.success)
        assertEquals(1, listPayload.data?.size)

        val detailResponse = client.get("/api/scenarios/5")
        assertEquals(HttpStatusCode.OK, detailResponse.status)
        val detailPayload = detailResponse.body<ApiResponse<ScenarioLesson>>()
        assertTrue(detailPayload.success)
        assertEquals(5, detailPayload.data?.id)
    }

    @Test
    fun `scenario routes returns not found when scenario missing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(scenarioService) }
        }

        val response = client.get("/api/scenarios/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `emotion routes returns emotion list and single emotion detail`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(emotionService) }
        }

        val listResponse = client.get("/api/emotions")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val listPayload = listResponse.body<ApiResponse<List<EmotionCard>>>()
        assertTrue(listPayload.success)
        assertEquals(1, listPayload.data?.size)

        val detailResponse = client.get("/api/emotions/1")
        assertEquals(HttpStatusCode.OK, detailResponse.status)
        val detailPayload = detailResponse.body<ApiResponse<EmotionCard>>()
        assertTrue(detailPayload.success)
        assertEquals("Vui", detailPayload.data?.name)
    }

    @Test
    fun `journal routes returns created entry and history list`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { journalRoutes(journalService) }
        }

        val createResponse = client.post("/api/journal-entries") {
            contentType(ContentType.Application.Json)
            setBody("{\"childId\": \"child-100\", \"emotionType\": \"HAPPY\", \"note\": \"Giữ nhật ký\"}")
        }

        assertEquals(HttpStatusCode.Created, createResponse.status)
        val createPayload = createResponse.body<ApiResponse<JournalEntry>>()
        assertTrue(createPayload.success)
        assertEquals(44, createPayload.data?.id)

        val historyResponse = client.get("/api/journal-entries/child-100")
        assertEquals(HttpStatusCode.OK, historyResponse.status)
        val historyPayload = historyResponse.body<ApiResponse<List<JournalEntry>>>()
        assertTrue(historyPayload.success)
        assertEquals(1, historyPayload.data?.size)
    }

    @Test
    fun `practice routes accepts valid attempt and returns history`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(practiceService) }
        }

        val attemptResponse = client.post("/api/practice-attempts") {
            contentType(ContentType.Application.Json)
            setBody(
                "{\"childId\": \"child-200\", \"scenarioId\": 5, \"isCorrect\": false, \"promptEmotion\": \"SAD\"}",
            )
        }

        assertEquals(HttpStatusCode.Created, attemptResponse.status)
        val attemptPayload = attemptResponse.body<ApiResponse<PracticeAttempt>>()
        assertEquals(true, attemptPayload.success)
        assertEquals(33, attemptPayload.data?.id)

        val historyResponse = client.get("/api/practice-attempts/child-200")
        assertEquals(HttpStatusCode.OK, historyResponse.status)
        val historyPayload = historyResponse.body<ApiResponse<List<PracticeAttempt>>>()
        assertTotalEntries(historyPayload, 1, "child-200")
    }

    @Test
    fun `expression practice returns positive feedback for unknown emotion`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(practiceService) }
        }

        val response = client.post("/api/expression-practice/result") {
            contentType(ContentType.Application.Json)
            setBody("{\"childId\": \"child-300\", \"promptedEmotion\": \"CONFUSED\"}")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<ExpressionPracticeResult>>()
        assertTrue(payload.success)
        assertEquals("CONFUSED", payload.data?.promptedEmotion)
        assertTrue(payload.data?.feedback?.contains("làm tốt") == true)
    }

    @Test
    fun `progress routes return summary and history`() = testApplication {
        val progressService = com.emotionfriend.api.service.ProgressService(object : com.emotionfriend.api.repository.ProgressRepository {
            override suspend fun getProgressSummary(childId: String) = com.emotionfriend.api.model.ProgressSummary(childId = childId, completedLessons = 10, accuracyRate = 0.95f, journalCount = 5, mostMistakenEmotion = EmotionType.SAD)
        })

        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val summaryResponse = client.get("/api/progress/child-400")
        assertEquals(HttpStatusCode.OK, summaryResponse.status)
        val summaryPayload = summaryResponse.body<ApiResponse<com.emotionfriend.api.model.ProgressSummary>>()
        assertTrue(summaryPayload.success)
        assertEquals(10, summaryPayload.data?.completedLessons)

        val historyResponse = client.get("/api/progress/child-400/history")
        assertEquals(HttpStatusCode.OK, historyResponse.status)
        val historyPayload = historyResponse.body<ApiResponse<List<PracticeAttempt>>>()
        assertTrue(historyPayload.success)
        assertEquals(1, historyPayload.data?.size)
    }

    @Test
    fun `story routes return list and stories not found with 404`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(storyService) }
        }

        val listResponse = client.get("/api/stories")
        assertEquals(HttpStatusCode.OK, listResponse.status)

        val missingResponse = client.get("/api/stories/999")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
        val missingPayload = missingResponse.body<ApiResponse<Unit>>()
        assertFalse(missingPayload.success)
    }

    @Test
    fun `admin route rejects invalid token and bad request on malformed ids`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, com.emotionfriend.api.service.MusicService(object : com.emotionfriend.api.repository.MusicRepository {
                override suspend fun getAll(): List<MusicTrack> = emptyList()
                override suspend fun getById(id: Int): MusicTrack? = null
                override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 100)
                override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = null
                override suspend fun delete(id: Int): Boolean = false
            }), topicService) }
        }

        val unauthorized = client.get("/admin/topics")
        assertEquals(HttpStatusCode.Unauthorized, unauthorized.status)

        val invalidId = client.get("/admin/scenarios/abc") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidId.status)
        val invalidPayload = invalidId.body<ApiResponse<Unit>>()
        assertFalse(invalidPayload.success)
        assertEquals("id must be an integer", invalidPayload.error)
    }

    @Test
    fun `health route returns version and ok status`() = testApplication {
        application {
            configureSerialization()
            routing { healthRoute() }
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<Map<String, String>>>()
        assertTrue(payload.success)
        assertEquals("ok", payload.data?.get("status"))
        assertEquals("1.0.0", payload.data?.get("version"))
    }

    private fun assertTotalEntries(response: ApiResponse<List<PracticeAttempt>>, expectedSize: Int, expectedChildId: String) {
        assertTrue(response.success)
        assertEquals(expectedSize, response.data?.size)
        assertEquals(expectedChildId, response.data?.firstOrNull()?.childId)
    }
}
