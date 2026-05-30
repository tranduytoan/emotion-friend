package com.emotionfriend.domain.model

/**
 * User roles in Emotion Friend.
 *
 * - CHILD : the child who uses all learning/practice features.
 * - ADMIN : administrator who manages content via the admin web panel.
 *
 * Role is persisted in DataStore and determines the navigation start destination
 * after login (see [EmotionFriendNavHost]).
 */
enum class UserRole(val displayName: String) {
    CHILD("Trẻ em"),
    ADMIN("Quản trị viên"),
}
