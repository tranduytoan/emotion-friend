package com.emotionfriend.api.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppConfigTest {
    @Test
    fun `default port uses fallback when PORT is missing`() {
        val config = AppConfig()
        assertEquals(8080, config.port)
        assertNull(config.database)
    }
}
