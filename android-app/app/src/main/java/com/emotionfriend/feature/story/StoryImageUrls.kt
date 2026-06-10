package com.emotionfriend.feature.story

import com.emotionfriend.core.config.AppConfig
import com.emotionfriend.domain.model.Story

private const val STORY_PAGE_COUNT = 4

internal fun Story.storyImageBaseUrl(): String? {
    val folder = imageFolder?.trim().orEmpty()
    if (folder.isEmpty()) return null
    return "${AppConfig.BASE_URL.trimEnd('/')}/img/stories/$folder"
}

internal fun Story.storyCoverUrl(): String? =
    storyImageBaseUrl()?.let { "$it/1.png" }

internal fun Story.storyPageUrls(): List<String> =
    storyImageBaseUrl()?.let { baseUrl ->
        (1..STORY_PAGE_COUNT).map { index -> "$baseUrl/$index.png" }
    } ?: emptyList()

internal val storyImagePageCount: Int
    get() = STORY_PAGE_COUNT
