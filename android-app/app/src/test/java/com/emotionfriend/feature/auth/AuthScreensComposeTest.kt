package com.emotionfriend.feature.auth

import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthScreensComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private class FakeAuthViewModel : AuthViewModel.Fake() {
        private val _loginForm = MutableStateFlow(LoginFormState())
        override val loginForm: StateFlow<LoginFormState> = _loginForm

        private val _registerForm = MutableStateFlow(RegisterFormState())
        override val registerForm: StateFlow<RegisterFormState> = _registerForm

        override fun onLoginEmailChange(value: String) {
            _loginForm.value = _loginForm.value.copy(email = value, errorMessage = null)
        }

        override fun onLoginPasswordChange(value: String) {
            _loginForm.value = _loginForm.value.copy(password = value, errorMessage = null)
        }

        override fun onLoginPasswordVisibilityToggle() {
            _loginForm.value = _loginForm.value.copy(passwordVisible = !_loginForm.value.passwordVisible)
        }

        override fun onRegisterEmailChange(value: String) {
            _registerForm.value = _registerForm.value.copy(email = value, errorMessage = null)
        }

        override fun onRegisterPasswordChange(value: String) {
            _registerForm.value = _registerForm.value.copy(password = value, errorMessage = null)
        }

        override fun onRegisterConfirmPasswordChange(value: String) {
            _registerForm.value = _registerForm.value.copy(confirmPassword = value, errorMessage = null)
        }

        override fun onRegisterDisplayNameChange(value: String) {
            _registerForm.value = _registerForm.value.copy(displayName = value, errorMessage = null)
        }

        override fun onRegisterRoleChange(role: UserRole) {
            _registerForm.value = _registerForm.value.copy(selectedRole = role)
        }
    }

    @Test
    fun testLoginScreenRendersEmailField() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LoginScreen(
                        viewModel = FakeAuthViewModel(),
                        onLoginSuccess = {},
                        onNavigateToRegister = {},
                        onNavigateToForgotPassword = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Địa chỉ email").assertIsDisplayed()
    }

    @Test
    fun testLoginScreenRendersPasswordField() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LoginScreen(
                        viewModel = FakeAuthViewModel(),
                        onLoginSuccess = {},
                        onNavigateToRegister = {},
                        onNavigateToForgotPassword = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Mật khẩu").assertIsDisplayed()
    }

    @Test
    fun testLoginScreenEmailInputCanBeChanged() {
        val viewModel = FakeAuthViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {},
                        onNavigateToRegister = {},
                        onNavigateToForgotPassword = {},
                    )
                }
            }
        }

        // Find the email field by tag and input text
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@example.com")

        // Verify the view model received the change
        assert(viewModel.loginForm.value.email == "test@example.com")
    }

    @Test
    fun testLoginScreenPasswordVisibilityToggle() {
        val viewModel = FakeAuthViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {},
                        onNavigateToRegister = {},
                        onNavigateToForgotPassword = {},
                    )
                }
            }
        }

        // Initially password should be hidden
        assert(viewModel.loginForm.value.passwordVisible == false)

        // Find and click the visibility toggle
        composeTestRule.onNodeWithTag("password_visibility_toggle").performClick()

        // Verify the visibility toggled
        assert(viewModel.loginForm.value.passwordVisible == true)
    }

    @Test
    fun testRegisterScreenRendersAllFields() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    RegisterScreen(
                        viewModel = FakeAuthViewModel(),
                        onRegisterSuccess = {},
                        onNavigateToLogin = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Địa chỉ email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mật khẩu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Xác nhận mật khẩu").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tên hiển thị").assertIsDisplayed()
    }

    @Test
    fun testRegisterScreenEmailInputWorks() {
        val viewModel = FakeAuthViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegisterSuccess = {},
                        onNavigateToLogin = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("register_email_field").performTextInput("newuser@example.com")

        assert(viewModel.registerForm.value.email == "newuser@example.com")
    }

    @Test
    fun testRegisterScreenDisplayNameInputWorks() {
        val viewModel = FakeAuthViewModel()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    RegisterScreen(
                        viewModel = viewModel,
                        onRegisterSuccess = {},
                        onNavigateToLogin = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag("register_displayname_field").performTextInput("John Doe")

        assert(viewModel.registerForm.value.displayName == "John Doe")
    }

    @Test
    fun testLoginScreenShowsRegisterLink() {
        val navigateToRegisterCalled = mutableListOf<Unit>()
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    LoginScreen(
                        viewModel = FakeAuthViewModel(),
                        onLoginSuccess = {},
                        onNavigateToRegister = { navigateToRegisterCalled.add(Unit) },
                        onNavigateToForgotPassword = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Chưa có tài khoản? Đăng ký").performClick()

        assert(navigateToRegisterCalled.size == 1)
    }

    @Test
    fun testForgotPasswordScreenRendersEmailField() {
        composeTestRule.setContent {
            EmotionFriendTheme {
                Surface {
                    ForgotPasswordScreen(
                        viewModel = FakeAuthViewModel(),
                        onSuccess = {},
                        onNavigateBack = {},
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Địa chỉ email").assertIsDisplayed()
    }
}

// Extension for testing - base fake viewmodel
abstract class AuthViewModelBase : AuthViewModel(
    authRepository = object : com.emotionfriend.data.repository.AuthRepository {
        override suspend fun login(email: String, password: String): com.emotionfriend.domain.model.AuthUser? = null
        override suspend fun register(email: String, password: String, displayName: String, role: UserRole): com.emotionfriend.domain.model.AuthUser? = null
        override suspend fun forgotPassword(email: String): String = "Reset link sent"
        override suspend fun verifyEmail(email: String, code: String): com.emotionfriend.domain.model.AuthUser? = null
    },
    sessionManager = object : com.emotionfriend.data.auth.SessionManager {
        override val sessionFlow = kotlinx.coroutines.flow.MutableStateFlow<com.emotionfriend.domain.model.AuthUser?>(null)
    }
) {
    abstract fun onLoginEmailChange(value: String)
    abstract fun onLoginPasswordChange(value: String)
    abstract fun onLoginPasswordVisibilityToggle()
    abstract fun onRegisterEmailChange(value: String)
    abstract fun onRegisterPasswordChange(value: String)
    abstract fun onRegisterConfirmPasswordChange(value: String)
    abstract fun onRegisterDisplayNameChange(value: String)
    abstract fun onRegisterRoleChange(role: UserRole)

    companion object {
        fun Fake(): AuthViewModelBase = object : AuthViewModelBase() {
            override fun onLoginEmailChange(value: String) {}
            override fun onLoginPasswordChange(value: String) {}
            override fun onLoginPasswordVisibilityToggle() {}
            override fun onRegisterEmailChange(value: String) {}
            override fun onRegisterPasswordChange(value: String) {}
            override fun onRegisterConfirmPasswordChange(value: String) {}
            override fun onRegisterDisplayNameChange(value: String) {}
            override fun onRegisterRoleChange(role: UserRole) {}
        }
    }
}
