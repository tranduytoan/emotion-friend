package com.emotionfriend.data.repository

import com.emotionfriend.data.remote.ApiResult
import com.emotionfriend.data.remote.EmotionFriendApiClient
import com.emotionfriend.data.remote.dto.AuthResponseDto
import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteAuthRepository @Inject constructor(
    private val apiClient: EmotionFriendApiClient,
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return when (val response = apiClient.login(email = email, password = password)) {
            is ApiResult.Success -> Result.success(response.data.toDomainUser())
            is ApiResult.Error -> Result.failure(Exception(response.message))
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
    ): Result<AuthUser> {
        return when (
            val response = apiClient.register(
                email = email,
                password = password,
                displayName = displayName,
                role = role.name,
            )
        ) {
            is ApiResult.Success -> Result.success(response.data.toDomainUser())
            is ApiResult.Error -> Result.failure(Exception(response.message))
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return when (val response = apiClient.forgotPassword(email = email)) {
            is ApiResult.Success -> Result.success(response.data)
            is ApiResult.Error -> Result.failure(Exception(response.message))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<AuthUser> {
        return when (val response = apiClient.verifyEmail(email = email, code = code)) {
            is ApiResult.Success -> Result.success(response.data.toDomainUser())
            is ApiResult.Error -> Result.failure(Exception(response.message))
        }
    }

    private fun AuthResponseDto.toDomainUser(): AuthUser = AuthUser(
        id = userId.toString(),
        email = email,
        displayName = displayName,
        role = role.toRole(),
        isVerified = isVerified,
    )

    private fun String.toRole(): UserRole = when (uppercase()) {
        UserRole.ADMIN.name -> UserRole.ADMIN
        else -> UserRole.CHILD
    }
}
