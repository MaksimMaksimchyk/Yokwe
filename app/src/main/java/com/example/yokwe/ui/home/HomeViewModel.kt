package com.example.yokwe.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yokwe.domain.interactors.FamilyInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val familyInteractor: FamilyInteractor
) : ViewModel() {

    private val familyIdFlow = MutableSharedFlow<String>(replay = 1)

    val state: StateFlow<HomeScreenState> = familyIdFlow.flatMapLatest { familyId ->
        combine(
            familyInteractor.getFamilyFlow(familyId),
            familyInteractor.getGoalsStatsFlow(familyId),
            familyInteractor.getPetFlow(familyId)
        ) { family, stats, pet ->
            HomeScreenState(
                membersCount = family.members.size,
                inviteCode = family.inviteCode,
                goalStats = stats,
                pet = pet,
                isLoading = false
            )
        }.onStart {
            emit(HomeScreenState(isLoading = true))
        }.catch { e ->
            emit(HomeScreenState(error = e.message, isLoading = false))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState(isLoading = true)
    )

    fun loadData(familyId: String) {
        familyIdFlow.tryEmit(familyId)
    }

}




