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

package com.niyaj.category

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
import com.niyaj.common.tags.CategoryConstants.ADD_EDIT_CATEGORY_BTN
import com.niyaj.common.tags.CategoryConstants.CATEGORY_AVAILABLE_SWITCH
import com.niyaj.common.tags.CategoryConstants.CATEGORY_ITEM_TAG
import com.niyaj.common.tags.CategoryConstants.CATEGORY_LIST
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_EMPTY_ERROR
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_ERROR_TAG
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_FIELD
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NOT_AVAILABLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SCREEN_TITLE
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CategoryConstants.CATEGORY_SETTINGS_TITLE
import com.niyaj.common.tags.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_MESSAGE
import com.niyaj.common.tags.CategoryConstants.DELETE_CATEGORY_ITEM_TITLE
import com.niyaj.common.tags.CategoryConstants.EXPORT_CATEGORY_TITLE
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_NOTE_TEXT
import com.niyaj.common.tags.CategoryConstants.IMPORT_CATEGORY_TITLE
import com.niyaj.common.tags.CategoryConstants.UPDATE_CATEGORY
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
import com.niyaj.model.Category
import com.niyaj.model.searchCategory
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
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_CATEGORY_SCREEN
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
class CategoryEndToEndTest {

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
    private val categoryList = CategoryPreviewData.categoryList

    private val newCategory = Category(
        categoryId = 1,
        categoryName = "Test Category",
        isAvailable = false,
    )

    private val updatedCategory = Category(
        categoryId = 1,
        categoryName = "Updated Category",
        isAvailable = true,
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
                    PoposTestNavHost(appState, navGraphSpec = CategoryNavGraph)
                }
            }
        }
    }

    @Test
    fun categoryScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.CATEGORY_SCREEN, currentRoute)

            onNodeWithTag(CATEGORY_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun categoryScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_CATEGORY).assertIsDisplayed().assertHasClickAction()
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
            gotoAddEditCategoryScreen()

            onNodeWithText(CREATE_NEW_CATEGORY).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CATEGORY_SCREEN.plus("?categoryId={categoryId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_CATEGORY).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditAddressScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditCategoryScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(CATEGORY_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(CATEGORY_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithText("Marked as available").assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_addEditAddressScreen_validationErrors() {
        composeTestRule.apply {
            gotoAddEditCategoryScreen()

            // Perform invalid input on itemName field and check for validation error
            onNodeWithText(CATEGORY_NAME_EMPTY_ERROR).assertIsDisplayed()

            onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput("Te")
            onNodeWithText(CATEGORY_NAME_LENGTH_ERROR).assertIsDisplayed()
            onNodeWithTag(CATEGORY_NAME_FIELD)
                .onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .performClick()

            onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput(newCategory.categoryName)
            onNodeWithTag(CATEGORY_NAME_ERROR_TAG).assertIsNotDisplayed()

            onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).assertIsDisplayed().assertIsOn()
            onNodeWithText("Marked as available").assertIsDisplayed()
            onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).performClick()

            onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).assertIsDisplayed().assertIsOff()
            onNodeWithText("Marked as not available").assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewCategory(newCategory)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CATEGORY_SCREEN, route)

            onNodeWithTag(CATEGORY_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newCategory.categoryName)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1))
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
            createNewCategory(newCategory)
            composeTestRule.waitForIdle()

            createNewAddresses(2)

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(CATEGORY_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(ADD_EDIT_CATEGORY_SCREEN.plus("?categoryId={categoryId}"), currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(UPDATE_CATEGORY).assertIsDisplayed()

            // Check data has been populated or not
            onNodeWithTag(CATEGORY_NAME_FIELD).assertTextContains(newCategory.categoryName)
            onNodeWithText(CATEGORY_NAME_ERROR_TAG).assertDoesNotExist()

            if (newCategory.isAvailable) {
                onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).assertIsOn()
                onNodeWithText("Marked as available").assertIsDisplayed()
            } else {
                onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).assertIsOff()
                onNodeWithText("Marked as not available").assertIsDisplayed()
            }

            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsDisplayed().assertIsEnabled()

            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsEnabled().performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CATEGORY_SCREEN, route)

            onNodeWithTag(CATEGORY_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_NAME_FIELD).performTextReplacement(updatedCategory.categoryName)

            onNodeWithTag(ADD_EDIT_CATEGORY_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedCategory.categoryName)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_CATEGORY_ITEM_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_CATEGORY_ITEM_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(CATEGORY_LIST).assertDoesNotExist()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewCategory(newCategory)

            createNewAddresses(3)

            waitForIdle()

            categoryList.take(3).forEach {
                onNodeWithTag(CATEGORY_ITEM_TAG.plus(it.categoryId.plus(1)))
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

            categoryList.take(3).forEach {
                onNodeWithTag(CATEGORY_ITEM_TAG.plus(it.categoryId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(CATEGORY_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newCategory)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CATEGORY_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CATEGORY_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewCategory(newCategory)
            waitForIdle()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            createNewAddresses(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            createNewAddresses(4)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Books")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

//            val searchResultCount = addressList.take(4).searchAddress("Books").count()
//            val listSize = onNodeWithTag(CATEGORY_LIST).fetchSemanticsNode().children.size
//            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(CATEGORY_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_CATEGORY_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_CATEGORY_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_CATEGORY_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CATEGORY_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_CATEGORY_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 category will be exported.").assertIsDisplayed()
            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 category will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            createNewAddresses(2)
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 categories will be exported.").assertIsDisplayed()
            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 categories will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 categories will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_CATEGORY_TITLE).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CATEGORY_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            createNewAddresses(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()

            createNewAddresses(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Books")

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()

            val searchResultCount = categoryList.take(4).searchCategory("Books").count()
            val listSize = onNodeWithTag(CATEGORY_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(CATEGORY_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(CATEGORY_LIST).assertIsDisplayed()
            onNodeWithTag(CATEGORY_ITEM_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToCategoryScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewCategory(newCategory)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(CATEGORY_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

    private fun gotoAddEditCategoryScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CATEGORY)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewCategory(newCategory: Category) {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_CATEGORY)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule.onNodeWithTag(CATEGORY_NAME_FIELD)
            .performTextInput(newCategory.categoryName)
        if (!newCategory.isAvailable) {
            composeTestRule.onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).performClick()
        }

        composeTestRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()
    }

    private fun createAndSelectItem(newAddress: Category) {
        createNewCategory(newAddress)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(CATEGORY_ITEM_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewAddresses(limit: Int) {
        categoryList.take(limit).forEach {
            composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()

            composeTestRule.onNodeWithTag(CATEGORY_NAME_FIELD).performTextInput(it.categoryName)
            if (!it.isAvailable) {
                composeTestRule.onNodeWithTag(CATEGORY_AVAILABLE_SWITCH).performClick()
            }

            composeTestRule.onNodeWithTag(ADD_EDIT_CATEGORY_BTN).performClick()

            composeTestRule.waitForIdle()
        }
    }

    private fun ComposeTestRule.navigateToExportScreen() {
        this.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(EXPORT_CATEGORY_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CATEGORY_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToCategoryScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.CATEGORY_SCREEN, currentRoute)
        }
    }
}
