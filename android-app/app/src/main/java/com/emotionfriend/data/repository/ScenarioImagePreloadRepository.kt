package com.emotionfriend.data.repository

import android.content.Context
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import com.emotionfriend.core.config.AppConfig
import com.emotionfriend.core.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScenarioImagePreloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    @ApplicationScope private val appScope: CoroutineScope,
) {
    private companion object {
        // Keep first lessons extra warm so opening Learn is instant.
        const val PRIORITY_IMAGE_COUNT = 24
    }

    private val preloadedUrls = Collections.synchronizedSet(mutableSetOf<String>())

    fun preload(imageNames: List<String?>) {
        val urls = imageNames
            .mapNotNull { scenarioImageUrl(it) }
            .distinct()

        if (urls.isEmpty()) return

        val priorityUrls = urls.take(PRIORITY_IMAGE_COUNT)
        val otherUrls = urls.drop(PRIORITY_IMAGE_COUNT)

        val priorityToLoad = priorityUrls.filter { preloadedUrls.add(it) }
        val othersToLoad = otherUrls.filter { preloadedUrls.add(it) }

        if (priorityToLoad.isEmpty() && othersToLoad.isEmpty()) return

        appScope.launch {
            val metrics = context.resources.displayMetrics
            val targetWidthPx = metrics.widthPixels.coerceAtLeast(720)
            val targetHeightPx = (targetWidthPx * 0.56f).toInt().coerceAtLeast(360)

            priorityToLoad.forEach { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .size(targetWidthPx, targetHeightPx)
                    .precision(Precision.INEXACT)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoader.execute(request)
            }

            othersToLoad.forEach { url ->
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .size(targetWidthPx, targetHeightPx)
                    .precision(Precision.INEXACT)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build()
                imageLoader.enqueue(request)
            }
        }
    }

    private fun scenarioImageUrl(imageName: String?): String? {
        val name = imageName?.trim().orEmpty()
        if (name.isEmpty()) return null
        return "${AppConfig.BASE_URL.trimEnd('/')}/img/scenario_lessons/$name"
    }
}