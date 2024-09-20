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

package com.niyaj.expenses

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import androidx.test.filters.SmallTest
import com.niyaj.common.tags.ExpenseTestTags.ADD_EDIT_EXPENSE_BUTTON
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EDIT_EXPENSE_ITEM
import com.niyaj.common.tags.ExpenseTestTags.EXPENSES_PRICE_IS_NOT_VALID
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_AMOUNT_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_DATE_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_LIST
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOTE_FIELD
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOT_AVAILABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SCREEN_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SETTINGS_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_TAG
import com.niyaj.common.tags.ExpenseTestTags.EXPORT_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_NOTE_TEXT
import com.niyaj.common.tags.ExpenseTestTags.IMPORT_EXPENSE_TITLE
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
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.toDate
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.expenses.destinations.AddEditExpenseScreenDestination
import com.niyaj.expenses.destinations.ExpensesExportScreenDestination
import com.niyaj.expenses.destinations.ExpensesImportScreenDestination
import com.niyaj.model.Expense
import com.niyaj.model.searchExpense
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
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
import com.niyaj.ui.parameterProvider.ExpensePreviewData
import com.niyaj.ui.utils.Screens
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
class ExpensesEndToEndTest {

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
    private val expensesList = ExpensePreviewData.expenses

    private val newExpense = Expense(
        expenseId = 11,
        expenseName = "Test Expense",
        expenseAmount = "500",
        expenseDate = getStartTime,
        expenseNote = "Test Note",
    )

    private val updatedExpense = Expense(
        expenseId = 11,
        expenseName = "Updated Expense",
        expenseAmount = "100",
        expenseDate = getDateInMilliseconds(8),
        expenseNote = "Updated Note",
    )

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
                    PoposTestNavHost(appState, navGraphSpec = ExpensesNavGraph)
                }
            }
        }
    }

    @Test
    fun expenseScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.EXPENSES_SCREEN, currentRoute)

            onNodeWithTag(EXPENSE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun expenseScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_EXPENSE).assertIsDisplayed().assertHasClickAction()
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
    fun onClickAddNewItem_navigateTo_addEditExpenseScreen() {
        composeTestRule.apply {
            gotoAddEditExpenseScreen()

            onNodeWithText(CREATE_NEW_EXPENSE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(AddEditExpenseScreenDestination.route, currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_EXPENSE).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditExpenseScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditExpenseScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(EXPENSE_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(EXPENSE_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(EXPENSE_AMOUNT_FIELD).assertIsDisplayed()
            onNodeWithText(EXPENSE_PRICE_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(EXPENSE_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("changeDate").assertIsDisplayed()

            onNodeWithTag(EXPENSE_NOTE_FIELD).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditExpenseScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditExpenseScreen()

            // Initial state of the screen
            onNodeWithText(EXPENSE_NAME_EMPTY_ERROR).assertIsDisplayed()
            onNodeWithText(EXPENSE_PRICE_EMPTY_ERROR).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsDisplayed().assertIsNotEnabled()

            // Perform invalid input on itemName field and check for validation error
            onNodeWithTag(EXPENSE_NAME_FIELD).performTextInput("Te")
            onNodeWithText(EXPENSE_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(EXPENSE_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EXPENSE_NAME_FIELD).performTextInput(newExpense.expenseName)
            onNodeWithTag(EXPENSE_NAME_ERROR).assertIsNotDisplayed()

            // Perform invalid input on itemPrice field and check for validation error
            onNodeWithTag(EXPENSE_AMOUNT_FIELD).performTextInput("5")
            onNodeWithText(EXPENSE_PRICE_LESS_THAN_TEN_ERROR).assertIsDisplayed()
            onNodeWithTag(EXPENSE_AMOUNT_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            // Perform invalid input on itemPrice field and check for validation error
            onNodeWithTag(EXPENSE_AMOUNT_FIELD).performTextInput("5C")
            onNodeWithText(EXPENSES_PRICE_IS_NOT_VALID).assertIsDisplayed()
            onNodeWithTag(EXPENSE_AMOUNT_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(EXPENSE_AMOUNT_FIELD).performTextInput(newExpense.expenseAmount)
            onNodeWithTag(EXPENSE_AMOUNT_ERROR).assertIsNotDisplayed()

            val date = newExpense.expenseDate.toDate
            onNodeWithTag(EXPENSE_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("changeDate").assertIsDisplayed().performClick()
            onNodeWithText("SELECT DATE").assertIsDisplayed()
            onNodeWithTag("positive").assertIsDisplayed()
            onNodeWithTag("negative").assertIsDisplayed()
            onNodeWithTag("dialog_date_selection_$date").assertIsDisplayed().performClick()

            // Perform input on expense note
            onNodeWithTag(EXPENSE_NOTE_FIELD).performTextInput(newExpense.expenseNote)

            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewExpense(newExpense)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EXPENSES_SCREEN, route)

            onNodeWithTag(EXPENSE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            onNodeWithTag(EXPENSE_TAG.plus(1))
                .assertIsDisplayed()
                .onChildren()[0]
                .assertTextContains(newExpense.expenseName, true)
                .assertTextContains(newExpense.expenseAmount.toRupee, true)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(EXPENSE_TAG.plus(1))
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
            createNewExpense(newExpense)
            composeTestRule.waitForIdle()

            createNewExpensesList(2)

            onNodeWithTag(EXPENSE_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_LIST).performTouchInput { swipeUp() }

            onNodeWithTag(EXPENSE_TAG.plus(2)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(EXPENSE_TAG.plus(2)).assertIsNotSelected()

            onNodeWithTag(EXPENSE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(AddEditExpenseScreenDestination.route, currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(EDIT_EXPENSE_ITEM).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(EXPENSE_NAME_FIELD).assertTextContains(newExpense.expenseName)

            onNodeWithTag(EXPENSE_AMOUNT_FIELD).assertTextContains(newExpense.expenseAmount)

            onNodeWithTag(EXPENSE_DATE_FIELD).assertIsDisplayed()

            onNodeWithTag(EXPENSE_NOTE_FIELD).assertTextContains(newExpense.expenseNote)

            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsDisplayed().assertIsEnabled()

            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsEnabled().performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EXPENSES_SCREEN, route)

            onNodeWithTag(EXPENSE_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_NAME_FIELD).performTextReplacement(updatedExpense.expenseName)
            onNodeWithTag(EXPENSE_AMOUNT_FIELD).performTextReplacement(updatedExpense.expenseAmount)

            val date = updatedExpense.expenseDate.toDate
            onNodeWithTag(EXPENSE_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("changeDate").assertIsDisplayed().performClick()
            onNodeWithTag("positive").assertIsDisplayed()
            onNodeWithTag("dialog_date_selection_$date").assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_NOTE_FIELD).performTextReplacement(updatedExpense.expenseNote)

            onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(EXPENSE_TAG.plus(1))
                .assertIsDisplayed()
                .onChildren()[0]
                .assertTextContains(updatedExpense.expenseName)
                .assertTextContains(updatedExpense.expenseAmount.toRupee)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_EXPENSE_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(EXPENSE_LIST).assertDoesNotExist()
            onNodeWithTag(EXPENSE_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(2)

            waitForIdle()

            onNodeWithTag(EXPENSE_LIST).performTouchInput { swipeUp() }

            expensesList.take(2).forEach {
                onNodeWithTag(EXPENSE_TAG.plus(it.expenseId))
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

            expensesList.take(2).forEach {
                onNodeWithTag(EXPENSE_TAG.plus(it.expenseId))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(EXPENSE_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newExpense)
            waitForIdle()

            Espresso.pressBack()

            waitUntil {
                onNodeWithTag(EXPENSE_SCREEN_TITLE).isDisplayed()
            }

            onNodeWithTag(EXPENSE_TAG.plus(1)).printToLog("Expense Item")

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EXPENSE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EXPENSE_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewExpense(newExpense)
            waitForIdle()

            onNodeWithTag(EXPENSE_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(3)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Groceries")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            val searchResultCount = expensesList.take(3).searchExpense("Groceries").count()
            val listSize = onNodeWithTag(EXPENSE_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(EXPENSE_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_EXPENSE_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_EXPENSE_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_EXPENSE_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentDestination
            assertNotNull(currentRoute)
            assertEquals(ExpensesImportScreenDestination.route, currentRoute.route)

            onNodeWithText(IMPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_EXPENSE_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsDisplayed()

            val currentRoute = appState.navController.currentDestination
            assertNotNull(currentRoute)
            assertEquals(ExpensesExportScreenDestination.route, currentRoute.route)
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 expense will be exported.").assertIsDisplayed()
            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 expense will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(2)
            waitForIdle()

            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 expenses will be exported.").assertIsDisplayed()
            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(EXPENSE_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(EXPENSE_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 expenses will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(EXPENSE_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(EXPENSE_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 expenses will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsSelected()
            onNodeWithText("1 Selected").assertIsDisplayed()

            // For go back
            Espresso.pressBack()

            waitUntil {
                onNodeWithTag(EXPORT_EXPENSE_TITLE).isDisplayed()
            }

            waitForIdle()

            onNodeWithTag(EXPENSE_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_EXPENSE_TITLE).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EXPENSE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()

            createNewExpensesList(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Rent")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()

            val searchResultCount = expensesList.take(4).searchExpense("Rent").count()
            val listSize = onNodeWithTag(EXPENSE_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(EXPENSE_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(EXPENSE_LIST).assertIsDisplayed()
            onNodeWithTag(EXPENSE_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToExpenseScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewExpense(newExpense)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(EXPENSE_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    private fun gotoAddEditExpenseScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_EXPENSE)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewExpense(newExpense: Expense, useList: Boolean = false) {
        if (useList) {
            composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()
        } else {
            composeTestRule
                .onNodeWithTag(CREATE_NEW_EXPENSE)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }

        composeTestRule.onNodeWithTag(EXPENSE_NAME_FIELD).performTextInput(newExpense.expenseName)
        composeTestRule.onNodeWithTag(EXPENSE_AMOUNT_FIELD)
            .performTextInput(newExpense.expenseAmount)

//        val date = newExpense.expenseDate.toDate
//        composeTestRule.onNodeWithTag(EXPENSE_DATE_FIELD).assertIsDisplayed()
//        composeTestRule.onNodeWithTag("changeDate").performClick()
//        composeTestRule.onNodeWithTag("dialog_date_selection_${date}")
//            .assertIsDisplayed()
//            .performClick()

        composeTestRule.onNodeWithTag(EXPENSE_NOTE_FIELD).performTextInput(newExpense.expenseNote)

        composeTestRule.onNodeWithTag(ADD_EDIT_EXPENSE_BUTTON).performClick()

        composeTestRule.waitForIdle()
    }

    private fun createAndSelectItem(newExpense: Expense) {
        createNewExpense(newExpense)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(EXPENSE_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewExpensesList(limit: Int) {
        composeTestRule.apply {
            expensesList.take(limit).forEach {
                createNewExpense(it, true)
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

            onNodeWithTag(EXPORT_EXPENSE_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentDestination
            assertNotNull(currentRoute)
            assertEquals(ExpensesExportScreenDestination.route, currentRoute.route)
        }
    }

    private fun navigateBackToExpenseScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.EXPENSES_SCREEN, currentRoute)
        }
    }
}
