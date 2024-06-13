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

object EmployeeTestTags {

    const val EMPLOYEE_SCREEN_TITLE = "Employees"
    const val EMPLOYEE_NOT_AVAILABLE = "Employee Not Available"
    const val NO_ITEMS_IN_EMPLOYEE = "Employees Not Found"
    const val EMPLOYEE_SEARCH_PLACEHOLDER = "Search for Employee..."

    const val CREATE_NEW_EMPLOYEE = "Create New Employee"
    const val EDIT_EMPLOYEE = "Update Employee"

    const val EMPLOYEE_NAME_FIELD = "Employee Name"
    const val EMPLOYEE_NAME_ERROR = "Employee NameError"

    const val EMPLOYEE_PHONE_FIELD = "Employee Phone"
    const val EMPLOYEE_PHONE_ERROR = "Employee PhoneError"

    const val EMPLOYEE_SALARY_FIELD = "Employee Salary"
    const val EMPLOYEE_SALARY_ERROR = "Employee SalaryError"

    const val EMPLOYEE_EMAIL_FIELD = "Employee Email"

    const val EMPLOYEE_SALARY_TYPE_FIELD = "Employee Salary Type"

    const val EMPLOYEE_TYPE_FIELD = "Employee Type"

    const val EMPLOYEE_PARTNER_FIELD = "Delivery Partner"

    const val EMPLOYEE_PARTNER_CHECKED_FIELD = "Marked as a Delivery Partner"
    const val EMPLOYEE_PARTNER_UNCHECKED_FIELD = "Mark as a Delivery Partner"
    const val QR_CODE_NOTE = "Please scan a QR code for this partner, otherwise restaurant QR code will be shown on order bill."

    const val EMPLOYEE_POSITION_FIELD = "Employee Position"
    const val EMPLOYEE_POSITION_ERROR = "Employee PositionError"

    const val EMPLOYEE_JOINED_DATE_FIELD = "Employee JoinedDate"

    const val ADD_EDIT_EMPLOYEE_BUTTON = "AddEdit EmployeeButton"

    const val EMPLOYEE_NAME_EMPTY_ERROR = "Employee name must not be empty"
    const val EMPLOYEE_NAME_DIGIT_ERROR = "Employee name must not contain any digit"
    const val EMPLOYEE_NAME_LENGTH_ERROR = "Employee name must be more than 4 characters"
    const val EMPLOYEE_NAME_ALREADY_EXIST_ERROR = "Employee name already exists."

    const val EMPLOYEE_PHONE_EMPTY_ERROR = "Employee phone must not be empty."
    const val EMPLOYEE_PHONE_LETTER_ERROR = "The phone no should not contains any letter."
    const val EMPLOYEE_PHONE_LENGTH_ERROR = "Employee phone must be 10 digits."
    const val EMPLOYEE_PHONE_ALREADY_EXIST_ERROR = "Employee phone already exists."

    const val EMPLOYEE_POSITION_EMPTY_ERROR = "Employee position must not be empty."

    const val EMPLOYEE_SALARY_EMPTY_ERROR = "Employee salary must not be empty"
    const val EMPLOYEE_SALARY_LETTER_ERROR = "Employee salary must not contain any characters"
    const val EMPLOYEE_SALARY_LENGTH_ERROR = "Employee salary must be more than 5 digits"

    const val DELETE_EMPLOYEE_TITLE = "Delete Employee?"
    const val DELETE_EMPLOYEE_MESSAGE = "Are you sure to delete these employee? This action cannot be undone."

    const val EMPLOYEE_TAG = "Customer-"

    // Details screen tags
    const val EMPLOYEE_DETAILS = "Employee Details"
    const val REMAINING_AMOUNT_TEXT = "remainingAmount"

    const val EMPLOYEE_SETTINGS_TITLE = "Employees Settings"

    const val IMPORT_EMPLOYEE_TITLE = "Import Employees"
    const val IMPORT_EMPLOYEE_SUB_TITLE = "Click here to import data from file."

    const val IMPORT_EMPLOYEE_NOTE_TEXT = "Make sure to open employees.json file."
    const val IMPORT_EMPLOYEE_OPN_FILE = "Open File"

    const val EXPORT_EMPLOYEE_TITLE = "Export Employees"
    const val EXPORT_EMPLOYEE_SUB_TITLE = "Click here to export data to file."

    const val EXPORT_EMPLOYEE_FILE_NAME = "employees"
}
