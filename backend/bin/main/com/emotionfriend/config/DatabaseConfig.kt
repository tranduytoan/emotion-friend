package com.emotionfriend.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object DatabaseConfig {
    /**
     * Returns a live [HikariDataSource] when all required env vars are present
     * and the connection succeeds. Returns `null` otherwise — all repositories
     * fall back to embedded mock data so the server stays functional without a DB.
     *
     * Required env vars : DB_HOST, DB_USER, DB_PASSWORD
     * Optional env vars : DB_PORT (default 3306), DB_NAME (default emotion_friend)
     */
    fun init(): DataSource? {
        val host = System.getenv("DB_HOST")     ?: return null.also { log("DB_HOST not set — using mock data") }
        val user = System.getenv("DB_USER")     ?: return null.also { log("DB_USER not set — using mock data") }
        val pass = System.getenv("DB_PASSWORD") ?: return null.also { log("DB_PASSWORD not set — using mock data") }
        val port = System.getenv("DB_PORT")     ?: "3306"
        val name = System.getenv("DB_NAME")     ?: "emotion_friend"

        return try {
            HikariDataSource(HikariConfig().apply {
                jdbcUrl = "jdbc:mysql://$host:$port/$name" +
                          "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
                username         = user
                password         = pass
                driverClassName  = "com.mysql.cj.jdbc.Driver"
                maximumPoolSize  = 5
                connectionTimeout = 5_000L
                validationTimeout = 3_000L
            }).also { log("MySQL connected at $host:$port/$name") }
        } catch (e: Exception) {
            log("MySQL connection failed: ${e.message} — using mock data")
            null
        }
    }

    private fun log(msg: String) = println("[DatabaseConfig] $msg")
}
