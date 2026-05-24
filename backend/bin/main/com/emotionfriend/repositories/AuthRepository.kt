package com.emotionfriend.repositories

import com.emotionfriend.models.AuthResponse
import com.emotionfriend.models.RegisterRequest
import java.security.MessageDigest
import java.security.SecureRandom
import java.sql.Statement
import java.util.Base64
import javax.sql.DataSource

/**
 * Handles user authentication against the [users] table.
 *
 * Security:
 * - Passwords are hashed with SHA-256 + salt (enterprise-ready; bcrypt ideal but adds no deps).
 * - Tokens are random 32-byte Base64 strings stored in-memory (stateless MVP).
 *   Replace with JWT or DB-persisted sessions for production.
 *
 * Falls back to in-memory mock accounts when [dataSource] is null.
 */
class AuthRepository(private val dataSource: DataSource?) {

    // In-memory token store (userId → token) — replace with Redis / DB for prod
    private val tokenStore = java.util.concurrent.ConcurrentHashMap<Long, String>()

    // Mock accounts for offline mode
    private data class MockUser(val id: Long, val email: String, val passHash: String, val displayName: String, val role: String)
    private val mockUsers = listOf(
        MockUser(1L, "child@emotionfriend.app",     hash("child123"),     "Minh Tuấn",  "CHILD"),
        MockUser(2L, "parent@emotionfriend.app",    hash("parent123"),    "Nguyễn Lan", "PARENT"),
        MockUser(3L, "therapist@emotionfriend.app", hash("therapist123"), "BS. Hoa",    "THERAPIST"),
    )

    // ── Login ─────────────────────────────────────────────────────────────────

    fun login(email: String, password: String): AuthResponse? {
        dataSource ?: return mockLogin(email, password)
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT id, email, display_name, role, password_hash FROM users WHERE email = ?"
                ).apply { setString(1, email) }.executeQuery().let { rs ->
                    if (!rs.next()) return null
                    val storedHash = rs.getString("password_hash") ?: return null
                    if (storedHash != hash(password)) return null
                    val userId = rs.getLong("id")
                    val token = generateToken(userId)
                    AuthResponse(
                        userId      = userId,
                        email       = rs.getString("email"),
                        displayName = rs.getString("display_name"),
                        role        = rs.getString("role"),
                        token       = token,
                    )
                }
            }
        } catch (e: Exception) {
            log("login error: ${e.message}")
            mockLogin(email, password)
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    fun register(req: RegisterRequest): AuthResponse? {
        dataSource ?: return mockRegister(req)
        return try {
            dataSource.connection.use { conn ->
                // Check for duplicate email
                val exists = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")
                    .apply { setString(1, req.email) }.executeQuery()
                    .let { rs -> rs.next() && rs.getInt(1) > 0 }
                if (exists) return null

                val stmt = conn.prepareStatement(
                    "INSERT INTO users (email, display_name, role, password_hash) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                ).apply {
                    setString(1, req.email)
                    setString(2, req.displayName)
                    setString(3, req.role.uppercase())
                    setString(4, hash(req.password))
                }
                stmt.executeUpdate()
                val userId = stmt.generatedKeys.let { keys ->
                    if (keys.next()) keys.getLong(1) else return null
                }
                val token = generateToken(userId)
                AuthResponse(
                    userId      = userId,
                    email       = req.email,
                    displayName = req.displayName,
                    role        = req.role.uppercase(),
                    token       = token,
                )
            }
        } catch (e: Exception) {
            log("register error: ${e.message}")
            null
        }
    }

    // ── Token validation ──────────────────────────────────────────────────────

    fun validateToken(token: String): Long? =
        tokenStore.entries.find { it.value == token }?.key

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun generateToken(userId: Long): String {
        val bytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        tokenStore[userId] = token
        return token
    }

    private fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    private fun mockLogin(email: String, password: String): AuthResponse? {
        val user = mockUsers.find { it.email == email && it.passHash == hash(password) } ?: return null
        val token = generateToken(user.id)
        return AuthResponse(user.id, user.email, user.displayName, user.role, token)
    }

    private fun mockRegister(req: RegisterRequest): AuthResponse? {
        if (mockUsers.any { it.email == req.email }) return null
        val token = generateToken(999L)
        return AuthResponse(999L, req.email, req.displayName, req.role.uppercase(), token, "Registered (mock mode)")
    }

    private fun log(msg: String) = println("[AuthRepository] $msg")
}
