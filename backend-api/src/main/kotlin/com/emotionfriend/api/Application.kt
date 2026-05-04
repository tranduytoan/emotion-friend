package com.emotionfriend.api

import com.emotionfriend.api.config.DatabaseConfig
import com.emotionfriend.api.db.DatabaseFactory
import com.emotionfriend.api.plugins.configureHTTP
import com.emotionfriend.api.plugins.configureRouting
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureStatusPages()

    // Initialise database + run Flyway migrations when DATABASE_URL is configured.
    // Without it the app runs on in-memory fake repositories (dev/test mode).
    val dbConfig = DatabaseConfig.fromEnv()
    val useDatabase = dbConfig != null
    if (dbConfig != null) {
        log.info("Database URL detected — connecting and running Flyway migrations…")
        DatabaseFactory.init(
            url = dbConfig.url,
            user = dbConfig.user,
            password = dbConfig.password,
            driver = dbConfig.driver,
        )
        log.info("Flyway migrations complete.")
    } else {
        log.warn("DATABASE_URL not set — running with in-memory fake repositories.")
    }

    configureRouting(useDatabase = useDatabase)
}
