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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.dimensions

/**
 * Standard screen scaffold for all feature screens.
 *
 * Provides:
 * - Material3 Scaffold with CenterAlignedTopAppBar
 * - Optional back navigation button
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
        containerColor      = MaterialTheme.colorScheme.background,
        topBar = {
            if (title != null) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text  = title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
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
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    horizontal = MaterialTheme.dimensions.screenHorizontalPadding,
                    vertical = MaterialTheme.dimensions.screenVerticalPadding,
                ),
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
