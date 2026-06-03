package com.emotionfriend.api.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScenarioLessonImageResolverTest {
    @Test
    fun `resolveImageName returns trimmed name when supplied`() {
        val result = ScenarioLessonImageResolver.resolveImageName(123, "  sample.png  ")
        assertEquals("sample.png", result)
    }

    @Test
    fun `resolveImageName returns null when no current image and no matching file exists`() {
        val result = ScenarioLessonImageResolver.resolveImageName(999999, "")
        assertNull(result)
    }
}
