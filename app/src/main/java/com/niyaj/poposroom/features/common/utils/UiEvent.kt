package com.niyaj.poposroom.features.common.utils

sealed class UiEvent{
    data class IsLoading(val isLoading: Boolean? = false) : UiEvent()
    data class OnSuccess(val successMessage: String) : UiEvent()
    data class OnError(val errorMessage: String): UiEvent()
}