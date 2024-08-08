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

package com.niyaj.employee

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.filters.SmallTest
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.niyaj.common.tags.EmployeeTestTags.ADD_EDIT_EMPLOYEE_BTN
import com.niyaj.common.tags.EmployeeTestTags.CREATE_NEW_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.DELETE_EMPLOYEE_MESSAGE
import com.niyaj.common.tags.EmployeeTestTags.DELETE_EMPLOYEE_TITLE
import com.niyaj.common.tags.EmployeeTestTags.EDIT_EMPLOYEE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_DETAILS
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_EMAIL_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_JOINED_DATE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_LIST
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_DIGIT_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NAME_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_NOT_AVAILABLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PARTNER_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_PHONE_LETTER_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_POSITION_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_EMPTY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_LENGTH_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_LETTER_ERROR
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SALARY_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SCREEN_TITLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_SETTINGS_TITLE
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TAG
import com.niyaj.common.tags.EmployeeTestTags.EMPLOYEE_TYPE_FIELD
import com.niyaj.common.tags.EmployeeTestTags.EMP_ABSENT_NOTE
import com.niyaj.common.tags.EmployeeTestTags.EMP_PAYMENTS_NOTE
import com.niyaj.common.tags.EmployeeTestTags.EXPORT_EMPLOYEE_TITLE
import com.niyaj.common.tags.EmployeeTestTags.IMPORT_EMPLOYEE_NOTE_TEXT
import com.niyaj.common.tags.EmployeeTestTags.IMPORT_EMPLOYEE_TITLE
import com.niyaj.common.tags.EmployeeTestTags.QR_CODE_NOTE
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DIALOG_CONFIRM_TEXT
import com.niyaj.common.utils.Constants.DIALOG_DISMISS_TEXT
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_DELETE_DIALOG
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employee.destinations.EmployeeDetailsScreenDestination
import com.niyaj.employee.details.EmployeeDetailsScreen
import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType.Daily
import com.niyaj.model.EmployeeSalaryType.Monthly
import com.niyaj.model.EmployeeType.FullTime
import com.niyaj.model.EmployeeType.PartTime
import com.niyaj.model.searchEmployee
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
import com.niyaj.testing.util.EmptyOpenResultRecipient
import com.niyaj.testing.util.PoposTestAppState
import com.niyaj.testing.util.PoposTestNavHost
import com.niyaj.testing.util.rememberPoposTestAppState
import com.niyaj.ui.components.IMPORT_EXPORT_BTN
import com.niyaj.ui.components.IMPORT_OPN_FILE
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.components.PRIMARY_APP_DRAWER
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_EMPLOYEE_SCREEN
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.utils.route
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@HiltAndroidTest
@SmallTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class EmployeeEndToEndTest {

    /**
     * Manages the components' state and is used to perform injection on your test
     */
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    /**
     * Use a test activity to set the content on.
     */
    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<HiltComponentActivity>()

    private lateinit var appState: PoposTestAppState
    private val customerList = EmployeePreviewData.employeeList

    private val newEmployee = Employee(
        employeeId = 1,
        employeeName = "Test Employee",
        employeePhone = "9078563412",
        employeeSalary = "10000",
        employeePosition = "Chef",
        employeeType = PartTime,
        employeeSalaryType = Daily,
    )

    private val updatedEmployee = Employee(
        employeeId = 1,
        employeeName = "Updated Employee",
        employeePhone = "1234567890",
        employeeSalary = "12000",
        employeePosition = "Master",
        employeeType = FullTime,
        employeeSalaryType = Monthly,
        isDeliveryPartner = true,
    )

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            appState = rememberPoposTestAppState()

            CompositionLocalProvider(
                // Replaces images with placeholders
                LocalInspectionMode provides true,
            ) {
                PoposRoomTheme {
                    PoposTestNavHost(
                        appState = appState,
                        navGraphSpec = EmployeeNavGraph,
                    ) {
                        composable(EmployeeDetailsScreenDestination) {
                            EmployeeDetailsScreen(
                                employeeId = navBackStackEntry.arguments?.getInt("employeeId") ?: 0,
                                navigator = destinationsNavigator,
                                onClickAddPayment = {},
                                onClickAddAbsent = {},
                                paymentRecipient = EmptyOpenResultRecipient(),
                                absentRecipient = EmptyOpenResultRecipient(),
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun employeeScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.EMPLOYEE_SCREEN, currentRoute)

            onNodeWithTag(EMPLOYEE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun employeeScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_EMPLOYEE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
        }
    }

    @Test
    fun onClickDrawerBtn_shouldOpenDrawer() {
        composeTestRule.apply {
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction().performClick()
            onNodeWithTag(PRIMARY_APP_DRAWER).assertIsDisplayed()
        }
    }

    @Test
    fun onClickAddNewItem_navigateTo_addEditEmployeeScreen() {
        composeTestRule.apply {
            gotoAddEditEmployeeScreen()

            onNodeWithText(CREATE_NEW_EMPLOYEE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_EMPLOYEE_SCREEN.plus("?employeeId={employeeId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_EMPLOYEE).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditEmployeeScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditEmployeeScreen()

            waitForIdle()

            onNodeWithTag("addEditEmployeeFields").assertIsDisplayed()

            // Check screen has correct field
            onNodeWithTag(EMPLOYEE_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_PHONE_FIELD).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_PHONE_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_SALARY_FIELD).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_SALARY_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_POSITION_FIELD).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_POSITION_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag("addEditEmployeeFields").performTouchInput {
                swipeUp(
                    startY = center.y + 200f,
                    endY = center.y - 200f,
                )
            }

            waitForIdle()

            onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).assertIsDisplayed()

            onNodeWithText(EMPLOYEE_TYPE_FIELD).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_EMAIL_FIELD).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditEmployeeScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditEmployeeScreen()

            // Perform invalid input on customerName field and check for validation error
            onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput("Te")
            onNodeWithText(EMPLOYEE_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput("Test4")
            onNodeWithText(EMPLOYEE_NAME_DIGIT_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextInput(newEmployee.employeeName)
            onNodeWithTag(EMPLOYEE_NAME_ERROR).assertIsNotDisplayed()

            // Perform invalid input on customerPhone field and check for validation error
            onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput("Te")
            onNodeWithText(EMPLOYEE_PHONE_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_PHONE_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput("Test123444")
            onNodeWithText(EMPLOYEE_PHONE_LETTER_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_PHONE_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextInput(newEmployee.employeePhone)
            onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertIsNotDisplayed()

            onNodeWithTag(EMPLOYEE_SALARY_FIELD).performTextInput("1200")
            onNodeWithText(EMPLOYEE_SALARY_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_SALARY_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_SALARY_FIELD).performTextInput("Test1")
            onNodeWithText(EMPLOYEE_SALARY_LETTER_ERROR).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_SALARY_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EMPLOYEE_SALARY_FIELD).performTextInput(newEmployee.employeeSalary)
            onNodeWithTag(EMPLOYEE_SALARY_ERROR).assertIsNotDisplayed()

            onNodeWithTag(EMPLOYEE_POSITION_FIELD).assertIsDisplayed().performClick()
            onNodeWithTag("positionList").assertIsDisplayed()
            onNodeWithText("Chef").assertIsDisplayed().performClick()

            onNodeWithTag("addEditEmployeeFields").performTouchInput {
                swipeUp(
                    startY = center.y + 200f,
                    endY = center.y - 200f,
                )
            }

            // Perform input on customerEmail
            onNodeWithTag(EMPLOYEE_EMAIL_FIELD).assertIsDisplayed()
            newEmployee.employeeEmail?.let {
                onNodeWithTag(EMPLOYEE_EMAIL_FIELD).performTextInput(it)
            }

            onNodeWithTag(EMPLOYEE_JOINED_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("datePicker").assertIsDisplayed().assertHasClickAction()

            onNodeWithText(EMPLOYEE_TYPE_FIELD).assertIsDisplayed()
            newEmployee.employeeType.let {
                onNodeWithTag(EMPLOYEE_TYPE_FIELD.plus(it.name))
                    .assertIsDisplayed().performClick().assertIsSelected()
            }

            onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD).assertIsDisplayed()
            newEmployee.employeeSalaryType.let {
                onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(it.name))
                    .assertIsDisplayed().performClick().assertIsSelected()
            }

            onNodeWithTag(EMPLOYEE_PARTNER_FIELD).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_PARTNER_FIELD).performClick()
            onNodeWithText("Scan QR").assertIsDisplayed()
            onNodeWithText(QR_CODE_NOTE).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_SCREEN, route)

            onNodeWithTag(EMPLOYEE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newEmployee.employeePhone)
                .assertTextContains(newEmployee.employeeName)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_TAG.plus(1))
                .assertIsDisplayed()
                .performTouchInput { longClick() }
                .assertIsSelected()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
        }
    }

    @Test
    fun onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            composeTestRule.waitForIdle()

            createNewEmployeeList(2)

            waitForIdle()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(EMPLOYEE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_EMPLOYEE_SCREEN.plus("?employeeId={employeeId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(EDIT_EMPLOYEE).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(EMPLOYEE_NAME_FIELD).assertTextContains(newEmployee.employeeName)
            onNodeWithTag(EMPLOYEE_NAME_ERROR).assertDoesNotExist()

            onNodeWithTag(EMPLOYEE_PHONE_FIELD).assertTextContains(newEmployee.employeePhone)
            onNodeWithTag(EMPLOYEE_PHONE_ERROR).assertDoesNotExist()

            onNodeWithTag(EMPLOYEE_SALARY_FIELD).assertTextContains(newEmployee.employeeSalary)
            onNodeWithTag(EMPLOYEE_SALARY_ERROR).assertDoesNotExist()

            onNodeWithTag(EMPLOYEE_POSITION_FIELD).assertTextContains(newEmployee.employeePosition)
            onNodeWithTag(EMPLOYEE_POSITION_EMPTY_ERROR).assertDoesNotExist()

            onNodeWithTag("addEditEmployeeFields").performTouchInput {
                swipeUp(
                    startY = center.y + 200f,
                    endY = center.y - 200f,
                )
            }

            onNodeWithTag(EMPLOYEE_TYPE_FIELD.plus(newEmployee.employeeType.name)).assertIsSelected()

            onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(newEmployee.employeeSalaryType.name)).assertIsSelected()

            if (newEmployee.isDeliveryPartner) {
                onNodeWithTag(EMPLOYEE_PARTNER_FIELD).assertIsOn()
            } else {
                onNodeWithTag(EMPLOYEE_PARTNER_FIELD).assertIsOff()
            }

            onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).assertIsDisplayed().assertIsEnabled()
                .performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_SCREEN, route)

            onNodeWithTag(EMPLOYEE_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_PHONE_FIELD).performTextReplacement(updatedEmployee.employeePhone)
            onNodeWithTag(EMPLOYEE_NAME_FIELD).performTextReplacement(updatedEmployee.employeeName)
            onNodeWithTag(EMPLOYEE_SALARY_FIELD).performTextReplacement(updatedEmployee.employeeSalary)

            onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(EMPLOYEE_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedEmployee.employeeName)
                .assertTextContains(updatedEmployee.employeePhone)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_EMPLOYEE_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(EMPLOYEE_LIST).assertDoesNotExist()
            onNodeWithTag(EMPLOYEE_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(2)

            waitForIdle()

            customerList.take(2).forEach {
                onNodeWithTag(EMPLOYEE_TAG.plus(it.employeeId.plus(1)))
                    .assertIsDisplayed()
                    .performTouchInput { longClick() }
                    .assertIsSelected()
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()
            onNodeWithText("2 Selected").assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().performClick()

            customerList.take(2).forEach {
                onNodeWithTag(EMPLOYEE_TAG.plus(it.employeeId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(EMPLOYEE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newEmployee)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun onSelected_pressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            onNodeWithTag(EMPLOYEE_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("John Doe")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()

            waitForIdle()

            val searchResultCount = customerList.take(4).searchEmployee("John Doe").count()
            val listSize = onNodeWithTag(EMPLOYEE_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(EMPLOYEE_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_EMPLOYEE_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_EMPLOYEE_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_EMPLOYEE_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_EXPORT_SCREEN, currentRoute)
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 employee will be exported.").assertIsDisplayed()
            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 employee will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(2)
            waitForIdle()

            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 employees will be exported.").assertIsDisplayed()
            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 employees will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(EMPLOYEE_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 employees will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()

            createNewEmployeeList(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Jane Smith")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()

            waitForIdle()

            val searchResultCount = customerList.take(4).searchEmployee("Jane Smith").count()
            val listSize = onNodeWithTag(EMPLOYEE_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(EMPLOYEE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EMPLOYEE_LIST).assertIsDisplayed()
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToEmployeeScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EMPLOYEE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun employeeDetails_onClickEmployee_shouldNavigateToDetailsScreen() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)

            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(
                Screens.EMPLOYEE_DETAILS_SCREEN.plus("?employeeId={employeeId}"),
                currentRoute,
            )

            onNodeWithTag(EMPLOYEE_DETAILS).assertIsDisplayed()
            onNodeWithTag("EmployeeDetailsList").assertIsDisplayed()
        }
    }

    @Test
    fun employeeDetails_onNavigated_detailsWillBeVisibleToUser() {
        composeTestRule.apply {
            createNewEmployee(newEmployee)
            onNodeWithTag(EMPLOYEE_TAG.plus(1)).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag("EmployeeDetailsList").assertIsDisplayed()

            // Employee Details
            onNodeWithText("Name - ${newEmployee.employeeName}").assertIsDisplayed()
            onNodeWithText("Phone - ${newEmployee.employeePhone}").assertIsDisplayed()
            onNodeWithText("Position - ${newEmployee.employeePosition}").assertIsDisplayed()
            onNodeWithText("Salary - ${newEmployee.employeeSalary.toRupee}").assertIsDisplayed()
            onNodeWithText("Type - ${newEmployee.employeeType}").assertIsDisplayed()
            onNodeWithText("Salary Type - ${newEmployee.employeeSalaryType}").assertIsDisplayed()

            onNodeWithTag("EmployeeDetailsList").performTouchInput { swipeUp() }

            onNodeWithTag("PaymentDetailsExpand").assertIsDisplayed()
                .assertHasClickAction().performClick()
            onNodeWithText(EMP_PAYMENTS_NOTE).assertIsDisplayed()

            onNodeWithTag("EmployeeDetailsList").performTouchInput { swipeUp() }

            onNodeWithTag("AbsentDetailsExpand").assertIsDisplayed()
                .assertHasClickAction().performClick()

            onNodeWithTag("EmployeeDetailsList").performTouchInput { swipeUp() }

            onNodeWithText(EMP_ABSENT_NOTE).assertIsDisplayed()
        }
    }

    // TODO: Add More test class to test payment, absent details of employee

    private fun gotoAddEditEmployeeScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_EMPLOYEE)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewEmployee(newEmployee: Employee, usingList: Boolean = false) {
        if (usingList) {
            composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()
        } else {
            composeTestRule
                .onNodeWithTag(CREATE_NEW_EMPLOYEE)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag(EMPLOYEE_NAME_FIELD)
            .performTextInput(newEmployee.employeeName)

        composeTestRule.onNodeWithTag(EMPLOYEE_PHONE_FIELD)
            .performTextInput(newEmployee.employeePhone)

        composeTestRule.onNodeWithTag(EMPLOYEE_SALARY_FIELD)
            .performTextInput(newEmployee.employeeSalary)

        composeTestRule.onNodeWithTag(EMPLOYEE_POSITION_FIELD).performClick()
        composeTestRule.onNodeWithTag("positionList").assertIsDisplayed()
        composeTestRule.onNodeWithText(this.newEmployee.employeePosition).assertIsDisplayed()
            .performClick()

        composeTestRule.onNodeWithTag("addEditEmployeeFields").performTouchInput {
            swipeUp(
                startY = center.y + 200f,
                endY = center.y - 200f,
            )
        }

        newEmployee.employeeEmail?.let {
            composeTestRule.onNodeWithTag(EMPLOYEE_EMAIL_FIELD)
                .performTextInput(newEmployee.employeeEmail.toString())
        }

        composeTestRule.onNodeWithTag(EMPLOYEE_TYPE_FIELD.plus(newEmployee.employeeType.name))
            .performClick().assertIsSelected()

        composeTestRule
            .onNodeWithTag(EMPLOYEE_SALARY_TYPE_FIELD.plus(newEmployee.employeeSalaryType.name))
            .performClick().assertIsSelected()

        if (newEmployee.isDeliveryPartner) {
            composeTestRule.onNodeWithTag(EMPLOYEE_PARTNER_FIELD).performClick().assertIsOn()
        }

        composeTestRule.onNodeWithTag(ADD_EDIT_EMPLOYEE_BTN).performClick()

        composeTestRule.waitForIdle()
    }

    private fun createAndSelectItem(newEmployee: Employee) {
        createNewEmployee(newEmployee)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(EMPLOYEE_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewEmployeeList(limit: Int) {
        composeTestRule.apply {
            customerList.take(limit).forEach {
                createNewEmployee(it, true)

                waitForIdle()
            }
        }
    }

    private fun navigateToExportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            waitForIdle()

            onNodeWithTag(EXPORT_EMPLOYEE_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToEmployeeScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EMPLOYEE_SCREEN, currentRoute)
        }
    }
}
