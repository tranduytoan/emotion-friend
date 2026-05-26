package com.emotionfriend.data.sync

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central coordinator for all Room ↔ Backend synchronisation.
 *
 * - Schedules [SyncWorker] as a periodic background job (every 30 min, requires network).
 * - Exposes [syncState] so the UI can observe progress without tight coupling.
 * - [triggerNow] enqueues a one-time sync (e.g., after the user saves a journal entry).
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val workManager = WorkManager.getInstance(context)

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    /** Network-required constraints applied to all sync work requests. */
    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // ── Periodic sync ─────────────────────────────────────────────────────────

    /**
     * Registers the 30-minute periodic background sync.
     * Safe to call multiple times — uses [ExistingPeriodicWorkPolicy.KEEP].
     * Call once from Application.onCreate.
     */
    fun schedulePeriodicSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniquePeriodicWork(
            TAG_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Log.d(TAG, "Periodic sync scheduled (30 min, network required).")
    }

    // ── On-demand sync ────────────────────────────────────────────────────────

    /**
     * Enqueues a one-time sync immediately (e.g., triggered by a user action).
     * Uses [ExistingWorkPolicy.REPLACE] so a pending sync is restarted.
     */
    fun triggerNow() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
            .build()

        workManager.enqueueUniqueWork(TAG_ONE_TIME, ExistingWorkPolicy.REPLACE, request)
        Log.d(TAG, "One-time sync enqueued.")
    }

    // ── State helpers (called by SyncWorker) ──────────────────────────────────

    internal fun onSyncStarted()  { _syncState.value = SyncState.Syncing }
    internal fun onSyncSuccess()  { _syncState.value = SyncState.Success(System.currentTimeMillis()) }
    internal fun onSyncError(msg: String) { _syncState.value = SyncState.Error(msg) }

    companion object {
        private const val TAG = "SyncManager"
        const val TAG_PERIODIC  = "emotion_friend_periodic_sync"
        const val TAG_ONE_TIME  = "emotion_friend_one_time_sync"
    }
}
