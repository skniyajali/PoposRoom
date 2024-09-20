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

package com.niyaj.charges

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
import androidx.test.espresso.Espresso
import androidx.test.filters.SmallTest
import com.niyaj.common.tags.ChargesTestTags.ADD_EDIT_CHARGES_BTN
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_ERROR_TAG
import com.niyaj.common.tags.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.common.tags.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.common.tags.ChargesTestTags.CHARGES_LIST
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_DIGIT_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_ERROR_TAG
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NAME_LENGTH_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NOT_AVAILABLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_PRICE_LESS_THAN_TEN_ERROR
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SCREEN_TITLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SETTINGS_TITLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_TAG
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_MESSAGE
import com.niyaj.common.tags.ChargesTestTags.DELETE_CHARGES_TITLE
import com.niyaj.common.tags.ChargesTestTags.EDIT_CHARGES_ITEM
import com.niyaj.common.tags.ChargesTestTags.EXPORT_CHARGES_TITLE
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_NOTE_TEXT
import com.niyaj.common.tags.ChargesTestTags.IMPORT_CHARGES_TITLE
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
import com.niyaj.model.Charges
import com.niyaj.model.searchCharges
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
import com.niyaj.ui.parameterProvider.ChargesPreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_CHARGES_SCREEN
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
class ChargesEndToEndTest {

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
    private val chargesList = ChargesPreviewData.chargesList

    private val newCharges = Charges(
        chargesId = 1,
        chargesName = "New Charges",
        chargesPrice = 10,
        isApplicable = false,
    )

    private val updatedCharges = Charges(
        chargesId = 1,
        chargesName = "Updated Charges",
        chargesPrice = 20,
        isApplicable = true,
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
                    PoposTestNavHost(appState, navGraphSpec = ChargesNavGraph)
                }
            }
        }
    }

    @Test
    fun chargesScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.CHARGES_SCREEN, currentRoute)

            onNodeWithTag(CHARGES_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun chargesScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_CHARGES).assertIsDisplayed().assertHasClickAction()
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
    fun onClickAddNewItem_navigateTo_addEditChargesScreen() {
        composeTestRule.apply {
            gotoAddEditChargesScreen()

            onNodeWithText(CREATE_NEW_CHARGES).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CHARGES_SCREEN.plus("?chargesId={chargesId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_CHARGES).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditChargesScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditChargesScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(CHARGES_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(CHARGES_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(CHARGES_AMOUNT_FIELD).assertIsDisplayed()
            onNodeWithText(CHARGES_PRICE_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsDisplayed().assertIsOff()
            onNodeWithText("Marked as not applied").assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditChargesScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditChargesScreen()

            // Initial state of the screen
            onNodeWithText(CHARGES_NAME_EMPTY_ERROR).assertIsDisplayed()
            onNodeWithText(CHARGES_PRICE_EMPTY_ERROR).assertIsDisplayed()
            onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsDisplayed().assertIsOff()
            onNodeWithText("Marked as not applied").assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsDisplayed().assertIsNotEnabled()

            // Perform invalid input on itemName field and check for validation error
            onNodeWithTag(CHARGES_NAME_FIELD).performTextInput("Te")
            onNodeWithText(CHARGES_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(CHARGES_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CHARGES_NAME_FIELD).performTextInput("Test4")
            onNodeWithText(CHARGES_NAME_DIGIT_ERROR).assertIsDisplayed()
            onNodeWithTag(CHARGES_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(newCharges.chargesName)
            onNodeWithTag(CHARGES_NAME_ERROR_TAG).assertIsNotDisplayed()

            // Perform invalid input on itemPrice field and check for validation error
            onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput("5")
            onNodeWithText(CHARGES_PRICE_LESS_THAN_TEN_ERROR).assertIsDisplayed()
            onNodeWithTag(CHARGES_AMOUNT_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput(newCharges.chargesPrice.toString())
            onNodeWithTag(CHARGES_AMOUNT_ERROR_TAG).assertIsNotDisplayed()

            // Perform input on applied switch
            onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsDisplayed().assertIsOff()
            onNodeWithText("Marked as not applied").assertIsDisplayed()
            onNodeWithTag(CHARGES_APPLIED_SWITCH).performClick()

            onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithText("Marked as applied").assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewCharges(newCharges)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_SCREEN, route)

            onNodeWithTag(CHARGES_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()
            onNodeWithTag(CHARGES_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newCharges.chargesName)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(CHARGES_TAG.plus(1))
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
            createNewCharges(newCharges)
            composeTestRule.waitForIdle()

            createNewChargesList(2)

            onNodeWithTag(CHARGES_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CHARGES_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CHARGES_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CHARGES_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(CHARGES_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CHARGES_SCREEN.plus("?chargesId={chargesId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(EDIT_CHARGES_ITEM).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(CHARGES_NAME_FIELD).assertTextContains(newCharges.chargesName)
            onNodeWithTag(CHARGES_NAME_ERROR_TAG).assertDoesNotExist()

            onNodeWithTag(CHARGES_AMOUNT_FIELD).assertTextContains(newCharges.chargesPrice.toString())
            onNodeWithTag(CHARGES_AMOUNT_ERROR_TAG).assertDoesNotExist()

            if (newCharges.isApplicable) {
                onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsOn()
                onNodeWithText("Marked as applied").assertIsDisplayed()
            } else {
                onNodeWithTag(CHARGES_APPLIED_SWITCH).assertIsOff()
                onNodeWithText("Marked as not applied").assertIsDisplayed()
            }

            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsDisplayed().assertIsEnabled()

            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsEnabled().performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_SCREEN, route)

            onNodeWithTag(CHARGES_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_NAME_FIELD).performTextReplacement(updatedCharges.chargesName)
            onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextReplacement(updatedCharges.chargesPrice.toString())

            if (updatedCharges.isApplicable) {
                onNodeWithTag(CHARGES_APPLIED_SWITCH).performClick()
                onNodeWithText("Marked as applied").assertIsDisplayed()
            }

            onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(CHARGES_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedCharges.chargesName)
                .assertTextContains(updatedCharges.chargesPrice.toRupee)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_CHARGES_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(CHARGES_LIST).assertDoesNotExist()
            onNodeWithTag(CHARGES_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(3)

            waitForIdle()

            chargesList.take(3).forEach {
                onNodeWithTag(CHARGES_TAG.plus(it.chargesId.plus(1)))
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

            chargesList.take(3).forEach {
                onNodeWithTag(CHARGES_TAG.plus(it.chargesId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(CHARGES_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newCharges)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CHARGES_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CHARGES_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewCharges(newCharges)
            waitForIdle()

            onNodeWithTag(CHARGES_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(4)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Discount")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            val searchResultCount = chargesList.take(4).searchCharges("Discount").count()
            val listSize = onNodeWithTag(CHARGES_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(CHARGES_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_CHARGES_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_CHARGES_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_CHARGES_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_CHARGES_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_EXPORT_SCREEN, currentRoute)
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 charges will be exported.").assertIsDisplayed()
            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 charges will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(2)
            waitForIdle()

            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 charges will be exported.").assertIsDisplayed()
            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CHARGES_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CHARGES_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 charges will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CHARGES_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CHARGES_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 charges will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CHARGES_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CHARGES_TITLE).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CHARGES_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()

            createNewChargesList(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Discount")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            val searchResultCount = chargesList.take(4).searchCharges("Discount").count()
            val listSize = onNodeWithTag(CHARGES_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()
            onNodeWithTag(CHARGES_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToCategoryScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCharges(newCharges)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CHARGES_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    private fun gotoAddEditChargesScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CHARGES)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewCharges(newCharges: Charges) {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CHARGES)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(newCharges.chargesName)
        composeTestRule.onNodeWithTag(CHARGES_AMOUNT_FIELD)
            .performTextInput(newCharges.chargesPrice.toString())

        if (newCharges.isApplicable) {
            composeTestRule.onNodeWithTag(CHARGES_APPLIED_SWITCH).performClick()
        }

        composeTestRule.onNodeWithTag(ADD_EDIT_CHARGES_BTN).performClick()

        composeTestRule.waitForIdle()
    }

    private fun createAndSelectItem(newCharges: Charges) {
        createNewCharges(newCharges)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CHARGES_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewChargesList(limit: Int) {
        composeTestRule.apply {
            chargesList.take(limit).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                waitForIdle()

                onNodeWithTag(CHARGES_NAME_FIELD).performTextInput(it.chargesName)
                onNodeWithTag(CHARGES_AMOUNT_FIELD).performTextInput(it.chargesPrice.toString())

                if (it.isApplicable) {
                    onNodeWithTag(CHARGES_APPLIED_SWITCH).performClick()
                }

                onNodeWithTag(ADD_EDIT_CHARGES_BTN).assertIsEnabled().performClick()
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

            onNodeWithTag(EXPORT_CHARGES_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToCategoryScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CHARGES_SCREEN, currentRoute)
        }
    }
}
