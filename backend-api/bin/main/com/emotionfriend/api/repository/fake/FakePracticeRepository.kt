package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.repository.PracticeRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FakePracticeRepository : PracticeRepository {

    private val attempts = CopyOnWriteArrayList<PracticeAttempt>()

    override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> =
        attempts.filter { it.childId == childId }

    override suspend fun create(attempt: PracticeAttempt): PracticeAttempt {
        val saved = attempt.copy(
            id = UUID.randomUUID().toString(),
            createdAt = Instant.now().toString(),
        )
        attempts.add(saved)
        return saved
    }
}
