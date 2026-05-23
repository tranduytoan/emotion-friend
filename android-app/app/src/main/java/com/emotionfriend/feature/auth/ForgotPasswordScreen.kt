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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.WarmCream

/**
 * Forgot password screen.
 *
 * User enters their email; the system sends a reset link (or in demo mode, shows
 * a confirmation message). The user is returned to Login after success.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
) {
    val form by viewModel.forgotForm.collectAsState()

    Scaffold(
        containerColor = WarmCream,
        topBar = {
            TopAppBar(
                title = { Text("Quên mật khẩu") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCream),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ── Illustration text ─────────────────────────────────────────
                Text(
                    text      = "🔑",
                    style     = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text       = "Khôi phục mật khẩu",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = MaterialTheme.colorScheme.onBackground,
                    textAlign  = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text      = "Nhập email bạn đã đăng ký. Chúng tôi sẽ gửi hướng dẫn đặt lại mật khẩu.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(32.dp))

                // ── Email field ───────────────────────────────────────────────
                AuthTextField(
                    value         = form.email,
                    onValueChange = viewModel::onForgotEmailChange,
                    label         = "Địa chỉ email",
                    leadingIcon   = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = SkyBlue40,
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction    = ImeAction.Done,
                    ),
                    isError = form.errorMessage != null,
                )

                Spacer(Modifier.height(16.dp))

                // ── Feedback banners ──────────────────────────────────────────
                when {
                    form.successMessage != null -> {
                        SuccessBanner(message = form.successMessage!!)
                        Spacer(Modifier.height(12.dp))
                    }
                    form.errorMessage != null -> {
                        ErrorBanner(message = form.errorMessage!!)
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // ── Submit button ─────────────────────────────────────────────
                AuthPrimaryButton(
                    text      = "Gửi email khôi phục",
                    onClick   = viewModel::onForgotPasswordSubmit,
                    isLoading = form.isLoading,
                    enabled   = form.successMessage == null, // Disable after success
                )

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
