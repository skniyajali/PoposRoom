package com.niyaj.poposroom.features.main_feed.data.repository

import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.main_feed.data.dao.MainFeedDao
import com.niyaj.poposroom.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.product.domain.model.filterProducts
import com.niyaj.poposroom.features.selected.domain.model.Selected
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class MainFeedRepositoryImpl(
    private val mainFeedDao: MainFeedDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : MainFeedRepository {

    override fun getAllCategory(): Flow<List<Category>> {
        return mainFeedDao.getAllCategories()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getAllProduct(
        searchText: String,
        selectedCategory: Int
    ): Flow<List<Product>> {
        return withContext(ioDispatcher) {
            mainFeedDao.getAllProduct().mapLatest { list ->
                if (selectedCategory != 0) {
                    list.filter { it.categoryId == selectedCategory }
                } else list
            }.mapLatest {
                it.filterProducts(searchText)
            }
        }
    }

    override fun getSelectedOrder(): Flow<Selected?> {
        return mainFeedDao.getSelectedOrder()
    }
}