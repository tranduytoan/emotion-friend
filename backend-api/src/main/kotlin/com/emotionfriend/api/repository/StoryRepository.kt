package com.emotionfriend.api.repository

import com.emotionfriend.api.model.Story

interface StoryRepository {
    suspend fun getAll(): List<Story>
    suspend fun getById(id: String): Story?
    suspend fun create(story: Story): Story
    suspend fun update(id: String, story: Story): Story?
    suspend fun delete(id: String): Boolean
}
