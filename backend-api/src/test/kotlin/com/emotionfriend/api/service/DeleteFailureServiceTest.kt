package com.emotionfriend.api.service

import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteFailureServiceTest {
    private class FakeTopicRepo : LessonTopicRepository {
        override suspend fun getAll(): List<LessonTopic> = emptyList()
        override suspend fun getById(id: Int): LessonTopic? = null
        override suspend fun getScenariosForTopic(topicId: Int): List<ScenarioLesson> = emptyList()
        override suspend fun create(topic: LessonTopic): LessonTopic = topic
        override suspend fun update(id: Int, topic: LessonTopic): LessonTopic? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = emptyList()
        override suspend fun getById(id: Int): ScenarioLesson? = null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = emptyList()
        override suspend fun getById(id: Int): Story? = null
        override suspend fun create(story: Story): Story = story
        override suspend fun update(id: Int, story: Story): Story? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = emptyList()
        override suspend fun getById(id: Int): MusicTrack? = null
        override suspend fun create(track: MusicTrack): MusicTrack = track
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = null
        override suspend fun delete(id: Int): Boolean = false
    }

    @Test
    fun `lesson topic delete returns false when missing`() {
        val service = LessonTopicService(FakeTopicRepo())
        assertEquals(false, service.delete(999))
    }

    @Test
    fun `scenario delete returns false when missing`() {
        val service = ScenarioService(FakeScenarioRepo())
        assertEquals(false, service.delete(999))
    }

    @Test
    fun `story delete returns false when missing`() {
        val service = StoryService(FakeStoryRepo())
        assertEquals(false, service.delete(999))
    }

    @Test
    fun `music delete returns false when missing`() {
        val service = MusicService(FakeMusicRepo())
        assertEquals(false, service.delete(999))
    }
}
