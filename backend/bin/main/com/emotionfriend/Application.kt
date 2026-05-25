package com.emotionfriend

import com.emotionfriend.config.DatabaseConfig
import com.emotionfriend.repositories.EmotionLogRepository
import com.emotionfriend.repositories.EmotionRepository
import com.emotionfriend.repositories.ProgressRepository
import com.emotionfriend.repositories.SituationRepository
import com.emotionfriend.routes.emotionLogRoutes
import com.emotionfriend.routes.emotionRoutes
import com.emotionfriend.routes.healthRoutes
import com.emotionfriend.routes.progressRoutes
import com.emotionfriend.routes.situationRoutes
import com.emotionfriend.services.EmotionLogService
import com.emotionfriend.services.EmotionService
import com.emotionfriend.services.ProgressService
import com.emotionfriend.services.SituationService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val db = DatabaseConfig.init()

    // ── Repositories ──────────────────────────────────────────────────────────
    val emotionRepo    = EmotionRepository(db)
    val situationRepo  = SituationRepository(db)
    val progressRepo   = ProgressRepository(db)
    val emotionLogRepo = EmotionLogRepository(db)

    // ── Services ──────────────────────────────────────────────────────────────
    val emotionService    = EmotionService(emotionRepo)
    val situationService  = SituationService(situationRepo)
    val progressService   = ProgressService(progressRepo)
    val emotionLogService = EmotionLogService(emotionLogRepo)

    // ── Plugins ───────────────────────────────────────────────────────────────
    install(ContentNegotiation) {
        json(Json { prettyPrint = false; ignoreUnknownKeys = true })
    }
    install(DefaultHeaders)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("success" to false, "error" to (cause.message ?: "Internal server error"))
            )
        }
    }

    // ── Routing ───────────────────────────────────────────────────────────────
    routing {
        healthRoutes(dbConnected = db != null)
        emotionRoutes(emotionService)
        situationRoutes(situationService)
        progressRoutes(progressService)
        emotionLogRoutes(emotionLogService)
    }
}
