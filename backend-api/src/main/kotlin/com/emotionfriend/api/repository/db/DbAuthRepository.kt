package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.AccountTable
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.security.MessageDigest
import java.time.Instant

class DbAuthRepository : AuthRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun authenticate(email: String, password: String): AuthenticatedUser? = dbQuery {
        val normalizedEmail = email.trim().lowercase()
        val hashedPassword = sha256(password)

        AccountTable
            .selectAll()
            .where { (AccountTable.account eq normalizedEmail) and (AccountTable.password eq hashedPassword) }
            .singleOrNull()
            ?.toAuthenticatedUser()
    }

    override suspend fun findByEmail(email: String): AuthenticatedUser? = dbQuery {
        val normalizedEmail = email.trim().lowercase()
        AccountTable
            .selectAll()
            .where { AccountTable.account eq normalizedEmail }
            .singleOrNull()
            ?.toAuthenticatedUser()
    }

    override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser = dbQuery {
        val normalizedEmail = email.trim().lowercase()
        val now = Instant.now()

        val generatedId = AccountTable.insert {
            it[account] = normalizedEmail
            it[this.password] = sha256(password)
            it[name] = displayName.trim()
            it[age] = 8
            it[avatarUrl] = "avatar/default.png"
            it[createdAt] = now
            it[updatedAt] = now
        }[AccountTable.id]

        AuthenticatedUser(
            id = generatedId,
            email = normalizedEmail,
            displayName = displayName.trim(),
            role = "CHILD",
            isVerified = false,
        )
    }

    private fun ResultRow.toAuthenticatedUser(): AuthenticatedUser = AuthenticatedUser(
        id = this[AccountTable.id],
        email = this[AccountTable.account],
        displayName = this[AccountTable.name],
        role = "CHILD",
        isVerified = true,
    )

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return digest.joinToString(separator = "") { "%02x".format(it) }
    }
}
