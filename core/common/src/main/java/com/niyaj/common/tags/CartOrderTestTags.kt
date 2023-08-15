package com.niyaj.common.tags

object CartOrderTestTags {

    //Cart Order Screen

    const val CART_ORDER_SCREEN_TITLE = "Cart Orders"
    const val CART_ORDER_NOT_AVAIlABLE = "Cart Order Not Available"
    const val CART_ORDER_SEARCH_PLACEHOLDER = "Search for Cart Orders..."
    const val ADD_EDIT_CART_ORDER_SCREEN = "AddEdit CartOrder Screen"
    const val CREATE_NEW_CART_ORDER = "Create New Order"
    const val EDIT_CART_ORDER = "Update Cart Order"

    //Add Edit Cart Order Screen Tags

    const val ORDER_TYPE_FIELD = "Order Type"
    const val ORDER_ID_FIELD = "Order ID"

    const val ADDRESS_NAME_FIELD = "Customer Address"
    const val ADDRESS_NAME_ERROR_FIELD = "Customer AddressError"

    const val CUSTOMER_PHONE_FIELD = "Customer Phone"
    const val CUSTOMER_PHONE_ERROR_FIELD = "Customer PhoneError"

    const val CHARGES_INCLUDED_FIELD = "Charges Included"

    const val CART_ORDER_ID_EMPTY_ERROR = "The order id must not be empty"
    const val CART_ORDER_ID_EXIST_ERROR = "The order id already exists"

    const val CART_ORDER_NAME_EMPTY_ERROR = "Address name must not be empty."
    const val ADDRESS_NAME_LENGTH_ERROR = "The address name must be more than 2 characters long."
    const val CART_ORDER_NAME_ERROR = "Unable to create or get address."

    const val ORDER_SHORT_NAME_EMPTY_ERROR = "Address short name cannot be empty."
    const val ORDER_PRICE_LESS_THAN_TWO_ERROR = "Address short name must be more than 2 characters long"

    const val CART_ORDER_PHONE_EMPTY_ERROR = "Customer phone must not be empty."
    const val CUSTOMER_PHONE_LETTER_ERROR = "The phone no should not contains any characters."
    const val CUSTOMER_PHONE_LENGTH_ERROR = "Customer phone must be 10 digits."
    const val CART_ORDER_PHONE_ERROR = "Unable to create or get customer."

    const val DELETE_CART_ORDER_ITEM_TITLE = "Delete Cart Order?"
    const val DELETE_CART_ORDER_ITEM_MESSAGE = "Are you sure to delete these orders?"

    const val CART_ORDER_ITEM_TAG = "CartOrder-"
}