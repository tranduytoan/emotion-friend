package com.emotionfriend.api.service

import com.emotionfriend.api.model.Story
import com.emotionfriend.api.repository.StoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StoryServiceTest {
    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(
            Story(id = 1, title = "Câu chuyện 1", content = "Nội dung", category = "Truyện"),
        )

        override suspend fun getById(id: Int): Story? = if (id == 1) {
            Story(id = 1, title = "Câu chuyện 1", content = "Nội dung", category = "Truyện")
        } else null

        override suspend fun create(story: Story): Story = story.copy(id = 3)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 1) story.copy(id = 1) else null
        override suspend fun delete(id: Int): Boolean = id == 1
    }

    private val service = StoryService(FakeStoryRepo())

    @Test
    fun `getAll returns stories list`() {
        assertEquals(1, service.getAll().size)
    }

    @Test
    fun `getById throws when story missing`() {
        assertFailsWith<NoSuchElementException> { service.getById(99) }
    }

    @Test
    fun `getById returns story details`() {
        val story = service.getById(1)
        assertEquals("Câu chuyện 1", story.title)
    }

    @Test
    fun `create returns story with assigned id`() {
        val created = service.create(Story(title = "New", content = "Text", category = "Cat"))
        assertEquals(3, created.id)
    }

    @Test
    fun `update returns story when exists`() {
        val updated = service.update(1, Story(id = 1, title = "Updated", content = "Text", category = "Cat"))
        assertEquals(1, updated.id)
        assertEquals("Updated", updated.title)
    }

    @Test
    fun `delete returns true for existing story`() {
        assertEquals(true, service.delete(1))
    }
}
