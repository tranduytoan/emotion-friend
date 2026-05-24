package com.emotionfriend.routes

import com.emotionfriend.models.MessageResponse
import com.emotionfriend.models.SyncPullResponse
import com.emotionfriend.models.SyncPushRequest
import com.emotionfriend.models.SyncPushResponse
import com.emotionfriend.repositories.AuthRepository
import com.emotionfriend.repositories.EmotionLogRepository
import com.emotionfriend.repositories.EmotionRepository
import com.emotionfriend.repositories.SituationRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Routing.syncRoutes(
    authRepo: AuthRepository,
    emotionRepo: EmotionRepository,
    situationRepo: SituationRepository,
    emotionLogRepo: EmotionLogRepository,
) {
    route("/api/sync") {

        /**
         * GET /api/sync/pull
         * Headers: Authorization: Bearer <token>
         * Returns master-data (emotions + situations) for the client to upsert into Room.
         */
        get("/pull") {
            val token = call.request.headers["Authorization"]
                ?.removePrefix("Bearer ")?.trim()

            // Auth is optional in dev/mock mode — allow through if token is missing
            if (token != null && authRepo.validateToken(token) == null) {
                call.respond(HttpStatusCode.Unauthorized, MessageResponse("Invalid or expired token", false))
                return@get
            }

            val emotions   = emotionRepo.findAll()
            val situations = situationRepo.findAll()

            call.respond(
                HttpStatusCode.OK,
                SyncPullResponse(
                    emotions   = emotions,
                    situations = situations,
                )
            )
        }

        /**
         * POST /api/sync/push
         * Headers: Authorization: Bearer <token>
         * Body: SyncPushRequest
         * Writes journal entries and practice attempts from the client.
         */
        post("/push") {
            val token = call.request.headers["Authorization"]
                ?.removePrefix("Bearer ")?.trim()

            if (token != null && authRepo.validateToken(token) == null) {
                call.respond(HttpStatusCode.Unauthorized, MessageResponse("Invalid or expired token", false))
                return@post
            }

            val req = call.receive<SyncPushRequest>()
            var accepted = 0
            var rejected = 0

            for (entry in req.journalEntries) {
                try {
                    emotionLogRepo.insert(req.userId, entry.emotionId, entry.note)
                    accepted++
                } catch (e: Exception) {
                    println("[SyncRoutes] Failed to insert journal ${entry.localId}: ${e.message}")
                    rejected++
                }
            }

            call.respond(
                HttpStatusCode.OK,
                SyncPushResponse(accepted = accepted, rejected = rejected)
            )
        }
    }
}
