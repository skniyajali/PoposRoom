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

package com.niyaj.model

import com.niyaj.model.utils.toDateString
import com.niyaj.model.utils.toJoinedDate

data class MarketListWithTypes(
    val marketList: MarketList,

    val marketTypes: List<MarketListWithType> = emptyList(),
)

fun List<MarketListWithTypes>.searchMarketList(searchText: String): List<MarketListWithTypes> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.marketList.marketDate.toDateString.contains(searchText, true) ||
                it.marketList.marketDate.toJoinedDate.contains(searchText, true) ||
                it.marketList.createdAt.toDateString.contains(searchText, true) ||
                it.marketList.updatedAt?.toDateString?.contains(searchText, true) == true
        }
    } else {
        this
    }
}
