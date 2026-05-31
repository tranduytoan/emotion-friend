package com.emotionfriend.feature.express

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.FeedbackBanner
import com.emotionfriend.core.designsystem.components.FeedbackType
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.components.TeacherMyMessages
import com.emotionfriend.core.designsystem.components.VyEmotion
import com.emotionfriend.core.designsystem.components.toVyEmotionForCompanion
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.domain.model.EmotionType

// ---------------------------------------------------------------------------
// Camera practice screen (shown after permission is granted)
// ---------------------------------------------------------------------------

@Composable
fun ExpressCameraScreen(
    state    : ExpressUiState,
    onCapture: () -> Unit,
    onRetry  : () -> Unit,
    onNext   : () -> Unit,
    modifier : Modifier = Modifier
) {
    val context = LocalContext.current
    // Owned here; passed to CameraPreview so both can share the same instance.
    val imageCapture   = remember { ImageCapture.Builder().build() }
    var cameraError    by remember { mutableStateOf(false) }
    val tts            = rememberTtsPlayer()
    val teacherMessage = remember { TeacherMyMessages.randomCamera() }
    val teacherEmotion = if (state.captureState == CaptureState.DONE) {
        VyEmotion.CELEBRATING
    } else {
        state.promptEmotion.toVyEmotionForCompanion()
    }

    Column(
        modifier            = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Teacher My companion
        TeacherMyGuide(
            message = teacherMessage,
            onSpeak = { tts.speak(teacherMessage) },
            vyEmotion = teacherEmotion,
        )

        // Prompt card
        EmotionCard {
            Text(
                text      = state.promptEmotion.toEmoji(),
                style     = MaterialTheme.typography.displaySmall,
                modifier  = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text      = "Hãy làm khuôn mặt ${state.promptEmotion.toPromptLabel()} nào!",
                style     = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        // Camera viewfinder (or fallback if camera cannot be opened)
        if (cameraError) {
            CameraFallbackPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            )
        } else {
            CameraPreview(
                imageCapture  = imageCapture,
                onCameraError = { cameraError = true },
                modifier      = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(MaterialTheme.shapes.large)
            )
        }

        // Feedback
        FeedbackBanner(
            visible = state.captureState == CaptureState.DONE,
            type    = FeedbackType.CORRECT,
            message = state.feedbackMessage
        )

        // Action buttons
        if (state.captureState == CaptureState.DONE) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick  = onRetry,
                    modifier = Modifier.weight(1f)
                ) { Text("🔄 Thử lại") }

                EmotionPrimaryButton(
                    text     = "Cảm xúc khác →",
                    onClick  = onNext,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            EmotionPrimaryButton(
                text    = "📸 Chụp nào!",
                onClick = {
                    // In-memory capture — ImageProxy is closed immediately,
                    // no image is written to disk or any persistent store.
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                image.close() // Discard — no persistent storage
                                onCapture()
                            }
                            override fun onError(exception: ImageCaptureException) {
                                onCapture() // Still show feedback on capture error
                            }
                        }
                    )
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Fallback shown when CameraX cannot be bound (emulator / no camera)
// ---------------------------------------------------------------------------

@Composable
private fun CameraFallbackPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier         = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📷", style = MaterialTheme.typography.displayMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Camera không khả dụng",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVar
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Helpers (internal to feature/express package)
// ---------------------------------------------------------------------------

internal fun EmotionType.toPromptLabel(): String = when (this) {
    EmotionType.HAPPY     -> "VUI"
    EmotionType.SAD       -> "BUỒN"
    EmotionType.ANGRY     -> "TỨC GIẬN"
    EmotionType.SURPRISED -> "NGẠC NHIÊN"
    EmotionType.CALM      -> "BÌNH TĨNH"
    EmotionType.TIRED     -> "MỆT MỎI"
}

internal fun EmotionType.toEmoji(): String = when (this) {
    EmotionType.HAPPY     -> "😊"
    EmotionType.SAD       -> "😢"
    EmotionType.ANGRY     -> "😠"
    EmotionType.SURPRISED -> "😲"
    EmotionType.CALM      -> "😌"
    EmotionType.TIRED     -> "😴"
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CameraScreenIdlePreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            // Use fallback path (no real camera in preview)
            ExpressCameraScreen(
                state     = ExpressUiState(
                    permissionState = PermissionState.GRANTED,
                    captureState    = CaptureState.IDLE,
                    promptEmotion   = EmotionType.HAPPY
                ),
                onCapture = {},
                onRetry   = {},
                onNext    = {}
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CameraScreenFeedbackPreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            ExpressCameraScreen(
                state     = ExpressUiState(
                    permissionState = PermissionState.GRANTED,
                    captureState    = CaptureState.DONE,
                    promptEmotion   = EmotionType.SURPRISED,
                    feedbackMessage = "Con làm tốt lắm! 🌟"
                ),
                onCapture = {},
                onRetry   = {},
                onNext    = {}
            )
        }
    }
}
