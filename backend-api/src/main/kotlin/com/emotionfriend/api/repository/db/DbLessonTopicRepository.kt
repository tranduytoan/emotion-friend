package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.LessonTopicTable
import com.emotionfriend.api.db.ScenarioLessonTable
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.LessonTopicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbLessonTopicRepository : LessonTopicRepository {

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<LessonTopic> = dbQuery {
        LessonTopicTable
            .selectAll()
            .orderBy(LessonTopicTable.sortOrder, SortOrder.ASC)
            .map { it.toTopic() }
    }

    override suspend fun getById(id: Int): LessonTopic? = dbQuery {
        LessonTopicTable
            .selectAll()
            .where { LessonTopicTable.id eq id }
            .singleOrNull()
            ?.toTopic()
    }

    override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = dbQuery {
        ScenarioLessonTable
            .selectAll()
            .where { ScenarioLessonTable.topicId eq topicId }
            .orderBy(ScenarioLessonTable.sortOrder, SortOrder.ASC)
            .map { row ->
                val opts = json.decodeFromString<List<String>>(row[ScenarioLessonTable.options])
                ScenarioLesson(
                    id             = row[ScenarioLessonTable.id],
                    title          = row[ScenarioLessonTable.title],
                    situation      = row[ScenarioLessonTable.situation],
                    options        = opts,
                    correctEmotion = row[ScenarioLessonTable.correctEmotion],
                    explanation    = row[ScenarioLessonTable.explanation],
                    sortOrder      = row[ScenarioLessonTable.sortOrder],
                    topicId        = row[ScenarioLessonTable.topicId],
                )
            }
    }

    override suspend fun create(topic: LessonTopic): LessonTopic = dbQuery {
        val generatedId = LessonTopicTable.insert {
            it[title]       = topic.title
            it[description] = topic.description
            it[difficulty]  = topic.difficulty
            it[sortOrder]   = topic.sortOrder
        }[LessonTopicTable.id]
        topic.copy(id = generatedId)
    }

    override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = dbQuery {
        val updated = LessonTopicTable.update({ LessonTopicTable.id eq id }) {
            it[title]       = topic.title
            it[description] = topic.description
            it[difficulty]  = topic.difficulty
            it[sortOrder]   = topic.sortOrder
        }
        if (updated > 0) topic.copy(id = id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        LessonTopicTable.deleteWhere { LessonTopicTable.id eq id } > 0
    }

    private fun ResultRow.toTopic() = LessonTopic(
        id          = this[LessonTopicTable.id],
        title       = this[LessonTopicTable.title],
        description = this[LessonTopicTable.description],
        difficulty  = this[LessonTopicTable.difficulty],
        sortOrder   = this[LessonTopicTable.sortOrder],
    )
}
