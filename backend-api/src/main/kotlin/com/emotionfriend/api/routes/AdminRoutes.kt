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
import com.emotionfriend.api.service.LessonTopicService
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import com.emotionfriend.api.util.ScenarioLessonImageResolver
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

/**
 * Admin CRUD routes — protected by a simple Bearer token.
 * Token is read from the ADMIN_TOKEN environment variable.
 */
fun Route.adminRoutes(
    scenarioService: ScenarioService,
    storyService: StoryService,
    musicService: MusicService,
    topicService: LessonTopicService,
) {
    val adminToken = System.getenv("ADMIN_TOKEN") ?: "admin-secret-token"

    route("/admin") {
        // ── Bearer token guard ────────────────────────────────────────────────
        intercept(ApplicationCallPipeline.Plugins) {
            val authHeader = call.request.headers["Authorization"] ?: ""
            val token = authHeader.removePrefix("Bearer ").trim()
            if (token != adminToken) {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ApiResponse<Unit>(success = false, error = "Invalid or missing admin token"),
                )
                finish()
                return@intercept
            }
        }

        // ── Lesson Topics ─────────────────────────────────────────────────────
        route("/topics") {
            get {
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = topicService.getAll()))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                runCatching { topicService.getById(id) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            get("/{id}/scenarios") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val scenarios = topicService.getScenariosForTopic(id)
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarios))
            }

            post {
                val req = runCatching { call.receive<LessonTopicRequest>() }.getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val topic = LessonTopic(
                    title       = req.title,
                    description = req.description,
                    difficulty  = req.difficulty,
                    sortOrder   = req.sortOrder,
                )
                val created = topicService.create(topic)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val req = runCatching { call.receive<LessonTopicRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val topic = LessonTopic(
                    id          = id,
                    title       = req.title,
                    description = req.description,
                    difficulty  = req.difficulty,
                    sortOrder   = req.sortOrder,
                )
                runCatching { topicService.update(id, topic) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val deleted = topicService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = "Deleted"))
                else call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Not found"))
            }
        }

        // ── Scenario Lessons ──────────────────────────────────────────────────
        route("/scenarios") {
            get {
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarioService.getAll()))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                runCatching { scenarioService.getById(id) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            post {
                val req = runCatching { call.receive<ScenarioLessonRequest>() }.getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val lesson = ScenarioLesson(
                    title          = req.title,
                    situation      = req.situation,
                    imageName      = req.imageName?.trim()?.takeIf { it.isNotEmpty() },
                    options        = req.options,
                    correctEmotion = req.correctEmotion,
                    explanation    = req.explanation,
                    sortOrder      = req.sortOrder,
                    topicId        = req.topicId,
                )
                val created = scenarioService.create(lesson)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val req = runCatching { call.receive<ScenarioLessonRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val lesson = ScenarioLesson(
                    id             = id,
                    title          = req.title,
                    situation      = req.situation,
                    imageName      = req.imageName?.trim()?.takeIf { it.isNotEmpty() },
                    options        = req.options,
                    correctEmotion = req.correctEmotion,
                    explanation    = req.explanation,
                    sortOrder      = req.sortOrder,
                    topicId        = req.topicId,
                )
                runCatching { scenarioService.update(id, lesson) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            post("/{id}/image") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "id must be an integer"),
                )

                val currentLesson = runCatching { scenarioService.getById(id) }.getOrElse {
                    return@post call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse<Unit>(success = false, error = it.message ?: "Scenario not found"),
                    )
                }

                val imageDir = ScenarioLessonImageResolver.resolveScenarioLessonImageDir()
                if (!imageDir.exists() && !imageDir.mkdirs()) {
                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Unit>(success = false, error = "Cannot create image directory: ${imageDir.absolutePath}"),
                    )
                }

                val targetFile = File(imageDir, "$id.png")
                val parts = runCatching { call.receiveMultipart().readAllParts() }.getOrElse {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(success = false, error = "Request must be multipart/form-data with field 'file'"),
                    )
                }
                var wroteImage = false
                var hasInvalidPart = false

                parts.forEach { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            if (part.name == "file") {
                                val lowerName = (part.originalFileName ?: "").lowercase()
                                val isImageByName = lowerName.endsWith(".png") ||
                                    lowerName.endsWith(".jpg") ||
                                    lowerName.endsWith(".jpeg") ||
                                    lowerName.endsWith(".webp")
                                if (!isImageByName) {
                                    hasInvalidPart = true
                                } else {
                                    part.streamProvider().use { input ->
                                        targetFile.outputStream().buffered().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    wroteImage = true
                                }
                            }
                        }

                        else -> Unit
                    }
                    part.dispose()
                }

                if (hasInvalidPart) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(success = false, error = "Only image files are allowed"),
                    )
                }

                if (!wroteImage) {
                    return@post call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Unit>(success = false, error = "Missing image file field 'file'"),
                    )
                }

                val updated = scenarioService.update(id, currentLesson.copy(imageName = "$id.png"))
                if (updated == null) {
                    call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Scenario not found"))
                    return@post
                }

                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = updated))
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val deleted = scenarioService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = "Deleted"))
                else call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Not found"))
            }
        }

        // ── Stories ───────────────────────────────────────────────────────────
        route("/stories") {
            get {
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = storyService.getAll()))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                runCatching { storyService.getById(id) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            post {
                val req = runCatching { call.receive<StoryRequest>() }.getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val story = Story(
                    title       = req.title,
                    content     = req.content,
                    category    = req.category,
                    imageUrl    = req.imageUrl,
                    sortOrder   = req.sortOrder,
                    imageFolder = req.imageFolder,
                )
                val created = storyService.create(story)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val req = runCatching { call.receive<StoryRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val story = Story(
                    id          = id,
                    title       = req.title,
                    content     = req.content,
                    category    = req.category,
                    imageUrl    = req.imageUrl,
                    sortOrder   = req.sortOrder,
                    imageFolder = req.imageFolder,
                )
                runCatching { storyService.update(id, story) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val deleted = storyService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = "Deleted"))
                else call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Not found"))
            }
        }

        // ── Music Tracks ──────────────────────────────────────────────────────
        route("/music") {
            get {
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = musicService.getAll()))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                runCatching { musicService.getById(id) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            post {
                val req = runCatching { call.receive<MusicTrackRequest>() }.getOrElse {
                    return@post call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val track = MusicTrack(
                    title     = req.title,
                    artist    = req.artist,
                    filename  = req.filename,
                    sortOrder = req.sortOrder,
                )
                val created = musicService.create(track)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val req = runCatching { call.receive<MusicTrackRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val track = MusicTrack(
                    id        = id,
                    title     = req.title,
                    artist    = req.artist,
                    filename  = req.filename,
                    sortOrder = req.sortOrder,
                )
                runCatching { musicService.update(id, track) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id must be an integer")
                )
                val deleted = musicService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = "Deleted"))
                else call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Not found"))
            }
        }
    }
}

