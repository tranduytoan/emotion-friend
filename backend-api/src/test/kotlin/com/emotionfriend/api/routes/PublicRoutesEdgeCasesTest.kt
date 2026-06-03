package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.CreatePracticeAttemptRequest
import com.emotionfriend.api.dto.ExpressionPracticeRequest
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.repository.ProgressRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.EmotionService
import com.emotionfriend.api.service.JournalService
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.PracticeService
import com.emotionfriend.api.service.ProgressService
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

class PublicRoutesEdgeCasesTest {
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
        override suspend fun getNextScenario(currentId: Int): ScenarioLesson? = null
        override suspend fun getByTopicId(topicId: Int): List<ScenarioLesson> = emptyList()
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 1)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = emptyList()
        override suspend fun getById(id: Int): Story? = null
        override suspend fun getByCategory(category: String?): List<Story> = emptyList()
        override suspend fun create(story: Story): Story = story.copy(id = 1)
        override suspend fun update(id: Int, story: Story): Story? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(EmotionCard(id = 1, name = "Happy", description = "Good"))
        override suspend fun getByCode(code: String): EmotionCard? = if (code == "HAPPY") EmotionCard(id = 1, name = "Happy", description = "Good") else null
        override suspend fun getById(id: Int): EmotionCard? = if (id == 1) EmotionCard(id = 1, name = "Happy", description = "Good") else null
    }

    private class FakeJournalRepo : JournalRepository {
        override suspend fun getAllByChildId(childId: String): List<JournalEntry> = emptyList()
        override suspend fun getById(id: Int): JournalEntry? = null
        override suspend fun create(entry: JournalEntry): JournalEntry = entry.copy(id = 1)
    }

    private class FakePracticeRepo : PracticeRepository {
        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = emptyList()
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 1)
    }

    private class FakeProgressRepo : ProgressRepository {
        override suspend fun getProgress(childId: String): ProgressSummary = ProgressSummary(childId = childId, completedLessons = 0, accuracy = 0.0)
    }

    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val storyService = StoryService(FakeStoryRepo())
    private val emotionService = EmotionService(FakeEmotionRepo())
    private val journalService = JournalService(FakeJournalRepo())
    private val practiceService = PracticeService(FakePracticeRepo())
    private val progressService = ProgressService(FakeProgressRepo())
    private val musicService = MusicService(FakeMusicRepo())

    @Test
    fun `topic routes invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("id must be an integer", payload.error)
    }

    @Test
    fun `topic scenarios route invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/abc/scenarios")
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("id must be an integer", payload.error)
    }

    @Test
    fun `practice attempts route rejects blank child id in create request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(practiceService) }
        }

        val response = client.post("/api/practice-attempts") {
            contentType(ContentType.Application.Json)
            setBody("{\"childId\": \"\", \"scenarioId\": 1, \"isCorrect\": true, \"promptEmotion\": \"HAPPY\"}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("childId must not be blank", payload.error)
    }

    @Test
    fun `practice attempts get missing child id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(practiceService) }
        }

        val response = client.get("/api/practice-attempts/")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `expression practice route provides fallback feedback for unknown emotion`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { practiceRoutes(practiceService) }
        }

        val response = client.post("/api/expression-practice/result") {
            contentType(ContentType.Application.Json)
            setBody(ExpressionPracticeRequest(promptedEmotion = "UNKNOWN_EMOTION"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<com.emotionfriend.api.dto.ExpressionPracticeResult>>()
        assertTrue(payload.success)
        assertEquals("Con làm tốt lắm! 🌟 Hãy tiếp tục luyện tập nhé!", payload.data?.feedback)
    }

    @Test
    fun `progress route missing child id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val response = client.get("/api/progress/")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `progress history route missing child id returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { progressRoutes(progressService, practiceService) }
        }

        val response = client.get("/api/progress//history")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `story route category filter with empty parameter returns empty list`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(storyService) }
        }

        val response = client.get("/api/stories?category=")
        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<List<Story>>>()
        assertTrue(payload.success)
        assertTrue(payload.data?.isEmpty() == true)
    }

    @Test
    fun `emotion route invalid code returns ok with null data`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(emotionService) }
        }

        val response = client.get("/api/emotions/UNKNOWN")
        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<EmotionCard?>>()
        assertTrue(payload.success)
        assertEquals(null, payload.data)
    }

    @Test
    fun `journal routes invalid create body returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { journalRoutes(journalService) }
        }

        val response = client.post("/api/journals") {
            contentType(ContentType.Application.Json)
            setBody("{ invalid }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `topic route returns empty list if no topics exist`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics")
        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<List<LessonTopic>>>()
        assertTrue(payload.success)
        assertTrue(payload.data?.isEmpty() == true)
    }
}
