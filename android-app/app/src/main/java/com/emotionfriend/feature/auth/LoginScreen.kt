package com.emotionfriend.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Login screen.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val form by viewModel.loginForm.collectAsState()

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
            // ── Logo / header ─────────────────────────────────────────────────
            AppLogoHeader(
                title    = "Chào mừng trở lại!",
                subtitle = "Đăng nhập để tiếp tục hành trình cảm xúc của bạn",
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text      = "Tài khoản demo: demo@gmail.com / 123456",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(36.dp))

            // ── Email ─────────────────────────────────────────────────────────
            AuthTextField(
                value         = form.email,
                onValueChange = viewModel::onLoginEmailChange,
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
                isError = form.errorMessage != null,
            )

            Spacer(Modifier.height(12.dp))

            // ── Password ──────────────────────────────────────────────────────
            AuthTextField(
                value         = form.password,
                onValueChange = viewModel::onLoginPasswordChange,
                label         = "Mật khẩu",
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
                        onToggle = viewModel::onLoginPasswordVisibilityToggle,
                    )
                },
                isPassword      = true,
                passwordVisible = form.passwordVisible,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done,
                ),
                isError = form.errorMessage != null,
            )

            // ── Forgot password link ───────────────────────────────────────────
            Row(
                modifier       = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onNavigateToForgotPassword) {
                    Text(
                        text  = "Quên mật khẩu?",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // ── Error banner ───────────────────────────────────────────────────
            if (form.errorMessage != null) {
                Spacer(Modifier.height(4.dp))
                ErrorBanner(message = form.errorMessage!!)
                Spacer(Modifier.height(8.dp))
            } else {
                Spacer(Modifier.height(8.dp))
            }

            // ── Login button ───────────────────────────────────────────────────
            AuthPrimaryButton(
                text      = "Đăng nhập",
                onClick   = { viewModel.onLoginSubmit(onSuccess = { onLoginSuccess() }) },
                isLoading = form.isLoading,
            )

            Spacer(Modifier.height(24.dp))

            // ── Divider ────────────────────────────────────────────────────────
            DividerWithText("Chưa có tài khoản?")

            Spacer(Modifier.height(16.dp))

            // ── Register link ──────────────────────────────────────────────────
            TextButton(
                onClick  = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text       = "Đăng ký ngay",
                    color      = MaterialTheme.colorScheme.primary,
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// ── Shared sub-components ─────────────────────────────────────────────────────

@Composable
internal fun AppLogoHeader(title: String, subtitle: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
    ) {
        Text(text = "😊", style = MaterialTheme.typography.headlineLarge)
    }

    Spacer(Modifier.height(20.dp))

    Text(
        text       = title,
        style      = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.onBackground,
        textAlign  = TextAlign.Center,
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text      = subtitle,
        style     = MaterialTheme.typography.bodyMedium,
        color     = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
internal fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Spacer(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            text     = "  $text  ",
            style    = MaterialTheme.typography.bodySmall,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(
            Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

