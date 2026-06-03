package com.emotionfriend.api.plugins

import com.emotionfriend.api.dto.ApiResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Serializable
private data class TestPayload(val name: String)

class PluginCoverageTest {
    @Test
    fun `configure serialization ignores unknown json keys`() = testApplication {
        application {
            configureSerialization()
            routing {
                post("/parse") {
                    val payload = call.receive<TestPayload>()
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, data = payload))
                }
            }
        }

        val response = client.post("/parse") {
            contentType(ContentType.Application.Json)
            setBody(
                """
                {"name": "Emo Kid", "extra": "ignore me", "score": 10}
                """.trimIndent(),
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val result = response.body<ApiResponse<TestPayload>>()
        assertEquals(true, result.success)
        assertEquals("Emo Kid", result.data?.name)
    }

    @Test
    fun `configure HTTP adds default headers and allows cors preflight`() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            routing {
                route("/test-cors") {
                    post {
                        call.respond(HttpStatusCode.OK)
                    }
                }
                get("/test-header") {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        val headerResponse = client.get("/test-header")
        assertEquals(HttpStatusCode.OK, headerResponse.status)
        assertEquals("Ktor", headerResponse.headers["X-Engine"])

        val response = client.options("/test-cors") {
            header(HttpHeaders.Origin, "http://localhost")
            header(HttpHeaders.AccessControlRequestMethod, HttpMethod.Post.toString())
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.headers[HttpHeaders.AccessControlAllowOrigin] != null)
    }
}
