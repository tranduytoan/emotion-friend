package com.emotionfriend.feature.loading

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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

@OptIn(ExperimentalCoroutinesApi::class)
class PaginationTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var paginator: Paginator<String>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val items = (1..100).map { "Item $it" }
        paginator = Paginator(items, pageSize = 10)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `first page is loaded automatically`() {
        val page = paginator.currentPage.value
        assertNotNull("First page should exist", page)
        assertEquals(10, page?.size)
    }

    @Test
    fun `page 1 contains first 10 items`() {
        val page = paginator.currentPage.value
        assertEquals("Item 1", page?.firstOrNull())
        assertEquals("Item 10", page?.lastOrNull())
    }

    @Test
    fun `next page increments page number`() {
        val initialPage = paginator.currentPageNumber.value
        paginator.nextPage()
        val nextPage = paginator.currentPageNumber.value

        assertEquals(initialPage + 1, nextPage)
    }

    @Test
    fun `previous page decrements page number`() {
        paginator.nextPage()
        var currentPage = paginator.currentPageNumber.value
        assertEquals(2, currentPage)

        paginator.previousPage()
        currentPage = paginator.currentPageNumber.value
        assertEquals(1, currentPage)
    }

    @Test
    fun `cannot go before first page`() {
        paginator.previousPage()
        val pageNum = paginator.currentPageNumber.value
        assertEquals(1, pageNum)
    }

    @Test
    fun `cannot go beyond last page`() {
        repeat(15) { paginator.nextPage() }
        val lastPage = paginator.currentPageNumber.value
        assertEquals(10, lastPage)
    }

    @Test
    fun `has more pages indicator`() {
        assertTrue("Should have more", paginator.hasMorePages.value)

        repeat(9) { paginator.nextPage() }
        assertFalse("Should be last page", paginator.hasMorePages.value)
    }

    @Test
    fun `jump to specific page`() {
        paginator.jumpToPage(5)

        val page = paginator.currentPageNumber.value
        assertEquals(5, page)

        val content = paginator.currentPage.value
        assertEquals("Item 41", content?.firstOrNull())
    }

    @Test
    fun `total pages calculated correctly`() {
        val total = paginator.totalPages.value
        assertEquals(10, total)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class InfiniteScrollTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var infiniteScroll: InfiniteScroll<String>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        infiniteScroll = InfiniteScroll(initialItems = (1..20).map { "Item $it" })
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial items are loaded`() {
        val items = infiniteScroll.items.value
        assertEquals(20, items.size)
    }

    @Test
    fun `loading more items appends to list`() = runTest {
        val initialCount = infiniteScroll.items.value.size

        infiniteScroll.loadMore()

        testDispatcher.scheduler.advanceUntilIdle()

        val newCount = infiniteScroll.items.value.size
        assertTrue("Item count should increase", newCount > initialCount)
    }

    @Test
    fun `is loading state is managed`() = runTest {
        assertFalse("Initially not loading", infiniteScroll.isLoading.value)

        infiniteScroll.loadMore()
        // Loading should be true during operation

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Should finish loading", infiniteScroll.isLoading.value)
    }

    @Test
    fun `end of list is detected`() = runTest {
        while (infiniteScroll.hasMore.value) {
            infiniteScroll.loadMore()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        assertFalse("Should reach end", infiniteScroll.hasMore.value)
    }

    @Test
    fun `error during loading is handled`() = runTest {
        infiniteScroll.setError(true)
        infiniteScroll.loadMore()

        testDispatcher.scheduler.advanceUntilIdle()

        val error = infiniteScroll.error.value
        assertNotNull("Error should be recorded", error)
    }

    @Test
    fun `retry after error`() = runTest {
        infiniteScroll.setError(true)
        infiniteScroll.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        infiniteScroll.setError(false)
        infiniteScroll.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = infiniteScroll.error.value
        assertEquals(null, error)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LoadingStateTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var loader: LoadingManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loader = LoadingManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is not loading`() {
        assertFalse("Initially not loading", loader.isLoading.value)
    }

    @Test
    fun `loading state transitions`() = runTest {
        loader.startLoading()
        assertTrue("Should be loading", loader.isLoading.value)

        loader.stopLoading()
        assertFalse("Should stop loading", loader.isLoading.value)
    }

    @Test
    fun `loading progress is tracked`() = runTest {
        loader.setProgress(0)
        assertEquals(0, loader.progress.value)

        loader.setProgress(50)
        assertEquals(50, loader.progress.value)

        loader.setProgress(100)
        assertEquals(100, loader.progress.value)
    }

    @Test
    fun `multiple loading operations are queued`() = runTest {
        loader.startLoading("operation1")
        loader.startLoading("operation2")
        loader.startLoading("operation3")

        val count = loader.activeOperations.value
        assertEquals(3, count)

        loader.stopLoading("operation1")

        val afterStop = loader.activeOperations.value
        assertEquals(2, afterStop)
    }

    @Test
    fun `loading message is displayed`() {
        loader.setLoadingMessage("Fetching data...")

        val message = loader.loadingMessage.value
        assertEquals("Fetching data...", message)
    }

    @Test
    fun `skeleton loading is supported`() {
        loader.enableSkeletonLoading(true)

        assertTrue("Skeleton loading enabled", loader.skeletonLoadingEnabled.value)

        loader.enableSkeletonLoading(false)

        assertFalse("Skeleton loading disabled", loader.skeletonLoadingEnabled.value)
    }
}

// Pagination implementation

private class Paginator<T>(
    private val allItems: List<T>,
    private val pageSize: Int = 10
) {
    val currentPageNumber = MutableStateFlow(1)
    val currentPage = MutableStateFlow<List<T>?>(null)
    val totalPages = MutableStateFlow(0)
    val hasMorePages = MutableStateFlow(true)

    init {
        totalPages.value = (allItems.size + pageSize - 1) / pageSize
        loadPage(1)
    }

    fun nextPage() {
        if (currentPageNumber.value < totalPages.value) {
            loadPage(currentPageNumber.value + 1)
        }
    }

    fun previousPage() {
        if (currentPageNumber.value > 1) {
            loadPage(currentPageNumber.value - 1)
        }
    }

    fun jumpToPage(page: Int) {
        if (page in 1..totalPages.value) {
            loadPage(page)
        }
    }

    private fun loadPage(page: Int) {
        val start = (page - 1) * pageSize
        val end = minOf(start + pageSize, allItems.size)
        currentPage.value = allItems.subList(start, end)
        currentPageNumber.value = page
        hasMorePages.value = page < totalPages.value
    }
}

// Infinite scroll implementation

private class InfiniteScroll<T>(
    initialItems: List<T> = emptyList()
) {
    val items = MutableStateFlow(initialItems)
    val isLoading = MutableStateFlow(false)
    val hasMore = MutableStateFlow(true)
    val error = MutableStateFlow<String?>(null)

    private var shouldError = false

    suspend fun loadMore() {
        isLoading.value = true
        
        if (shouldError) {
            error.value = "Failed to load more"
        } else {
            val more = (items.value.size until items.value.size + 10)
                .map { "Item ${it + 1}" } as List<T>
            items.value = items.value + more
            
            if (items.value.size > 100) {
                hasMore.value = false
            }
        }
        
        isLoading.value = false
    }

    fun setError(shouldError: Boolean) {
        this.shouldError = shouldError
    }

    suspend fun retry() {
        shouldError = false
        loadMore()
    }
}

// Loading manager implementation

private class LoadingManager {
    val isLoading = MutableStateFlow(false)
    val progress = MutableStateFlow(0)
    val loadingMessage = MutableStateFlow("")
    val activeOperations = MutableStateFlow(0)
    val skeletonLoadingEnabled = MutableStateFlow(false)

    private val operations = mutableSetOf<String>()

    fun startLoading(operationId: String = "default") {
        operations.add(operationId)
        isLoading.value = true
        activeOperations.value = operations.size
    }

    fun stopLoading(operationId: String = "default") {
        operations.remove(operationId)
        activeOperations.value = operations.size
        isLoading.value = operations.isNotEmpty()
    }

    fun setProgress(value: Int) {
        progress.value = value
    }

    fun setLoadingMessage(message: String) {
        loadingMessage.value = message
    }

    fun enableSkeletonLoading(enabled: Boolean) {
        skeletonLoadingEnabled.value = enabled
    }
}
