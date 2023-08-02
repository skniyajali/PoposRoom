package com.niyaj.category.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.common.result.Resource
import com.niyaj.data.repository.CategoryRepository
import com.niyaj.data.repository.validation.CategoryValidationRepository
import com.niyaj.model.Category
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AddEditCategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val validationRepository: CategoryValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var categoryId = savedStateHandle.get<Int>("addOnItemId")

    var addEditState by mutableStateOf(AddEditCategoryState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Int>("addOnItemId")?.let { addOnItemId ->
            getCategoryById(addOnItemId)
        }
    }

    val nameError: StateFlow<String?> = snapshotFlow { addEditState.categoryName }
        .mapLatest {
            validationRepository.validateCategoryName(it, categoryId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )


    fun onEvent(event: AddEditCategoryEvent) {
        when (event) {
            is AddEditCategoryEvent.CategoryNameChanged -> {
                addEditState = addEditState.copy(categoryName = event.categoryName)
            }

            is AddEditCategoryEvent.CategoryAvailabilityChanged -> {
                addEditState = addEditState.copy(isAvailable = !addEditState.isAvailable)
            }

            is AddEditCategoryEvent.CreateUpdateAddEditCategory -> {
                createOrUpdateCategory(event.categoryId)
            }
        }
    }

    private fun getCategoryById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            when (val result = categoryRepository.getCategoryById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.OnError(result.message ?: "unable"))
                }

                is Resource.Success -> {
                    result.data?.let { category ->
                        categoryId = category.categoryId

                        addEditState = addEditState.copy(
                            categoryName = category.categoryName,
                            isAvailable = category.isAvailable
                        )
                    }
                }
            }
        }
    }

    private fun createOrUpdateCategory(categoryId: Int = 0) {
        viewModelScope.launch(ioDispatcher) {
            if (nameError.value == null) {
                val addOnItem = Category(
                    categoryId = categoryId,
                    categoryName = addEditState.categoryName,
                    isAvailable = addEditState.isAvailable,
                    createdAt = Date(),
                    updatedAt = if (categoryId != 0) Date() else null
                )

                when (categoryRepository.upsertCategory(addOnItem)) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.OnError("Unable To Create Category."))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.OnSuccess("Category Created Successfully."))
                    }
                }
                addEditState = AddEditCategoryState()
            }
        }
    }
}