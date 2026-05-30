package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.ScenarioLessonTable
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = dbQuery {
        ScenarioLessonTable.insert {
            it[id]           = lesson.id
            it[title]        = lesson.title
            it[situation]    = lesson.situation
            it[options]      = json.encodeToString(lesson.options)
            it[correctIndex] = lesson.correctIndex
            it[explanation]  = lesson.explanation
            it[sortOrder]    = lesson.sortOrder
        }
        lesson
    }

    override suspend fun update(id: String, lesson: ScenarioLesson): ScenarioLesson? = dbQuery {
        val updated = ScenarioLessonTable.update({ ScenarioLessonTable.id eq id }) {
            it[title]        = lesson.title
            it[situation]    = lesson.situation
            it[options]      = json.encodeToString(lesson.options)
            it[correctIndex] = lesson.correctIndex
            it[explanation]  = lesson.explanation
            it[sortOrder]    = lesson.sortOrder
        }
        if (updated > 0) lesson.copy(id = id) else null
    }

    override suspend fun delete(id: String): Boolean = dbQuery {
        ScenarioLessonTable.deleteWhere { ScenarioLessonTable.id eq id } > 0
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
            sortOrder    = this[ScenarioLessonTable.sortOrder],
        )
    }
}
