package com.emotionfriend.routes

import com.emotionfriend.services.SituationService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.situationRoutes(service: SituationService) {
    route("/api/situations") {
        get {
            call.respond(service.getAll())
        }
    }
}
