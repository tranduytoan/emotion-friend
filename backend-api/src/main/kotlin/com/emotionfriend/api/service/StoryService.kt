package com.emotionfriend.api.service

import com.emotionfriend.api.model.Story
import com.emotionfriend.api.repository.StoryRepository

class StoryService(private val repo: StoryRepository) {
    suspend fun getAll(): List<Story> = repo.getAll()
    suspend fun getById(id: Int): Story = repo.getById(id) ?: throw NoSuchElementException("Story '$id' not found")
    suspend fun create(story: Story): Story = repo.create(story)
    suspend fun update(id: Int, story: Story): Story = repo.update(id, story) ?: throw NoSuchElementException("Story '$id' not found")
    suspend fun delete(id: Int): Boolean = repo.delete(id)
}
