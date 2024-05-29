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

package com.niyaj.market.marketItem.createOrUpdate

import com.niyaj.model.MarketTypeIdAndName
import com.niyaj.model.MeasureUnit

sealed class AddEditMarketItemEvent {

    data class ItemTypeChanged(val marketType: MarketTypeIdAndName) : AddEditMarketItemEvent()

    data class ItemNameChanged(val name: String) : AddEditMarketItemEvent()

    data class ItemPriceChanged(val price: String) : AddEditMarketItemEvent()

    data class ItemDescriptionChanged(val description: String) : AddEditMarketItemEvent()

    data class ItemMeasureUnitChanged(val unit: MeasureUnit) : AddEditMarketItemEvent()

    data class ItemMeasureUnitNameChanged(val unitName: String) : AddEditMarketItemEvent()

    data object AddOrUpdateItem : AddEditMarketItemEvent()
}
