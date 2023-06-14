package com.niyaj.poposroom.features.product.presentation.add_edit

import com.niyaj.poposroom.features.category.domain.model.Category

sealed interface AddEditProductEvent {

    data class CategoryChanged(val category: Category): AddEditProductEvent

    data class ProductNameChanged(val productName: String): AddEditProductEvent

    data class ProductPriceChanged(val productPrice: String): AddEditProductEvent

    data class ProductDescChanged(val productDesc: String): AddEditProductEvent

    object ProductAvailabilityChanged: AddEditProductEvent

    data class AddOrUpdateProduct(val productId: Int = 0): AddEditProductEvent
}