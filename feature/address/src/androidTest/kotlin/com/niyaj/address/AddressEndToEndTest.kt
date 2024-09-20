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

package com.niyaj.address

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
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso
import androidx.test.filters.SmallTest
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_FULL_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.ADDRESS_ITEM_TAG
import com.niyaj.common.tags.AddressTestTags.ADDRESS_LIST
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_NOT_AVAILABLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SCREEN_TITLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SETTINGS_TITLE
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_ERROR
import com.niyaj.common.tags.AddressTestTags.ADDRESS_SHORT_NAME_FIELD
import com.niyaj.common.tags.AddressTestTags.ADDRESS_S_NAME_LESS_THAN_TWO_ERROR
import com.niyaj.common.tags.AddressTestTags.ADD_EDIT_ADDRESS_BTN
import com.niyaj.common.tags.AddressTestTags.CREATE_ADDRESS_SCREEN
import com.niyaj.common.tags.AddressTestTags.CREATE_NEW_ADDRESS
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_MESSAGE
import com.niyaj.common.tags.AddressTestTags.DELETE_ADDRESS_ITEM_TITLE
import com.niyaj.common.tags.AddressTestTags.EXPORT_ADDRESS_TITLE
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_NOTE_TEXT
import com.niyaj.common.tags.AddressTestTags.IMPORT_ADDRESS_TITLE
import com.niyaj.common.tags.AddressTestTags.UPDATE_ADDRESS_SCREEN
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
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Address
import com.niyaj.model.searchAddress
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
import com.niyaj.ui.parameterProvider.AddressPreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_ADDRESS_SCREEN
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
class AddressEndToEndTest {

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
    private val addressList = AddressPreviewData.addressList

    private val newAddress = Address(
        addressId = 1,
        addressName = "Test Address",
        shortName = "TA",
    )

    private val updatedAddress = Address(
        addressId = 1,
        addressName = "Updated Address",
        shortName = "UA",
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
                    PoposTestNavHost(appState, navGraphSpec = AddressNavGraph)
                }
            }
        }
    }

    @Test
    fun addressScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.ADDRESS_SCREEN, currentRoute)

            onNodeWithTag(ADDRESS_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun addressScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_ADDRESS).assertIsDisplayed().assertHasClickAction()
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
    fun onClickAddNewItem_navigateTo_addEditAddressScreen() {
        composeTestRule.apply {
            gotoAddEditAddressScreen()

            onNodeWithText(CREATE_NEW_ADDRESS).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_ADDRESS_SCREEN.plus("?addressId={addressId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_ADDRESS_SCREEN).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditAddressScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditAddressScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(ADDRESS_FULL_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(ADDRESS_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(ADDRESS_SHORT_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditAddressScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditAddressScreen()

            // Perform invalid input on itemName field and check for validation error
            onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput("Te")
            onNodeWithText(ADDRESS_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(ADDRESS_FULL_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput(newAddress.addressName)
            onNodeWithTag(ADDRESS_FULL_NAME_ERROR).assertIsNotDisplayed()

            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).performTextReplacement("A")
            onNodeWithText(ADDRESS_S_NAME_LESS_THAN_TWO_ERROR).assertIsDisplayed()
            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).performTextInput(newAddress.shortName)
            onNodeWithTag(ADDRESS_SHORT_NAME_ERROR).assertIsNotDisplayed()

            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewAddress(newAddress)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADDRESS_SCREEN, route)

            onNodeWithTag(ADDRESS_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newAddress.addressName)
                .assertTextContains(newAddress.shortName)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1))
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
            createNewAddress(newAddress)
            composeTestRule.waitForIdle()

            createNewAddresses(2)

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(ADDRESS_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_ADDRESS_SCREEN.plus("?addressId={addressId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(UPDATE_ADDRESS_SCREEN).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(ADDRESS_FULL_NAME_FIELD).assertTextContains(newAddress.addressName)
            onNodeWithText(ADDRESS_FULL_NAME_ERROR).assertDoesNotExist()

            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).assertTextContains(newAddress.shortName)
            onNodeWithText(ADDRESS_SHORT_NAME_ERROR).assertDoesNotExist()

            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsDisplayed().assertIsEnabled()

            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsEnabled().performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADDRESS_SCREEN, route)

            onNodeWithTag(ADDRESS_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextReplacement(updatedAddress.addressName)
            onNodeWithTag(ADDRESS_SHORT_NAME_FIELD).performTextReplacement(updatedAddress.shortName)

            onNodeWithTag(ADD_EDIT_ADDRESS_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedAddress.addressName)
                .assertTextContains(updatedAddress.shortName)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_ADDRESS_ITEM_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_ADDRESS_ITEM_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(ADDRESS_LIST).assertDoesNotExist()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewAddress(newAddress)

            createNewAddresses(3)

            waitForIdle()

            addressList.take(3).forEach {
                onNodeWithTag(ADDRESS_ITEM_TAG.plus(it.addressId.plus(1)))
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

            addressList.take(3).forEach {
                onNodeWithTag(ADDRESS_ITEM_TAG.plus(it.addressId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(ADDRESS_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newAddress)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDRESS_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDRESS_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewAddress(newAddress)
            waitForIdle()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            createNewAddresses(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            createNewAddresses(4)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Main")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()

//            val searchResultCount = addressList.take(4).searchAddress("Main").count()
//            val listSize = onNodeWithTag(ADDRESS_LIST).fetchSemanticsNode().children.size
//            assertEquals(searchResultCount, listSize)
//
//            // Search by price
//            onNodeWithTag(STANDARD_SEARCH_BAR).performTextReplacement("Pine")
//            val resultCount = addressList.take(4).searchAddress("Pine").count()
//
//            val size = onNodeWithTag(ADDRESS_LIST).fetchSemanticsNode().children.size
//            assertEquals(resultCount, size)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(ADDRESS_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_ADDRESS_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_ADDRESS_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_ADDRESS_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADDRESS_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_ADDRESS_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 address will be exported.").assertIsDisplayed()
            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 address will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            createNewAddresses(2)
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 addresses will be exported.").assertIsDisplayed()
            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 addresses will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 addresses will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ADDRESS_TITLE).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDRESS_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            createNewAddresses(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()

            createNewAddresses(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Main")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()

            val searchResultCount = addressList.take(4).searchAddress("Main").count()
            val listSize = onNodeWithTag(ADDRESS_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)

            // Search by price
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextReplacement("Pine")
            waitForIdle()

            val resultCount = addressList.take(4).searchAddress("Pine").count()
            val size = onNodeWithTag(ADDRESS_LIST).fetchSemanticsNode().children.size
            assertEquals(resultCount, size)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(ADDRESS_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDRESS_LIST).assertIsDisplayed()
            onNodeWithTag(ADDRESS_ITEM_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToAddressScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewAddress(newAddress)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDRESS_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    private fun gotoAddEditAddressScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADDRESS)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewAddress(newAddress: Address) {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADDRESS)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD)
            .performTextInput(newAddress.addressName)
        composeTestRule.onNodeWithTag(ADDRESS_SHORT_NAME_FIELD)
            .performTextReplacement(newAddress.shortName)

        composeTestRule.onNodeWithTag(ADD_EDIT_ADDRESS_BTN).performClick()
    }

    private fun createAndSelectItem(newAddress: Address) {
        createNewAddress(newAddress)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ADDRESS_ITEM_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewAddresses(limit: Int) {
        addressList.take(limit).forEach {
            composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

            composeTestRule.onNodeWithTag(ADDRESS_FULL_NAME_FIELD).performTextInput(it.addressName)
            composeTestRule.onNodeWithTag(ADDRESS_SHORT_NAME_FIELD)
                .performTextReplacement(it.shortName)

            composeTestRule.onNodeWithTag(ADD_EDIT_ADDRESS_BTN).performClick()

            composeTestRule.waitForIdle()
        }
    }

    private fun ComposeTestRule.navigateToExportScreen() {
        this.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(EXPORT_ADDRESS_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADDRESS_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToAddressScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADDRESS_SCREEN, currentRoute)
        }
    }
}
