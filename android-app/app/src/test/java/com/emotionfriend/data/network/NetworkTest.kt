package com.emotionfriend.data.network

import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkSimulationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var mockApi: MockApi

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApi = MockApi()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login request succeeds with valid credentials`() = runTest {
        val result = mockApi.login("test@example.com", "password123")

        assertNotNull("Login should succeed", result)
        assertEquals("test@example.com", result?.email)
    }

    @Test
    fun `login request fails with invalid credentials`() = runTest {
        val result = mockApi.login("test@example.com", "wrongpassword")

        assertNull("Login should fail with wrong password", result)
    }

    @Test
    fun `register request succeeds with valid data`() = runTest {
        val result = mockApi.register(
            email = "newuser@example.com",
            password = "password123",
            displayName = "New User",
            role = UserRole.CHILD
        )

        assertNotNull("Register should succeed", result)
        assertEquals("newuser@example.com", result?.email)
        assertEquals("New User", result?.displayName)
    }

    @Test
    fun `register fails with existing email`() = runTest {
        mockApi.register(
            email = "existing@example.com",
            password = "pass123",
            displayName = "User1",
            role = UserRole.CHILD
        )

        val result = mockApi.register(
            email = "existing@example.com",
            password = "pass456",
            displayName = "User2",
            role = UserRole.CHILD
        )

        assertNull("Register should fail for existing email", result)
    }

    @Test
    fun `fetch emotions returns list`() = runTest {
        val emotions = mockApi.getEmotions()

        assertNotNull("Should fetch emotions", emotions)
        assertTrue("Should have emotions", emotions!!.isNotEmpty())
    }

    @Test
    fun `fetch specific emotion by id`() = runTest {
        val emotion = mockApi.getEmotionById("happy")

        assertNotNull("Should fetch emotion", emotion)
        assertEquals(EmotionType.HAPPY, emotion?.type)
    }

    @Test
    fun `fetch nonexistent emotion returns null`() = runTest {
        val emotion = mockApi.getEmotionById("nonexistent")

        assertNull("Should return null for nonexistent emotion", emotion)
    }

    @Test
    fun `api error is handled gracefully`() = runTest {
        mockApi.simulateError = true

        val result = mockApi.login("test@example.com", "password123")

        assertNull("Should handle API error", result)
        mockApi.simulateError = false
    }

    @Test
    fun `api timeout is handled`() = runTest {
        val start = System.currentTimeMillis()
        mockApi.simulateTimeout = true

        val result = mockApi.login("test@example.com", "password123")

        assertNull("Should handle timeout", result)
        mockApi.simulateTimeout = false
    }

    @Test
    fun `response data validation`() = runTest {
        val user = mockApi.login("test@example.com", "password123")

        assertNotNull("User should be valid", user)
        assertNotNull("Token should be present", user?.token)
        assertTrue("Email should not be empty", user?.email?.isNotEmpty() == true)
    }

    @Test
    fun `batch request succeeds`() = runTest {
        val ids = listOf("happy", "sad", "angry")
        val results = mockApi.getEmotionsByIds(ids)

        assertEquals(3, results.size)
    }

    @Test
    fun `empty request body is handled`() = runTest {
        val result = mockApi.register("", "", "", UserRole.CHILD)

        assertNull("Should handle empty request", result)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ValidationTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email validation accepts valid emails`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.co.uk",
            "user+tag@example.com"
        )

        for (email in validEmails) {
            assertTrue("Should validate $email", isValidEmail(email))
        }
    }

    @Test
    fun `email validation rejects invalid emails`() {
        val invalidEmails = listOf(
            "invalid",
            "@example.com",
            "test@",
            "test @example.com"
        )

        for (email in invalidEmails) {
            assertFalse("Should reject $email", isValidEmail(email))
        }
    }

    @Test
    fun `password validation requires minimum length`() {
        assertFalse("Password too short", isValidPassword("123"))
        assertTrue("Password valid", isValidPassword("password123"))
    }

    @Test
    fun `display name validation`() {
        assertTrue("Valid name", isValidDisplayName("John Doe"))
        assertFalse("Empty name invalid", isValidDisplayName(""))
        assertFalse("Too long name invalid", isValidDisplayName("A".repeat(100)))
    }

    @Test
    fun `emotion data validation`() {
        val validEmotion = EmotionCard(
            id = "1",
            name = "Happy",
            emoji = "😊",
            type = EmotionType.HAPPY,
            description = "Happy emotion"
        )

        assertTrue("Valid emotion", isValidEmotion(validEmotion))
    }

    @Test
    fun `emotion with missing fields is invalid`() {
        val invalidEmotion = EmotionCard(
            id = "",
            name = "",
            emoji = "",
            type = EmotionType.HAPPY,
            description = ""
        )

        assertFalse("Invalid emotion with empty fields", isValidEmotion(invalidEmotion))
    }
}

// Mock API implementation

private class MockApi {
    private val users = mutableMapOf<String, AuthUser>()
    private val emotions = listOf(
        EmotionCard("happy", "Happy", "😊", EmotionType.HAPPY, "Happy emotion"),
        EmotionCard("sad", "Sad", "😢", EmotionType.SAD, "Sad emotion"),
        EmotionCard("angry", "Angry", "😠", EmotionType.ANGRY, "Angry emotion"),
    )

    var simulateError = false
    var simulateTimeout = false

    suspend fun login(email: String, password: String): AuthUser? {
        if (simulateError || simulateTimeout) return null
        return if (email == "test@example.com" && password == "password123") {
            AuthUser(1, email, "Test User", UserRole.CHILD, "token123", true)
        } else null
    }

    suspend fun register(
        email: String,
        password: String,
        displayName: String,
        role: UserRole
    ): AuthUser? {
        if (email.isEmpty() || password.isEmpty()) return null
        if (users.containsKey(email)) return null

        val user = AuthUser(
            id = users.size.toLong() + 1,
            email = email,
            displayName = displayName,
            role = role,
            token = "token${users.size}",
            isVerified = false
        )
        users[email] = user
        return user
    }

    suspend fun getEmotions(): List<EmotionCard>? = if (simulateError) null else emotions

    suspend fun getEmotionById(id: String): EmotionCard? = emotions.find { it.id == id }

    suspend fun getEmotionsByIds(ids: List<String>): List<EmotionCard> =
        emotions.filter { it.id in ids }
}

// Validation functions

private fun isValidEmail(email: String): Boolean =
    email.contains("@") && email.contains(".") && email.length > 5

private fun isValidPassword(password: String): Boolean = password.length >= 6

private fun isValidDisplayName(name: String): Boolean =
    name.isNotEmpty() && name.length <= 50

private fun isValidEmotion(emotion: EmotionCard): Boolean =
    emotion.id.isNotEmpty() &&
    emotion.name.isNotEmpty() &&
    emotion.emoji.isNotEmpty() &&
    emotion.description.isNotEmpty()
