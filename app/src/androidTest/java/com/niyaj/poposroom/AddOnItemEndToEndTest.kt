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

package com.niyaj.poposroom

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOn
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
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.niyaj.addonitem.AddonitemNavGraph
import com.niyaj.common.tags.AddOnTestTags.ADDON_APPLIED_SWITCH
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_LIST
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_DIGIT_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_LENGTH_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_SCREEN_TITLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddOnTestTags.ADDON_SETTINGS_TITLE
import com.niyaj.common.tags.AddOnTestTags.ADD_EDIT_ADDON_BUTTON
import com.niyaj.common.tags.AddOnTestTags.ADD_EDIT_ADDON_SCREEN
import com.niyaj.common.tags.AddOnTestTags.APPLIED_TEXT
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnTestTags.DELETE_ADD_ON_ITEM_MESSAGE
import com.niyaj.common.tags.AddOnTestTags.DELETE_ADD_ON_ITEM_TITLE
import com.niyaj.common.tags.AddOnTestTags.EDIT_ADD_ON_ITEM
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_BTN
import com.niyaj.common.tags.AddOnTestTags.EXPORT_ADDON_TITLE
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_NOTE_TEXT
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_OPN_FILE
import com.niyaj.common.tags.AddOnTestTags.IMPORT_ADDON_TITLE
import com.niyaj.common.tags.AddOnTestTags.NOT_APPLIED_TEXT
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
import com.niyaj.model.AddOnItem
import com.niyaj.model.searchAddOnItem
import com.niyaj.poposroom.ui.PoposApp
import com.niyaj.poposroom.ui.PoposAppState
import com.niyaj.poposroom.ui.rememberPoposAppState
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.components.PRIMARY_APP_DRAWER
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN
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
class AddOnItemEndToEndTest {

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

    private lateinit var appState: PoposAppState

    private val itemList = AddOnPreviewData.addOnItemList

    private val newItem = AddOnItem(
        itemId = 1,
        itemName = "New Item",
        itemPrice = 10,
        isApplicable = false,
        createdAt = 3850,
        updatedAt = null,
    )

    private val updatedItem = AddOnItem(
        itemId = 1,
        itemName = "Updated Item",
        itemPrice = 20,
        isApplicable = true,
        createdAt = 3850,
        updatedAt = null,
    )

    @OptIn(ExperimentalMaterialNavigationApi::class)
    @Before
    fun setup() {
        hiltRule.inject()

        composeTestRule.setContent {
            appState = rememberPoposAppState()
            CompositionLocalProvider(
                // Replaces images with placeholders
                LocalInspectionMode provides true,
            ) {
                PoposRoomTheme {
                    PoposApp(
                        appState = appState,
                        startRoute = AddonitemNavGraph,
                    )
                }
            }
        }
    }

    @Test
    fun addOnItemScreen_isDisplayed() {
        composeTestRule.apply {
            onNodeWithTag(ADDON_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun addOnItemScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_ADD_ON).assertIsDisplayed().assertHasClickAction()
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
    fun onClickAddNewItem_navigateTo_addEditItemScreen() {
        composeTestRule.apply {
            gotoAddEditAddItemScreen()

            onNodeWithText(CREATE_NEW_ADD_ON).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_ADD_ON_ITEM_SCREEN.plus("?itemId={itemId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditItemScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditAddItemScreen()

            // Check screen has correct field
            onNodeWithTag(ADDON_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(ADDON_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(ADDON_PRICE_FIELD).assertIsDisplayed()
            onNodeWithText(ADDON_PRICE_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(ADDON_APPLIED_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditItemScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditAddItemScreen()

            // Perform invalid input on itemName field and check for validation error
            onNodeWithTag(ADDON_NAME_FIELD).performTextInput("Test")
            onNodeWithText(ADDON_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(ADDON_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(ADDON_NAME_FIELD).performTextInput("Test2")
            onNodeWithText(ADDON_NAME_DIGIT_ERROR).assertIsDisplayed()
            onNodeWithTag(ADDON_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(ADDON_NAME_FIELD).performTextInput(newItem.itemName)
            onNodeWithTag(ADDON_NAME_ERROR_TAG).assertIsNotDisplayed()

            onNodeWithTag(ADDON_PRICE_FIELD).performTextInput("2")
            onNodeWithText(ADDON_PRICE_LESS_THAN_FIVE_ERROR).assertIsDisplayed()
            onNodeWithTag(ADDON_PRICE_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(ADDON_PRICE_FIELD).performTextInput("${newItem.itemPrice}")
            onNodeWithTag(ADDON_PRICE_ERROR_TAG).assertIsNotDisplayed()

            onNodeWithTag(ADDON_APPLIED_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithText(APPLIED_TEXT).assertIsDisplayed()

            onNodeWithTag(ADDON_APPLIED_SWITCH).performClick()
            onNodeWithText(NOT_APPLIED_TEXT).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewItem(newItem)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADD_ON_ITEM_SCREEN, route)

            onNodeWithTag(ADDON_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newItem.itemName)
                .assertTextContains(newItem.itemPrice.toRupee)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1))
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
            createNewItem(newItem)
            composeTestRule.waitForIdle()

            itemList.take(2).forEach {
                composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
                    .performClick()

                composeTestRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                composeTestRule.onNodeWithTag(ADDON_PRICE_FIELD)
                    .performTextInput(it.itemPrice.toString())

                composeTestRule.onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                composeTestRule.waitForIdle()
            }

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDON_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(ADDON_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditItemScreen() {
        composeTestRule.apply {
            createAndSelectItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN)
                .performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_ADD_ON_ITEM_SCREEN.plus("?itemId={itemId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ADDON_SCREEN).assertIsDisplayed()

            onNodeWithText(EDIT_ADD_ON_ITEM).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(ADDON_NAME_FIELD).assertTextContains(newItem.itemName)
            onNodeWithText(ADDON_NAME_ERROR_TAG).assertDoesNotExist()

            onNodeWithTag(ADDON_PRICE_FIELD).assertTextContains(newItem.itemPrice.toString())
            onNodeWithText(ADDON_PRICE_ERROR_TAG).assertDoesNotExist()

            onNodeWithTag(ADDON_APPLIED_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).assertIsDisplayed().assertIsEnabled()

            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADD_ON_ITEM_SCREEN, route)

            onNodeWithTag(ADDON_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).performClick()

            onNodeWithTag(ADDON_NAME_FIELD).performTextReplacement(updatedItem.itemName)
            onNodeWithTag(ADDON_PRICE_FIELD).performTextReplacement(updatedItem.itemPrice.toString())

            onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

            waitForIdle()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedItem.itemName)
                .assertTextContains(updatedItem.itemPrice.toRupee)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_ADD_ON_ITEM_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_ADD_ON_ITEM_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItem_andItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(ADDON_ITEM_LIST).assertDoesNotExist()
            onNodeWithTag(ADDON_ITEM_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewItem(newItem)

            itemList.take(3).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(it.itemPrice.toString())
                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }

            waitForIdle()

            itemList.take(3).forEach {
                onNodeWithTag(ADDON_ITEM_TAG.plus(it.itemId.plus(1)))
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

            itemList.take(3).forEach {
                onNodeWithTag(ADDON_ITEM_TAG.plus(it.itemId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(ADDON_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newItem)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDON_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDON_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewItem(newItem)
            waitForIdle()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            itemList.take(2).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD)
                    .performTextInput(it.itemPrice.toString())

                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Test")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            itemList.take(4).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD)
                    .performTextInput(it.itemPrice.toString())

                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()

            val searchResultCount = itemList.take(4).searchAddOnItem("Extra").count()
            val listSize = onNodeWithTag(ADDON_ITEM_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)

            // Search by price
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextReplacement("50")
            val resultCount = itemList.take(4).searchAddOnItem("50").count()

            val size = onNodeWithTag(ADDON_ITEM_LIST).fetchSemanticsNode().children.size
            assertEquals(resultCount, size)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_AddOnSettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(ADDON_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_ADDON_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_ADDON_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_ADDON_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADD_ON_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_ADDON_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_ADDON_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(EXPORT_ADDON_BTN).assertIsDisplayed()

            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithText("All addon items will be exported.").assertIsDisplayed()
            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 addon items will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            itemList.take(2).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(it.itemPrice.toString())

                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithText("All addon items will be exported.").assertIsDisplayed()
            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 addon items will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ADDON_ITEM_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All addon items will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewItem(newItem)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ADDON_TITLE).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDON_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()

            itemList.take(2).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(it.itemPrice.toString())

                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }

            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Test")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()
            itemList.take(4).forEach {
                onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

                onNodeWithTag(ADDON_NAME_FIELD).performTextInput(it.itemName)
                onNodeWithTag(ADDON_PRICE_FIELD).performTextInput(it.itemPrice.toString())

                onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()

                waitForIdle()
            }

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Avocado")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()

//            val searchResultCount = itemList.take(4).searchAddOnItem("Avocado").count()
//            val listSize = onNodeWithTag(ADDON_ITEM_LIST).fetchSemanticsNode().children.size
//            assertEquals(searchResultCount, listSize)

            // Search by price
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextReplacement("300")
            waitForIdle()

//            val resultCount = itemList.take(4).searchAddOnItem("300").count()
//            val size = onNodeWithTag(ADDON_ITEM_LIST).fetchSemanticsNode().children.size
//            assertEquals(resultCount, size)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(ADDON_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ADDON_ITEM_LIST).assertIsDisplayed()
            onNodeWithTag(ADDON_ITEM_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToAddOnScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewItem(newItem)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ADDON_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    private fun gotoAddEditAddItemScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADD_ON)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewItem(newItem: AddOnItem) {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADD_ON)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithTag(ADDON_NAME_FIELD).performTextInput(newItem.itemName)
        composeTestRule.onNodeWithTag(ADDON_PRICE_FIELD)
            .performTextInput(newItem.itemPrice.toString())

        composeTestRule.onNodeWithTag(ADD_EDIT_ADDON_BUTTON).performClick()
    }

    private fun createAndSelectItem(newItem: AddOnItem) {
        createNewItem(newItem)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ADDON_ITEM_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun ComposeTestRule.navigateToExportScreen() {
        this.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(EXPORT_ADDON_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADD_ON_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToAddOnScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ADD_ON_ITEM_SCREEN, currentRoute)
        }
    }
}
