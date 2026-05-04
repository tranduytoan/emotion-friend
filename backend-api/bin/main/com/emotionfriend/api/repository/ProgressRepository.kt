package com.emotionfriend.api.repository

import com.emotionfriend.api.model.ProgressSummary

interface ProgressRepository {
    suspend fun getProgressSummary(childId: String): ProgressSummary
}
