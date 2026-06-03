package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.EmotionService
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteCoverageExtraTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(LessonTopic(id = 1, title = "Topic One"))
        override suspend fun getById(id: Int): LessonTopic? = if (id == 1) LessonTopic(id = 1, title = "Topic One") else null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = listOf(
            ScenarioLesson(
                id = 11,
                title = "Scenario One",
                situation = "Một tình huống",
                options = listOf("HAPPY", "SAD"),
                correctEmotion = "HAPPY",
                explanation = "Giải thích",
                topicId = topicId,
            ),
        )
        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 2)
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 1) topic.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(
            ScenarioLesson(id = 5, title = "Demo", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok"),
        )

        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 5) {
            ScenarioLesson(id = 5, title = "Demo", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok")
        } else null

        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 6)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 5) lesson.copy(id = 5) else null
        override suspend fun delete(id: Int): Boolean = id == 5
    }

    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả"))
        override suspend fun getById(id: Int): EmotionCard? = if (id == 1) EmotionCard(id = 1, name = "Vui", type = "HAPPY", description = "Mô tả") else null
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(Story(id = 1, title = "Story 1", content = "Once upon a time", category = "Moral"))
        override suspend fun getById(id: Int): Story? = if (id == 1) Story(id = 1, title = "Story 1", content = "Once upon a time", category = "Moral") else null
        override suspend fun create(story: Story): Story = story.copy(id = 2)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 1) story.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val emotionService = EmotionService(FakeEmotionRepo())
    private val storyService = StoryService(FakeStoryRepo())

    @Test
    fun `get topic by id returns topic details`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/1")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<LessonTopic>>()
        assertEquals(true, result.success)
        assertEquals(1, result.data?.id)
    }

    @Test
    fun `get topic by id returns not found for missing topic`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { topicRoutes(topicService) }
        }

        val response = client.get("/api/topics/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
    }

    @Test
    fun `get scenario by id returns scenario details`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(scenarioService) }
        }

        val response = client.get("/api/scenarios/5")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<ScenarioLesson>>()
        assertEquals(true, result.success)
        assertEquals(5, result.data?.id)
    }

    @Test
    fun `get scenario by id not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { scenarioRoutes(scenarioService) }
        }

        val response = client.get("/api/scenarios/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
    }

    @Test
    fun `get emotion by id returns emotion details`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(emotionService) }
        }

        val response = client.get("/api/emotions/1")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<EmotionCard>>()
        assertEquals(true, result.success)
        assertEquals("Vui", result.data?.name)
    }

    @Test
    fun `get emotion by id not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { emotionRoutes(emotionService) }
        }

        val response = client.get("/api/emotions/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertEquals(false, result.success)
    }

    @Test
    fun `get story by id returns story details`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { storyRoutes(storyService) }
        }

        val response = client.get("/api/stories/1")

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<Story>>()
        assertEquals(true, result.success)
        assertEquals("Story 1", result.data?.title)
    }
}
