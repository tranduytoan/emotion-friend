package com.emotionfriend.api.service

import com.emotionfriend.api.model.EmotionCard
import com.emotionfriend.api.model.EmotionType
import com.emotionfriend.api.model.JournalEntry
import com.emotionfriend.api.model.LessonTopic
import com.emotionfriend.api.model.MusicTrack
import com.emotionfriend.api.model.PracticeAttempt
import com.emotionfriend.api.model.ProgressSummary
import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.model.Story
import com.emotionfriend.api.model.AuthenticatedUser
import com.emotionfriend.api.repository.EmotionRepository
import com.emotionfriend.api.repository.JournalRepository
import com.emotionfriend.api.repository.LessonTopicRepository
import com.emotionfriend.api.repository.MusicRepository
import com.emotionfriend.api.repository.PracticeRepository
import com.emotionfriend.api.repository.ProgressRepository
import com.emotionfriend.api.repository.ScenarioRepository
import com.emotionfriend.api.repository.StoryRepository
import com.emotionfriend.api.repository.AuthRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ServiceCoverageExtraTest {
    private class FakeEmotionRepo : EmotionRepository {
        override suspend fun getAll(): List<EmotionCard> = listOf(EmotionCard(id = 2, name = "Buồn", type = "SAD", description = "Buồn"))
        override suspend fun getById(id: Int): EmotionCard? = if (id == 2) EmotionCard(id = 2, name = "Buồn", type = "SAD", description = "Buồn") else null
    }

    private class FakeScenarioRepo : ScenarioRepository {
        override suspend fun getAll(): List<ScenarioLesson> = listOf(ScenarioLesson(id = 20, title = "Tình huống 20", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok"))
        override suspend fun getById(id: Int): ScenarioLesson? = if (id == 20) ScenarioLesson(id = 20, title = "Tình huống 20", situation = "...", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok") else null
        override suspend fun create(lesson: ScenarioLesson): ScenarioLesson = lesson.copy(id = 21)
        override suspend fun update(id: Int, lesson: ScenarioLesson): ScenarioLesson? = if (id == 20) lesson.copy(id = 20) else null
        override suspend fun delete(id: Int): Boolean = id == 20
    }

    private class FakeStoryRepo : StoryRepository {
        override suspend fun getAll(): List<Story> = listOf(Story(id = 11, title = "Story 11", content = "Once", category = "X"))
        override suspend fun getById(id: Int): Story? = if (id == 11) Story(id = 11, title = "Story 11", content = "Once", category = "X") else null
        override suspend fun create(story: Story): Story = story.copy(id = 12)
        override suspend fun update(id: Int, story: Story): Story? = if (id == 11) story.copy(id = 11) else null
        override suspend fun delete(id: Int): Boolean = id == 11
    }

    private class FakeMusicRepo : MusicRepository {
        override suspend fun getAll(): List<MusicTrack> = listOf(MusicTrack(id = 7, title = "Track 7", artist = "Artist 7", filename = "track7.mp3"))
        override suspend fun getById(id: Int): MusicTrack? = if (id == 7) MusicTrack(id = 7, title = "Track 7", artist = "Artist 7", filename = "track7.mp3") else null
        override suspend fun create(track: MusicTrack): MusicTrack = track.copy(id = 8)
        override suspend fun update(id: Int, track: MusicTrack): MusicTrack? = if (id == 7) track.copy(id = 7) else null
        override suspend fun delete(id: Int): Boolean = id == 7
    }

    private class FakePracticeRepo : PracticeRepository {
        override suspend fun create(attempt: PracticeAttempt): PracticeAttempt = attempt.copy(id = 77)
        override suspend fun getAllByChildId(childId: String): List<PracticeAttempt> = listOf(PracticeAttempt(id = 77, childId = childId, scenarioId = 1, isCorrect = true, promptEmotion = "CALM"))
    }

    private class FakeJournalRepo : JournalRepository {
        override suspend fun create(entry: JournalEntry): JournalEntry = entry.copy(id = 88, createdAt = "2026-06-03T15:00:00Z")
        override suspend fun getAllByChildId(childId: String): List<JournalEntry> = listOf(JournalEntry(id = 88, childId = childId, emotionType = EmotionType.TIRED, note = "Nhật ký", createdAt = "2026-06-03T15:00:00Z"))
    }

    private class FakeProgressRepo : ProgressRepository {
        override suspend fun getProgressSummary(childId: String): ProgressSummary = ProgressSummary(childId = childId, completedLessons = 4, accuracyRate = 0.78f, journalCount = 2, mostMistakenEmotion = EmotionType.SAD)
    }

    private class FakeAuthRepo : AuthRepository {
        override suspend fun authenticate(email: String, password: String): AuthenticatedUser? = if (email == "existing@service.test" && password == "pass") AuthenticatedUser(id = 99, email = email, displayName = "Existing") else null
        override suspend fun findByEmail(email: String): AuthenticatedUser? = if (email == "existing@service.test") AuthenticatedUser(id = 99, email = email, displayName = "Existing", isVerified = false) else null
        override suspend fun register(email: String, password: String, displayName: String): AuthenticatedUser = AuthenticatedUser(id = 100, email = email, displayName = displayName)
    }

    @Test
    fun `scenario service getAll returns all scenarios`() {
        val service = ScenarioService(FakeScenarioRepo())
        val all = service.getAll()
        assertEquals(1, all.size)
    }

    @Test
    fun `scenario service getById throws when missing`() {
        val service = ScenarioService(FakeScenarioRepo())
        assertFailsWith<NoSuchElementException> { service.getById(900) }
    }

    @Test
    fun `scenario service create returns generated id`() {
        val service = ScenarioService(FakeScenarioRepo())
        val created = service.create(ScenarioLesson(title = "New", situation = "Test", options = listOf("SAD"), correctEmotion = "SAD", explanation = "Ok"))
        assertEquals(21, created.id)
    }

    @Test
    fun `scenario service update throws when missing`() {
        val service = ScenarioService(FakeScenarioRepo())
        assertFailsWith<NoSuchElementException> { service.update(999, ScenarioLesson(title = "X", situation = "x", options = listOf("HAPPY"), correctEmotion = "HAPPY", explanation = "Ok")) }
    }

    @Test
    fun `story service getById throws when missing`() {
        val service = StoryService(FakeStoryRepo())
        assertFailsWith<NoSuchElementException> { service.getById(999) }
    }

    @Test
    fun `story service update returns same id when found`() {
        val service = StoryService(FakeStoryRepo())
        val updated = service.update(11, Story(id = 11, title = "Updated", content = "Changed", category = "X"))
        assertEquals(11, updated.id)
    }

    @Test
    fun `music service getById throws when missing`() {
        val service = MusicService(FakeMusicRepo())
        assertFailsWith<NoSuchElementException> { service.getById(999) }
    }

    @Test
    fun `music service delete returns false when missing`() {
        val service = MusicService(FakeMusicRepo())
        assertEquals(false, service.delete(999))
    }

    @Test
    fun `emotion service getById throws when missing`() {
        val service = EmotionService(FakeEmotionRepo())
        assertFailsWith<NoSuchElementException> { service.getById(999) }
    }

    @Test
    fun `jonral service create persists entry`() {
        val service = JournalService(FakeJournalRepo())
        val created = service.create(JournalEntry(childId = "x", emotionType = EmotionType.HAPPY, note = "Note"))
        assertEquals(88, created.id)
        assertEquals("2026-06-03T15:00:00Z", created.createdAt)
    }

    @Test
    fun `practice service create returns stored id`() {
        val service = PracticeService(FakePracticeRepo())
        val created = service.create(PracticeAttempt(childId = "x", scenarioId = 1, isCorrect = false, promptEmotion = "HAPPY"))
        assertEquals(77, created.id)
    }

    @Test
    fun `progress service returns summary details`() {
        val service = ProgressService(FakeProgressRepo())
        val summary = service.getProgress("child-z")
        assertEquals(4, summary.completedLessons)
        assertEquals(0.78f, summary.accuracyRate)
        assertEquals(EmotionType.SAD, summary.mostMistakenEmotion)
    }

    @Test
    fun `lesson topic service update throws when missing`() {
        val service = LessonTopicService(object : LessonTopicRepository {
            override suspend fun getAll() = emptyList<LessonTopic>()
            override suspend fun getById(id: Int) = null
            override suspend fun getScenariosForTopic(topicId: Int) = emptyList<ScenarioLesson>()
            override suspend fun create(topic: LessonTopic) = topic
            override suspend fun update(id: Int, topic: LessonTopic) = null
            override suspend fun delete(id: Int) = false
        })
        assertFailsWith<NoSuchElementException> { service.update(5, LessonTopic(title = "Missing")) }
    }

    @Test
    fun `auth service login rejects blank email and blank password`() {
        val service = com.emotionfriend.api.service.AuthService(FakeAuthRepo())
        assertFailsWith<IllegalArgumentException> { service.login("", "pass") }
        assertFailsWith<IllegalArgumentException> { service.login("existing@service.test", "") }
    }

    @Test
    fun `auth service verifyEmail returns verified user and rejects missing email`() {
        val service = com.emotionfriend.api.service.AuthService(FakeAuthRepo())
        val verified = service.verifyEmail("existing@service.test", "123456")
        assertTrue(verified.isVerified)
        assertFailsWith<IllegalArgumentException> { service.verifyEmail("missing@service.test", "123456") }
    }

    @Test
    fun `auth service register rejects blank display name`() {
        val service = com.emotionfriend.api.service.AuthService(FakeAuthRepo())
        assertFailsWith<IllegalArgumentException> { service.register("new@example.com", "pass", "") }
    }
}
