package com.heallog.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heallog.data.datastore.UserProfileStore
import com.heallog.data.repository.InjuryRepository
import com.heallog.model.InjuryStatus
import com.heallog.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class InjuryStats(
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val avgRecoveryDays: Double = 0.0,
    val mostInjuredPart: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileStore: UserProfileStore,
    private val injuryRepository: InjuryRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userProfileStore.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile())

    val injuryStats: StateFlow<InjuryStats> = injuryRepository.getAllInjuries()
        .map { injuries ->
            val totalCount = injuries.size
            val activeCount = injuries.count { it.status == InjuryStatus.ACTIVE }
            val healedWithDate = injuries.filter {
                it.status == InjuryStatus.HEALED && it.updatedAt != null
            }
            val avgRecoveryDays = if (healedWithDate.isEmpty()) 0.0
            else healedWithDate.map {
                ChronoUnit.DAYS.between(it.occurredAt, it.updatedAt!!.toLocalDate()).toDouble()
            }.average()
            val mostInjuredPart = injuries
                .groupBy { it.bodyPart }
                .maxByOrNull { it.value.size }
                ?.key
            InjuryStats(totalCount, activeCount, avgRecoveryDays, mostInjuredPart)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InjuryStats())

    fun updateAll(profile: UserProfile) = viewModelScope.launch {
        userProfileStore.updateAll(profile)
    }

    fun updateNickname(value: String) = viewModelScope.launch {
        userProfileStore.updateNickname(value)
    }

    fun updateProfileImageUri(value: String) = viewModelScope.launch {
        userProfileStore.updateProfileImageUri(value)
    }
}
