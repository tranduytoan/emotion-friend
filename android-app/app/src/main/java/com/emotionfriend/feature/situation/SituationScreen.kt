package com.emotionfriend.feature.situation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold

@Composable
fun SituationScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmotionScreenScaffold(title = "Tình huống xã hội", onBack = onBack) {
        Box(
            modifier         = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = "Tình huống xã hội",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
