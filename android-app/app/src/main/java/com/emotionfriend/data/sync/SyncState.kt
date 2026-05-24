package com.emotionfriend.data.sync

/**
 * Represents the current state of a sync operation.
 *
 * Observed by the UI to show sync status indicators where needed.
 */
sealed class SyncState {
    /** No sync in progress. */
    data object Idle : SyncState()
    /** Sync is actively running (push or pull). */
    data object Syncing : SyncState()
    /** Last sync completed successfully. [timestamp] is epoch millis. */
    data class Success(val timestamp: Long) : SyncState()
    /** Last sync failed. [message] describes the reason. */
    data class Error(val message: String) : SyncState()
}
