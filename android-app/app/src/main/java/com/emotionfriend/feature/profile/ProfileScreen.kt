package com.emotionfriend.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.dimensions

// ---------------------------------------------------------------------------
// Avatar options available for the picker
// ---------------------------------------------------------------------------

private val avatarOptions = listOf(
    "🧒", "👦", "👧", "🦊", "🐱", "🐶", "🐻", "🐼", "🦁", "🐸", "🐨", "🐰"
)

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state      by viewModel.uiState.collectAsState()
    val loggedOut  by viewModel.loggedOut.collectAsState()

    // Navigate away once logout completes
    LaunchedEffect(loggedOut) {
        if (loggedOut) onLogout()
    }

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf("") }
    var ageInput by rememberSaveable { mutableStateOf("") }
    var avatarInput by rememberSaveable { mutableStateOf("") }
    var soundEnabled by rememberSaveable { mutableStateOf(false) }
    var notificationEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderTime by rememberSaveable { mutableStateOf("") }
    var language by rememberSaveable { mutableStateOf("") }
    var theme by rememberSaveable { mutableStateOf(com.emotionfriend.core.designsystem.theme.AppTheme.SYSTEM) }

    val startEdit = {
        nameInput = state.name
        ageInput = state.age.toString()
        avatarInput = state.avatarEmoji
        soundEnabled = state.settings.soundEnabled
        notificationEnabled = state.settings.notificationEnabled
        reminderTime = state.settings.reminderTime
        language = state.settings.language
        theme = state.settings.theme
        isEditing = true
    }

    val cancelEdit = {
        isEditing = false
    }

    val saveEdit = {
        val safeName = nameInput.trim().ifBlank { state.name }
        val safeAge = ageInput.trim().toIntOrNull() ?: state.age
        val safeReminder = reminderTime.trim().ifBlank { state.settings.reminderTime }
        val safeLanguage = language.trim().ifBlank { state.settings.language }
        val safeAvatar = avatarInput.ifBlank { state.avatarEmoji }

        viewModel.updateProfile(name = safeName, age = safeAge)
        viewModel.updateAvatar(safeAvatar)
        viewModel.updateSettings(
            soundEnabled = soundEnabled,
            notificationEnabled = notificationEnabled,
            reminderTime = safeReminder,
            language = safeLanguage,
            theme = theme
        )
        isEditing = false
    }

    EmotionScreenScaffold(title = "Hồ sơ cá nhân", onBack = onBack) {
        ProfileContent(
            state = state,
            isEditing = isEditing,
            nameInput = nameInput,
            ageInput = ageInput,
            avatarInput = avatarInput,
            soundEnabled = soundEnabled,
            notificationEnabled = notificationEnabled,
            reminderTime = reminderTime,
            language = language,
            theme = theme,
            onStartEdit = startEdit,
            onCancelEdit = cancelEdit,
            onSaveEdit = saveEdit,
            onNameChange = { nameInput = it },
            onAgeChange = { ageInput = it },
            onAvatarChange = { avatarInput = it },
            onSoundToggle = { soundEnabled = it },
            onNotificationToggle = { notificationEnabled = it },
            onReminderChange = { reminderTime = it },
            onLanguageChange = { language = it },
            onThemeChange = { theme = it },
            onLogout = viewModel::logout,
            modifier = modifier
        )
    }
}

// ---------------------------------------------------------------------------
// Main content
// ---------------------------------------------------------------------------

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    isEditing: Boolean,
    nameInput: String,
    ageInput: String,
    avatarInput: String,
    soundEnabled: Boolean,
    notificationEnabled: Boolean,
    reminderTime: String,
    language: String,
    theme: com.emotionfriend.core.designsystem.theme.AppTheme,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onReminderChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (com.emotionfriend.core.designsystem.theme.AppTheme) -> Unit,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.dimensions

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(spacing.spacingMd)
    ) {
        // --- Header card ----------------------------------------------------
        EmotionCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarBubble(
                    emoji = state.avatarEmoji,
                    backgroundColor = state.accentBackground,
                    modifier = Modifier
                        .size(88.dp)
                        .semantics {
                            contentDescription = "Ảnh đại diện: ${state.avatarEmoji} của ${state.name}"
                        }
                )
                Spacer(Modifier.width(spacing.spacingMd))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tuổi: ${state.age}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Bé đang luyện cảm xúc mỗi ngày 🌟",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!isEditing) {
            EmotionPrimaryButton(text = "Chỉnh sửa hồ sơ", onClick = onStartEdit)
        } else {
            EditProfileCard(
                nameInput = nameInput,
                ageInput = ageInput,
                avatarInput = avatarInput,
                soundEnabled = soundEnabled,
                notificationEnabled = notificationEnabled,
                reminderTime = reminderTime,
                language = language,
                theme = theme,
                onNameChange = onNameChange,
                onAgeChange = onAgeChange,
                onAvatarChange = onAvatarChange,
                onSoundToggle = onSoundToggle,
                onNotificationToggle = onNotificationToggle,
                onReminderChange = onReminderChange,
                onLanguageChange = onLanguageChange,
                onThemeChange = onThemeChange,
                onCancelEdit = onCancelEdit,
                onSaveEdit = onSaveEdit
            )
        }

        // --- Basic info -----------------------------------------------------
        SectionHeader(text = "Thông tin cơ bản")
        EmotionCard(padding = 0.dp) {
            Column {
                InfoRow(label = "ID", value = state.userId.toString())
                InfoRow(label = "Họ tên", value = state.name)
                InfoRow(label = "Tuổi", value = state.age.toString())
            }
        }

        // --- Settings --------------------------------------------------------
        SectionHeader(text = "Cài đặt")
        EmotionCard(padding = 0.dp) {
            Column {
                SettingRow(label = "Âm thanh", enabled = state.settings.soundEnabled)
                SettingRow(label = "Thông báo", enabled = state.settings.notificationEnabled)
                InfoRow(label = "Giờ nhắc", value = state.settings.reminderTime)
                InfoRow(label = "Ngôn ngữ", value = state.settings.language)
                InfoRow(label = "Giao diện", value = when(state.settings.theme) {
                    com.emotionfriend.core.designsystem.theme.AppTheme.SYSTEM -> "Theo hệ thống"
                    com.emotionfriend.core.designsystem.theme.AppTheme.LIGHT -> "Sáng"
                    com.emotionfriend.core.designsystem.theme.AppTheme.DARK -> "Tối"
                })
            }
        }

        // --- Logout ----------------------------------------------------------
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick  = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors   = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border   = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Text(
                text  = "🚪 Đăng xuất",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Building blocks
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

@Composable
private fun AvatarBubble(
    emoji: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(color = backgroundColor, shape = CircleShape)
    ) {
        Text(text = emoji, style = MaterialTheme.typography.headlineLarge)
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            AssistChip(
                onClick = {},
                label = { Text(text = value, style = MaterialTheme.typography.labelMedium) },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier
    )
}

@Composable
private fun SettingRow(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val chipText = if (enabled) "Bật" else "Tắt"
    val containerColor = if (enabled) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (enabled) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            AssistChip(
                onClick = {},
                label = { Text(text = chipText, style = MaterialTheme.typography.labelMedium) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = containerColor,
                    labelColor = contentColor
                ),
                border = null
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = modifier
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileCard(
    nameInput: String,
    ageInput: String,
    avatarInput: String,
    soundEnabled: Boolean,
    notificationEnabled: Boolean,
    reminderTime: String,
    language: String,
    theme: com.emotionfriend.core.designsystem.theme.AppTheme,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onAvatarChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onReminderChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onThemeChange: (com.emotionfriend.core.designsystem.theme.AppTheme) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit
) {
    val spacing = MaterialTheme.dimensions

    EmotionCard {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.spacingSm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🛠️", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(spacing.spacingSm))
                Column {
                    Text(
                        text = "Cập nhật hồ sơ",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Ba mẹ có thể giúp con điền nhé!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // --- Avatar picker ----------------------------------------------
            AvatarPickerGrid(
                selected = avatarInput,
                onSelect = onAvatarChange
            )

            OutlinedTextField(
                value = nameInput,
                onValueChange = onNameChange,
                label = { Text(text = "Tên của con") },
                supportingText = { Text(text = "Ví dụ: Bé Minh") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ageInput,
                onValueChange = { value ->
                    onAgeChange(value.filter { it.isDigit() }.take(2))
                },
                label = { Text(text = "Con bao nhiêu tuổi?") },
                supportingText = { Text(text = "Ví dụ: 8") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = reminderTime,
                onValueChange = onReminderChange,
                label = { Text(text = "Giờ nhắc luyện tập") },
                supportingText = { Text(text = "Ví dụ: 19:00") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = language,
                onValueChange = onLanguageChange,
                label = { Text(text = "Ngôn ngữ") },
                supportingText = { Text(text = "Ví dụ: Tiếng Việt") },
                modifier = Modifier.fillMaxWidth()
            )

            // --- Theme selection --------------------------------------------
            Text(
                text = "Giao diện ứng dụng:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                com.emotionfriend.core.designsystem.theme.AppTheme.entries.forEach { themeOption ->
                    val isSelected = theme == themeOption
                    val label = when(themeOption) {
                        com.emotionfriend.core.designsystem.theme.AppTheme.SYSTEM -> "Hệ thống"
                        com.emotionfriend.core.designsystem.theme.AppTheme.LIGHT -> "Sáng"
                        com.emotionfriend.core.designsystem.theme.AppTheme.DARK -> "Tối"
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onThemeChange(themeOption) },
                        label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            SettingToggleRow(
                label = "Âm thanh",
                helper = "Bật nhạc và hiệu ứng vui nhộn",
                checked = soundEnabled,
                onCheckedChange = onSoundToggle
            )
            SettingToggleRow(
                label = "Thông báo",
                helper = "Nhắc con luyện tập mỗi ngày",
                checked = notificationEnabled,
                onCheckedChange = onNotificationToggle
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.spacingSm),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onCancelEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Hủy")
                }
                EmotionPrimaryButton(
                    text = "Lưu thay đổi",
                    onClick = onSaveEdit,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun AvatarPickerGrid(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Text(
            text  = "Chọn ảnh đại diện cho bé:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        avatarOptions.chunked(4).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { emoji ->
                    val isSelected = selected == emoji
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelect(emoji) },
                        label = { Text(text = emoji, style = MaterialTheme.typography.titleMedium) },
                        shape = CircleShape,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    label: String,
    helper: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Text(
                text = helper,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.semantics { role = Role.Switch }
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}


// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    EmotionFriendTheme {
        ProfileContent(
            state = ProfileUiState(
                userId = 1,
                name = "Bé Minh",
                age = 8,
                avatarEmoji = "🧒",
                accentColor = MaterialTheme.colorScheme.primary,
                accentBackground = MaterialTheme.colorScheme.secondaryContainer,
                settings = ProfileSettings(
                    soundEnabled = true,
                    notificationEnabled = true,
                    reminderTime = "19:00",
                    language = "Tiếng Việt",
                    theme = com.emotionfriend.core.designsystem.theme.AppTheme.SYSTEM
                ),
                progress = ProfileProgress(
                    totalExercises = 12,
                    correctAnswers = 9,
                    currentStreak = 3,
                    longestStreak = 5,
                ),
            ),
            isEditing = true,
            nameInput = "Bé Minh",
            ageInput = "8",
            avatarInput = "🧒",
            soundEnabled = true,
            notificationEnabled = true,
            reminderTime = "19:00",
            language = "Tiếng Việt",
            theme = com.emotionfriend.core.designsystem.theme.AppTheme.SYSTEM,
            onStartEdit = {},
            onCancelEdit = {},
            onSaveEdit = {},
            onNameChange = {},
            onAgeChange = {},
            onAvatarChange = {},
            onSoundToggle = {},
            onNotificationToggle = {},
            onReminderChange = {},
            onLanguageChange = {},
            onThemeChange = {}
        )
    }
}
