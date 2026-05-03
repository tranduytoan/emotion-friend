package com.emotionfriend.data.repository

import com.emotionfriend.data.local.EmotionCardDao
import com.emotionfriend.data.mapper.toDomain
import com.emotionfriend.data.mapper.toEntity
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalEmotionRepository @Inject constructor(
    private val dao: EmotionCardDao
) : EmotionRepository {

    override fun getAll(): Flow<List<EmotionCard>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): EmotionCard? =
        dao.getById(id)?.toDomain()

    override suspend fun upsertAll(cards: List<EmotionCard>) =
        dao.upsertAll(cards.map { it.toEntity() })

    override fun getByType(type: EmotionType): Flow<List<EmotionCard>> =
        dao.getAll().map { list -> list.filter { it.type == type }.map { it.toDomain() } }
}
