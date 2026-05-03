package com.emotionfriend.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold

@Composable
fun HomeScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToSituation: () -> Unit,
    onNavigateToExpress: () -> Unit,
    onNavigateToRelax: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmotionScreenScaffold(title = "Emotion Friend") {
        Column(
            modifier            = modifier
                .fillMaxSize()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Chọn hoạt động",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            EmotionPrimaryButton(text = "Học cảm xúc 📚",      onClick = onNavigateToLearn)
            Spacer(modifier = Modifier.height(12.dp))
            EmotionPrimaryButton(text = "Tình huống xã hội 🤝", onClick = onNavigateToSituation)
            Spacer(modifier = Modifier.height(12.dp))
            EmotionPrimaryButton(text = "Luyện biểu đạt 📷",   onClick = onNavigateToExpress)
            Spacer(modifier = Modifier.height(12.dp))
            EmotionPrimaryButton(text = "Thư giãn 🌈",          onClick = onNavigateToRelax)
            Spacer(modifier = Modifier.height(12.dp))
            EmotionPrimaryButton(text = "Cảm xúc hôm nay 📓",  onClick = onNavigateToJournal)
            Spacer(modifier = Modifier.height(12.dp))
            EmotionPrimaryButton(text = "Tiến trình 🌟",        onClick = onNavigateToProgress)
        }
    }
}
