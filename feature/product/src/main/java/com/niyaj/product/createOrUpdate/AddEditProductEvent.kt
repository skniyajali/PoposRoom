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

package com.niyaj.product.createOrUpdate

import com.niyaj.model.Category

sealed interface AddEditProductEvent {

    data class CategoryChanged(val category: Category) : AddEditProductEvent

    data class ProductNameChanged(val productName: String) : AddEditProductEvent

    data class ProductPriceChanged(val productPrice: String) : AddEditProductEvent

    data class ProductDescChanged(val productDesc: String) : AddEditProductEvent

    data class TagNameChanged(val tagName: String) : AddEditProductEvent

    data class OnSelectTag(val tagName: String) : AddEditProductEvent

    data object ProductAvailabilityChanged : AddEditProductEvent

    data object AddOrUpdateProduct : AddEditProductEvent
}
