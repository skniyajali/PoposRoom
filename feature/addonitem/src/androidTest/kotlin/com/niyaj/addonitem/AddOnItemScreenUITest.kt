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

package com.niyaj.addonitem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_LIST
import com.niyaj.common.tags.AddOnTestTags.ADDON_ITEM_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_LOADING_INDICATOR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SCREEN_TITLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.AddOnItem
import com.niyaj.model.searchAddOnItem
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.event.UiState
import com.niyaj.ui.parameterProvider.AddOnPreviewData
import org.junit.Rule
import org.junit.Test

class AddOnItemScreenUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val itemList = mutableStateOf(AddOnPreviewData.addOnItemList)

    private val selectedItems = mutableStateListOf<Int>()
    private var showSearchBar = mutableStateOf(false)
    private var searchText = mutableStateOf("")

    @Test
    fun loadingState_whenStateIsLoading_showLoadingIndicator() {
        composeTestRule.setContent {
            AddOnScreen(uiState = UiState.Loading)
        }

        composeTestRule
            .onNodeWithContentDescription(ADDON_LOADING_INDICATOR)
            .assertExists()
    }

    @Test
    fun emptyState_whenStateIsEmpty_thenShowEmptyState() {
        composeTestRule.setContent {
            AddOnScreen(uiState = UiState.Empty)
        }

        composeTestRule
            .onNodeWithText(ADDON_NOT_AVAILABLE)
            .assertIsDisplayed()
    }

    @Test
    fun successState_whenStateIsSuccess_thenShowList() {
        composeTestRule.setContent {
            AddOnScreen(uiState = UiState.Success(itemList.value))
        }

        composeTestRule
            .onNodeWithTag(ADDON_ITEM_LIST)
            .assertIsDisplayed()

        itemList.value.forEach {
            composeTestRule
                .onNodeWithTag(ADDON_ITEM_TAG.plus(it.itemId))
                .assertIsDisplayed()
                .assertHasClickAction()
        }

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(NAV_SETTING_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(STANDARD_FAB_BUTTON)
            .assertIsDisplayed()
            .assertContentDescriptionEquals(CREATE_NEW_ADD_ON)
    }

    @Test
    fun itemSelected_whenPerformLongClick_thenShowSelectionCount() {
        composeTestRule.setContent {
            AddOnScreen(
                uiState = UiState.Success(itemList.value),
                selectedItems = listOf(1),
            )
        }

        composeTestRule
            .onNodeWithTag(STANDARD_BACK_BUTTON)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(STANDARD_FAB_BUTTON)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(CLEAR_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("1 Selected")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_EDIT_BTN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_DELETE_BTN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SELECT_ALL_BTN)
            .assertIsDisplayed()
    }

    @Test
    fun itemSelected_whenPerformSelectAll_thenShowTotalSelectionCount() {
        composeTestRule.setContent {
            AddOnScreen(
                uiState = UiState.Success(itemList.value),
                selectedItems = selectedItems.toList(),
                onSelectAllClick = {
                    selectAllItems()
                },
                onDeselect = {
                    selectedItems.clear()
                },
                onItemClick = {
                    if (selectedItems.contains(it)) {
                        selectedItems.remove(it)
                    } else {
                        selectedItems.add(it)
                    }
                },
            )
        }

        composeTestRule
            .onNodeWithTag(ADDON_ITEM_TAG.plus(1))
            .assertIsDisplayed()
            .assertHasClickAction()
            .performTouchInput {
                longClick()
            }

        composeTestRule
            .onNodeWithTag(STANDARD_FAB_BUTTON)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(DRAWER_ICON)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(CLEAR_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("1 Selected")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SELECT_ALL_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithText("${itemList.value.size} Selected")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_EDIT_BTN)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_DELETE_BTN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(CLEAR_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithText(ADDON_SCREEN_TITLE)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(DRAWER_ICON)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(NAV_SETTING_BTN)
            .assertIsDisplayed()
    }

    @Test
    fun onSearch_whenClickSearch_thenShowSearchBarAndResult() {
        composeTestRule.setContent {
            AddOnScreen(
                uiState = UiState.Success(itemList.value),
                searchText = searchText.value,
                showSearchBar = showSearchBar.value,
                onSearchClick = {
                    showSearchBar.value = true
                },
                onCloseSearchBar = {
                    // Close search bar
                    showSearchBar.value = false
                },
                onSearchTextChanged = {
                    onSearchTextChanged(it)
                },
            )
        }

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(STANDARD_BACK_BUTTON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(STANDARD_SEARCH_BAR)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(STANDARD_SEARCH_BAR)
            .assertTextContains(ADDON_SEARCH_PLACEHOLDER)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON)
            .assertIsNotDisplayed()

        composeTestRule
            .onNodeWithTag(STANDARD_SEARCH_BAR)
            .performTextInput("Test")

        composeTestRule
            .onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithTag(STANDARD_BACK_BUTTON)
            .performClick()

        composeTestRule
            .onNodeWithTag(NAV_SEARCH_BTN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(DRAWER_ICON)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(STANDARD_FAB_BUTTON)
            .assertIsDisplayed()
    }

    @Composable
    private fun AddOnScreen(
        uiState: UiState<List<AddOnItem>>,
        selectedItems: List<Int> = emptyList(),
        showSearchBar: Boolean = false,
        searchText: String = "",
        onItemClick: (Int) -> Unit = {},
        onCreateNewClick: () -> Unit = {},
        onEditClick: () -> Unit = {},
        onDeleteClick: () -> Unit = {},
        onSettingsClick: () -> Unit = {},
        onSelectAllClick: () -> Unit = {},
        onClearSearchClick: () -> Unit = {},
        onSearchClick: () -> Unit = {},
        onSearchTextChanged: (String) -> Unit = {},
        onCloseSearchBar: () -> Unit = {},
        onBackClick: () -> Unit = {},
        onDeselect: () -> Unit = {},
    ) {
        PoposRoomTheme {
            AddOnItemScreenContent(
                uiState = uiState,
                selectedItems = selectedItems,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onItemClick = onItemClick,
                onCreateNewClick = onCreateNewClick,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                onSelectAllClick = onSelectAllClick,
                onClearSearchClick = onClearSearchClick,
                onSearchClick = onSearchClick,
                onSearchTextChanged = onSearchTextChanged,
                onCloseSearchBar = onCloseSearchBar,
                onBackClick = onBackClick,
                onDeselect = onDeselect,
                onNavigateToScreen = {},
            )
        }
    }

    private fun selectAllItems() {
        selectedItems.clear()
        selectedItems.addAll(itemList.value.map { it.itemId })
    }

    private fun onSearchTextChanged(text: String) {
        searchText.value += text
        itemList.value = itemList.value.searchAddOnItem(text)
    }
}
