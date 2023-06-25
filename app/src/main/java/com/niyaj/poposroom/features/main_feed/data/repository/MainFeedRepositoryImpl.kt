package com.niyaj.poposroom.features.main_feed.data.repository

import com.niyaj.poposroom.features.category.domain.model.Category
import com.niyaj.poposroom.features.common.utils.Dispatcher
import com.niyaj.poposroom.features.common.utils.PoposDispatchers
import com.niyaj.poposroom.features.main_feed.data.dao.MainFeedDao
import com.niyaj.poposroom.features.main_feed.domain.model.ProductWithFlowQuantity
import com.niyaj.poposroom.features.main_feed.domain.model.filterByCategory
import com.niyaj.poposroom.features.main_feed.domain.model.filterBySearch
import com.niyaj.poposroom.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.poposroom.features.product.domain.model.Product
import com.niyaj.poposroom.features.selected.domain.model.Selected
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class MainFeedRepositoryImpl(
    private val mainFeedDao: MainFeedDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher
) : MainFeedRepository {

    override fun getAllCategory(): Flow<List<Category>> {
        return mainFeedDao.getAllCategories()
    }

    override suspend fun getAllProduct(
        searchText: String,
        selectedCategory: Int
    ): Flow<List<ProductWithFlowQuantity>> {
        return channelFlow {
            withContext(ioDispatcher) {
                val selectedCartOrder = mainFeedDao.getSelectedOrder()
                val products = mainFeedDao.getAllProducts()

                selectedCartOrder.combine(products) { order, list ->
                    mapProductToProductWithQuantity(order?.orderId, list)
                }.collectLatest { result ->
                    val filteredProducts = result
                        .filter {
                            it.filterByCategory(selectedCategory)
                        }.filter {
                            it.filterBySearch(searchText)
                        }

                    send(filteredProducts)
                }
            }
        }
    }

    override fun getSelectedOrder(): Flow<Selected?> {
        return mainFeedDao.getSelectedOrder()
    }

    private suspend fun getQuantity(orderId: Int?, productId: Int): Flow<Int> {
        return withContext(ioDispatcher) {
            if (orderId == null) flowOf(0) else {
                mainFeedDao.getProductQuantity(orderId, productId)
            }
        }
    }

    private suspend fun mapProductToProductWithQuantity(
        selectedCartOrder: Int? = null,
        products: List<Product>
    ): List<ProductWithFlowQuantity> {
        val data = products.map { product ->
            ProductWithFlowQuantity(
                categoryId = product.categoryId,
                productId = product.productId,
                productName = product.productName,
                productPrice = product.productPrice,
                quantity = getQuantity(selectedCartOrder, product.productId).distinctUntilChanged()
            )
        }

        return data
    }
}