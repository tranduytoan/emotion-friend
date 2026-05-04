package com.emotionfriend.api.service

import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.repository.ProgressRepository

class ProgressService(private val repo: ProgressRepository) {
    suspend fun getProgress(childId: String): ProgressSummary = repo.getProgressSummary(childId)
}
