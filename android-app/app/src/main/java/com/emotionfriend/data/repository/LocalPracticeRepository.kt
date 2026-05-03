package com.emotionfriend.data.repository

import com.emotionfriend.data.local.PracticeAttemptDao
import com.emotionfriend.data.mapper.toDomain
import com.emotionfriend.data.mapper.toEntity
import com.emotionfriend.domain.model.PracticeAttempt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalPracticeRepository @Inject constructor(
    private val dao: PracticeAttemptDao
) : PracticeRepository {

    override fun getByChildId(childId: String): Flow<List<PracticeAttempt>> =
        dao.getByChildId(childId).map { list -> list.map { it.toDomain() } }

    override fun getRecentByChildId(childId: String, limit: Int): Flow<List<PracticeAttempt>> =
        dao.getRecentByChildId(childId, limit).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(attempt: PracticeAttempt) =
        dao.insert(attempt.toEntity())
}
