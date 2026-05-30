package com.emotionfriend.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.emotionfriend.data.local.EmotionCardEntity
import com.emotionfriend.data.local.JournalEntryDao
import com.emotionfriend.data.local.PracticeAttemptDao
import com.emotionfriend.data.local.StoryDao
import com.emotionfriend.data.local.StoryEntity
import com.emotionfriend.data.local.SyncStatus
import com.emotionfriend.data.remote.EmotionFriendApiClient
import com.emotionfriend.data.remote.ApiResult
import com.emotionfriend.data.remote.dto.CreateJournalEntryRequest
import com.emotionfriend.data.remote.dto.CreatePracticeAttemptRequest
import com.emotionfriend.data.repository.EmotionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Background sync worker — runs on WorkManager's Hilt-aware executor.
 *
 * Responsibilities:
 * 1. **Push** — upload locally PENDING [JournalEntryEntity] and [PracticeAttemptEntity] rows.
 * 2. **Pull** — fetch master data (emotions) from backend and upsert into Room.
 *
 * Returns [Result.success] when all operations complete (even partial failures are logged
 * but do not block future runs). Returns [Result.retry] on network errors.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiClient: EmotionFriendApiClient,
    private val journalEntryDao: JournalEntryDao,
    private val practiceAttemptDao: PracticeAttemptDao,
    private val emotionRepository: EmotionRepository,
    private val storyDao: StoryDao,
    private val syncManager: SyncManager,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        syncManager.onSyncStarted()
        Log.i(TAG, "Sync started (attempt ${runAttemptCount + 1})")

        return try {
            pushJournalEntries()
            pushPracticeAttempts()
            pullEmotions()
            pullStories()
            syncManager.onSyncSuccess()
            Log.i(TAG, "Sync completed successfully.")
            Result.success()
        } catch (e: Exception) {
            val msg = e.message ?: "Unknown sync error"
            Log.w(TAG, "Sync failed: $msg", e)
            syncManager.onSyncError(msg)
            // Retry up to 3 times (WorkManager default) then give up gracefully
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    // ── Push ──────────────────────────────────────────────────────────────────

    private suspend fun pushJournalEntries() {
        val pending = journalEntryDao.getPending()
        if (pending.isEmpty()) return
        Log.d(TAG, "Pushing ${pending.size} journal entries…")

        for (entry in pending) {
            val request = CreateJournalEntryRequest(
                childId    = entry.childId,
                emotionType = entry.emotionType.name,
                note       = entry.note ?: "",
            )
            when (val result = apiClient.submitJournalEntry(request)) {
                is ApiResult.Success -> journalEntryDao.updateSyncStatus(entry.id, SyncStatus.SYNCED)
                is ApiResult.Error   -> {
                    Log.w(TAG, "Failed to push journal ${entry.id}: ${result.message}")
                    journalEntryDao.updateSyncStatus(entry.id, SyncStatus.CONFLICT)
                }
            }
        }
    }

    private suspend fun pushPracticeAttempts() {
        val pending = practiceAttemptDao.getPending()
        if (pending.isEmpty()) return
        Log.d(TAG, "Pushing ${pending.size} practice attempts…")

        for (attempt in pending) {
            val request = CreatePracticeAttemptRequest(
                childId       = attempt.childId,
                scenarioId    = attempt.promptId,
                selectedIndex = 0, // index not stored; backend is best-effort
                isCorrect     = attempt.isCorrect ?: false,
                promptEmotion = attempt.correctEmotion?.name,
            )
            when (val result = apiClient.submitPracticeAttempt(request)) {
                is ApiResult.Success -> practiceAttemptDao.updateSyncStatus(attempt.id, SyncStatus.SYNCED)
                is ApiResult.Error   -> {
                    Log.w(TAG, "Failed to push attempt ${attempt.id}: ${result.message}")
                    practiceAttemptDao.updateSyncStatus(attempt.id, SyncStatus.CONFLICT)
                }
            }
        }
    }

    // ── Pull ──────────────────────────────────────────────────────────────────

    private suspend fun pullEmotions() {
        when (val result = apiClient.getEmotions()) {
            is ApiResult.Success -> {
                val cards = result.data.map { dto ->
                    com.emotionfriend.data.local.EmotionCardEntity(
                        id          = dto.id,
                        label       = dto.label,
                        emoji       = dto.emoji,
                        type        = runCatching {
                            com.emotionfriend.domain.model.EmotionType.valueOf(dto.emotionType)
                        }.getOrElse { com.emotionfriend.domain.model.EmotionType.HAPPY },
                        description = dto.description,
                    )
                }
                emotionRepository.upsertAll(cards.map { entity ->
                    com.emotionfriend.domain.model.EmotionCard(
                        id          = entity.id,
                        label       = entity.label,
                        emoji       = entity.emoji,
                        type        = entity.type,
                        description = entity.description,
                    )
                })
                Log.d(TAG, "Pulled ${cards.size} emotions from backend.")
            }
            is ApiResult.Error -> Log.d(TAG, "Pull emotions skipped (offline?): ${result.message}")
        }
    }

    private suspend fun pullStories() {
        when (val result = apiClient.getStories()) {
            is ApiResult.Success -> {
                val entities = result.data.map { dto ->
                    StoryEntity(
                        id       = dto.id,
                        title    = dto.title,
                        content  = dto.content,
                        category = dto.category,
                    )
                }
                storyDao.upsertAll(entities)
                Log.d(TAG, "Pulled ${entities.size} stories from backend.")
            }
            is ApiResult.Error -> Log.d(TAG, "Pull stories skipped (offline?): ${result.message}")
        }
    }

    companion object {
        private const val TAG = "SyncWorker"
        private const val MAX_RETRIES = 3
    }
}
