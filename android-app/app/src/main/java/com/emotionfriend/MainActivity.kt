package com.emotionfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.navigation.EmotionFriendNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmotionFriendTheme {
                EmotionFriendNavHost()
            }
        }
    }
}
