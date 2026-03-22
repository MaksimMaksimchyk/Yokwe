package com.example.yokwe.domain.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PetEventBus @Inject constructor() {

    private val _lastEvent = MutableStateFlow<PetEventData?>(null)
    val lastEvent: StateFlow<PetEventData?> = _lastEvent.asStateFlow()

    suspend fun sendEvent(event: PetEventData) {
        _lastEvent.emit(event)
    }
}