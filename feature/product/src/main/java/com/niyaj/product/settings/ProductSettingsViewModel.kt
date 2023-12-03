package com.niyaj.product.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.niyaj.common.result.Resource
import com.niyaj.common.utils.safeInt
import com.niyaj.data.repository.ProductRepository
import com.niyaj.model.Product
import com.niyaj.model.ProductIdWithPrice
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.utils.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class ProductSettingsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
) : BaseViewModel() {

    private val _selectedCategory = mutableStateListOf<Int>()
    val selectedCategory: SnapshotStateList<Int> = _selectedCategory

    val categories = productRepository.getAllCategory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val products = snapshotFlow { mSearchText.value }.flatMapLatest { searchText ->
        productRepository.getAllProduct(searchText)
    }.mapLatest { list ->
        totalItems = list.map { item -> item.productId }
        list
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _exportedProducts = MutableStateFlow<List<Product>>(emptyList())
    val exportedProducts = _exportedProducts.asStateFlow()

    private val _importedProducts = MutableStateFlow<List<Product>>(emptyList())
    val importedProducts = _importedProducts.asStateFlow()

    private val _productPrice = mutableIntStateOf(0)
    val productPrice: State<Int> = _productPrice

    private fun getProducts(categoryId: Int): List<Int> {
        return products.value.filter { it.categoryId == categoryId }.map { it.productId }
    }

    /**
     *
     */
    fun onEvent(event: ProductSettingsEvent) {
        when (event) {

            is ProductSettingsEvent.OnSelectCategory -> {
                viewModelScope.launch {
                    val products = getProducts(event.categoryId)

                    if (_selectedCategory.contains(event.categoryId)) {
                        mSelectedItems.removeAll(products.toSet())
                        _selectedCategory.remove(event.categoryId)
                    } else {
                        _selectedCategory.add(event.categoryId)

                        products.forEach {
                            if (!mSelectedItems.contains(it)) {
                                mSelectedItems.add(it)
                            }
                        }
                    }
                }
            }

            is ProductSettingsEvent.GetExportedProduct -> {
                viewModelScope.launch {
                    if (mSelectedItems.isEmpty()) {
                        _exportedProducts.value = products.value
                    } else {
                        val newProducts = mutableListOf<Product>()

                        mSelectedItems.forEach { id ->
                            val product = products.value.distinct().find { it.productId == id }

                            if (product != null) {
                                newProducts.add(product)
                            }
                        }

                        _exportedProducts.emit(newProducts.toList())
                    }
                }
            }

            is ProductSettingsEvent.OnImportProductsFromFile -> {
                viewModelScope.launch {
                    _importedProducts.value = emptyList()

                    if (event.data.isNotEmpty()) {
                        totalItems = event.data.map { it.productId }
                        _importedProducts.value = event.data
                    }
                }
            }

            is ProductSettingsEvent.ImportProductsToDatabase -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { product ->
                            _importedProducts.value.filter { it.productId == product }
                        }
                    } else {
                        _importedProducts.value
                    }

                    when (val result = productRepository.importProductsToDatabase(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(
                                UiEvent.OnError(
                                    result.message ?: "Unable to import products"
                                )
                            )
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(
                                UiEvent.OnSuccess(
                                    "${data.size} Products has been imported."
                                )
                            )
                        }
                    }
                }
            }

            is ProductSettingsEvent.OnChangeProductPrice -> {
                _productPrice.intValue = event.price.safeInt()
            }

            is ProductSettingsEvent.OnIncreaseProductPrice -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { productId ->
                            products.value.filter { it.productId == productId }.map {
                                ProductIdWithPrice(
                                    productId = productId,
                                    productPrice = it.productPrice + _productPrice.intValue
                                )
                            }
                        }
                    } else {
                        products.value.map {
                            ProductIdWithPrice(
                                productId = it.productId,
                                productPrice = it.productPrice + _productPrice.intValue
                            )
                        }
                    }

                    when (val result = productRepository.increaseProductsPrice(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} products price has been increased"))
                        }
                    }
                }
            }

            is ProductSettingsEvent.OnDecreaseProductPrice -> {
                viewModelScope.launch {
                    val data = if (mSelectedItems.isNotEmpty()) {
                        mSelectedItems.flatMap { productId ->
                            products.value.filter { it.productId == productId }.map {
                                ProductIdWithPrice(
                                    productId = productId,
                                    productPrice = it.productPrice - _productPrice.intValue
                                )
                            }
                        }
                    } else {
                        products.value.map {
                            ProductIdWithPrice(
                                productId = it.productId,
                                productPrice = it.productPrice - _productPrice.intValue
                            )
                        }
                    }

                    when (val result = productRepository.decreaseProductsPrice(data)) {
                        is Resource.Error -> {
                            mEventFlow.emit(UiEvent.OnError(result.message ?: "Unable"))
                        }

                        is Resource.Success -> {
                            mEventFlow.emit(UiEvent.OnSuccess("${data.size} products price has been decreased"))
                        }
                    }
                }
            }
        }
    }

    override fun deselectItems() {
        super.deselectItems()

        deselectCategories()
    }

    private fun deselectCategories() {
        viewModelScope.launch {
            _selectedCategory.clear()
        }
    }
}