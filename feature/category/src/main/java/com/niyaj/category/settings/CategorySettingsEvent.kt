package com.niyaj.category.settings

import com.niyaj.model.Category

sealed class CategorySettingsEvent {
    data object GetExportedCategory: CategorySettingsEvent()

    data class OnImportCategoriesFromFile(val data: List<Category>): CategorySettingsEvent()

    data object ImportCategoriesToDatabase: CategorySettingsEvent()
}