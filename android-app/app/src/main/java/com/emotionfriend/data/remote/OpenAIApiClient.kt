package com.emotionfriend.data.remote

import android.util.Log
import com.emotionfriend.core.config.AppConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Thin Ktor wrapper around the OpenAI Chat Completions API.
 *
 * Cô Vy system prompt teaches the model to respond in warm, child-friendly Vietnamese.
 *
 * Usage: inject via Hilt and call [chat] from a ViewModel.
 */
class OpenAIApiClient(private val httpClient: HttpClient) {

    private val systemPrompt =
        "Bạn là cô giáo Vy, người bạn đồng hành thân thiện của trẻ em mắc chứng tự kỷ. " +
        "Hãy lắng nghe và động viên trẻ với ngôn ngữ đơn giản, ấm áp, đồng cảm. " +
        "Giúp trẻ cảm thấy được lắng nghe và dễ chia sẻ hơn. " +
        "Tuyệt đối không nói về chủ đề người lớn hoặc bạo lực. " +
        "Câu trả lời ngắn gọn, không quá 3 câu. " +
        "Nếu con đang buồn hoặc muốn nghe kể chuyện, THÊM [SUGGEST:STORY] vào cuối câu trả lời. " +
        "Nếu con mệt mỏi, muốn thư giãn hoặc nghe nhạc, THÊM [SUGGEST:RELAX] vào cuối câu trả lời. " +
        "Nếu con đang lo lắng, tức giận và cần bình tĩnh, THÊM [SUGGEST:BREATHING] vào cuối câu trả lời. " +
        "Chỉ thêm tối đa một tag suggest và đặt ở cuối phản hồi."

    /**
     * Sends [userMessage] to the OpenAI chat endpoint and returns the assistant reply text.
     * Returns an [ApiResult.Error] if the API key is missing or the call fails.
     */
    suspend fun chat(userMessage: String): ApiResult<String> {
        if (AppConfig.OPENAI_API_KEY.isBlank()) {
            return ApiResult.Error("OpenAI API key chưa được cấu hình. Vui lòng thêm key vào AppConfig.")
        }
        return try {
            val request = ChatRequest(
                model    = "gpt-4o-mini",
                messages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user",   content = userMessage),
                ),
            )
            val response: ChatResponse = httpClient.post("${AppConfig.OPENAI_BASE_URL}/chat/completions") {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${AppConfig.OPENAI_API_KEY}")
                setBody(request)
            }.body()
            val text = response.choices.firstOrNull()?.message?.content?.trim()
                ?: "Cô Vy không hiểu con nói gì. Con thử lại nhé!"
            ApiResult.Success(text)
        } catch (e: Exception) {
            Log.e("OpenAIApiClient", "Chat request failed", e)
            ApiResult.Error("Không thể kết nối với cô Vy lúc này. Con thử lại sau nhé!", null)
        }
    }
}

// ─── Request / Response DTOs ─────────────────────────────────────────────────

@Serializable
private data class ChatRequest(
    val model    : String,
    val messages : List<ChatMessage>,
)

@Serializable
private data class ChatMessage(
    val role    : String,
    val content : String,
)

@Serializable
private data class ChatResponse(
    val choices : List<ChatChoice>,
)

@Serializable
private data class ChatChoice(
    val message : ChatMessage,
    @SerialName("finish_reason") val finishReason: String? = null,
)
