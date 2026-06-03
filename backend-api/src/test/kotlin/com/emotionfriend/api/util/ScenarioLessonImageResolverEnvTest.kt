package com.emotionfriend.api.util

import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File

class ScenarioLessonImageResolverEnvTest {
    @Test
    fun `resolveScenarioLessonImageDir uses explicit SCENARIO_LESSONS_IMAGE_PATH if provided`() {
        val tempDir = createTempDir("scenario-image-path")
        try {
            withEnvironmentVariables(mapOf("SCENARIO_LESSONS_IMAGE_PATH" to tempDir.absolutePath)) {
                val result = ScenarioLessonImageResolver.resolveScenarioLessonImageDir()
                assertEquals(tempDir.absolutePath, result.absolutePath)
            }
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun `resolveScenarioLessonImageDir uses STATIC_FILES_PATH when explicit path is not set`() {
        val tempRoot = createTempDir("static-files-root")
        val scenarioDir = File(tempRoot, "scenario_lessons").apply { mkdirs() }
        try {
            withEnvironmentVariables(
                mapOf(
                    "SCENARIO_LESSONS_IMAGE_PATH" to null,
                    "STATIC_FILES_PATH" to tempRoot.absolutePath,
                ),
            ) {
                val result = ScenarioLessonImageResolver.resolveScenarioLessonImageDir()
                assertEquals(scenarioDir.absolutePath, result.absolutePath)
            }
        } finally {
            scenarioDir.deleteRecursively()
            tempRoot.deleteRecursively()
        }
    }
}

private fun withEnvironmentVariables(updates: Map<String, String?>, block: () -> Unit) {
    val originalValues = updates.keys.associateWith { System.getenv(it) }
    setEnvironmentVariables(updates)
    try {
        block()
    } finally {
        setEnvironmentVariables(originalValues)
    }
}

@Suppress("UNCHECKED_CAST")
private fun setEnvironmentVariables(values: Map<String, String?>) {
    val env = System.getenv()
    try {
        val field = env.javaClass.getDeclaredField("m").apply { isAccessible = true }
        val map = field.get(env) as MutableMap<String, String>
        values.forEach { (key, value) ->
            if (value == null) map.remove(key) else map[key] = value
        }
    } catch (ignored: NoSuchFieldException) {
        try {
            val processEnvironment = Class.forName("java.lang.ProcessEnvironment")
            val theEnvironmentField = processEnvironment.getDeclaredField("theEnvironment").apply { isAccessible = true }
            val envMap = theEnvironmentField.get(null) as MutableMap<String, String>
            values.forEach { (key, value) ->
                if (value == null) envMap.remove(key) else envMap[key] = value
            }
            val theCaseInsensitiveEnvironmentField = processEnvironment.getDeclaredField("theCaseInsensitiveEnvironment").apply { isAccessible = true }
            val ciMap = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
            values.forEach { (key, value) ->
                if (value == null) ciMap.remove(key) else ciMap[key] = value
            }
        } catch (ignored2: Exception) {
            // best effort: some JVMs do not allow modification of environment variables
        }
    }
}
