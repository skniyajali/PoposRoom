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

package com.niyaj.customer

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
import com.niyaj.common.tags.CustomerTestTags.ADD_EDIT_CUSTOMER_BTN
import com.niyaj.common.tags.CustomerTestTags.CREATE_NEW_CUSTOMER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_DETAILS_CARD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_DETAILS_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_LIST
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NOT_AVAILABLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_FIELD
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_RECENT_ORDERS
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SCREEN_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_SETTINGS_TITLE
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_TAG
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_MESSAGE
import com.niyaj.common.tags.CustomerTestTags.DELETE_CUSTOMER_TITLE
import com.niyaj.common.tags.CustomerTestTags.EDIT_CUSTOMER_ITEM
import com.niyaj.common.tags.CustomerTestTags.EXPORT_CUSTOMER_TITLE
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_NOTE_TEXT
import com.niyaj.common.tags.CustomerTestTags.IMPORT_CUSTOMER_TITLE
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
import com.niyaj.customer.destinations.CustomerDetailsScreenDestination
import com.niyaj.customer.details.CustomerDetailsScreen
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Customer
import com.niyaj.model.searchCustomer
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
import com.niyaj.testing.repository.TestCustomerRepository
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
import com.niyaj.ui.parameterProvider.CustomerPreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_CUSTOMER_SCREEN
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
class CustomerEndToEndTest {

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
    private val customerList = CustomerPreviewData.customerList
    private val repository = TestCustomerRepository()

    private val newCustomer = Customer(
        customerId = 1,
        customerPhone = "9078563421",
        customerName = "Test Customer",
        customerEmail = "test@gmail.com",
    )

    private val updatedCustomer = Customer(
        customerId = 1,
        customerPhone = "1234567890",
        customerName = "Updated Customer",
        customerEmail = "updated@gmail.com",
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
                    PoposTestNavHost(appState, navGraphSpec = CustomerNavGraph) {
                        composable(CustomerDetailsScreenDestination) {
                            CustomerDetailsScreen(
                                customerId = navBackStackEntry.arguments?.getInt("customerId") ?: 0,
                                navigator = destinationsNavigator,
                                onClickOrder = {},
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun customerScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.CUSTOMER_SCREEN, currentRoute)

            onNodeWithTag(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun customerScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_CUSTOMER).assertIsDisplayed().assertHasClickAction()
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
    fun onClickAddNewItem_navigateTo_addEditCustomerScreen() {
        composeTestRule.apply {
            gotoAddEditCustomerScreen()

            onNodeWithText(CREATE_NEW_CUSTOMER).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CUSTOMER_SCREEN.plus("?customerId={customerId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_CUSTOMER).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditCustomerScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditCustomerScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(CUSTOMER_PHONE_FIELD).assertIsDisplayed()
            onNodeWithText(CUSTOMER_PHONE_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_NAME_FIELD).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_EMAIL_FIELD).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditCustomerScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditCustomerScreen()

            // Initial state of the screen
            onNodeWithText(CUSTOMER_PHONE_EMPTY_ERROR).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsDisplayed().assertIsNotEnabled()

            // Perform invalid input on customerPhone field and check for validation error
            onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput("Te")
            onNodeWithText(CUSTOMER_PHONE_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_PHONE_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput("Test123444")
            onNodeWithText(CUSTOMER_PHONE_LETTER_ERROR).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_PHONE_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput(newCustomer.customerPhone)
            onNodeWithTag(CUSTOMER_PHONE_ERROR).assertIsNotDisplayed()

            // Perform invalid input on customerName field and check for validation error
            onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput("Te")
            onNodeWithText(CUSTOMER_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput(newCustomer.customerName!!)
            onNodeWithTag(CUSTOMER_NAME_ERROR).assertIsNotDisplayed()

            // Perform input on customerEmail
            onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput("test")
            onNodeWithText(CUSTOMER_EMAIL_VALID_ERROR).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput(newCustomer.customerEmail!!)
            onNodeWithTag(CUSTOMER_EMAIL_ERROR).assertIsNotDisplayed()

            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_SCREEN, route)

            onNodeWithTag(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newCustomer.customerPhone)
                .assertTextContains(newCustomer.customerName!!)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(CUSTOMER_TAG.plus(1))
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
            createNewCustomer(newCustomer)
            composeTestRule.waitForIdle()

            createNewCustomerList(2)

            onNodeWithTag(CUSTOMER_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CUSTOMER_SCREEN.plus("?customerId={customerId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(EDIT_CUSTOMER_ITEM).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(CUSTOMER_PHONE_FIELD).assertTextContains(newCustomer.customerPhone)
            onNodeWithTag(CUSTOMER_PHONE_ERROR).assertDoesNotExist()

            onNodeWithTag(CUSTOMER_NAME_FIELD).assertTextContains(newCustomer.customerName!!)
            onNodeWithTag(CUSTOMER_NAME_ERROR).assertDoesNotExist()

            onNodeWithTag(CUSTOMER_EMAIL_FIELD).assertTextContains(newCustomer.customerEmail!!)
            onNodeWithTag(CUSTOMER_EMAIL_ERROR).assertDoesNotExist()

            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsDisplayed().assertIsEnabled()
                .performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_SCREEN, route)

            onNodeWithTag(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextReplacement(updatedCustomer.customerPhone)
            onNodeWithTag(CUSTOMER_NAME_FIELD).performTextReplacement(updatedCustomer.customerName!!)
            onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextReplacement(updatedCustomer.customerEmail!!)

            onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(CUSTOMER_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedCustomer.customerPhone)
                .assertTextContains(updatedCustomer.customerName!!)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_CUSTOMER_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(CUSTOMER_LIST).assertDoesNotExist()
            onNodeWithTag(CUSTOMER_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(3)

            waitForIdle()

            customerList.take(3).forEach {
                onNodeWithTag(CUSTOMER_TAG.plus(it.customerId.plus(1)))
                    .assertIsDisplayed()
                    .performTouchInput { longClick() }
                    .assertIsSelected()
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().performClick()

            customerList.take(3).forEach {
                onNodeWithTag(CUSTOMER_TAG.plus(it.customerId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(CUSTOMER_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newCustomer)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CUSTOMER_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CUSTOMER_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(CUSTOMER_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(4)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("John Doe")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            waitForIdle()

            val searchResultCount = customerList.take(4).searchCustomer("John Doe").count()
            val listSize = onNodeWithTag(CUSTOMER_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(CUSTOMER_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_CUSTOMER_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_CUSTOMER_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_CUSTOMER_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_CUSTOMER_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_EXPORT_SCREEN, currentRoute)
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 customer will be exported.").assertIsDisplayed()
            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 customer will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(2)
            waitForIdle()

            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 customers will be exported.").assertIsDisplayed()
            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 customers will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CUSTOMER_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 customers will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CUSTOMER_TITLE).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CUSTOMER_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            createNewCustomerList(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Jane Smith")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()

            waitForIdle()

            val searchResultCount = customerList.take(4).searchCustomer("Jane Smith").count()
            val listSize = onNodeWithTag(CUSTOMER_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(CUSTOMER_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToCustomerScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CUSTOMER_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun customerDetails_onClickCustomer_shouldNavigateToDetailsScreen() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(
                Screens.CUSTOMER_DETAILS_SCREEN.plus("?customerId={customerId}"),
                currentRoute,
            )

            onNodeWithTag(CUSTOMER_DETAILS_TITLE).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_LIST).assertIsDisplayed()
        }
    }

    @Test
    fun customerDetails_withEmptyOrder_shouldVisibleToUser() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(
                Screens.CUSTOMER_DETAILS_SCREEN.plus("?customerId={customerId}"),
                currentRoute,
            )

            onNodeWithTag(CUSTOMER_DETAILS_CARD).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_DETAILS_CARD).apply {
                assertTextContains("Phone - ${newCustomer.customerPhone}")
                assertTextContains("Name - ${newCustomer.customerName}")
//                assertTextContains("Email - ${newCustomer.customerEmail}")
            }

            onNodeWithTag(CUSTOMER_DETAILS_TITLE).performTouchInput { swipeUp() }

            onNodeWithTag(CUSTOMER_RECENT_ORDERS)
                .assertIsDisplayed()
                .performTouchInput { swipeUp() }

            onNodeWithTag(CUSTOMER_RECENT_ORDERS).apply {
                assertTextContains("No orders made using this customer.")
            }
        }
    }

    @Test
    fun customerDetails_withOrders_shouldVisibleToUser() {
        composeTestRule.apply {
            createNewCustomer(newCustomer)
            waitForIdle()

            val orders = CustomerPreviewData.customerWiseOrders.take(5)
            repository.updateCustomerWiseOrderData(orders)

            waitForIdle()

            onNodeWithTag(CUSTOMER_TAG.plus(1)).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(
                Screens.CUSTOMER_DETAILS_SCREEN.plus("?customerId={customerId}"),
                currentRoute,
            )

            onNodeWithTag(CUSTOMER_DETAILS_CARD).assertIsDisplayed()
            onNodeWithTag(CUSTOMER_DETAILS_CARD).apply {
                assertTextContains("Phone - ${newCustomer.customerPhone}")
                assertTextContains("Name - ${newCustomer.customerName}")
            }

            onNodeWithTag(CUSTOMER_LIST).performTouchInput { swipeUp() }

            /*
            onNodeWithTag(CUSTOMER_RECENT_ORDERS).assertIsDisplayed()
            onNodeWithTag("ItemNotAvailableHalf").assertIsNotDisplayed()
            onNodeWithText("No orders made using this customer.").assertIsNotDisplayed()

            onNodeWithTag(CUSTOMER_RECENT_ORDERS).apply {
                orders.forEach {
                    assertTextContains(it.customerAddress)
                    assertTextContains(it.totalPrice.toRupee)
                }
            }

            orders.forEach {
                onNodeWithTag("Order-${it.orderId}")
                    .assertIsDisplayed()
                    .assertTextContains(it.orderId.toString())
            }

             */
        }
    }

    private fun gotoAddEditCustomerScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CUSTOMER)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewCustomer(newCustomer: Customer) {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CUSTOMER)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithTag(CUSTOMER_PHONE_FIELD)
            .performTextInput(newCustomer.customerPhone)
        composeTestRule.onNodeWithTag(CUSTOMER_NAME_FIELD)
            .performTextInput(newCustomer.customerName.toString())
        composeTestRule.onNodeWithTag(CUSTOMER_EMAIL_FIELD)
            .performTextInput(newCustomer.customerEmail.toString())

        composeTestRule.onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).performClick()

        composeTestRule.waitForIdle()
    }

    private fun createAndSelectItem(newCustomer: Customer) {
        createNewCustomer(newCustomer)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CUSTOMER_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewCustomerList(limit: Int) {
        composeTestRule.apply {
            customerList.take(limit).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                waitForIdle()

                onNodeWithTag(CUSTOMER_PHONE_FIELD).performTextInput(it.customerPhone)
                onNodeWithTag(CUSTOMER_NAME_FIELD).performTextInput(it.customerName.toString())
                onNodeWithTag(CUSTOMER_EMAIL_FIELD).performTextInput(it.customerEmail.toString())

                onNodeWithTag(ADD_EDIT_CUSTOMER_BTN).assertIsEnabled().performClick()

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

            onNodeWithTag(EXPORT_CUSTOMER_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToCustomerScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CUSTOMER_SCREEN, currentRoute)
        }
    }
}
