package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.WarmCream

/**
 * Standard screen scaffold for all feature screens.
 *
 * Provides:
 * - Warm cream background
 * - Optional top app bar with back button
 * - Safe-drawing insets
 * - Content slot with consistent horizontal padding
 *
 * @param title       Screen title shown in the top bar (null = no top bar)
 * @param onBack      Back navigation callback (null = no back button)
 * @param content     Screen body
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionScreenScaffold(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor      = WarmCream,
        topBar = {
            if (title != null) {
                TopAppBar(
                    title = {
                        Text(
                            text  = title,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector       = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Quay lại"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = WarmCream,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmotionScreenScaffoldPreview() {
    EmotionFriendTheme {
        EmotionScreenScaffold(title = "Học cảm xúc", onBack = {}) {
            Box(modifier = Modifier.fillMaxSize())
        }
    }
}
