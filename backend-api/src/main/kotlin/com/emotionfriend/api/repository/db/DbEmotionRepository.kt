package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.EmotionCardTable
import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.repository.EmotionRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbEmotionRepository : EmotionRepository {
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<EmotionCard> = dbQuery {
        EmotionCardTable
            .selectAll()
            .orderBy(EmotionCardTable.sortOrder, SortOrder.ASC)
            .map { it.toEmotionCard() }
    }

    override suspend fun getById(id: Int): EmotionCard? = dbQuery {
        EmotionCardTable
            .selectAll()
            .where { EmotionCardTable.id eq id }
            .singleOrNull()
            ?.toEmotionCard()
    }

    private fun ResultRow.toEmotionCard(): EmotionCard = EmotionCard(
        id = this[EmotionCardTable.id],
        emotionType = EmotionType.valueOf(this[EmotionCardTable.emotionType]),
        emoji = this[EmotionCardTable.emoji],
        label = this[EmotionCardTable.label],
        description = this[EmotionCardTable.description],
    )
}
