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

object CustomerTestTags {

    const val CUSTOMER_SCREEN_TITLE = "Customers"
    const val CUSTOMER_NOT_AVAILABLE = "Customer Not Available"
    const val CUSTOMER_SEARCH_PLACEHOLDER = "Search for Customer..."

    const val CUSTOMER_DETAILS_TITLE = "Customer Details"

    const val CREATE_NEW_CUSTOMER = "Create New Customer"
    const val EDIT_CUSTOMER_ITEM = "Update Customer"

    const val ADD_EDIT_CUSTOMER_BTN = "AddEditCustomerBtn"

    const val CUSTOMER_NAME_FIELD = "Customer Name"
    const val CUSTOMER_NAME_ERROR = "NameError"

    const val CUSTOMER_PHONE_FIELD = "Customer Phone"
    const val CUSTOMER_PHONE_ERROR = "PhoneError"

    const val CUSTOMER_EMAIL_FIELD = "Customer Email"
    const val CUSTOMER_EMAIL_ERROR = "EmailError"

    const val CUSTOMER_NAME_LENGTH_ERROR = "Customer name must be 3 characters long."

    const val CUSTOMER_EMAIL_VALID_ERROR = "Customer email is not a valid email address."

    const val CUSTOMER_PHONE_EMPTY_ERROR = "Customer phone must not be empty."
    const val CUSTOMER_PHONE_LETTER_ERROR = "The phone no should not contains any characters."
    const val CUSTOMER_PHONE_LENGTH_ERROR = "Customer phone must be 10 digits."
    const val CUSTOMER_PHONE_ALREADY_EXIST_ERROR = "Customer phone already exists."

    const val DELETE_CUSTOMER_TITLE = "Delete Customer?"
    const val DELETE_CUSTOMER_MESSAGE =
        "Are you sure to delete these customers, all corresponding orders will be deleted."

    const val CUSTOMER_TAG = "Customer-"
    const val CUSTOMER_LIST = "CustomerList"

    const val CUSTOMER_SETTINGS_TITLE = "Customer Settings"

    const val IMPORT_CUSTOMER_TITLE = "Import Customers"
    const val IMPORT_CUSTOMER_SUB_TITLE = "Click here to import data from file."
    const val IMPORT_CUSTOMER_NOTE_TEXT = "Make sure to open customers.json file."

    const val EXPORT_CUSTOMER_TITLE = "Export Customers"
    const val EXPORT_CUSTOMER_SUB_TITLE = "Click here to export data from file."

    const val EXPORT_CUSTOMER_FILE_NAME = "customers"

    const val CUSTOMER_DETAILS_CARD = "CustomerDetailsCard"
    const val CUSTOMER_RECENT_ORDERS = "CustomerRecentOrders"
    const val TOTAL_ORDER_DETAILS_CARD = "TotalOrderDetailsCard"
}
