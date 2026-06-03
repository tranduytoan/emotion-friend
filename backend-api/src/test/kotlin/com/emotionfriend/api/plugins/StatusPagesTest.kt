package com.emotionfriend.api.plugins

import com.emotionfriend.api.dto.ApiResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class StatusPagesTest {
    @Test
    fun `illegal argument exception produces bad request`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                get("/boom") { throw IllegalArgumentException("Bad request error") }
            }
        }

        val response = client.get("/boom")

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertFalse(result.success)
        assertEquals("Bad request error", result.error)
        assertNull(result.data)
    }

    @Test
    fun `unhandled exception produces internal server error`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                get("/boom2") { throw RuntimeException("unexpected") }
            }
        }

        val response = client.get("/boom2")

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertFalse(result.success)
        assertEquals("Internal server error", result.error)
        assertNull(result.data)
    }

    @Test
    fun `no such element exception produces not found`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                get("/missing") { throw NoSuchElementException("Not found item") }
            }
        }

        val response = client.get("/missing")

        assertEquals(HttpStatusCode.NotFound, response.status)
        val result = response.body<ApiResponse<Unit>>()
        assertFalse(result.success)
        assertEquals("Not found item", result.error)
        assertNull(result.data)
    }
}
