package com.emotionfriend.feature.express

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview as CameraPreview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.FeedbackBanner
import com.emotionfriend.core.designsystem.components.FeedbackType
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.domain.model.EmotionType

// ---------------------------------------------------------------------------
// Screen entry point
// ---------------------------------------------------------------------------

@Composable
fun ExpressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpressViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> viewModel.onPermissionResult(granted) }

    LaunchedEffect(Unit) {
        if (state.permissionState == PermissionState.UNKNOWN) {
            permLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = onBack) {
        when (state.permissionState) {
            PermissionState.UNKNOWN -> {
                Box(
                    modifier         = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            PermissionState.DENIED  -> PermissionDeniedContent(modifier = modifier)
            PermissionState.GRANTED -> ExpressGrantedContent(
                state     = state,
                onCapture = viewModel::onCapture,
                onRetry   = viewModel::onRetry,
                onNext    = viewModel::nextPrompt,
                modifier  = modifier
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Camera content
// ---------------------------------------------------------------------------

@Composable
private fun ExpressGrantedContent(
    state    : ExpressUiState,
    onCapture: () -> Unit,
    onRetry  : () -> Unit,
    onNext   : () -> Unit,
    modifier : Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier            = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        // CameraX live preview — no images are stored
        AndroidView(
            factory  = { ctx ->
                val previewView         = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener(
                    {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = CameraPreview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_FRONT_CAMERA,
                                preview
                            )
                        } catch (_: Exception) {
                            try {
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview
                                )
                            } catch (_: Exception) { /* no camera available */ }
                        }
                    },
                    ContextCompat.getMainExecutor(ctx)
                )
                previewView
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(MaterialTheme.shapes.large)
        )

        // Feedback banner (shown after mock capture)
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
                ) {
                    Text("🔄 Thử lại")
                }
                EmotionPrimaryButton(
                    text     = "Cảm xúc khác →",
                    onClick  = onNext,
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            EmotionPrimaryButton(
                text    = "📸 Chụp nào!",
                onClick = onCapture
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Permission denied UI
// ---------------------------------------------------------------------------

@Composable
private fun PermissionDeniedContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier            = modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📷", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            text  = "Cần quyền truy cập camera",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = "Hãy cấp quyền camera trong cài đặt để dùng tính năng này.",
            style     = MaterialTheme.typography.bodyLarge,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        EmotionPrimaryButton(
            text    = "Mở cài đặt",
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }
}

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

private fun EmotionType.toPromptLabel(): String = when (this) {
    EmotionType.HAPPY     -> "VUI"
    EmotionType.SAD       -> "BUỒN"
    EmotionType.ANGRY     -> "TỨC GIẬN"
    EmotionType.SURPRISED -> "NGẠC NHIÊN"
    EmotionType.CALM      -> "BÌNH TĨNH"
    EmotionType.TIRED     -> "MỆT MỎI"
}

private fun EmotionType.toEmoji(): String = when (this) {
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
private fun ExpressIdlePreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            ExpressGrantedContent(
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
private fun ExpressFeedbackPreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            ExpressGrantedContent(
                state     = ExpressUiState(
                    permissionState = PermissionState.GRANTED,
                    captureState    = CaptureState.DONE,
                    promptEmotion   = EmotionType.HAPPY,
                    feedbackMessage = "Con làm tốt lắm! 🌟"
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
private fun ExpressDeniedPreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            PermissionDeniedContent()
        }
    }
}
