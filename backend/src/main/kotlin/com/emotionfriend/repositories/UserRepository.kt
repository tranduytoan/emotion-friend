package com.emotionfriend.repositories

import com.emotionfriend.models.User
import javax.sql.DataSource

class UserRepository(private val dataSource: DataSource?) {

    private var mockUser = User(
        id = 1,
        name = "Bé Minh",
        age = 8,
        avatarUrl = null
    )

    fun findById(userId: Long): User {
        dataSource ?: return mockUser.copy(id = userId)
        return try {
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "SELECT id, name, age, avatar_url FROM users WHERE id = ?"
                ).apply { setLong(1, userId) }.executeQuery().let { rs ->
                    if (rs.next()) {
                        User(
                            id = rs.getLong("id"),
                            name = rs.getString("name"),
                            age = rs.getInt("age"),
                            avatarUrl = rs.getString("avatar_url")
                        )
                    } else {
                        mockUser.copy(id = userId)
                    }
                }
            }
        } catch (e: Exception) {
            mockUser.copy(id = userId)
        }
    }

    fun update(userId: Long, name: String?, age: Int?, avatarUrl: String?): User {
        dataSource ?: return updateMock(userId, name, age, avatarUrl)
        return try {
            val current = findById(userId)
            val next = current.copy(
                name = name ?: current.name,
                age = age ?: current.age,
                avatarUrl = avatarUrl ?: current.avatarUrl
            )
            dataSource.connection.use { conn ->
                conn.prepareStatement(
                    "UPDATE users SET name = ?, age = ?, avatar_url = ? WHERE id = ?"
                ).apply {
                    setString(1, next.name)
                    setInt(2, next.age)
                    setString(3, next.avatarUrl)
                    setLong(4, userId)
                }.executeUpdate()
            }
            next
        } catch (e: Exception) {
            updateMock(userId, name, age, avatarUrl)
        }
    }

    private fun updateMock(userId: Long, name: String?, age: Int?, avatarUrl: String?): User {
        val current = mockUser.copy(id = userId)
        val next = current.copy(
            name = name ?: current.name,
            age = age ?: current.age,
            avatarUrl = avatarUrl ?: current.avatarUrl
        )
        mockUser = next
        return next
    }
}
