package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.Story
import com.emotionfriend.api.repository.StoryRepository
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FakeStoryRepository : StoryRepository {

    private val stories = CopyOnWriteArrayList(
        mutableListOf(
            Story("story-001", "Cậu bé và cơn tức giận", "Hôm nay ở trường, Nam bị bạn Minh lấy đồ chơi mà không hỏi. Nam đã hít thở sâu và nói chuyện bình tĩnh với bạn.", "anger", null, 1),
            Story("story-002", "Ngày đầu tiên đến trường", "Hôm nay là ngày đầu tiên Linh đến trường mới. Linh cảm thấy lo lắng nhưng dần dần quen với bạn bè mới.", "anxiety", null, 2),
            Story("story-003", "Khi bị điểm kém", "An nhận bài kiểm tra với điểm thấp. Mẹ An đã lắng nghe và an ủi, giúp An cảm thấy tốt hơn.", "sadness", null, 3),
        )
    )

    override suspend fun getAll(): List<Story> = stories.toList()

    override suspend fun getById(id: String): Story? = stories.find { it.id == id }

    override suspend fun create(story: Story): Story {
        val newStory = story.copy(id = story.id.ifBlank { UUID.randomUUID().toString() })
        stories.add(newStory)
        return newStory
    }

    override suspend fun update(id: String, story: Story): Story? {
        val idx = stories.indexOfFirst { it.id == id }
        if (idx < 0) return null
        val updated = story.copy(id = id)
        stories[idx] = updated
        return updated
    }

    override suspend fun delete(id: String): Boolean {
        return stories.removeIf { it.id == id }
    }
}
