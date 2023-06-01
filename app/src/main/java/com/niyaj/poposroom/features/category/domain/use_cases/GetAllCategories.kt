package com.niyaj.poposroom.features.category.domain.use_cases

import com.niyaj.poposroom.features.category.dao.CategoryDao
import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.category.domain.model.searchCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class GetAllCategories @Inject constructor(
    private val categoryDao: CategoryDao
) {

    operator fun invoke(searchText: String): Flow<List<Category>> {
        return categoryDao.getAllCategories().mapLatest { it.searchCategory(searchText) }
    }
}