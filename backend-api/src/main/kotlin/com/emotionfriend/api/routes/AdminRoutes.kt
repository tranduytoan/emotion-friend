package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.MusicTrackRequest
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.ScenarioLessonRequest
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.model.StoryRequest
import com.emotionfriend.api.service.MusicService
import com.emotionfriend.api.service.ScenarioService
import com.emotionfriend.api.service.StoryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.UUID

/**
 * Admin CRUD routes — protected by a simple Bearer token.
 *
 * Token is read from the ADMIN_TOKEN environment variable.
 * If not set, defaults to "admin-secret-token" for development.
 *
 * All admin routes are prefixed with /admin/
 */
fun Route.adminRoutes(
    scenarioService: ScenarioService,
    storyService: StoryService,
    musicService: MusicService,
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

        // ── Scenario Lessons ──────────────────────────────────────────────────
        route("/scenarios") {
            get {
                call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = scenarioService.getAll()))
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                    id           = req.id ?: UUID.randomUUID().toString(),
                    title        = req.title,
                    situation    = req.situation,
                    options      = req.options,
                    correctIndex = req.correctIndex,
                    explanation  = req.explanation,
                    sortOrder    = req.sortOrder,
                )
                val created = scenarioService.create(lesson)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
                )
                val req = runCatching { call.receive<ScenarioLessonRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val lesson = ScenarioLesson(
                    id           = id,
                    title        = req.title,
                    situation    = req.situation,
                    options      = req.options,
                    correctIndex = req.correctIndex,
                    explanation  = req.explanation,
                    sortOrder    = req.sortOrder,
                )
                runCatching { scenarioService.update(id, lesson) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                    id        = req.id ?: UUID.randomUUID().toString(),
                    title     = req.title,
                    content   = req.content,
                    category  = req.category,
                    imageUrl  = req.imageUrl,
                    sortOrder = req.sortOrder,
                )
                val created = storyService.create(story)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
                )
                val req = runCatching { call.receive<StoryRequest>() }.getOrElse {
                    return@put call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "Invalid request body"))
                }
                val story = Story(
                    id        = id,
                    title     = req.title,
                    content   = req.content,
                    category  = req.category,
                    imageUrl  = req.imageUrl,
                    sortOrder = req.sortOrder,
                )
                runCatching { storyService.update(id, story) }
                    .onSuccess { call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = it)) }
                    .onFailure { call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = it.message)) }
            }

            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                    id        = req.id ?: UUID.randomUUID().toString(),
                    title     = req.title,
                    artist    = req.artist,
                    filename  = req.filename,
                    sortOrder = req.sortOrder,
                )
                val created = musicService.create(track)
                call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = created))
            }

            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
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
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, error = "id required")
                )
                val deleted = musicService.delete(id)
                if (deleted) call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = "Deleted"))
                else call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, error = "Not found"))
            }
        }
    }
}
