package com.emotionfriend.routes

import com.emotionfriend.services.EmotionService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.emotionRoutes(service: EmotionService) {
    route("/api/emotions") {
        get {
            call.respond(service.getAll())
        }
    }
}
