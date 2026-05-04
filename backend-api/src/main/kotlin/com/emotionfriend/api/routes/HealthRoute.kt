package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
private data class HealthStatus(val status: String, val version: String)

fun Route.healthRoute() {
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            ApiResponse(success = true, data = HealthStatus(status = "ok", version = "1.0.0")),
        )
    }
}
