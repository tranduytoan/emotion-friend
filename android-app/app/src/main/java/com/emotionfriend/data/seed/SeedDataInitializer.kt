package com.emotionfriend.data.seed

import android.content.Context
import android.util.Log
import com.emotionfriend.core.di.ApplicationScope
import com.emotionfriend.data.local.EmotionCardDao
import com.emotionfriend.data.local.EmotionCardEntity
import com.emotionfriend.data.local.ScenarioLessonDao
import com.emotionfriend.data.local.ScenarioLessonEntity
import com.emotionfriend.domain.model.EmotionType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds Room from bundled JSON assets on first launch (when tables are empty).
 * All work is done on the [ApplicationScope] coroutine scope so it never
 * blocks the main thread.
 *
 * Call [initialize] once from [EmotionFriendApplication.onCreate].
 */
@Singleton
class SeedDataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emotionCardDao: EmotionCardDao,
    private val scenarioLessonDao: ScenarioLessonDao,
    @ApplicationScope private val scope: CoroutineScope,
) {

    private val json = Json { ignoreUnknownKeys = true }

    fun initialize() {
        scope.launch {
            try {
                seedEmotionCards()
                seedScenarioLessons()
            } catch (e: Exception) {
                Log.e(TAG, "Seed failed — app will still work with empty Room tables", e)
            }
        }
    }

    // ── private ───────────────────────────────────────────────────────────────

    private suspend fun seedEmotionCards() {
        if (emotionCardDao.count() > 0) return

        val raw = context.assets.open(ASSET_EMOTION_CARDS).bufferedReader().readText()
        val dtos = json.decodeFromString<List<SeedEmotionCardDto>>(raw)
        val entities = dtos.mapNotNull { dto ->
            val type = EmotionType.entries.find { it.name == dto.type } ?: return@mapNotNull null
            EmotionCardEntity(
                id          = dto.id,
                label       = dto.label,
                emoji       = dto.emoji,
                type        = type,
                description = dto.description,
            )
        }
        emotionCardDao.upsertAll(entities)
        Log.i(TAG, "Seeded ${entities.size} emotion cards from assets.")
    }

    private suspend fun seedScenarioLessons() {
        if (scenarioLessonDao.count() > 0) return

        val raw = context.assets.open(ASSET_SCENARIO_LESSONS).bufferedReader().readText()
        val dtos = json.decodeFromString<List<SeedScenarioLessonDto>>(raw)
        val entities = dtos.mapNotNull { dto ->
            val correctEmotion = EmotionType.entries.find { it.name == dto.correctEmotion }
                ?: return@mapNotNull null
            val options = dto.options.mapNotNull { name ->
                EmotionType.entries.find { it.name == name }
            }
            ScenarioLessonEntity(
                id             = dto.id,
                title          = dto.title,
                situationText  = dto.situationText,
                imageName      = dto.imageName,
                correctEmotion = correctEmotion,
                options        = options,
                explanation    = dto.explanation,
            )
        }
        scenarioLessonDao.upsertAll(entities)
        Log.i(TAG, "Seeded ${entities.size} scenario lessons from assets.")
    }

    companion object {
        private const val TAG = "SeedDataInitializer"
        private const val ASSET_EMOTION_CARDS    = "seed/emotion_cards.json"
        private const val ASSET_SCENARIO_LESSONS = "seed/scenario_lessons.json"
    }
}
