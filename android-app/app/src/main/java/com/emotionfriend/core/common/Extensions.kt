package com.emotionfriend.core.common

import android.content.Context
import android.widget.Toast

/**
 * App-wide Kotlin / Android extension functions.
 *
 * Keep additions here minimal and general-purpose.
 * Screen-specific helpers belong in their own feature package.
 */

/** Show a short [Toast] without needing to chain `.show()` explicitly. */
fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Returns `null` when the string is blank (empty or whitespace-only),
 * otherwise returns the string itself.
 *
 * Useful when persisting optional text fields to Room / JSON.
 */
fun String.nullIfBlank(): String? = ifBlank { null }
