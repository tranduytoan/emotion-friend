package com.emotionfriend.api

import io.ktor.server.testing.testApplication
import kotlin.test.Test

class ApplicationModuleTest {
    @Test
    fun `module loads without database configuration`() = testApplication {
        application {
            module()
        }
    }
}
