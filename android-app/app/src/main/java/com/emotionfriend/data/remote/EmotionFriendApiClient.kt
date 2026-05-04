package com.emotionfriend.data.remote

import com.emotionfriend.data.remote.dto.ApiResponseDto
import com.emotionfriend.data.remote.dto.CreateJournalEntryRequest
import com.emotionfriend.data.remote.dto.CreatePracticeAttemptRequest
import com.emotionfriend.data.remote.dto.EmotionCardDto
import com.emotionfriend.data.remote.dto.JournalEntryDto
import com.emotionfriend.data.remote.dto.PracticeAttemptDto
import com.emotionfriend.data.remote.dto.ProgressSummaryDto
import com.emotionfriend.data.remote.dto.ScenarioLessonDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import javax.inject.Inject

/**
 * Thin HTTP client for the Emotion Friend backend API.
 *
 * All methods return [ApiResult] and are safe to call from coroutines.
 * The app continues to work with local Room data when remote calls fail.
 */
class EmotionFriendApiClient @Inject constructor(
    private val httpClient: HttpClient,
) {

    /** GET /api/emotions — returns all emotion cards. */
    suspend fun getEmotions(): ApiResult<List<EmotionCardDto>> = safeCall {
        val response = httpClient.get("${ApiConstants.BASE_URL}${ApiConstants.PATH_EMOTIONS}")
        val body: ApiResponseDto<List<EmotionCardDto>> = response.body()
        requireNotNull(body.data) { "No data in response" }
    }

    /** GET /api/scenarios — returns all scenario lessons. */
    suspend fun getScenarios(): ApiResult<List<ScenarioLessonDto>> = safeCall {
        val response = httpClient.get("${ApiConstants.BASE_URL}${ApiConstants.PATH_SCENARIOS}")
        val body: ApiResponseDto<List<ScenarioLessonDto>> = response.body()
        requireNotNull(body.data) { "No data in response" }
    }

    /** POST /api/journal-entries — submits a new journal entry. */
    suspend fun submitJournalEntry(
        request: CreateJournalEntryRequest,
    ): ApiResult<JournalEntryDto> = safeCall {
        val response = httpClient.post("${ApiConstants.BASE_URL}${ApiConstants.PATH_JOURNAL_ENTRIES}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val body: ApiResponseDto<JournalEntryDto> = response.body()
        requireNotNull(body.data) { "No data in response" }
    }

    /** POST /api/practice-attempts — submits a practice attempt result. */
    suspend fun submitPracticeAttempt(
        request: CreatePracticeAttemptRequest,
    ): ApiResult<PracticeAttemptDto> = safeCall {
        val response = httpClient.post("${ApiConstants.BASE_URL}${ApiConstants.PATH_PRACTICE_ATTEMPTS}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val body: ApiResponseDto<PracticeAttemptDto> = response.body()
        requireNotNull(body.data) { "No data in response" }
    }

    /** GET /api/progress/{childId} — retrieves progress summary for a child. */
    suspend fun getProgress(childId: String): ApiResult<ProgressSummaryDto> = safeCall {
        val response = httpClient.get("${ApiConstants.BASE_URL}${ApiConstants.PATH_PROGRESS}/$childId")
        val body: ApiResponseDto<ProgressSummaryDto> = response.body()
        requireNotNull(body.data) { "No data in response" }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private inline fun <T> safeCall(block: () -> T): ApiResult<T> = try {
        ApiResult.Success(block())
    } catch (e: Exception) {
        ApiResult.Error(message = e.message ?: "Unknown error")
    }
}
