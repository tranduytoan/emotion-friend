package com.emotionfriend.feature.auth

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.ErrorRed
import com.emotionfriend.core.designsystem.theme.ErrorRedContainer
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.core.designsystem.theme.SurfaceVariant
import com.emotionfriend.domain.model.UserRole

// ── AuthTextField ─────────────────────────────────────────────────────────────

@Composable
internal fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = SkyBlue40,
            focusedLabelColor    = SkyBlue40,
            cursorColor          = SkyBlue40,
            errorBorderColor     = ErrorRed,
            errorLabelColor      = ErrorRed,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
    )
}

// ── PasswordVisibilityToggle ───────────────────────────────────────────────────

@Composable
internal fun PasswordVisibilityToggle(
    visible: Boolean,
    onToggle: () -> Unit,
) {
    IconButton(onClick = onToggle) {
        // Use text-based toggle since material-icons-extended is not in the dependency set.
        // The label clearly communicates the action to screen readers and users alike.
        Text(
            text  = if (visible) "ẨN" else "HIỆN",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── AuthPrimaryButton ─────────────────────────────────────────────────────────

@Composable
internal fun AuthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SkyBlue40,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .animateContentSize(),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color     = MaterialTheme.colorScheme.onPrimary,
                modifier  = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
            )
        } else {
            Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

// ── ErrorBanner ───────────────────────────────────────────────────────────────

@Composable
internal fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        color  = ErrorRedContainer,
        shape  = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text  = message,
            color = ErrorRed,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        )
    }
}

// ── SuccessBanner ─────────────────────────────────────────────────────────────

@Composable
internal fun SuccessBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        color  = MintGreen80,
        shape  = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MintGreen40,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text  = message,
                color = MintGreen40,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

// ── RoleSelector ─────────────────────────────────────────────────────────────

@Composable
internal fun RoleSelector(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text  = "Bạn là ai?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            UserRole.entries.forEach { role ->
                RoleChip(
                    role       = role,
                    isSelected = role == selectedRole,
                    onClick    = { onRoleSelected(role) },
                    modifier   = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun RoleChip(
    role: UserRole,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor     = if (isSelected) SkyBlueLight else SurfaceVariant
    val borderColor = if (isSelected) SkyBlue40    else SurfaceVariant
    val textColor   = if (isSelected) SkyBlue40    else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick   = onClick,
        color     = bgColor,
        shape     = RoundedCornerShape(12.dp),
        modifier  = modifier
            .height(56.dp)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text  = role.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}
