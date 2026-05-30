package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.StoryTable
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbStoryRepository : StoryRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<Story> = dbQuery {
        StoryTable.selectAll().orderBy(StoryTable.sortOrder, SortOrder.ASC).map { it.toStory() }
    }

    override suspend fun getById(id: Int): Story? = dbQuery {
        StoryTable.selectAll().where { StoryTable.id eq id }.singleOrNull()?.toStory()
    }

    override suspend fun create(story: Story): Story = dbQuery {
        val generatedId = StoryTable.insert {
            it[title]     = story.title
            it[content]   = story.content
            it[category]  = story.category
            it[imageUrl]  = story.imageUrl
            it[sortOrder] = story.sortOrder
        }[StoryTable.id]
        story.copy(id = generatedId)
    }

    override suspend fun update(id: Int, story: Story): Story? = dbQuery {
        val updated = StoryTable.update({ StoryTable.id eq id }) {
            it[title]     = story.title
            it[content]   = story.content
            it[category]  = story.category
            it[imageUrl]  = story.imageUrl
            it[sortOrder] = story.sortOrder
        }
        if (updated > 0) story.copy(id = id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        StoryTable.deleteWhere { StoryTable.id eq id } > 0
    }

    private fun ResultRow.toStory() = Story(
        id        = this[StoryTable.id],
        title     = this[StoryTable.title],
        content   = this[StoryTable.content],
        category  = this[StoryTable.category],
        imageUrl  = this[StoryTable.imageUrl],
        sortOrder = this[StoryTable.sortOrder],
    )
}
