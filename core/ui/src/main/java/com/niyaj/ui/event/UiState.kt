package com.niyaj.ui.event

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>

    object Empty : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>
}