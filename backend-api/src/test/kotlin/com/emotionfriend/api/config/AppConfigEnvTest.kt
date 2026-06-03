package com.emotionfriend.api.config

import kotlin.test.Test
import kotlin.test.assertEquals

class AppConfigEnvTest {
    @Test
    fun `port reads from PORT environment variable`() {
        withEnvironmentVariables(mapOf("PORT" to "45123")) {
            val config = AppConfig()
            assertEquals(45123, config.port)
        }
    }

    @Test
    fun `database config reads environment variables when available`() {
        withEnvironmentVariables(
            mapOf(
                "DATABASE_URL" to "jdbc:mysql://localhost:3306/testdb",
                "DATABASE_USER" to "tester",
                "DATABASE_PASSWORD" to "secret",
            ),
        ) {
            val databaseConfig = DatabaseConfig.fromEnv()
            assertEquals("jdbc:mysql://localhost:3306/testdb", databaseConfig?.url)
            assertEquals("tester", databaseConfig?.user)
            assertEquals("secret", databaseConfig?.password)
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
