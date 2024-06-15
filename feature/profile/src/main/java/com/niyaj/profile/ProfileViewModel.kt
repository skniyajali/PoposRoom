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

package com.niyaj.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.saveImageToInternalStorage
import com.niyaj.data.repository.AccountRepository
import com.niyaj.data.repository.ProfileRepository
import com.niyaj.data.repository.UserDataRepository
import com.niyaj.model.DEFAULT_RES_ID
import com.niyaj.model.Profile
import com.niyaj.model.RESTAURANT_LOGO_NAME
import com.niyaj.model.RESTAURANT_PRINT_LOGO_NAME
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val accountRepository: AccountRepository,
    private val userDataRepository: UserDataRepository,
    private val application: Application,
    @Dispatcher(PoposDispatchers.IO)
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val resId = userDataRepository.loggedInUserId.map {
        if (it == 0) {
            repository.insertOrUpdateProfile(Profile.defaultProfileInfo)

            userDataRepository.setUserLoggedIn(DEFAULT_RES_ID)
        }
        it
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    // TODO:: Setting default profile
    init {
        setDefaultProfile()
    }

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val info = resId.flatMapLatest {
        repository.getProfileInfo(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Profile.defaultProfileInfo,
    )

    val accountInfo = resId.flatMapLatest {
        accountRepository.getAccountInfo(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun logoutProfile() {
        viewModelScope.launch {
            accountRepository.logOut(resId.value)
        }
    }

    fun changePrintLogo(uri: Uri) {
        viewModelScope.launch {
            val fileName = "$RESTAURANT_PRINT_LOGO_NAME-${System.currentTimeMillis()}.png"

            val result = withContext(dispatcher) {
                application.saveImageToInternalStorage(
                    uri,
                    fileName,
                    info.value.printLogo,
                    dispatcher,
                )
            }

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to save print image into device"))
                }

                is Resource.Success -> {
                    when (repository.updatePrintLogo(resId.value, fileName)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Print photo has been updated"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to update print photo"))
                        }
                    }
                }
            }
        }
    }

    fun changeRestaurantLogo(uri: Uri) {
        viewModelScope.launch {
            val fileName = "$RESTAURANT_LOGO_NAME-${System.currentTimeMillis()}.png"

            val result = withContext(dispatcher) {
                application.saveImageToInternalStorage(
                    uri,
                    fileName,
                    info.value.logo,
                    dispatcher,
                )
            }

            when (result) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError("Unable to save logo into device"))
                }

                is Resource.Success -> {
                    when (repository.updateRestaurantLogo(resId = resId.value, fileName)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Profile photo has been updated"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError("Unable to update profile photo"))
                        }
                    }
                }
            }
        }
    }

    // TODO:: Setting default profile
    private fun setDefaultProfile() {
        viewModelScope.launch {
            userDataRepository.loggedInUserId.mapLatest {
                if (it == 0) {
                    repository.insertOrUpdateProfile(Profile.defaultProfileInfo)

                    userDataRepository.setUserLoggedIn(DEFAULT_RES_ID)
                }
            }
        }
    }
}
