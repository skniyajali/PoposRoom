/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.settings

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.CreateBackupResult
import com.niyaj.common.result.Resource
import com.niyaj.common.result.RestoreBackupResult
import com.niyaj.common.utils.restartApplication
import com.niyaj.data.repository.BackupRepository
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.model.DarkThemeConfig
import com.niyaj.model.ThemeBrand
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val backupRepository: BackupRepository,
    private val dataDeletionRepository: DataDeletionRepository,
    private val application: Application,
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var cacheBackupUri: Uri? = null
        private set

    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    settings = UserEditableSettings(
                        brand = userData.themeBrand,
                        useDynamicColor = userData.useDynamicColor,
                        darkThemeConfig = userData.darkThemeConfig,
                        sendOrderSms = userData.sendOrderSms,
                        useDeliveryPartnerQrCode = userData.useDeliveryPartnerQrCode,
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds),
                initialValue = SettingsUiState.Loading,
            )

    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {
            userDataRepository.setThemeBrand(themeBrand)
        }
    }

    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(useDynamicColor)
        }
    }

    fun updateSendOrderSms(sendOrderSms: Boolean) {
        viewModelScope.launch {
            userDataRepository.setSendOrderSms(sendOrderSms)
        }
    }

    fun updateUseDeliveryPartnerQrCode(useDeliveryPartnerQrCode: Boolean) {
        viewModelScope.launch {
            userDataRepository.setUseDeliveryPartnerQrCode(useDeliveryPartnerQrCode)
        }
    }

    fun createBackup(onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            val backupResult = backupRepository.createBackup()
            _isLoading.update { false }

            when (backupResult) {
                is CreateBackupResult.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Error creating backup"))
                }

                is CreateBackupResult.EmptyDatabase -> {
                    _eventFlow.emit(UiEvent.OnError("Database is empty"))
                }

                is CreateBackupResult.Success -> withContext(Dispatchers.Main) {
                    cacheBackupUri = backupResult.uri
                    onSuccess(backupResult.uri)
                }
            }
        }
    }

    fun restoreBackup(backupUri: Uri, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            val backupResult = backupRepository.restoreBackup(backupUri)
            _isLoading.update { false }

            when (backupResult) {
                RestoreBackupResult.FileError -> {
                    _eventFlow.emit(UiEvent.OnError("Error reading backup file"))
                }

                RestoreBackupResult.WrongFile -> {
                    _eventFlow.emit(UiEvent.OnError("Wrong backup file"))
                }

                RestoreBackupResult.DbError -> {
                    _eventFlow.emit(UiEvent.OnError("Error restoring backup"))
                }

                RestoreBackupResult.Success -> withContext(Dispatchers.Main) {
                    onSuccess()
                    _eventFlow.emit(UiEvent.OnSuccess("Database restored successfully"))
                    delay(2000)
                    _eventFlow.emit(UiEvent.OnSuccess("Restarting app within 5 seconds."))
                    delay(5000)
                    application.restartApplication()
                }
            }
        }
    }

    fun copyBackupFile(sourceUri: Uri, destUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                application.contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    application.contentResolver.openOutputStream(destUri)?.use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    // Show success message
                    _eventFlow.emit(UiEvent.OnSuccess("Backup saved successfully"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message
                    _eventFlow.emit(UiEvent.OnError("Failed to save backup: ${e.message}"))
                }
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            _isLoading.update { true }
            val result = dataDeletionRepository.deleteAllRecords()
            _isLoading.update { false }

            when (result) {
                is Resource.Success -> {
                    _eventFlow.emit(UiEvent.OnSuccess("All data deleted successfully"))
                }

                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Error deleting data"))
                }
            }
        }
    }
}

/**
 * Represents the settings which the user can edit within the app.
 */
data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
    val sendOrderSms: Boolean,
    val useDeliveryPartnerQrCode: Boolean,
)

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: UserEditableSettings) : SettingsUiState
}
