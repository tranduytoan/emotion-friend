package com.emotionfriend.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val database: String,
    val version: String
)

fun Routing.healthRoutes(dbConnected: Boolean) {
    get("/health") {
        call.respond(
            HttpStatusCode.OK,
            HealthResponse(
                status   = "ok",
                database = if (dbConnected) "connected" else "mock",
                version  = "1.0.0"
            )
        )
    }
}
