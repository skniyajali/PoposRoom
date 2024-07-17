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

package com.niyaj.common.tags

object CartOrderTestTags {

    // Cart Order Screen

    const val CART_ORDER_SCREEN_TITLE = "Cart Orders"
    const val CART_ORDER_NOT_AVAILABLE = "Cart Order Not Available"
    const val CART_ORDER_SEARCH_PLACEHOLDER = "Search for Cart Orders..."
    const val ADD_EDIT_CART_ORDER_SCREEN = "AddEdit CartOrder Screen"
    const val CREATE_NEW_CART_ORDER = "Create New Order"
    const val EDIT_CART_ORDER = "Update Cart Order"

    // Add Edit Cart Order Screen Tags

    const val ORDER_TYPE_FIELD = "Order Type"
    const val ORDER_ID_FIELD = "Order ID"

    const val ADDRESS_NAME_FIELD = "Customer Address"
    const val ADDRESS_NAME_ERROR_FIELD = "Customer AddressError"

    const val CUSTOMER_PHONE_FIELD = "Customer Phone"
    const val CUSTOMER_PHONE_ERROR_FIELD = "Customer PhoneError"

    const val CHARGES_INCLUDED_FIELD = "Charges Included"

    const val CART_ADD_ON_ITEMS = "Cart Order AddOns"
    const val CART_CHARGES_ITEMS = "Cart Order Charges"
    const val CART_PARTNER_ITEMS = "Delivery Partner"

    const val CART_ORDER_NAME_EMPTY_ERROR = "Address name must not be empty."
    const val ADDRESS_NAME_LENGTH_ERROR = "The address name must be more than 6 characters long."
    const val ADDRESS_NAME_INVALID = "Please enter a valid address. for example: AB Complex"
    const val CART_ORDER_NAME_ERROR = "Unable to create or get address."

    const val ORDER_SHORT_NAME_EMPTY_ERROR = "Address short name cannot be empty."
    const val ORDER_PRICE_LESS_THAN_TWO_ERROR = "Address short name must be more than 2 characters long"

    const val CART_ORDER_PHONE_EMPTY_ERROR = "Customer phone must not be empty."
    const val CUSTOMER_PHONE_LETTER_ERROR = "The phone no should not contains any characters."
    const val CUSTOMER_PHONE_LENGTH_ERROR = "Customer phone must be 10 digits."
    const val CART_ORDER_PHONE_ERROR = "Unable to create or get customer."

    const val DELETE_CART_ORDER_ITEM_TITLE = "Delete Cart Order!"
    const val DELETE_CART_ORDER_ITEM_MESSAGE = "Are you sure to delete these orders? This action cannot be undone."

    const val CART_ORDER_ITEM_TAG = "CartOrder-"

    const val CART_ORDER_NOTE = "Click an item to view details, long click to select."
}
