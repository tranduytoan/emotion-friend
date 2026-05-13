package com.emotionfriend.core.di

import com.emotionfriend.core.config.AppConfig
import com.emotionfriend.data.remote.EmotionFriendApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    android.util.Log.d("EmotionFriendApi", message)
                }
            }
            level = LogLevel.BODY
        }
        engine {
            connectTimeout = AppConfig.CONNECT_TIMEOUT_MS
            socketTimeout  = AppConfig.SOCKET_TIMEOUT_MS
        }
    }

    @Provides
    @Singleton
    fun provideApiClient(httpClient: HttpClient): EmotionFriendApiClient =
        EmotionFriendApiClient(httpClient)
}
