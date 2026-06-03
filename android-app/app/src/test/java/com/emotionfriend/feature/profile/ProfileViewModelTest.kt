package com.emotionfriend.feature.profile

import com.emotionfriend.data.auth.SessionManager
import com.emotionfriend.data.repository.EmotionRepository
import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.EmotionCard
import com.emotionfriend.domain.model.EmotionType
import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testUser = AuthUser(
        id = 1,
        email = "test@example.com",
        displayName = "Test User",
        role = UserRole.CHILD,
        token = "token123",
        isVerified = true
    )

    private lateinit var sessionManager: FakeSessionManager
    private lateinit var emotionRepo: FakeEmotionRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        sessionManager = FakeSessionManager()
        emotionRepo = FakeEmotionRepository()
        viewModel = ProfileViewModel(sessionManager, emotionRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `current user is retrieved from session manager`() = runTest {
        sessionManager._sessionFlow.value = testUser

        val user = viewModel.currentUser.value
        assertNotNull("Current user should not be null", user)
        assertEquals("test@example.com", user?.email)
        assertEquals("Test User", user?.displayName)
    }

    @Test
    fun `display name can be updated`() = runTest {
        viewModel.updateDisplayName("New Name")

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("New Name", viewModel.displayName.value)
    }

    @Test
    fun `favorite emotion can be selected`() = runTest {
        val emotion = EmotionCard(
            id = "1",
            name = "Happy",
            emoji = "😊",
            type = EmotionType.HAPPY,
            description = "Happy emotion"
        )

        viewModel.selectFavoriteEmotion(emotion)

        val favorite = viewModel.favoriteEmotion.value
        assertNotNull("Favorite emotion should be set", favorite)
        assertEquals(emotion.type, favorite?.type)
    }

    @Test
    fun `loading state is updated appropriately`() = runTest {
        assertFalse("Should not be loading initially", viewModel.isLoading.value)

        viewModel.loadUserProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Should finish loading", viewModel.isLoading.value)
    }

    @Test
    fun `session can be cleared on logout`() = runTest {
        sessionManager._sessionFlow.value = testUser
        var userBefore = viewModel.currentUser.value
        assertNotNull("User should be set before logout", userBefore)

        sessionManager._sessionFlow.value = null

        val userAfter = viewModel.currentUser.value
        assertEquals(null, userAfter)
    }

    @Test
    fun `error message is cleared when user resets it`() = runTest {
        viewModel.updateDisplayName("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Simulate error state
        var errorMessage = viewModel.errorMessage.value
        // Error should be set for empty display name

        viewModel.clearError()

        errorMessage = viewModel.errorMessage.value
        assertEquals(null, errorMessage)
    }

    @Test
    fun `profile role is read from current user`() = runTest {
        sessionManager._sessionFlow.value = testUser

        assertEquals(UserRole.CHILD, viewModel.currentUser.value?.role)
    }
}

private class FakeSessionManager : SessionManager {
    val _sessionFlow = MutableStateFlow<AuthUser?>(null)
    override val sessionFlow: StateFlow<AuthUser?> = _sessionFlow
}

private class FakeEmotionRepository : EmotionRepository {
    override fun getAll(): Flow<List<EmotionCard>> = MutableStateFlow(emptyList())
    override suspend fun getById(id: String): EmotionCard? = null
    override suspend fun upsertAll(cards: List<EmotionCard>) {}
    override fun getByType(type: EmotionType): Flow<List<EmotionCard>> = MutableStateFlow(emptyList())
}
