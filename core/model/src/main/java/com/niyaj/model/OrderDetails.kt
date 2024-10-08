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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class OrderDetails(
    val cartOrder: CartOrder = CartOrder(),
    val cartProducts: ImmutableList<CartProductItem> = persistentListOf(),
    val addOnItems: ImmutableList<AddOnItem> = persistentListOf(),
    val charges: ImmutableList<Charges> = persistentListOf(),
    val orderPrice: OrderPrice = OrderPrice(),
    val deliveryPartner: EmployeeNameAndId? = null,
)
