package com.niyaj.poposroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.niyaj.data.utils.WorkMonitor
import com.niyaj.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalPermissionsApi::class)
class MainActivityViewModel @Inject constructor(
    workMonitor: WorkMonitor,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<MainActivityUiState>(MainActivityUiState.Success(UserData()))
    val uiState = _uiState.asStateFlow()

    val reportState = workMonitor.isGeneratingReport.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

    val deleteState = workMonitor.isDeletingData.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        false,
    )

}


sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
