package com.niyaj.poposroom.features.category.presentation.add_edit

sealed class AddEditCategoryEvent {

    data class CategoryNameChanged(val categoryName: String) : AddEditCategoryEvent()

    object CategoryAvailabilityChanged: AddEditCategoryEvent()

    data class CreateUpdateAddEditCategory(val categoryId: Int = 0) : AddEditCategoryEvent()
}
