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

object ChargesTestTags {

    const val CHARGES_SCREEN_TITLE = "Charges"
    const val CHARGES_NOT_AVAILABLE = "Charges Not Available"
    const val CHARGES_SEARCH_PLACEHOLDER = "Search for Charges Items..."

    const val CREATE_NEW_CHARGES = "Create New Charges"
    const val EDIT_CHARGES_ITEM = "Update Charges Item"
    const val ADD_EDIT_CHARGES_BTN = "AddEditChargesBtn"

    const val CHARGES_NAME_FIELD = "Charges Name"
    const val CHARGES_NAME_ERROR_TAG = "ChargesNameError"

    const val CHARGES_AMOUNT_FIELD = "Charges Amount"
    const val CHARGES_AMOUNT_ERROR_TAG = "ChargesAmountError"

    const val CHARGES_APPLIED_SWITCH = "Charges Applied Switch"

    const val CHARGES_NAME_EMPTY_ERROR = "Charges name must not be empty."
    const val CHARGES_NAME_DIGIT_ERROR = "Charges name must not contains any digit."
    const val CHARGES_NAME_LENGTH_ERROR = "Charges name must be at least 5 characters long."
    const val CHARGES_NAME_ALREADY_EXIST_ERROR = "Charges name already exists."

    const val CHARGES_PRICE_EMPTY_ERROR = "Charges price must not be empty."
    const val CHARGES_PRICE_LESS_THAN_TEN_ERROR = "Charges price must greater than 10 rupees."

    const val DELETE_CHARGES_TITLE = "Delete Charges!"
    const val DELETE_CHARGES_MESSAGE = "Are you sure to delete these charges?. This action can't be undone."

    const val CHARGES_TAG = "Charges-"
    const val CHARGES_LIST = "ChargesList"

    const val CHARGES_SETTINGS_TITLE = "Charges Settings"

    const val IMPORT_CHARGES_TITLE = "Import Charges"
    const val IMPORT_CHARGES_SUB_TEXT = "Click here to import charges from file."
    const val IMPORT_CHARGES_NOTE_TEXT = "Make sure to open charges.json file."

    const val EXPORT_CHARGES_TITLE = "Export Charges"
    const val EXPORT_CHARGES_SUB_TEXT = "Click here to export charges to file."

    const val EXPORT_CHARGES_FILE_NAME = "charges"
}
