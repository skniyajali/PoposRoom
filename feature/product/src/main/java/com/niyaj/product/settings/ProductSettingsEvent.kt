package com.niyaj.product.settings

import com.niyaj.model.Product

sealed class ProductSettingsEvent {

    data class OnSelectCategory(val categoryId: Int): ProductSettingsEvent()

    data object GetExportedProduct: ProductSettingsEvent()

    data class OnImportProductsFromFile(val data: List<Product>): ProductSettingsEvent()

    data object ImportProductsToDatabase: ProductSettingsEvent()

    data class OnChangeProductPrice(val price: String): ProductSettingsEvent()

    data object OnIncreaseProductPrice: ProductSettingsEvent()

    data object OnDecreaseProductPrice: ProductSettingsEvent()
}
