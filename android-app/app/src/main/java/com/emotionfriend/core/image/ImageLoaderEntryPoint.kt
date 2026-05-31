package com.emotionfriend.core.image

import android.content.Context
import coil.ImageLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ImageLoaderEntryPoint {
    fun imageLoader(): ImageLoader
}

fun Context.appImageLoader(): ImageLoader =
    EntryPointAccessors
        .fromApplication(this, ImageLoaderEntryPoint::class.java)
        .imageLoader()
