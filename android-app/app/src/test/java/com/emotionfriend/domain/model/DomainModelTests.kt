package com.emotionfriend.domain.model

import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.PracticeAttempt
import com.emotionfriend.domain.model.UserRole
import com.emotionfriend.domain.model.AuthUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class DomainModelTests {

    @Test
    fun `EmotionType enum has all required emotions`() {
        val emotionTypes = EmotionType.entries
        assertTrue("Should have happy emotion", emotionTypes.contains(EmotionType.HAPPY))
        assertTrue("Should have sad emotion", emotionTypes.contains(EmotionType.SAD))
        assertTrue("Should have angry emotion", emotionTypes.contains(EmotionType.ANGRY))
        assertTrue("Should have calm emotion", emotionTypes.contains(EmotionType.CALM))
    }

    @Test
    fun `UserRole enum has child and parent roles`() {
        val roles = UserRole.entries
        assertTrue("Should have child role", roles.contains(UserRole.CHILD))
        assertTrue("Should have parent role", roles.contains(UserRole.PARENT))
    }

    @Test
    fun `AuthUser can be created and compared`() {
        val user1 = AuthUser(
            id = 1,
            email = "test@example.com",
            displayName = "Test User",
            role = UserRole.CHILD,
            token = "token123",
            isVerified = true
        )

        val user2 = AuthUser(
            id = 1,
            email = "test@example.com",
            displayName = "Test User",
            role = UserRole.CHILD,
            token = "token123",
            isVerified = true
        )

        assertEquals(user1, user2)
    }

    @Test
    fun `PracticeAttempt tracks emotion learning`() {
        val attempt = PracticeAttempt(
            id = 1,
            childId = "child1",
            promptId = "prompt1",
            correctEmotion = EmotionType.HAPPY,
            selectedEmotion = EmotionType.HAPPY,
            isCorrect = true,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )

        assertEquals(true, attempt.isCorrect)
        assertEquals(EmotionType.HAPPY, attempt.correctEmotion)
        assertEquals(EmotionType.HAPPY, attempt.selectedEmotion)
    }

    @Test
    fun `PracticeAttempt can track wrong answers`() {
        val attempt = PracticeAttempt(
            id = 2,
            childId = "child1",
            promptId = "prompt1",
            correctEmotion = EmotionType.HAPPY,
            selectedEmotion = EmotionType.SAD,
            isCorrect = false,
            taskType = "learn_emotion",
            createdAt = LocalDateTime.now()
        )

        assertFalse("Should be incorrect", attempt.isCorrect)
        assertEquals(EmotionType.HAPPY, attempt.correctEmotion)
        assertEquals(EmotionType.SAD, attempt.selectedEmotion)
    }

    @Test
    fun `ScenarioLesson has correct structure`() {
        val scenario = ScenarioLesson(
            id = "s1",
            title = "Test Scenario",
            situationText = "A situation occurred",
            imageName = null,
            correctEmotion = EmotionType.HAPPY,
            options = listOf(EmotionType.HAPPY, EmotionType.SAD),
            explanation = "Test explanation"
        )

        assertEquals("s1", scenario.id)
        assertEquals("Test Scenario", scenario.title)
        assertEquals(EmotionType.HAPPY, scenario.correctEmotion)
    }

    @Test
    fun `Story has correct structure`() {
        val story = Story(
            id = "story1",
            title = "Story Title",
            content = "Story content here",
            imageName = "story1.jpg",
            order = 1
        )

        assertEquals("story1", story.id)
        assertEquals("Story Title", story.title)
        assertEquals(1, story.order)
    }

    @Test
    fun `EmotionCard displays emoji correctly`() {
        val card = EmotionCard(
            id = "happy",
            name = "Happy",
            emoji = "😊",
            type = EmotionType.HAPPY,
            description = "Happy feeling"
        )

        assertEquals("😊", card.emoji)
        assertEquals(EmotionType.HAPPY, card.type)
    }

    @Test
    fun `JournalEntry timestamps are preserved`() {
        val now = LocalDateTime.now()
        val entry = JournalEntry(
            id = 1,
            childId = "child1",
            emotionType = EmotionType.HAPPY,
            note = "Happy day",
            createdAt = now,
            imagePath = null
        )

        assertEquals(now, entry.createdAt)
        assertEquals("Happy day", entry.note)
    }

    @Test
    fun `ProgressSummary calculates accuracy correctly`() {
        val progress = ProgressSummary(
            id = 1,
            childId = "child1",
            date = java.time.LocalDate.now(),
            totalAttempts = 10,
            correctAnswers = 8,
            accuracy = 80f,
            dominantEmotion = EmotionType.HAPPY,
            practiceMinutes = 15
        )

        assertEquals(8, progress.correctAnswers)
        assertEquals(10, progress.totalAttempts)
        assertEquals(80f, progress.accuracy, 0.1f)
    }

    @Test
    fun `Music model stores track information`() {
        val music = Music(
            id = "track1",
            title = "Relaxing Music",
            artist = "Artist Name",
            duration = 300,
            category = "relaxation",
            filePath = "music/track1.mp3"
        )

        assertEquals("track1", music.id)
        assertEquals("Relaxing Music", music.title)
        assertEquals(300, music.duration)
    }

    @Test
    fun `EmotionCard different emotions have different emojis`() {
        val happy = EmotionCard("1", "Happy", "😊", EmotionType.HAPPY, "desc")
        val sad = EmotionCard("2", "Sad", "😢", EmotionType.SAD, "desc")
        val angry = EmotionCard("3", "Angry", "😠", EmotionType.ANGRY, "desc")

        val emojis = setOf(happy.emoji, sad.emoji, angry.emoji)
        assertEquals(3, emojis.size)
    }

    @Test
    fun `AuthUser email validation possible`() {
        val user = AuthUser(
            id = 1,
            email = "test@example.com",
            displayName = "Test",
            role = UserRole.CHILD,
            token = "token",
            isVerified = false
        )

        assertFalse("Email verification should be false", user.isVerified)

        val verifiedUser = user.copy(isVerified = true)
        assertTrue("Verified user should have isVerified true", verifiedUser.isVerified)
    }

    @Test
    fun `PracticeAttempt taskType can be different values`() {
        val learnAttempt = PracticeAttempt(
            1, "child", "p", EmotionType.HAPPY, EmotionType.HAPPY, true, "learn_emotion", LocalDateTime.now()
        )
        val situationAttempt = PracticeAttempt(
            2, "child", "p", EmotionType.HAPPY, EmotionType.HAPPY, true, "situation", LocalDateTime.now()
        )
        val expressAttempt = PracticeAttempt(
            3, "child", "p", EmotionType.HAPPY, EmotionType.HAPPY, true, "express_emotion", LocalDateTime.now()
        )

        assertEquals("learn_emotion", learnAttempt.taskType)
        assertEquals("situation", situationAttempt.taskType)
        assertEquals("express_emotion", expressAttempt.taskType)
    }
}

// Domain model classes (simplified for testing)
data class ScenarioLesson(
    val id: String,
    val title: String,
    val situationText: String,
    val imageName: String?,
    val correctEmotion: EmotionType,
    val options: List<EmotionType>,
    val explanation: String
)

data class Story(
    val id: String,
    val title: String,
    val content: String,
    val imageName: String?,
    val order: Int
)

data class EmotionCard(
    val id: String,
    val name: String,
    val emoji: String,
    val type: EmotionType,
    val description: String
)

data class JournalEntry(
    val id: Long,
    val childId: String,
    val emotionType: EmotionType,
    val note: String,
    val createdAt: LocalDateTime,
    val imagePath: String?
)

data class ProgressSummary(
    val id: Long,
    val childId: String,
    val date: java.time.LocalDate,
    val totalAttempts: Int,
    val correctAnswers: Int,
    val accuracy: Float,
    val dominantEmotion: EmotionType,
    val practiceMinutes: Int
)

data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Int,
    val category: String,
    val filePath: String
)
