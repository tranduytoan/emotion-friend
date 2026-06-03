package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import java.io.File

class AdminRoutesRobustTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = listOf(LessonTopic(id = 1, title = "Topic One"))
        override suspend fun getById(id: Int): LessonTopic? = if (id == 1) LessonTopic(id = 1, title = "Topic One") else null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = if (topicId == 1) listOf(ScenarioLesson(id = 1, title = "Scenario One", situation = "Test", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Example", topicId = 1)) else emptyList()
        override suspend fun create(topic: LessonTopic): LessonTopic = topic.copy(id = 2)
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = if (id == 1) topic.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(ScenarioLesson(id = 1, title = "Scenario One", situation = "Test", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Example"))
        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 1) ScenarioLesson(id = 1, title = "Scenario One", situation = "Test", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Example") else null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 2)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 1) lesson.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(Story(id = 5, title = "Story One", content = "Content", category = "Test"))
        override suspend fun getById(id: Int): Story? = if (id == 5) Story(id = 5, title = "Story One", content = "Content", category = "Test") else null
        override suspend fun create(story: Story): Story = story.copy(id = 6)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 5) story.copy(id = 5) else null
        override suspend fun delete(id: Int): Boolean = id == 5
    }

    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = listOf(MusicTrack(id = 1, title = "Track One", artist = "Artist", filename = "track.mp3"))
        override suspend fun getById(id: Int): MusicTrack? = if (id == 1) MusicTrack(id = 1, title = "Track One", artist = "Artist", filename = "track.mp3") else null
        override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 2)
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = if (id == 1) track.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val topicService = LessonTopicService(FakeTopicRepo())
    private val scenarioService = ScenarioService(FakeScenarioRepo())
    private val storyService = StoryService(FakeStoryRepo())
    private val musicService = MusicService(FakeMusicRepo())

    @Test
    fun `admin get topic not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin update topic not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.put("/admin/topics/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{\"title\": \"No Topic\", \"description\": \"No\", \"difficulty\": 1, \"sortOrder\": 0}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin get scenario not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/scenarios/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin delete scenario not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.delete("/admin/scenarios/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin get story not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/stories/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin update story not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.put("/admin/stories/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{\"title\": \"Missing\", \"content\": \"None\", \"category\": \"X\"}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin delete story not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.delete("/admin/stories/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin get music not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/music/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin update music not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.put("/admin/music/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{\"title\": \"No Track\", \"artist\": \"Nobody\", \"filename\": \"none.mp3\", \"sortOrder\": 0}")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin delete music not found returns not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.delete("/admin/music/99") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin create topic rejects invalid request body`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.post("/admin/topics") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{ invalid }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin create scenario rejects invalid request body`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.post("/admin/scenarios") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{ invalid }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin create music rejects invalid request body`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.post("/admin/music") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{ invalid }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
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
            setBody("{ invalid }")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
    }

    @Test
    fun `admin scenario image upload rejects straight text file when form data contains non-image`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.submitFormWithBinaryData("/admin/scenarios/1/image", formData {
            append("file", "text content".byteInputStream(), Headers.build {
                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"file.txt\"")
                append(HttpHeaders.ContentType, "text/plain")
            })
        }) {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("Only image files are allowed", payload.error)
    }

    @Test
    fun `admin scenario image upload rejects non integer id before parsing`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.submitFormWithBinaryData("/admin/scenarios/abc/image", formData {
            append("file", "image data".byteInputStream(), Headers.build {
                append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"image.png\"")
                append(HttpHeaders.ContentType, "image/png")
            })
        }) {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("id must be an integer", payload.error)
    }

    @Test
    fun `admin topic update invalid id returns bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.put("/admin/topics/abc") {
            header(HttpHeaders.Authorization, "Bearer admin-secret-token")
            contentType(ContentType.Application.Json)
            setBody("{\"title\": \"Test\", \"description\": \"Test\", \"difficulty\": 1, \"sortOrder\": 1}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val payload = response.body<ApiResponse<Unit>>()
        assertFalse(payload.success)
        assertEquals("id must be an integer", payload.error)
    }

    @Test
    fun `admin nested invalid path still rejects unauthorized when missing token`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { adminRoutes(scenarioService, storyService, musicService, topicService) }
        }

        val response = client.get("/admin/topics/1/scenarios")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
