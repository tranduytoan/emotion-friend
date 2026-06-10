package com.emotionfriend.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emotionfriend.domain.model.AuthUser

/**
 * Email verification screen.
 *
 * Shows after registration. User enters the 6-digit OTP sent to their email.
 * In demo mode the code is always "123456".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyEmailScreen(
    email: String,
    viewModel: AuthViewModel,
    onVerifySuccess: (AuthUser) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val form by viewModel.verifyForm.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Xác thực email") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
                // ── Icon ──────────────────────────────────────────────────────
                Text(
                    text      = "📧",
                    style     = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text       = "Kiểm tra hộp thư của bạn",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign  = TextAlign.Center,
                    color      = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text      = "Chúng tôi đã gửi mã 6 chữ số tới",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text       = email,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center,
                )

                Spacer(Modifier.height(32.dp))

                // ── OTP input ─────────────────────────────────────────────────
                OtpInputRow(
                    code         = form.code,
                    onCodeChange = viewModel::onVerifyCodeChange,
                )

                Spacer(Modifier.height(16.dp))

                // ── Demo hint ─────────────────────────────────────────────────
                Text(
                    text      = "Demo: mã xác thực là 123456",
                    style     = MaterialTheme.typography.bodySmall,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                // ── Error ─────────────────────────────────────────────────────
                if (form.errorMessage != null) {
                    ErrorBanner(message = form.errorMessage!!)
                    Spacer(Modifier.height(12.dp))
                }

                // ── Verify button ─────────────────────────────────────────────
                AuthPrimaryButton(
                    text      = "Xác nhận",
                    onClick   = {
                        viewModel.onVerifySubmit(email = email, onSuccess = onVerifySuccess)
                    },
                    isLoading = form.isLoading,
                    enabled   = form.code.length == 6,
                )

                Spacer(Modifier.height(24.dp))

                // ── Resend link ────────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text  = "Không nhận được email? ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(
                        onClick = { /* TODO: connect resend API */ },
                    ) {
                        Text(
                            text  = "Gửi lại",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

// ── OTP boxes ─────────────────────────────────────────────────────────────────

@Composable
private fun OtpInputRow(
    code: String,
    onCodeChange: (String) -> Unit,
) {
    // Hidden TextField drives the OTP — visible cells just display characters
    Box(contentAlignment = Alignment.Center) {
        // Invisible full-width text field handles input
        BasicTextField(
            value         = code,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                    onCodeChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction    = ImeAction.Done,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .size(1.dp), // Invisible but focusable
            decorationBox = { _ ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    repeat(6) { index ->
                        OtpCell(
                            char    = code.getOrNull(index),
                            focused = code.length == index,
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun OtpCell(char: Char?, focused: Boolean) {
    val bg     = if (char != null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val border = if (focused) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .background(bg, MaterialTheme.shapes.small)
            .border(width = if (focused) 2.dp else 1.dp, color = border, shape = MaterialTheme.shapes.small),
    ) {
        Text(
            text       = char?.toString() ?: "",
            style      = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color      = if (char != null) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        )
    }
}
