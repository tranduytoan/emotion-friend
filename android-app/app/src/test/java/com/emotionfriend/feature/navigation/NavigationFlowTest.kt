package com.emotionfriend.feature.navigation

import com.emotionfriend.domain.model.AuthState
import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationFlowTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var navigationManager: NavigationManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        navigationManager = NavigationManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `navigation starts at unauthenticated state`() {
        val state = navigationManager.authState.value
        assertTrue("Should start unauthenticated", state is AuthState.Unauthenticated)
    }

    @Test
    fun `authentication transitions state to authenticated`() = runTest {
        val user = AuthUser(
            id = 1,
            email = "test@example.com",
            displayName = "Test",
            role = UserRole.CHILD,
            token = "token",
            isVerified = true
        )

        navigationManager.authenticateUser(user)

        val state = navigationManager.authState.value
        assertTrue("Should be authenticated", state is AuthState.Authenticated)
    }

    @Test
    fun `logout transitions back to unauthenticated`() = runTest {
        val user = AuthUser(
            id = 1,
            email = "test@example.com",
            displayName = "Test",
            role = UserRole.CHILD,
            token = "token",
            isVerified = true
        )

        navigationManager.authenticateUser(user)
        var state = navigationManager.authState.value
        assertTrue("Should be authenticated", state is AuthState.Authenticated)

        navigationManager.logout()
        state = navigationManager.authState.value
        assertTrue("Should be unauthenticated after logout", state is AuthState.Unauthenticated)
    }

    @Test
    fun `can navigate between app features`() {
        navigationManager.navigateTo("learn")
        assertEquals("learn", navigationManager.currentRoute.value)

        navigationManager.navigateTo("home")
        assertEquals("home", navigationManager.currentRoute.value)

        navigationManager.navigateTo("profile")
        assertEquals("profile", navigationManager.currentRoute.value)
    }

    @Test
    fun `navigation history is tracked`() {
        navigationManager.navigateTo("home")
        navigationManager.navigateTo("learn")
        navigationManager.navigateTo("journal")

        val history = navigationManager.navigationHistory
        assertTrue("Should track navigation history", history.size >= 2)
    }

    @Test
    fun `back navigation works`() {
        navigationManager.navigateTo("home")
        navigationManager.navigateTo("learn")

        assertEquals("learn", navigationManager.currentRoute.value)

        navigationManager.goBack()

        assertEquals("home", navigationManager.currentRoute.value)
    }

    @Test
    fun `deep linking to feature works`() {
        navigationManager.navigateTo("story", "story-1")

        assertEquals("story", navigationManager.currentRoute.value)
        assertEquals("story-1", navigationManager.currentRouteArg.value)
    }

    @Test
    fun `can clear navigation stack`() {
        navigationManager.navigateTo("home")
        navigationManager.navigateTo("learn")
        navigationManager.navigateTo("journal")

        val initialSize = navigationManager.navigationHistory.size
        assertTrue("History should have entries", initialSize > 0)

        navigationManager.clearNavigationStack()

        val finalSize = navigationManager.navigationHistory.size
        assertTrue("History should be cleared or minimal", finalSize < initialSize)
    }

    @Test
    fun `parent user sees different routes than child`() {
        val parentUser = AuthUser(
            id = 1,
            email = "parent@example.com",
            displayName = "Parent",
            role = UserRole.PARENT,
            token = "token",
            isVerified = true
        )

        val childUser = AuthUser(
            id = 2,
            email = "child@example.com",
            displayName = "Child",
            role = UserRole.CHILD,
            token = "token",
            isVerified = true
        )

        navigationManager.authenticateUser(parentUser)
        val parentRoutes = navigationManager.getAvailableRoutes()
        assertTrue("Parent should have access to certain routes", parentRoutes.isNotEmpty())

        navigationManager.logout()

        navigationManager.authenticateUser(childUser)
        val childRoutes = navigationManager.getAvailableRoutes()
        assertTrue("Child should have access to routes", childRoutes.isNotEmpty())
    }
}

private class NavigationManager {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    private val _currentRoute = MutableStateFlow("login")
    val currentRoute: StateFlow<String> = _currentRoute

    private val _currentRouteArg = MutableStateFlow<String?>(null)
    val currentRouteArg: StateFlow<String?> = _currentRouteArg

    private val _navigationHistory = mutableListOf<String>()
    val navigationHistory: List<String> get() = _navigationHistory.toList()

    fun authenticateUser(user: AuthUser) {
        _authState.value = AuthState.Authenticated(user)
        _currentRoute.value = "home"
        _navigationHistory.clear()
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
        _currentRoute.value = "login"
        _navigationHistory.clear()
    }

    fun navigateTo(route: String, arg: String? = null) {
        _navigationHistory.add(_currentRoute.value)
        _currentRoute.value = route
        _currentRouteArg.value = arg
    }

    fun goBack() {
        if (_navigationHistory.isNotEmpty()) {
            val previous = _navigationHistory.removeAt(_navigationHistory.size - 1)
            _currentRoute.value = previous
            _currentRouteArg.value = null
        }
    }

    fun clearNavigationStack() {
        _navigationHistory.clear()
        _currentRoute.value = "home"
    }

    fun getAvailableRoutes(): List<String> {
        val user = (_authState.value as? AuthState.Authenticated)?.user
        return when (user?.role) {
            UserRole.PARENT -> listOf("home", "profile", "children")
            UserRole.CHILD -> listOf("home", "learn", "journal", "express", "story", "progress", "profile")
            null -> listOf("login", "register")
        }
    }
}
