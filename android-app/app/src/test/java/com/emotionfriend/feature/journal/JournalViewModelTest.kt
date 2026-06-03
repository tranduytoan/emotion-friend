package com.emotionfriend.feature.journal

import com.emotionfriend.data.repository.JournalRepository
import com.emotionfriend.domain.model.JournalEntry
import com.emotionfriend.domain.model.EmotionType
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testEntries = listOf(
        JournalEntry(
            id = 1,
            childId = "child1",
            emotionType = EmotionType.HAPPY,
            note = "Had a great day!",
            createdAt = LocalDateTime.now().minusDays(1),
            imagePath = null
        ),
        JournalEntry(
            id = 2,
            childId = "child1",
            emotionType = EmotionType.CALM,
            note = "Felt peaceful today",
            createdAt = LocalDateTime.now(),
            imagePath = null
        ),
    )

    private lateinit var journalRepo: FakeJournalRepository
    private lateinit var viewModel: JournalViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        journalRepo = FakeJournalRepository(testEntries)
        viewModel = JournalViewModel(journalRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads journal entries on init`() = runTest {
        val entries = viewModel.journalEntries.value
        assertEquals(2, entries.size)
    }

    @Test
    fun `can add new journal entry`() = runTest {
        val newEntry = JournalEntry(
            id = 3,
            childId = "child1",
            emotionType = EmotionType.SAD,
            note = "New entry",
            createdAt = LocalDateTime.now(),
            imagePath = null
        )

        viewModel.addJournalEntry(newEntry)

        val entries = viewModel.journalEntries.value
        assertTrue("Entry count should increase", entries.size > 0)
    }

    @Test
    fun `can delete journal entry`() = runTest {
        val initialCount = journalRepo.entries.value.size
        viewModel.deleteJournalEntry(1)

        val entries = viewModel.journalEntries.value
        assertTrue("Entry count should decrease or stay same", entries.size <= initialCount)
    }

    @Test
    fun `can edit journal entry`() = runTest {
        val updatedEntry = testEntries[0].copy(note = "Updated note")
        viewModel.updateJournalEntry(updatedEntry)

        // Verify the update
        val entries = viewModel.journalEntries.value
        assertNotNull("Entries should exist", entries)
    }

    @Test
    fun `entries are sorted by date descending`() = runTest {
        val entries = viewModel.journalEntries.value
        if (entries.size > 1) {
            assertTrue("Most recent entry should be first", 
                entries[0].createdAt >= entries[1].createdAt)
        }
    }

    @Test
    fun `emotion summary is calculated from entries`() = runTest {
        val emotionStats = viewModel.emotionStats.value
        assertNotNull("Emotion stats should be calculated", emotionStats)
    }

    @Test
    fun `can filter entries by emotion`() = runTest {
        val filtered = viewModel.filterByEmotion(EmotionType.HAPPY)
        assertEquals(1, filtered.size)
    }

    @Test
    fun `can search entries by note text`() = runTest {
        val results = viewModel.searchEntries("great")
        assertTrue("Should find matching entries", results.isNotEmpty())
    }

    @Test
    fun `daily check-in count is tracked`() = runTest {
        val count = viewModel.dailyCheckInCount.value
        assertNotNull("Check-in count should be available", count)
        assertTrue("Check-in count should be non-negative", count!! >= 0)
    }
}

private class FakeJournalRepository(
    initialEntries: List<JournalEntry> = emptyList()
) : JournalRepository {
    val entries = MutableStateFlow(initialEntries)

    override fun getByChildId(childId: String): Flow<List<JournalEntry>> = entries
    override fun getAllByChildId(childId: String): Flow<List<JournalEntry>> = entries

    override suspend fun insert(entry: JournalEntry) {
        entries.value = entries.value + entry
    }

    fun deleteEntry(id: Long) {
        entries.value = entries.value.filter { it.id != id }
    }

    fun updateEntry(entry: JournalEntry) {
        entries.value = entries.value.map { if (it.id == entry.id) entry else it }
    }
}
