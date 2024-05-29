/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.product.settings

import com.niyaj.model.Product

sealed class ProductSettingsEvent {

    data class OnSelectCategory(val categoryId: Int) : ProductSettingsEvent()

    data object GetExportedProduct : ProductSettingsEvent()

    data class OnImportProductsFromFile(val data: List<Product>) : ProductSettingsEvent()

    data object ImportProductsToDatabase : ProductSettingsEvent()

    data class OnChangeProductPrice(val price: String) : ProductSettingsEvent()

    data object OnIncreaseProductPrice : ProductSettingsEvent()

    data object OnDecreaseProductPrice : ProductSettingsEvent()
}
