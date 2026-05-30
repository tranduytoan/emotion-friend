package com.emotionfriend.api.repository

import com.emotionfriend.api.model.Story

interface StoryRepository {
    suspend fun getAll(): List<Story>
    suspend fun getById(id: Int): Story?
    suspend fun create(story: Story): Story
    suspend fun update(id: Int, story: Story): Story?
    suspend fun delete(id: Int): Boolean
}
