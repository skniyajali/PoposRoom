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
import com.niyaj.common.tags.ChargesTestTags.CHARGES_LIST
import com.niyaj.common.tags.ChargesTestTags.CHARGES_NOT_AVAILABLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SCREEN_TITLE
import com.niyaj.common.tags.ChargesTestTags.CHARGES_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.ChargesTestTags.CHARGES_TAG
import com.niyaj.common.tags.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.common.utils.Constants.CLEAR_ICON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.SEARCH_BAR_CLEAR_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_SEARCH_BAR
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.model.Charges
import com.niyaj.model.searchCharges
import com.niyaj.ui.components.NAV_DELETE_BTN
import com.niyaj.ui.components.NAV_EDIT_BTN
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SELECT_ALL_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.event.UiState
import com.niyaj.ui.event.UiState.Empty
import com.niyaj.ui.event.UiState.Loading
import com.niyaj.ui.event.UiState.Success
import com.niyaj.ui.parameterProvider.ChargesPreviewData
import kotlinx.collections.immutable.toPersistentList
import org.junit.Rule
import org.junit.Test

class ChargesScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val itemList = mutableStateOf(ChargesPreviewData.chargesList)

    private val selectedItems = mutableStateListOf<Int>()
    private var showSearchBar = mutableStateOf(false)
    private var searchText = mutableStateOf("")

    @Test
    fun loadingState_whenStateIsLoading_showLoadingIndicator() {
        composeTestRule.apply {
            setContent {
                ChargesScreen(uiState = Loading)
            }

            onNodeWithText(CHARGES_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithContentDescription("loadingIndicator").assertIsDisplayed()
        }
    }

    @Test
    fun emptyState_whenStateIsEmpty_thenShowEmptyState() {
        composeTestRule.setContent {
            ChargesScreen(uiState = Empty)
        }

        composeTestRule.onNodeWithText(CHARGES_NOT_AVAILABLE).assertIsDisplayed()
    }

    @Test
    fun successState_whenStateIsSuccess_thenShowList() {
        composeTestRule.apply {
            setContent {
                ChargesScreen(uiState = Success(itemList.value))
            }

            onNodeWithTag(CHARGES_LIST).assertIsDisplayed()

            itemList.value.forEach {
                onNodeWithTag(CHARGES_TAG.plus(it.chargesId))
                    .assertIsDisplayed()
                    .assertHasClickAction()
            }

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_FAB_BUTTON)
                .assertIsDisplayed()
                .assertContentDescriptionEquals(CREATE_NEW_CHARGES)
        }
    }

    @Test
    fun itemSelected_whenPerformLongClick_thenShowSelectionCount() {
        composeTestRule.apply {
            setContent {
                ChargesScreen(
                    uiState = Success(itemList.value),
                    selectedItems = listOf(1),
                )
            }

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun itemSelected_whenPerformSelectAll_thenShowTotalSelectionCount() {
        composeTestRule.apply {
            setContent {
                ChargesScreen(
                    uiState = Success(itemList.value),
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

            onNodeWithTag(CHARGES_TAG.plus(1))
                .assertIsDisplayed()
                .assertHasClickAction()
                .performTouchInput {
                    longClick()
                }

            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText("${itemList.value.size} Selected").assertIsDisplayed()
            onNodeWithTag(NAV_EDIT_BTN).assertIsNotDisplayed()
            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed()

            onNodeWithTag(CLEAR_ICON)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithText(CHARGES_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed()
        }
    }

    @Test
    fun onSearch_whenClickSearch_thenShowSearchBarAndResult() {
        composeTestRule.apply {
            setContent {
                ChargesScreen(
                    uiState = Success(itemList.value),
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

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertTextContains(CHARGES_SEARCH_PLACEHOLDER)
                .assertIsDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsNotDisplayed()
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Books")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(STANDARD_BACK_BUTTON).performClick()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Composable
    private fun ChargesScreen(
        uiState: UiState<List<Charges>>,
        selectedItems: List<Int> = emptyList(),
        showSearchBar: Boolean = false,
        searchText: String = "",
        onItemClick: (Int) -> Unit = {},
        onCreateNewClick: () -> Unit = {},
        onEditClick: (Int) -> Unit = {},
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
            ChargesScreenContent(
                uiState = uiState,
                selectedItems = selectedItems,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onClickSelectItem = onItemClick,
                onSearchTextChanged = onSearchTextChanged,
                onCloseSearchBar = onCloseSearchBar,
                onNavigateToScreen = {},
                onClickSearchIcon = onSearchClick,
                onClickClear = onClearSearchClick,
                onClickSelectAll = onSelectAllClick,
                onClickDeselect = onDeselect,
                onClickDelete = onDeleteClick,
                onClickBack = onBackClick,
                onClickCreateNew = onCreateNewClick,
                onClickEdit = onEditClick,
                onClickSettings = onSettingsClick,
            )
        }
    }

    private fun selectAllItems() {
        selectedItems.clear()
        selectedItems.addAll(itemList.value.map { it.chargesId })
    }

    private fun onSearchTextChanged(text: String) {
        searchText.value += text
        itemList.value = itemList.value.searchCharges(text).toPersistentList()
    }
}
