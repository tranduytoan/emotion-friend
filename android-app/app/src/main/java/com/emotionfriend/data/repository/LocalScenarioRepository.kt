package com.emotionfriend.data.repository

import com.emotionfriend.data.local.ScenarioLessonDao
import com.emotionfriend.data.mapper.toDomain
import com.emotionfriend.data.mapper.toEntity
import com.emotionfriend.domain.model.ScenarioLesson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalScenarioRepository @Inject constructor(
    private val dao: ScenarioLessonDao
) : ScenarioRepository {

    override fun getAll(): Flow<List<ScenarioLesson>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): ScenarioLesson? =
        dao.getById(id)?.toDomain()

    override suspend fun upsertAll(lessons: List<ScenarioLesson>) =
        dao.upsertAll(lessons.map { it.toEntity() })
}
