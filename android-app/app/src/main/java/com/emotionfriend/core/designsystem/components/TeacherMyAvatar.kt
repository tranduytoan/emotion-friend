package com.emotionfriend.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.compose.AsyncImage
import coil.size.Precision
import com.emotionfriend.core.config.AppConfig
import com.emotionfriend.core.designsystem.theme.SunYellow80
import com.emotionfriend.core.designsystem.theme.dimensions
import com.emotionfriend.core.image.appImageLoader

/**
 * Circular avatar for "Cô giáo Vy".
 *
 * Displays a different emoji/image per [VyEmotion] state.
 * When real image files are added to res/drawable/ (vy_neutral.png, vy_excited.png, etc.)
 * replace the emoji fallback in [vyEmotionEmoji] with Image composable calls.
 *
 * @param size     Diameter in dp (default 64).
 * @param emotion  Current emotional state — drives which image/emoji is shown.
 */
@Composable
fun TeacherMyAvatar(
    modifier: Modifier = Modifier,
    size: Dp? = null,
    emotion: VyEmotion = VyEmotion.NEUTRAL,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val imageLoader = remember(context) { context.appImageLoader() }
    val resolvedSize = size ?: MaterialTheme.dimensions.emojiLg
    val sizePx = with(density) { resolvedSize.roundToPx() }
    val fontSize = with(density) { (resolvedSize.toPx() * 0.52f).toSp() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(resolvedSize)
            .clip(CircleShape)
            .background(SunYellow80),
    ) {
        Text(
            text     = vyEmotionEmoji(emotion),
            fontSize = fontSize,
        )
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(vyEmotionImageUrl(emotion))
                .size(sizePx, sizePx)
                .precision(Precision.INEXACT)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentDescription = "Cô giáo Vy",
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}

/** Maps VyEmotion to a placeholder emoji until designer images are ready. */
private fun vyEmotionEmoji(emotion: VyEmotion): String = when (emotion) {
    VyEmotion.NEUTRAL      -> "👩‍🏫"
    VyEmotion.EXCITED      -> "🥳"
    VyEmotion.HAPPY        -> "😄"
    VyEmotion.ENCOURAGING  -> "🤗"
    VyEmotion.CALM         -> "😌"
    VyEmotion.CELEBRATING  -> "🎉"
}

internal fun vyEmotionImageUrl(emotion: VyEmotion): String {
    val imageName = when (emotion) {
        VyEmotion.NEUTRAL     -> "Calm.png"
        VyEmotion.EXCITED     -> "Surprised.png"
        VyEmotion.HAPPY       -> "Happy.png"
        VyEmotion.ENCOURAGING -> "Sad.png"
        VyEmotion.CALM        -> "Calm.png"
        VyEmotion.CELEBRATING -> "Happy.png"
    }
    return "${AppConfig.BASE_URL.trimEnd('/')}/img/miss-vy/$imageName"
}
