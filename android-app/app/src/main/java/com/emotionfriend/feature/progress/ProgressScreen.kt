package com.emotionfriend.feature.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold

@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmotionScreenScaffold(title = "Tiến trình", onBack = onBack) {
        Box(
            modifier         = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "Tiến trình",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
