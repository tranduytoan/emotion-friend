package com.emotionfriend.api.config

data class AppConfig(
    val port: Int = System.getenv("PORT")?.toIntOrNull() ?: 8080,
    val database: DatabaseConfig? = null,
)

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String,
    val driver: String = "com.mysql.cj.jdbc.Driver",
) {
    companion object {
        fun fromEnv(): DatabaseConfig? {
            val url = System.getenv("DATABASE_URL") ?: return null
            return DatabaseConfig(
                url = url,
                user = System.getenv("DATABASE_USER") ?: "root",
                password = System.getenv("DATABASE_PASSWORD") ?: "",
            )
        }
    }
}
