package com.niyaj.data.data.repository

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.repository.HomeRepository
import com.niyaj.database.dao.MainFeedDao
import com.niyaj.database.model.asExternalModel
import com.niyaj.model.Category
import com.niyaj.model.Product
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.model.Selected
import com.niyaj.model.filterByCategory
import com.niyaj.model.filterBySearch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
    private val mainFeedDao: MainFeedDao,
    @Dispatcher(PoposDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) : HomeRepository {

    override fun getAllCategory(): Flow<List<Category>> {
        return mainFeedDao.getAllCategories().mapLatest { list ->
            list.map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getAllProduct(
        searchText: String,
        selectedCategory: Int,
    ): Flow<List<ProductWithFlowQuantity>> {
        return channelFlow {
            withContext(ioDispatcher) {
                val selectedCartOrder = mainFeedDao.getSelectedOrder()
                val products = mainFeedDao.getAllProducts()

                selectedCartOrder.combine(products) { order, list ->
                    mapProductToProductWithQuantity(
                        order?.orderId,
                        list.map { it.asExternalModel() })
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
        return mainFeedDao.getSelectedOrder().mapLatest { it?.asExternalModel() }
    }

    override suspend fun getSelectedOrderAddress(orderId: Int): String? {
        return withContext(ioDispatcher) {
            mainFeedDao.getSelectedOrderAddress(orderId)
        }
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
        products: List<Product>,
    ): List<ProductWithFlowQuantity> {
        return products.map { product ->
            ProductWithFlowQuantity(
                categoryId = product.categoryId,
                productId = product.productId,
                productName = product.productName,
                productPrice = product.productPrice,
                quantity = getQuantity(selectedCartOrder, product.productId).distinctUntilChanged()
            )
        }
    }
}