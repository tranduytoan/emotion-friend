package com.emotionfriend.data.local

/**
 * Tracks the sync lifecycle of a Room entity row.
 *
 * - PENDING  : created / modified locally, not yet pushed to backend
 * - SYNCED   : matches the server state
 * - CONFLICT : server rejected the row or there is a version mismatch
 */
enum class SyncStatus { PENDING, SYNCED, CONFLICT }
