package com.emotionfriend.services

import com.emotionfriend.models.ProfileUpdateRequest
import com.emotionfriend.models.UserProfile
import com.emotionfriend.repositories.ProgressRepository
import com.emotionfriend.repositories.SettingsRepository
import com.emotionfriend.repositories.UserRepository

class ProfileService(
    private val userRepo: UserRepository,
    private val settingsRepo: SettingsRepository,
    private val progressRepo: ProgressRepository
) {
    fun getProfile(userId: Long): UserProfile {
        val user = userRepo.findById(userId)
        val settings = settingsRepo.findByUserId(userId)
        val progress = progressRepo.findByUserId(userId)
        return UserProfile(user = user, settings = settings, progress = progress)
    }

    fun updateProfile(request: ProfileUpdateRequest): UserProfile {
        val user = userRepo.update(
            userId = request.userId,
            name = request.name,
            age = request.age,
            avatarUrl = request.avatarUrl
        )
        val settings = settingsRepo.update(
            userId = request.userId,
            soundEnabled = request.soundEnabled,
            notificationEnabled = request.notificationEnabled,
            reminderTime = request.reminderTime,
            language = request.language
        )
        val progress = progressRepo.findByUserId(request.userId)
        return UserProfile(user = user, settings = settings, progress = progress)
    }
}
