package com.emotionfriend.core.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    private const val NETWORK_CACHE_SIZE_BYTES = 64L * 1024L * 1024L   // 64 MB
    private const val DISK_CACHE_SIZE_BYTES = 256L * 1024L * 1024L     // 256 MB

    @Provides
    @Singleton
    fun provideImageOkHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val networkCacheDir = File(context.cacheDir, "image_network_cache")
        val networkCache = Cache(networkCacheDir, NETWORK_CACHE_SIZE_BYTES)

        return OkHttpClient.Builder()
            .cache(networkCache)
            .build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        imageOkHttpClient: OkHttpClient,
    ): ImageLoader {
        val diskCacheDir = File(context.cacheDir, "image_disk_cache")

        return ImageLoader.Builder(context)
            .okHttpClient(imageOkHttpClient)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(diskCacheDir)
                    .maxSizeBytes(DISK_CACHE_SIZE_BYTES)
                    .build()
            }
            .crossfade(true)
            .build()
    }
}
