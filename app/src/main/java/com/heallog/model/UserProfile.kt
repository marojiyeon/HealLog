package com.heallog.model

data class UserProfile(
    val nickname: String = "",
    val birthDate: String = "",         // ISO format: "1990-01-15"
    val gender: String = "",            // MALE / FEMALE / OTHER
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val bloodType: String = "",         // A / B / AB / O  (empty = 모름)
    val sports: List<String> = emptyList(),
    val exerciseFrequency: Int = 0,     // days per week, 0-7
    val medicalConditions: String = "",
    val allergies: String = "",
    val profileImageUri: String = ""
) {
    val bmi: Float
        get() = if (heightCm > 0f && weightKg > 0f) {
            val heightM = heightCm / 100f
            weightKg / (heightM * heightM)
        } else 0f
}
