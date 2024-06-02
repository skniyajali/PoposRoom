/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.niyaj.cart

sealed interface CartEvent {
    data class IncreaseQuantity(val orderId: Int, val productId: Int) : CartEvent

    data class DecreaseQuantity(val orderId: Int, val productId: Int) : CartEvent

    data class UpdateAddOnItemInCart(val orderId: Int, val itemId: Int) : CartEvent

    data class PlaceCartOrder(val orderId: Int) : CartEvent

}

sealed interface DineOutEvent: CartEvent {
    data class SelectDineOutCart(val orderId: Int) : CartEvent

    data class UpdateDeliveryPartner(val orderId: Int, val deliveryPartnerId: Int) : CartEvent

    data object SelectAllDineOutCart : CartEvent

    data object PlaceAllDineOutCart : CartEvent
}

sealed interface DineInEvent: CartEvent {
    data class SelectDineInCart(val orderId: Int) : CartEvent

    data object SelectAllDineInCart : CartEvent

    data object PlaceAllDineInCart : CartEvent
}