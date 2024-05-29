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

object PaymentScreenTags {

    const val PAYMENT_SCREEN_TITLE = "Payments"
    const val PAYMENT_NOT_AVAILABLE = "Payments Not Available"
    const val NO_ITEMS_IN_PAYMENT = "Payments Not Found"
    const val PAYMENT_SEARCH_PLACEHOLDER = "Search for Payments..."

    const val CREATE_NEW_PAYMENT = "Add New Payment"
    const val EDIT_PAYMENT_ITEM = "Update Payment"

    const val ADD_EDIT_PAYMENT_SCREEN = "AddEdit Payment Screen"

    const val PAYMENT_EMPLOYEE_NAME_FIELD = "Employee Name"
    const val PAYMENT_EMPLOYEE_NAME_ERROR = "Employee NameError"

    const val PAYMENT_TYPE_FIELD = "Payment Type"
    const val PAYMENT_TYPE_ERROR = "Payment TypeError"

    const val GIVEN_DATE_FIELD = "Given Date"
    const val GIVEN_DATE_ERROR = "Given DateError"

    const val PAYMENT_MODE_FIELD = "Payment Mode"
    const val PAYMENT_MODE_ERROR = "Payment ModeError"

    const val GIVEN_AMOUNT_FIELD = "Given Amount"
    const val GIVEN_AMOUNT_ERROR = "Given AmountError"

    const val PAYMENT_NOTE_FIELD = "Payment Note"
    const val PAYMENT_NOTE_ERROR = "Payment NoteError"

    const val ADD_EDIT_PAYMENT_ENTRY_BUTTON = "AddEdit PaymentEntry"

    const val PAYMENT_EMPLOYEE_NAME_EMPTY = "Employee name must not be empty"

    const val PAYMENT_GIVEN_DATE_EMPTY = "Given date must not be empty"
    const val PAYMENT_MODE_EMPTY = "Payment mode must not be empty."

    const val GIVEN_AMOUNT_EMPTY = "Given Amount must not be empty"
    const val GIVEN_AMOUNT_LENGTH_ERROR = "Payment amount must greater than two digits"
    const val GIVEN_AMOUNT_LETTER_ERROR = "Given amount must not contain any letters"

    const val PAYMENT_NOTE_EMPTY = "Payment note required, because you pay using both mode."

    const val PAYMENT_TYPE_EMPTY = "Payment type must not be empty."

    const val DELETE_PAYMENT_TITLE = "Delete Payment?"
    const val DELETE_PAYMENT_MESSAGE = "Are you sure to delete these payments?"

    const val PAYMENT_TAG = "Payment-"

    const val PAYMENT_SETTINGS_TITLE = "Payment Settings"

    const val IMPORT_PAYMENT_TITLE = "Import Payments"
    const val IMPORT_PAYMENT_BTN_TEXT = "Import Employee Payments"
    const val IMPORT_PAYMENT_OPN_FILE = "Open File"
    const val IMPORT_PAYMENT_NOTE_TEXT = "Make sure to open payments.json file."

    const val EXPORT_PAYMENT_TITLE = "Export Payments"
    const val EXPORT_PAYMENT_BTN_TEXT = "Export Employee Payments"
    const val EXPORT_PAYMENT_BTN = "Export Payment"

    const val EXPORT_PAYMENT_FILE_NAME = "payments"
}
