package com.emotionfriend.domain.model

/**
 * User roles in Emotion Friend.
 *
 * - CHILD      : the child with autism who uses all learning/practice features.
 * - PARENT     : parent/guardian who monitors progress and manages settings.
 * - THERAPIST  : specialist who can view multiple children's progress (future scope).
 *
 * Role is persisted in DataStore and determines the navigation start destination
 * after login (see [EmotionFriendNavHost]).
 */
enum class UserRole(val displayName: String) {
    CHILD("Trẻ em"),
    PARENT("Phụ huynh"),
    THERAPIST("Chuyên gia trị liệu"),
}
