package com.niyaj.poposroom.features.category.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.poposroom.features.category.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.category.domain.validation.CategoryValidationRepository
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.common.utils.UiEvent
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
    private val categoryDao: CategoryDao,
    private val validationRepository: CategoryValidationRepository,
    @Dispatcher(PoposDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
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
            validationRepository.validateCategoryName(categoryId, it).errorMessage
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

    fun getCategoryById(itemId: Int) {
        viewModelScope.launch(ioDispatcher) {
            categoryDao.getCategoryById(itemId)?.let { category ->
                categoryId = category.categoryId

                addEditState = addEditState.copy(
                    categoryName = category.categoryName,
                    isAvailable = category.isAvailable
                )
            }
        }
    }

    fun resetFields() {
        addEditState = AddEditCategoryState()
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

                val result = categoryDao.upsertCategory(addOnItem)

                if (result != 0L) {
                    _eventFlow.emit(UiEvent.OnSuccess("AddOn Item Created Successfully."))
                }else {
                    _eventFlow.emit(UiEvent.OnError("Unable To Create AddOn Item."))

                }

                addEditState = AddEditCategoryState()
            }
        }
    }
}