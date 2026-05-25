package com.emotionfriend.services

import com.emotionfriend.models.Progress
import com.emotionfriend.repositories.ProgressRepository

class ProgressService(private val repo: ProgressRepository) {
    fun getByUserId(userId: Long): Progress = repo.findByUserId(userId)
}
