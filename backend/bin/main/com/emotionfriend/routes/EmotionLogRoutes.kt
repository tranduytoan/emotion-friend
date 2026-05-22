package com.emotionfriend.routes

import com.emotionfriend.models.EmotionLogRequest
import com.emotionfriend.services.EmotionLogService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.emotionLogRoutes(service: EmotionLogService) {
    route("/api/emotion-log") {
        /**
         * POST /api/emotion-log
         *
         * Request body:
         * {
         *   "userId"   : 1,
         *   "emotionId": 3,
         *   "note"     : "Hôm nay hơi buồn"   // optional
         * }
         *
         * Response 201:
         * {
         *   "id"       : 42,
         *   "userId"   : 1,
         *   "emotionId": 3,
         *   "note"     : "Hôm nay hơi buồn",
         *   "loggedAt" : "2026-05-13T19:00:00"
         * }
         */
        post {
            val req = call.receive<EmotionLogRequest>()
            val log = service.log(req.userId, req.emotionId, req.note)
            call.respond(HttpStatusCode.Created, log)
        }
    }
}
