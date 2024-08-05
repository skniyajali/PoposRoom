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

object CategoryConstants {

    const val CATEGORY_SCREEN_TITLE = "Categories"
    const val CATEGORY_NOT_AVAILABLE = "Category Not Available"
    const val CATEGORY_SEARCH_PLACEHOLDER = "Search for Categories..."

    const val CREATE_NEW_CATEGORY = "Create New Category"
    const val UPDATE_CATEGORY = "Update Category"
    const val ADD_EDIT_CATEGORY_BTN = "AddEditCategoryBtn"

    const val CATEGORY_NAME_FIELD = "Category Name"
    const val CATEGORY_NAME_ERROR_TAG = "CategoryNameError"

    const val CATEGORY_AVAILABLE_SWITCH = "Category Available Switch"

    const val CATEGORY_NAME_EMPTY_ERROR = "Category name must not be empty."
    const val CATEGORY_NAME_LENGTH_ERROR = "Category name must be at least 3 characters long."
    const val CATEGORY_NAME_ALREADY_EXIST_ERROR = "Category name already exists."

    const val DELETE_CATEGORY_ITEM_TITLE = "Delete Category!"
    const val DELETE_CATEGORY_ITEM_MESSAGE =
        "Are you sure to delete these categories? This action cannot be undone."

    const val CATEGORY_ITEM_TAG = "Category-"
    const val CATEGORY_LIST = "CategoryList"

    const val CATEGORY_SETTINGS_TITLE = "Category Settings"

    const val IMPORT_CATEGORY_TITLE = "Import Categories"
    const val IMPORT_CATEGORY_NOTE = "Click here to import data from file."
    const val IMPORT_CATEGORY_NOTE_TEXT = "Make sure to open categories.json file."

    const val EXPORT_CATEGORY_TITLE = "Export Categories"
    const val EXPORT_CATEGORY_TITLE_NOTE = "Click here to export category to file."

    const val EXPORT_CATEGORY_FILE_NAME = "categories"
}
