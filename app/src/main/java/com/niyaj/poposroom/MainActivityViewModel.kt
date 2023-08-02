package com.niyaj.poposroom

import androidx.lifecycle.ViewModel
import com.niyaj.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
//    userDataRepository: UserDataRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<MainActivityUiState>(MainActivityUiState.Success(UserData()))
    val uiState = _uiState.asStateFlow()
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: UserData) : MainActivityUiState
}
