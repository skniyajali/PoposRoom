package com.niyaj.ui.utils

sealed class UiEvent {
    data class OnSuccess(val successMessage: String) : UiEvent()

    data class OnError(val errorMessage: String) : UiEvent()
}