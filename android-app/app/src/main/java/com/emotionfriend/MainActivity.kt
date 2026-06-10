package com.emotionfriend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.navigation.EmotionFriendNavHost
import com.emotionfriend.feature.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val uiState by profileViewModel.uiState.collectAsState()
            
            EmotionFriendTheme(appTheme = uiState.settings.theme) {
                EmotionFriendNavHost()
            }
        }
    }
}
