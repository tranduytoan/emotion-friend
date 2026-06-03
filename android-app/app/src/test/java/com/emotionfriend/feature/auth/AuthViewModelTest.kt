package com.emotionfriend.feature.auth

import com.emotionfriend.data.auth.SessionManager
import com.emotionfriend.data.repository.AuthRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var authRepo: FakeAuthRepository
    private lateinit var sessionManager: FakeSessionManager
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepo = FakeAuthRepository()
        sessionManager = FakeSessionManager()
        viewModel = AuthViewModel(authRepo, sessionManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login form initial state is empty`() {
        val form = viewModel.loginForm.value

        assertEquals("", form.email)
        assertEquals("", form.password)
        assertFalse("Password should be hidden initially", form.passwordVisible)
        assertFalse("Should not be loading", form.isLoading)
        assertEquals(null, form.errorMessage)
    }

    @Test
    fun `register form initial state is empty`() {
        val form = viewModel.registerForm.value

        assertEquals("", form.email)
        assertEquals("", form.password)
        assertEquals("", form.confirmPassword)
        assertEquals("", form.displayName)
        assertEquals(UserRole.CHILD, form.selectedRole)
        assertFalse("Should not be loading", form.isLoading)
        assertEquals(null, form.errorMessage)
    }

    @Test
    fun `onLoginEmailChange updates email in form`() {
        viewModel.onLoginEmailChange("test@example.com")

        val form = viewModel.loginForm.value
        assertEquals("test@example.com", form.email)
        assertEquals(null, form.errorMessage)
    }

    @Test
    fun `onLoginPasswordChange updates password in form`() {
        viewModel.onLoginPasswordChange("password123")

        val form = viewModel.loginForm.value
        assertEquals("password123", form.password)
        assertEquals(null, form.errorMessage)
    }

    @Test
    fun `onLoginPasswordVisibilityToggle toggles password visibility`() {
        assertFalse("Initially hidden", viewModel.loginForm.value.passwordVisible)

        viewModel.onLoginPasswordVisibilityToggle()
        assertTrue("Should be visible after first toggle", viewModel.loginForm.value.passwordVisible)

        viewModel.onLoginPasswordVisibilityToggle()
        assertFalse("Should be hidden after second toggle", viewModel.loginForm.value.passwordVisible)
    }

    @Test
    fun `onRegisterEmailChange updates register email`() {
        viewModel.onRegisterEmailChange("newuser@example.com")

        val form = viewModel.registerForm.value
        assertEquals("newuser@example.com", form.email)
    }

    @Test
    fun `onRegisterPasswordChange updates register password`() {
        viewModel.onRegisterPasswordChange("pass123")

        val form = viewModel.registerForm.value
        assertEquals("pass123", form.password)
    }

    @Test
    fun `onRegisterDisplayNameChange updates display name`() {
        viewModel.onRegisterDisplayNameChange("John Doe")

        val form = viewModel.registerForm.value
        assertEquals("John Doe", form.displayName)
    }

    @Test
    fun `onRegisterRoleChange updates selected role`() {
        viewModel.onRegisterRoleChange(UserRole.PARENT)

        val form = viewModel.registerForm.value
        assertEquals(UserRole.PARENT, form.selectedRole)
    }

    @Test
    fun `onForgotPasswordEmailChange updates email`() {
        viewModel.onForgotPasswordEmailChange("reset@example.com")

        val form = viewModel.forgotForm.value
        assertEquals("reset@example.com", form.email)
    }

    @Test
    fun `onVerifyEmailCodeChange updates code`() {
        viewModel.onVerifyEmailCodeChange("123456")

        val form = viewModel.verifyForm.value
        assertEquals("123456", form.code)
    }

    @Test
    fun `authState reflects session manager state`() = runTest {
        val user = AuthUser(
            id = 1L,
            email = "test@example.com",
            displayName = "Test User",
            role = UserRole.CHILD,
            token = "token123",
            isVerified = true,
        )
        sessionManager._sessionFlow.value = user

        val authState = viewModel.authState.value
        assertTrue("Should be authenticated", authState is com.emotionfriend.domain.model.AuthState.Authenticated)
    }

    @Test
    fun `multiple form field changes work together`() {
        viewModel.onRegisterEmailChange("user@test.com")
        viewModel.onRegisterPasswordChange("pass123")
        viewModel.onRegisterDisplayNameChange("Test User")
        viewModel.onRegisterRoleChange(UserRole.PARENT)

        val form = viewModel.registerForm.value
        assertEquals("user@test.com", form.email)
        assertEquals("pass123", form.password)
        assertEquals("Test User", form.displayName)
        assertEquals(UserRole.PARENT, form.selectedRole)
    }
}

private class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): AuthUser? =
        if (email == "test@example.com" && password == "pass123") {
            AuthUser(1L, email, "Test", UserRole.CHILD, "token", true)
        } else null

    override suspend fun register(email: String, password: String, displayName: String, role: UserRole): AuthUser? =
        AuthUser(2L, email, displayName, role, "token", false)

    override suspend fun forgotPassword(email: String): String = "Reset link sent"
    override suspend fun verifyEmail(email: String, code: String): AuthUser? =
        AuthUser(1L, email, "Test", UserRole.CHILD, "token", true)
}

private class FakeSessionManager : SessionManager {
    val _sessionFlow = MutableStateFlow<AuthUser?>(null)
    override val sessionFlow: StateFlow<AuthUser?> = _sessionFlow
}
