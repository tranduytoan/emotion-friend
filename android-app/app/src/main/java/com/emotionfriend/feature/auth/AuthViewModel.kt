package com.emotionfriend.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.auth.SessionManager
import com.emotionfriend.data.repository.AuthRepository
import com.emotionfriend.domain.model.AuthState
import com.emotionfriend.domain.model.AuthUser
import com.emotionfriend.domain.model.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Per-screen form state ─────────────────────────────────────────────────────

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class RegisterFormState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val selectedRole: UserRole = UserRole.CHILD,
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class ForgotPasswordFormState(
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
)

data class VerifyEmailFormState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

/**
 * Shared ViewModel for all authentication screens.
 *
 * Scoped to the auth nav-graph so it is created once when the user enters the
 * auth flow and destroyed when they successfully reach the main app.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    // ── Auth state (drives root NavHost start destination) ────────────────────

    val authState: StateFlow<AuthState> = sessionManager.sessionFlow
        .map { user ->
            if (user == null) AuthState.Unauthenticated
            else AuthState.Authenticated(user)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthState.Loading,
        )

    // ── Form states ───────────────────────────────────────────────────────────

    private val _loginForm = MutableStateFlow(LoginFormState())
    val loginForm: StateFlow<LoginFormState> = _loginForm.asStateFlow()

    private val _registerForm = MutableStateFlow(RegisterFormState())
    val registerForm: StateFlow<RegisterFormState> = _registerForm.asStateFlow()

    private val _forgotForm = MutableStateFlow(ForgotPasswordFormState())
    val forgotForm: StateFlow<ForgotPasswordFormState> = _forgotForm.asStateFlow()

    private val _verifyForm = MutableStateFlow(VerifyEmailFormState())
    val verifyForm: StateFlow<VerifyEmailFormState> = _verifyForm.asStateFlow()

    // Email stored while awaiting verification (passed from register to verify screen)
    private var pendingVerificationEmail: String = ""

    // ── Login ──────────────────────────────────────────────────────────────────

    fun onLoginEmailChange(value: String) {
        _loginForm.value = _loginForm.value.copy(email = value, errorMessage = null)
    }

    fun onLoginPasswordChange(value: String) {
        _loginForm.value = _loginForm.value.copy(password = value, errorMessage = null)
    }

    fun onLoginPasswordVisibilityToggle() {
        _loginForm.value = _loginForm.value.copy(passwordVisible = !_loginForm.value.passwordVisible)
    }

    fun onLoginSubmit(onSuccess: (AuthUser) -> Unit) {
        val form = _loginForm.value
        val validationError = validateLoginInput(form)
        if (validationError != null) {
            _loginForm.value = form.copy(errorMessage = validationError)
            return
        }
        _loginForm.value = form.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.login(form.email.trim(), form.password)
                .onSuccess { user ->
                    sessionManager.saveSession(user)
                    _loginForm.value = LoginFormState()
                    onSuccess(user)
                }
                .onFailure { ex ->
                    _loginForm.value = _loginForm.value.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Đăng nhập thất bại. Vui lòng thử lại.",
                    )
                }
        }
    }

    // ── Register ───────────────────────────────────────────────────────────────

    fun onRegisterEmailChange(value: String) {
        _registerForm.value = _registerForm.value.copy(email = value, errorMessage = null)
    }

    fun onRegisterDisplayNameChange(value: String) {
        _registerForm.value = _registerForm.value.copy(displayName = value, errorMessage = null)
    }

    fun onRegisterPasswordChange(value: String) {
        _registerForm.value = _registerForm.value.copy(password = value, errorMessage = null)
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _registerForm.value = _registerForm.value.copy(confirmPassword = value, errorMessage = null)
    }

    fun onRegisterPasswordVisibilityToggle() {
        _registerForm.value = _registerForm.value.copy(passwordVisible = !_registerForm.value.passwordVisible)
    }

    fun onRegisterConfirmPasswordVisibilityToggle() {
        _registerForm.value = _registerForm.value.copy(confirmPasswordVisible = !_registerForm.value.confirmPasswordVisible)
    }

    fun onRegisterRoleChange(role: UserRole) {
        _registerForm.value = _registerForm.value.copy(selectedRole = role, errorMessage = null)
    }

    fun onRegisterSubmit(onAwaitingVerification: (email: String) -> Unit) {
        val form = _registerForm.value
        val validationError = validateRegisterInput(form)
        if (validationError != null) {
            _registerForm.value = form.copy(errorMessage = validationError)
            return
        }
        _registerForm.value = form.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.register(
                email       = form.email.trim(),
                password    = form.password,
                displayName = form.displayName.trim(),
                role        = form.selectedRole,
            )
                .onSuccess { user ->
                    sessionManager.saveSession(user)
                    pendingVerificationEmail = user.email
                    _registerForm.value = RegisterFormState()
                    onAwaitingVerification(user.email)
                }
                .onFailure { ex ->
                    _registerForm.value = _registerForm.value.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Đăng ký thất bại. Vui lòng thử lại.",
                    )
                }
        }
    }

    // ── Forgot password ────────────────────────────────────────────────────────

    fun onForgotEmailChange(value: String) {
        _forgotForm.value = _forgotForm.value.copy(
            email = value, errorMessage = null, successMessage = null,
        )
    }

    fun onForgotPasswordSubmit() {
        val form = _forgotForm.value
        if (form.email.isBlank()) {
            _forgotForm.value = form.copy(errorMessage = "Vui lòng nhập email.")
            return
        }
        if (!isValidEmail(form.email.trim())) {
            _forgotForm.value = form.copy(errorMessage = "Địa chỉ email không hợp lệ.")
            return
        }
        _forgotForm.value = form.copy(isLoading = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            authRepository.forgotPassword(form.email.trim())
                .onSuccess { message ->
                    _forgotForm.value = _forgotForm.value.copy(
                        isLoading = false, successMessage = message,
                    )
                }
                .onFailure { ex ->
                    _forgotForm.value = _forgotForm.value.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Có lỗi xảy ra. Vui lòng thử lại.",
                    )
                }
        }
    }

    // ── Email verification ─────────────────────────────────────────────────────

    fun getPendingVerificationEmail(): String = pendingVerificationEmail

    fun onVerifyCodeChange(value: String) {
        if (value.length <= 6) {
            _verifyForm.value = _verifyForm.value.copy(code = value, errorMessage = null)
        }
    }

    fun onVerifySubmit(email: String, onSuccess: (AuthUser) -> Unit) {
        val form = _verifyForm.value
        if (form.code.length != 6) {
            _verifyForm.value = form.copy(errorMessage = "Mã xác thực gồm 6 chữ số.")
            return
        }
        _verifyForm.value = form.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.verifyEmail(email, form.code)
                .onSuccess { user ->
                    sessionManager.markVerified()
                    sessionManager.saveSession(user)
                    _verifyForm.value = VerifyEmailFormState()
                    onSuccess(user)
                }
                .onFailure { ex ->
                    _verifyForm.value = _verifyForm.value.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Xác thực thất bại.",
                    )
                }
        }
    }

    // ── Logout ─────────────────────────────────────────────────────────────────

    fun onLogout() {
        viewModelScope.launch { sessionManager.clearSession() }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun validateLoginInput(form: LoginFormState): String? = when {
        form.email.isBlank()    -> "Vui lòng nhập email."
        !isValidEmail(form.email.trim()) -> "Địa chỉ email không hợp lệ."
        form.password.isBlank() -> "Vui lòng nhập mật khẩu."
        else                    -> null
    }

    private fun validateRegisterInput(form: RegisterFormState): String? = when {
        form.displayName.isBlank()            -> "Vui lòng nhập họ tên."
        form.email.isBlank()                  -> "Vui lòng nhập email."
        !isValidEmail(form.email.trim())      -> "Địa chỉ email không hợp lệ."
        form.password.length < 8             -> "Mật khẩu tối thiểu 8 ký tự."
        !isStrongPassword(form.password)      -> "Mật khẩu cần có chữ hoa, chữ thường và chữ số."
        form.password != form.confirmPassword -> "Mật khẩu xác nhận không khớp."
        else                                  -> null
    }

    private fun isValidEmail(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isStrongPassword(password: String): Boolean =
        password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() }
}
