package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.CreatePracticeAttemptRequest
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.service.PracticeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.practiceRoutes(service: PracticeService) {
    route("/api/practice-attempts") {
        post {
            val req = call.receive<CreatePracticeAttemptRequest>()
            val attempt = service.create(
                PracticeAttempt(
                    childId       = req.childId,
                    scenarioId    = req.scenarioId,
                    selectedIndex = req.selectedIndex,
                    isCorrect     = req.isCorrect,
                    promptEmotion = req.promptEmotion,
                ),
            )
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = attempt))
        }
    }
}
