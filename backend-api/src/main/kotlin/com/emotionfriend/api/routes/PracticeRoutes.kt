package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.CreatePracticeAttemptRequest
import com.emotionfriend.api.dto.ExpressionPracticeRequest
import com.emotionfriend.api.dto.ExpressionPracticeResult
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.service.PracticeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.practiceRoutes(service: PracticeService) {
    // ── Scenario practice attempts ────────────────────────────────────────────
    route("/api/practice-attempts") {
        post {
            val req = call.receive<CreatePracticeAttemptRequest>()
            if (req.childId.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "childId must not be blank"),
                )
            }
            if (req.scenarioId.isBlank()) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "scenarioId must not be blank"),
                )
            }
            if (req.selectedIndex < 0) {
                return@post call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, error = "selectedIndex must be >= 0"),
                )
            }
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

        get("/{childId}") {
            val childId = call.parameters["childId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<List<Nothing>>(success = false, error = "childId is required"),
                )
            val attempts = service.getAllByChildId(childId)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = attempts))
        }
    }

    // ── Camera / expression practice (mock AI) ────────────────────────────────
    route("/api/expression-practice") {
        /**
         * POST /api/expression-practice/result
         *
         * Mock AI evaluation for camera expression practice.
         * In production this would call a real ML model; for the MVP we return
         * a deterministic positive result so the demo always works.
         */
        post("/result") {
            val req = call.receive<ExpressionPracticeRequest>()

            // Mock: always match with high confidence → positive UX for demo
            val result = ExpressionPracticeResult(
                matched          = true,
                confidence       = 0.87f,
                feedback         = mockFeedback(req.promptedEmotion),
                promptedEmotion  = req.promptedEmotion,
            )
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = result))
        }
    }
}

// ── Mock feedback messages (keyed by emotion name) ────────────────────────────

private fun mockFeedback(emotion: String): String = when (emotion.uppercase()) {
    "HAPPY"     -> "Con đã thể hiện nụ cười rất đẹp! 😊 Tuyệt lắm!"
    "SAD"       -> "Con đã thể hiện cảm xúc buồn rất tốt. 😢 Cố lên nhé!"
    "ANGRY"     -> "Con nhận biết cảm xúc tức giận rất giỏi! 😠"
    "SURPRISED" -> "Biểu cảm ngạc nhiên của con thật sinh động! 😮"
    "CALM"      -> "Con giữ được vẻ mặt bình thản rất tốt. 😌"
    "TIRED"     -> "Con diễn tả sự mệt mỏi rất chân thật! 😴"
    else        -> "Con làm tốt lắm! 🌟 Hãy tiếp tục luyện tập nhé!"
}
