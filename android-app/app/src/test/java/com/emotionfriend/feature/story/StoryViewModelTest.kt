package com.emotionfriend.feature.story

import com.emotionfriend.data.repository.StoryRepository
import com.emotionfriend.domain.model.Story
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class StoryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testStories = listOf(
        Story(
            id = "1",
            title = "Story 1",
            content = "Once upon a time...",
            imageName = "story1.jpg",
            order = 1
        ),
        Story(
            id = "2",
            title = "Story 2",
            content = "In a land far away...",
            imageName = "story2.jpg",
            order = 2
        ),
        Story(
            id = "3",
            title = "Story 3",
            content = "The adventure begins...",
            imageName = "story3.jpg",
            order = 3
        ),
    )

    private lateinit var storyRepo: FakeStoryRepository
    private lateinit var viewModel: StoryViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        storyRepo = FakeStoryRepository(testStories)
        viewModel = StoryViewModel(storyRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads all stories on init`() = runTest {
        assertEquals(testStories.size, viewModel.stories.value.size)
    }

    @Test
    fun `current story is set to first story initially`() = runTest {
        val currentStory = viewModel.currentStory.value
        assertNotNull("Current story should be set", currentStory)
        assertEquals("1", currentStory?.id)
        assertEquals("Story 1", currentStory?.title)
    }

    @Test
    fun `can navigate to next story`() = runTest {
        viewModel.nextStory()

        val currentStory = viewModel.currentStory.value
        assertNotNull("Current story should be set", currentStory)
        assertEquals("2", currentStory?.id)
        assertEquals("Story 2", currentStory?.title)
    }

    @Test
    fun `can navigate to previous story`() = runTest {
        viewModel.nextStory()
        viewModel.nextStory()
        viewModel.previousStory()

        val currentStory = viewModel.currentStory.value
        assertEquals("2", currentStory?.id)
    }

    @Test
    fun `cannot navigate beyond last story`() = runTest {
        // Navigate to last story
        viewModel.nextStory()
        viewModel.nextStory()

        // Try to go to next
        viewModel.nextStory()

        val currentStory = viewModel.currentStory.value
        assertEquals("3", currentStory?.id)
    }

    @Test
    fun `cannot navigate before first story`() = runTest {
        // Try to go to previous when at first
        viewModel.previousStory()

        val currentStory = viewModel.currentStory.value
        assertEquals("1", currentStory?.id)
    }

    @Test
    fun `story index is tracked correctly`() = runTest {
        assertEquals(0, viewModel.currentIndex.value)

        viewModel.nextStory()
        assertEquals(1, viewModel.currentIndex.value)

        viewModel.nextStory()
        assertEquals(2, viewModel.currentIndex.value)

        viewModel.previousStory()
        assertEquals(1, viewModel.currentIndex.value)
    }

    @Test
    fun `can jump to specific story`() = runTest {
        viewModel.jumpToStory(2)

        assertEquals(2, viewModel.currentIndex.value)
        assertEquals("3", viewModel.currentStory.value?.id)
    }

    @Test
    fun `reading progress is tracked`() = runTest {
        viewModel.markStoryRead("1")

        assertTrue("Story should be marked as read", viewModel.readStoryIds.value.contains("1"))
    }

    @Test
    fun `multiple stories can be marked as read`() = runTest {
        viewModel.markStoryRead("1")
        viewModel.markStoryRead("2")

        assertEquals(2, viewModel.readStoryIds.value.size)
        assertTrue("Should contain story 1", viewModel.readStoryIds.value.contains("1"))
        assertTrue("Should contain story 2", viewModel.readStoryIds.value.contains("2"))
    }
}

private class FakeStoryRepository(
    stories: List<Story>
) : StoryRepository {
    private val storyFlow = MutableStateFlow(stories)

    override fun getAll(): Flow<List<Story>> = storyFlow
    override suspend fun getById(id: String): Story? = storyFlow.value.find { it.id == id }
    override suspend fun upsertAll(stories: List<Story>) { this.storyFlow.value = stories }
}
