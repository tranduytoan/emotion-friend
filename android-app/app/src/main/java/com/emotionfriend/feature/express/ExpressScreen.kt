package com.emotionfriend.feature.express

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.domain.model.EmotionType

// ---------------------------------------------------------------------------
// Screen entry point — owns permission gate, delegates camera UI
// ---------------------------------------------------------------------------

@Composable
fun ExpressScreen(
    onBack  : () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpressCameraViewModel = hiltViewModel()
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
            PermissionState.UNKNOWN -> Box(
                modifier         = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            PermissionState.DENIED  -> PermissionDeniedContent(modifier = modifier)

            PermissionState.GRANTED -> ExpressCameraScreen(
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
        Text(text = "Cần quyền truy cập camera", style = MaterialTheme.typography.headlineSmall)
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
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PermissionDeniedPreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Luyện biểu đạt", onBack = {}) {
            PermissionDeniedContent()
        }
    }
}



