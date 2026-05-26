package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.dto.CreateJournalEntryRequest
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.service.JournalService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.journalRoutes(service: JournalService) {
    route("/api/journal-entries") {
        post {
            val req = call.receive<CreateJournalEntryRequest>()
            val entry = service.create(
                JournalEntry(childId = req.childId, emotionType = req.emotionType, note = req.note),
            )
            call.respond(HttpStatusCode.Created, ApiResponse(success = true, data = entry))
        }

        get("/{childId}") {
            val childId = call.parameters["childId"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<List<Nothing>>(success = false, error = "childId is required")
                )
            val entries = service.getAllByChildId(childId)
            call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = entries))
        }
    }
}
