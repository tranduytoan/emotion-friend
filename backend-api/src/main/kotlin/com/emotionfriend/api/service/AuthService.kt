package com.emotionfriend.api.service

import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.repository.AuthRepository

class AuthService(
    private val authRepository: AuthRepository,
) {
    suspend fun login(email: String, password: String): AuthenticatedUser {
        require(email.isNotBlank()) { "Email không được để trống." }
        require(password.isNotBlank()) { "Mật khẩu không được để trống." }

        return authRepository.authenticate(email, password)
            ?: throw IllegalArgumentException("Email hoặc mật khẩu không đúng.")
    }

    suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser {
        require(email.isNotBlank()) { "Email không được để trống." }
        require(password.isNotBlank()) { "Mật khẩu không được để trống." }
        require(displayName.isNotBlank()) { "Tên hiển thị không được để trống." }

        val existing = authRepository.findByEmail(email)
        require(existing == null) { "Email này đã được đăng ký." }

        return authRepository.register(email, password, displayName)
    }

    suspend fun forgotPassword(email: String): String {
        require(email.isNotBlank()) { "Email không được để trống." }
        return "Yêu cầu đặt lại mật khẩu đã được ghi nhận cho $email."
    }

    suspend fun verifyEmail(email: String, code: String): AuthenticatedUser {
        require(email.isNotBlank()) { "Email không được để trống." }
        require(code.isNotBlank()) { "Mã xác thực không được để trống." }
        require(code == "123456") { "Mã xác thực không đúng." }

        val user = authRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Email không tồn tại trong hệ thống.")

        return user.copy(isVerified = true)
    }
}
