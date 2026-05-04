package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.service.EmotionService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.emotionRoutes(service: EmotionService) {
    route("/api/emotions") {
        get {
            val emotions = service.getAll()
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = emotions))
        }
    }
}
