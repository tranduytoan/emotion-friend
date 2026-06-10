package com.emotionfriend.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emotionfriend.domain.model.AuthUser

/**
 * Register screen.
 *
 * Collects: display name, email, password + confirm, and role selection.
 * After successful registration, navigates to email verification.
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onAwaitingVerification: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    val form by viewModel.registerForm.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Header ─────────────────────────────────────────────────────────
            AppLogoHeader(
                title    = "Tạo tài khoản mới",
                subtitle = "Điền thông tin để bắt đầu hành trình cùng Emotion Friend",
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text      = "Tài khoản demo: demo@gmail.com / 123456",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Spacer(Modifier.height(32.dp))

            // ── Role selector ─────────────────────────────────────────────────
            RoleSelector(
                selectedRole   = form.selectedRole,
                onRoleSelected = viewModel::onRegisterRoleChange,
            )

            Spacer(Modifier.height(20.dp))

            // ── Display name ──────────────────────────────────────────────────
            AuthTextField(
                value         = form.displayName,
                onValueChange = viewModel::onRegisterDisplayNameChange,
                label         = "Họ và tên",
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction      = ImeAction.Next,
                ),
                isError = form.errorMessage?.contains("tên") == true,
            )

            Spacer(Modifier.height(12.dp))

            // ── Email ─────────────────────────────────────────────────────────
            AuthTextField(
                value         = form.email,
                onValueChange = viewModel::onRegisterEmailChange,
                label         = "Địa chỉ email",
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next,
                ),
                isError = form.errorMessage?.contains("email") == true,
            )

            Spacer(Modifier.height(12.dp))

            // ── Password ──────────────────────────────────────────────────────
            AuthTextField(
                value         = form.password,
                onValueChange = viewModel::onRegisterPasswordChange,
                label         = "Mật khẩu (tối thiểu 8 ký tự)",
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon  = {
                    PasswordVisibilityToggle(
                        visible  = form.passwordVisible,
                        onToggle = viewModel::onRegisterPasswordVisibilityToggle,
                    )
                },
                isPassword      = true,
                passwordVisible = form.passwordVisible,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Next,
                ),
                isError = form.errorMessage?.contains("Mật khẩu") == true,
            )

            Spacer(Modifier.height(12.dp))

            // ── Confirm password ──────────────────────────────────────────────
            AuthTextField(
                value         = form.confirmPassword,
                onValueChange = viewModel::onRegisterConfirmPasswordChange,
                label         = "Xác nhận mật khẩu",
                leadingIcon   = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon  = {
                    PasswordVisibilityToggle(
                        visible  = form.confirmPasswordVisible,
                        onToggle = viewModel::onRegisterConfirmPasswordVisibilityToggle,
                    )
                },
                isPassword      = true,
                passwordVisible = form.confirmPasswordVisible,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done,
                ),
                isError = form.errorMessage?.contains("xác nhận") == true,
            )

            Spacer(Modifier.height(16.dp))

            // ── Error banner ───────────────────────────────────────────────────
            if (form.errorMessage != null) {
                ErrorBanner(message = form.errorMessage!!)
                Spacer(Modifier.height(12.dp))
            }

            // ── Register button ────────────────────────────────────────────────
            AuthPrimaryButton(
                text      = "Đăng ký",
                onClick   = {
                    viewModel.onRegisterSubmit(onAwaitingVerification = onAwaitingVerification)
                },
                isLoading = form.isLoading,
            )

            Spacer(Modifier.height(24.dp))

            // ── Login link ─────────────────────────────────────────────────────
            DividerWithText("Đã có tài khoản?")
            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick  = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text       = "Đăng nhập",
                    color      = MaterialTheme.colorScheme.primary,
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
