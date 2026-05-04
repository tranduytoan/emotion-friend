package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.ScenarioLessonTable
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbScenarioRepository : ScenarioRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<ScenarioLesson> = dbQuery {
        ScenarioLessonTable
            .selectAll()
            .orderBy(ScenarioLessonTable.sortOrder, SortOrder.ASC)
            .map { it.toScenarioLesson() }
    }

    override suspend fun getById(id: String): ScenarioLesson? = dbQuery {
        ScenarioLessonTable
            .selectAll()
            .where { ScenarioLessonTable.id eq id }
            .singleOrNull()
            ?.toScenarioLesson()
    }

    private fun ResultRow.toScenarioLesson(): ScenarioLesson {
        val optionsJson = this[ScenarioLessonTable.options]
        val options = json.decodeFromString<List<String>>(optionsJson)
        return ScenarioLesson(
            id           = this[ScenarioLessonTable.id],
            title        = this[ScenarioLessonTable.title],
            situation    = this[ScenarioLessonTable.situation],
            options      = options,
            correctIndex = this[ScenarioLessonTable.correctIndex],
            explanation  = this[ScenarioLessonTable.explanation],
        )
    }
}
