package com.emotionfriend.api

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import java.io.File

class ApplicationModuleStaticTest {
    @Test
    fun `module serves static files when STATIC_FILES_PATH is configured`() {
        val tempRoot = createTempDir("static-path-root")
        val staticFile = File(tempRoot, "hello.txt").apply { writeText("static content") }

        try {
            withEnvironmentVariables(mapOf("STATIC_FILES_PATH" to tempRoot.absolutePath)) {
                testApplication {
                    application {
                        module()
                    }

                    val response = client.get("/img/hello.txt")
                    assertEquals(HttpStatusCode.OK, response.status)
                    assertEquals("static content", response.bodyAsText())
                }
            }
        } finally {
            staticFile.delete()
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
