package com.emotionfriend.api.repository.db

import com.emotionfriend.api.db.MusicTrackTable
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class DbMusicRepository : MusicRepository {

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun getAll(): List<MusicTrack> = dbQuery {
        MusicTrackTable.selectAll().orderBy(MusicTrackTable.sortOrder, SortOrder.ASC).map { it.toTrack() }
    }

    override suspend fun getById(id: Int): MusicTrack? = dbQuery {
        MusicTrackTable.selectAll().where { MusicTrackTable.id eq id }.singleOrNull()?.toTrack()
    }

    override suspend fun create(track: MusicTrack): MusicTrack = dbQuery {
        val generatedId = MusicTrackTable.insert {
            it[title]     = track.title
            it[artist]    = track.artist
            it[filename]  = track.filename
            it[sortOrder] = track.sortOrder
        }[MusicTrackTable.id]
        track.copy(id = generatedId)
    }

    override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = dbQuery {
        val updated = MusicTrackTable.update({ MusicTrackTable.id eq id }) {
            it[title]     = track.title
            it[artist]    = track.artist
            it[filename]  = track.filename
            it[sortOrder] = track.sortOrder
        }
        if (updated > 0) track.copy(id = id) else null
    }

    override suspend fun delete(id: Int): Boolean = dbQuery {
        MusicTrackTable.deleteWhere { MusicTrackTable.id eq id } > 0
    }

    private fun ResultRow.toTrack() = MusicTrack(
        id        = this[MusicTrackTable.id],
        title     = this[MusicTrackTable.title],
        artist    = this[MusicTrackTable.artist],
        filename  = this[MusicTrackTable.filename],
        sortOrder = this[MusicTrackTable.sortOrder],
    )
}
