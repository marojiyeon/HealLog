package com.heallog.util

/**
 * Central emoji mapping for body parts.
 * Ensures consistency across all UI components (home screen, widgets, body map).
 */
object EmojiMapper {
    fun getEmojiForBodyPart(bodyPartId: String): String = when (bodyPartId) {
        // Head and neck
        "head" -> "🧠"
        "neck" -> "🫙"

        // Shoulders and arms
        "left_shoulder", "right_shoulder" -> "💪"
        "left_upper_arm", "right_upper_arm" -> "💪"
        "left_elbow", "right_elbow" -> "🦾"
        "left_forearm", "right_forearm" -> "🦾"
        "left_wrist", "right_wrist" -> "⌚"
        "left_hand", "right_hand" -> "🖐"

        // Torso
        "chest" -> "🫀"
        "abdomen" -> "🫁"
        "upper_back", "lower_back" -> "🔙"

        // Hips and legs
        "left_hip", "right_hip" -> "🦴"
        "left_thigh", "right_thigh" -> "🦵"
        "left_knee", "right_knee" -> "🦵"
        "left_shin", "right_shin" -> "🦵"
        "left_ankle", "right_ankle" -> "🦶"
        "left_foot", "right_foot" -> "🦶"

        // Fallback for unknown parts
        else -> "🩹"
    }
}
