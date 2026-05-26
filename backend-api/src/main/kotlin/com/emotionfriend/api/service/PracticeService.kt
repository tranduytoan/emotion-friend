package com.emotionfriend.api.service

import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.repository.PracticeRepository

class PracticeService(private val repo: PracticeRepository) {
    suspend fun create(attempt: PracticeAttempt): PracticeAttempt = repo.create(attempt)
    suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = repo.getAllByChildId(childId)
}
