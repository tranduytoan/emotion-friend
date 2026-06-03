package com.emotionfriend.api.routes

import com.emotionfriend.api.dto.ApiResponse
import com.emotionfriend.api.plugins.configureSerialization
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthRouteTest {
    @Test
    fun `health endpoint returns ok payload`() = testApplication {
        application {
            configureSerialization()
            routing { healthRoute() }
        }

        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
        val payload = response.body<ApiResponse<Map<String, String>>>()
        assertEquals(true, payload.success)
        assertEquals("ok", payload.data?.get("status"))
        assertEquals("1.0.0", payload.data?.get("version"))
    }
}
