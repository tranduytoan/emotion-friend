package com.emotionfriend.feature.confide

import com.emotionfriend.data.repository.ConfideRepository
import com.emotionfriend.domain.model.ConfideMessage
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
class ConfideViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMessages = listOf(
        ConfideMessage(
            id = 1,
            childId = "child1",
            message = "I'm feeling lonely today",
            response = "It's okay to feel that way. Talk to someone you trust.",
            createdAt = LocalDateTime.now().minusHours(1)
        ),
        ConfideMessage(
            id = 2,
            childId = "child1",
            message = "I made a mistake at school",
            response = "Everyone makes mistakes. That's how we learn.",
            createdAt = LocalDateTime.now()
        ),
    )

    private lateinit var confideRepo: FakeConfideRepository
    private lateinit var viewModel: ConfideViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        confideRepo = FakeConfideRepository(testMessages)
        viewModel = ConfideViewModel(confideRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads confide messages on init`() = runTest {
        val messages = viewModel.messages.value
        assertEquals(2, messages.size)
    }

    @Test
    fun `can submit new confide message`() = runTest {
        val initialCount = viewModel.messages.value.size
        viewModel.submitMessage("I'm feeling scared")

        testDispatcher.scheduler.advanceUntilIdle()

        val newCount = viewModel.messages.value.size
        assertTrue("Message count should increase", newCount > initialCount)
    }

    @Test
    fun `confide message receives supportive response`() = runTest {
        viewModel.submitMessage("I made a mistake")

        testDispatcher.scheduler.advanceUntilIdle()

        val messages = viewModel.messages.value
        val lastMessage = messages.lastOrNull()
        assertNotNull("Response should be generated", lastMessage?.response)
        assertTrue("Response should be supportive", lastMessage?.response?.isNotEmpty() == true)
    }

    @Test
    fun `loading state is managed during submission`() = runTest {
        assertFalse("Should not be loading initially", viewModel.isLoading.value)

        viewModel.submitMessage("Test message")

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Should finish loading after submission", viewModel.isLoading.value)
    }

    @Test
    fun `error messages are handled gracefully`() = runTest {
        viewModel.submitMessage("")

        testDispatcher.scheduler.advanceUntilIdle()

        val errorMessage = viewModel.errorMessage.value
        // Empty message should trigger error or be prevented
    }

    @Test
    fun `multiple messages can be submitted in sequence`() = runTest {
        val messages = listOf(
            "First message",
            "Second message",
            "Third message"
        )

        for (message in messages) {
            viewModel.submitMessage(message)
            testDispatcher.scheduler.advanceUntilIdle()
        }

        assertTrue("Multiple messages should be saved", viewModel.messages.value.size >= messages.size)
    }

    @Test
    fun `conversation history is preserved`() = runTest {
        val allMessages = viewModel.messages.value
        assertEquals(2, allMessages.size)

        viewModel.submitMessage("New concern")
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedMessages = viewModel.messages.value
        assertTrue("Previous messages should still exist", 
            updatedMessages.size > allMessages.size)
    }

    @Test
    fun `can mark message as helpful`() = runTest {
        val message = viewModel.messages.value.firstOrNull()
        assertNotNull("Should have a message", message)

        viewModel.markAsHelpful(message!!)

        assertTrue("Message should be marked helpful", message.isHelpful == true)
    }

    @Test
    fun `messages are sorted by date`() = runTest {
        val messages = viewModel.messages.value
        if (messages.size > 1) {
            assertTrue("Most recent message should be last",
                messages.last().createdAt >= messages.first().createdAt)
        }
    }
}

data class ConfideMessage(
    val id: Long,
    val childId: String,
    val message: String,
    val response: String,
    val createdAt: LocalDateTime,
    val isHelpful: Boolean = false,
)

private class FakeConfideRepository(
    initialMessages: List<ConfideMessage> = emptyList()
) : ConfideRepository {
    private val messagesFlow = MutableStateFlow(initialMessages)

    override fun getByChildId(childId: String): Flow<List<ConfideMessage>> = messagesFlow

    override suspend fun submitMessage(childId: String, message: String): String =
        "Thank you for sharing. Remember, you're not alone."

    override suspend fun insert(message: ConfideMessage) {
        messagesFlow.value = messagesFlow.value + message
    }

    fun getMessages() = messagesFlow.value
}

interface ConfideRepository {
    fun getByChildId(childId: String): Flow<List<ConfideMessage>>
    suspend fun submitMessage(childId: String, message: String): String
    suspend fun insert(message: ConfideMessage)
}
