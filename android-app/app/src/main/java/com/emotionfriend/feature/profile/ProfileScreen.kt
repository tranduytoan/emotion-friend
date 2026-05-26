package com.emotionfriend.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
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
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.dimensions

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var nameInput by rememberSaveable { mutableStateOf("") }
    var ageInput by rememberSaveable { mutableStateOf("") }
    var soundEnabled by rememberSaveable { mutableStateOf(false) }
    var notificationEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderTime by rememberSaveable { mutableStateOf("") }
    var language by rememberSaveable { mutableStateOf("") }

    val startEdit = {
        nameInput = state.name
        ageInput = state.age.toString()
        soundEnabled = state.settings.soundEnabled
        notificationEnabled = state.settings.notificationEnabled
        reminderTime = state.settings.reminderTime
        language = state.settings.language
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

        viewModel.updateProfile(name = safeName, age = safeAge)
        viewModel.updateSettings(
            soundEnabled = soundEnabled,
            notificationEnabled = notificationEnabled,
            reminderTime = safeReminder,
            language = safeLanguage
        )
        isEditing = false
    }

    EmotionScreenScaffold(title = "Hồ sơ cá nhân", onBack = onBack) {
        ProfileContent(
            state = state,
            isEditing = isEditing,
            nameInput = nameInput,
            ageInput = ageInput,
            soundEnabled = soundEnabled,
            notificationEnabled = notificationEnabled,
            reminderTime = reminderTime,
            language = language,
            onStartEdit = startEdit,
            onCancelEdit = cancelEdit,
            onSaveEdit = saveEdit,
            onNameChange = { nameInput = it },
            onAgeChange = { ageInput = it },
            onSoundToggle = { soundEnabled = it },
            onNotificationToggle = { notificationEnabled = it },
            onReminderChange = { reminderTime = it },
            onLanguageChange = { language = it },
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
    soundEnabled: Boolean,
    notificationEnabled: Boolean,
    reminderTime: String,
    language: String,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveEdit: () -> Unit,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onReminderChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
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
                    modifier = Modifier.size(88.dp)
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
                        color = OnSurfaceVar
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Bé đang luyện cảm xúc mỗi ngày 🌟",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVar
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
                soundEnabled = soundEnabled,
                notificationEnabled = notificationEnabled,
                reminderTime = reminderTime,
                language = language,
                onNameChange = onNameChange,
                onAgeChange = onAgeChange,
                onSoundToggle = onSoundToggle,
                onNotificationToggle = onNotificationToggle,
                onReminderChange = onReminderChange,
                onLanguageChange = onLanguageChange,
                onCancelEdit = onCancelEdit,
                onSaveEdit = onSaveEdit
            )
        }

        // --- Basic info -----------------------------------------------------
        SectionHeader(text = "Thông tin cơ bản")
        EmotionCard {
            InfoRow(label = "ID", value = state.userId.toString())
            InfoRow(label = "Họ tên", value = state.name)
            InfoRow(label = "Tuổi", value = state.age.toString())
        }

        // --- Settings --------------------------------------------------------
        SectionHeader(text = "Cài đặt")
        EmotionCard {
            SettingRow(label = "Âm thanh", enabled = state.settings.soundEnabled)
            SettingRow(label = "Thông báo", enabled = state.settings.notificationEnabled)
            InfoRow(label = "Giờ nhắc", value = state.settings.reminderTime)
            InfoRow(label = "Ngôn ngữ", value = state.settings.language)
        }

        // --- Progress snapshot ----------------------------------------------
        SectionHeader(text = "Tiến trình")
        EmotionCard {
            InfoRow(label = "Bài đã làm", value = state.progress.totalExercises.toString())
            InfoRow(label = "Trả lời đúng", value = state.progress.correctAnswers.toString())
            InfoRow(label = "Chuỗi hiện tại", value = "${state.progress.currentStreak} ngày")
            InfoRow(label = "Chuỗi dài nhất", value = "${state.progress.longestStreak} ngày")
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
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun AvatarBubble(
    emoji: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVar,
            modifier = Modifier.weight(1f)
        )
        ValueChip(text = value)
    }
}

@Composable
private fun SettingRow(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val chipText = if (enabled) "Bật" else "Tắt"
    val chipColor = if (enabled) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVar,
            modifier = Modifier.weight(1f)
        )
        ValueChip(text = chipText, backgroundColor = chipColor)
    }
}

@Composable
private fun ValueChip(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surface
) {
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        tonalElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun EditProfileCard(
    nameInput: String,
    ageInput: String,
    soundEnabled: Boolean,
    notificationEnabled: Boolean,
    reminderTime: String,
    language: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onReminderChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
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
                        color = OnSurfaceVar
                    )
                }
            }
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

@Composable
private fun SettingToggleRow(
    label: String,
    helper: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVar
            )
            Text(
                text = helper,
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVar
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics { role = Role.Switch }
        )
    }
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
                    language = "Tiếng Việt"
                ),
                progress = ProfileProgress(
                    totalExercises = 0,
                    correctAnswers = 0,
                    currentStreak = 0,
                    longestStreak = 0
                )
            ),
            isEditing = true,
            nameInput = "Bé Minh",
            ageInput = "8",
            soundEnabled = true,
            notificationEnabled = true,
            reminderTime = "19:00",
            language = "Tiếng Việt",
            onStartEdit = {},
            onCancelEdit = {},
            onSaveEdit = {},
            onNameChange = {},
            onAgeChange = {},
            onSoundToggle = {},
            onNotificationToggle = {},
            onReminderChange = {},
            onLanguageChange = {}
        )
    }
}
