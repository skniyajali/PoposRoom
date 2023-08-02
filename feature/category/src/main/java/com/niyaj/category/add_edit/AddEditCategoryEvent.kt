package com.niyaj.category.add_edit

sealed class AddEditCategoryEvent {

    data class CategoryNameChanged(val categoryName: String) : AddEditCategoryEvent()

    data object CategoryAvailabilityChanged: AddEditCategoryEvent()

    data class CreateUpdateAddEditCategory(val categoryId: Int = 0) : AddEditCategoryEvent()
}
