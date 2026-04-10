package com.heallog.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.heallog.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userProfileDataStore: DataStore<Preferences>
        by preferencesDataStore(name = "user_profile")

@Singleton
class UserProfileStore @Inject constructor(private val context: Context) {

    private object Keys {
        val NICKNAME = stringPreferencesKey("nickname")
        val BIRTH_DATE = stringPreferencesKey("birth_date")
        val GENDER = stringPreferencesKey("gender")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val BLOOD_TYPE = stringPreferencesKey("blood_type")
        val SPORTS = stringPreferencesKey("sports")
        val EXERCISE_FREQUENCY = intPreferencesKey("exercise_frequency")
        val MEDICAL_CONDITIONS = stringPreferencesKey("medical_conditions")
        val ALLERGIES = stringPreferencesKey("allergies")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
    }

    val userProfile: Flow<UserProfile> = context.userProfileDataStore.data.map { prefs ->
        val sportsJson = prefs[Keys.SPORTS] ?: "[]"
        val sports = try {
            Json.decodeFromString<List<String>>(sportsJson)
        } catch (_: Exception) {
            emptyList()
        }
        UserProfile(
            nickname = prefs[Keys.NICKNAME] ?: "",
            birthDate = prefs[Keys.BIRTH_DATE] ?: "",
            gender = prefs[Keys.GENDER] ?: "",
            heightCm = prefs[Keys.HEIGHT_CM] ?: 0f,
            weightKg = prefs[Keys.WEIGHT_KG] ?: 0f,
            bloodType = prefs[Keys.BLOOD_TYPE] ?: "",
            sports = sports,
            exerciseFrequency = prefs[Keys.EXERCISE_FREQUENCY] ?: 0,
            medicalConditions = prefs[Keys.MEDICAL_CONDITIONS] ?: "",
            allergies = prefs[Keys.ALLERGIES] ?: "",
            profileImageUri = prefs[Keys.PROFILE_IMAGE_URI] ?: ""
        )
    }

    suspend fun updateAll(profile: UserProfile) {
        context.userProfileDataStore.edit { prefs ->
            prefs[Keys.NICKNAME] = profile.nickname
            prefs[Keys.BIRTH_DATE] = profile.birthDate
            prefs[Keys.GENDER] = profile.gender
            prefs[Keys.HEIGHT_CM] = profile.heightCm
            prefs[Keys.WEIGHT_KG] = profile.weightKg
            prefs[Keys.BLOOD_TYPE] = profile.bloodType
            prefs[Keys.SPORTS] = Json.encodeToString(profile.sports)
            prefs[Keys.EXERCISE_FREQUENCY] = profile.exerciseFrequency
            prefs[Keys.MEDICAL_CONDITIONS] = profile.medicalConditions
            prefs[Keys.ALLERGIES] = profile.allergies
            prefs[Keys.PROFILE_IMAGE_URI] = profile.profileImageUri
        }
    }

    suspend fun updateNickname(value: String) {
        context.userProfileDataStore.edit { it[Keys.NICKNAME] = value }
    }

    suspend fun updateBirthDate(value: String) {
        context.userProfileDataStore.edit { it[Keys.BIRTH_DATE] = value }
    }

    suspend fun updateGender(value: String) {
        context.userProfileDataStore.edit { it[Keys.GENDER] = value }
    }

    suspend fun updateHeightCm(value: Float) {
        context.userProfileDataStore.edit { it[Keys.HEIGHT_CM] = value }
    }

    suspend fun updateWeightKg(value: Float) {
        context.userProfileDataStore.edit { it[Keys.WEIGHT_KG] = value }
    }

    suspend fun updateBloodType(value: String) {
        context.userProfileDataStore.edit { it[Keys.BLOOD_TYPE] = value }
    }

    suspend fun updateSports(value: List<String>) {
        context.userProfileDataStore.edit { it[Keys.SPORTS] = Json.encodeToString(value) }
    }

    suspend fun updateExerciseFrequency(value: Int) {
        context.userProfileDataStore.edit { it[Keys.EXERCISE_FREQUENCY] = value }
    }

    suspend fun updateMedicalConditions(value: String) {
        context.userProfileDataStore.edit { it[Keys.MEDICAL_CONDITIONS] = value }
    }

    suspend fun updateAllergies(value: String) {
        context.userProfileDataStore.edit { it[Keys.ALLERGIES] = value }
    }

    suspend fun updateProfileImageUri(value: String) {
        context.userProfileDataStore.edit { it[Keys.PROFILE_IMAGE_URI] = value }
    }
}
