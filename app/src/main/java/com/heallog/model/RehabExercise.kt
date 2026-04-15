package com.heallog.model

data class RehabExercise(
    val id: String,
    val name: String,
    val description: String,
    val steps: List<String>,
    val durationMin: Int,
    val reps: String,
    val difficulty: Difficulty,
    val targetBodyParts: List<String>
) {
    enum class Difficulty(val label: String) {
        EASY("쉬움"),
        MEDIUM("보통"),
        HARD("어려움")
    }
}
