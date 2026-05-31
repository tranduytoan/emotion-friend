package com.emotionfriend.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for the [stories] table.
 *
 * [image1]..[image4] are drawable resource names (without the file extension)
 * so the UI can look them up via [context.resources.getIdentifier] or simply
 * use a fixed placeholder if the name doesn't resolve.
 */
@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val content: String,
    val image1: String = "",
    val image2: String = "",
    val image3: String = "",
    val image4: String = "",
    val category: String = "DEFAULT",
    val imageFolder: String? = null,
)
