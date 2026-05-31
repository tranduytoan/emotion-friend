package com.emotionfriend.data.repository

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.emotionfriend.domain.model.Story
import com.emotionfriend.feature.story.storyPageUrls
import com.emotionfriend.core.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryImagePreloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    @ApplicationScope private val appScope: CoroutineScope,
) {

    private val preloadedUrls = Collections.synchronizedSet(mutableSetOf<String>())

    fun preload(stories: List<Story>) {
        val urls = stories
            .flatMap { it.storyPageUrls() }
            .distinct()
            .filter { preloadedUrls.add(it) }

        if (urls.isEmpty()) return

        appScope.launch {
            val metrics = context.resources.displayMetrics
            val targetWidthPx = metrics.widthPixels.coerceAtLeast(720)
            val targetHeightPx = (targetWidthPx * 0.56f).toInt().coerceAtLeast(360)

            urls.forEach { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .size(targetWidthPx, targetHeightPx)
                    .precision(Precision.INEXACT)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()

                // Non-blocking preload so the UI stays responsive.
                imageLoader.enqueue(request)
            }
        }
    }
}
