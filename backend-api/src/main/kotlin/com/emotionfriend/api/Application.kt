package com.emotionfriend.api

import com.emotionfriend.api.config.DatabaseConfig
import com.emotionfriend.api.db.DatabaseFactory
import com.emotionfriend.api.plugins.configureHTTP
import com.emotionfriend.api.plugins.configureRouting
import com.emotionfriend.api.plugins.configureSerialization
import com.emotionfriend.api.plugins.configureStatusPages
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import java.io.File

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureStatusPages()

    val dbConfig = DatabaseConfig.fromEnv()
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
        log.warn("DATABASE_URL not set — backend will fail on DB calls. Please configure DATABASE_URL.")
    }

    // Serve static story images: GET /static/stories/{folder}/1.png
    val staticDir = System.getenv("STATIC_FILES_PATH") ?: "/app/static"
    val staticFile = File(staticDir)
    if (staticFile.exists()) {
        routing {
            staticFiles("/static", staticFile)
        }
    }

    configureRouting()
}
