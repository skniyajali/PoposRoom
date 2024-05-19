/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.customer.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.model.Customer
import com.niyaj.ui.utils.UiEvent
import com.samples.apps.core.analytics.AnalyticsEvent
import com.samples.apps.core.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val validationRepository: CustomerValidationRepository,
    private val analyticsHelper: AnalyticsHelper,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var customerId = savedStateHandle.get<Int>("customerId") ?: 0

    var addEditState by mutableStateOf(AddEditCustomerState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("customerId")?.let { customerId ->
            getCustomerById(customerId)
        }
    }

    val phoneError: StateFlow<String?> = snapshotFlow { addEditState.customerPhone }
        .mapLatest {
            validationRepository.validateCustomerPhone(it, customerId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.customerName }
        .mapLatest {
            validationRepository.validateCustomerName(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val emailError: StateFlow<String?> = snapshotFlow { addEditState.customerEmail }
        .mapLatest {
            validationRepository.validateCustomerEmail(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    fun onEvent(event: AddEditCustomerEvent) {
        when (event) {
            is AddEditCustomerEvent.CustomerNameChanged -> {
                addEditState = addEditState.copy(customerName = event.customerName)
            }

            is AddEditCustomerEvent.CustomerPhoneChanged -> {
                addEditState = addEditState.copy(customerPhone = event.customerPhone)

            }

            is AddEditCustomerEvent.CustomerEmailChanged -> {
                addEditState = addEditState.copy(customerEmail = event.customerEmail)
            }

            is AddEditCustomerEvent.CreateOrUpdateCustomer -> {
                createOrUpdateCustomer(event.customerId)
            }
        }
    }

    private fun getCustomerById(customerId: Int) {
        if (customerId != 0) {
            viewModelScope.launch {
                when (val result = customerRepository.getCustomerById(customerId)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable to retrieve customer"))
                    }

                    is Resource.Success -> {
                        result.data?.let { customer ->
                            this@AddEditCustomerViewModel.customerId = customer.customerId

                            addEditState = addEditState.copy(
                                customerPhone = customer.customerPhone,
                                customerName = customer.customerName,
                                customerEmail = customer.customerEmail,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createOrUpdateCustomer(customerId: Int = 0) {
        viewModelScope.launch {
            if (phoneError.value == null && nameError.value == null && emailError.value == null) {
                val newCustomer = Customer(
                    customerId = customerId,
                    customerPhone = addEditState.customerPhone.trimEnd(),
                    customerName = addEditState.customerName?.trimEnd()?.capitalizeWords,
                    customerEmail = addEditState.customerEmail?.trimEnd(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = if (customerId != 0) System.currentTimeMillis() else null,
                )

                when (customerRepository.upsertCustomer(newCustomer)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Customer."))
                    }

                    is Resource.Success -> {
                        val message = if (customerId == 0) "Created" else "Updated"
                        _eventFlow.emit(UiEvent.OnSuccess("Customer $message Successfully."))

                        analyticsHelper.logOnCreateOrUpdateCustomer(customerId, message)
                    }
                }

                addEditState = AddEditCustomerState()
            }
        }
    }
}

private fun AnalyticsHelper.logOnCreateOrUpdateCustomer(data: Int, message: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "customer_$message",
            extras = listOf(
                AnalyticsEvent.Param("customer_$message", data.toString()),
            ),
        ),
    )
}
