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

package com.niyaj.employeeAbsent

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.test.filters.SmallTest
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_DATE_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_EMPTY
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_EMPLOYEE_NAME_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_NOT_AVAILABLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_REASON_FIELD
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_SCREEN_TITLE
import com.niyaj.common.tags.AbsentScreenTags.ABSENT_TAG
import com.niyaj.common.tags.AbsentScreenTags.ADD_EDIT_ABSENT_BTN
import com.niyaj.common.tags.AbsentScreenTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTags.EXPORT_ABSENT_TITLE
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.common.utils.Constants.STANDARD_FAB_BUTTON
import com.niyaj.common.utils.getDateInMilliseconds
import com.niyaj.common.utils.getStartTime
import com.niyaj.common.utils.toDateString
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.employeeAbsent.createOrUpdate.AddEditAbsentScreen
import com.niyaj.employeeAbsent.destinations.AddEditAbsentScreenDestination
import com.niyaj.model.Absent
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
import com.niyaj.testing.repository.TestAbsentRepository
import com.niyaj.testing.util.PoposTestAppState
import com.niyaj.testing.util.PoposTestNavHost
import com.niyaj.testing.util.rememberPoposTestAppState
import com.niyaj.ui.components.NAV_SEARCH_BTN
import com.niyaj.ui.components.NAV_SETTING_BTN
import com.niyaj.ui.components.PRIMARY_APP_DRAWER
import com.niyaj.ui.parameterProvider.AbsentPreviewData
import com.niyaj.ui.parameterProvider.EmployeePreviewData
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_ABSENT_SCREEN
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.scope.resultBackNavigator
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
class AbsentEndToEndTest {

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
    private val items = AbsentPreviewData.absents
    private val repository = TestAbsentRepository()

    private val newAbsent = Absent(
        absentId = 1,
        employeeId = 1,
        absentDate = getStartTime,
        absentReason = "Sick Leave",
    )

    private val updatedAbsent = Absent(
        absentId = 1,
        employeeId = 1,
        absentDate = getDateInMilliseconds(15),
        absentReason = "Updated Leave",
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
                    PoposTestNavHost(
                        appState = appState,
                        navGraphSpec = EmployeeabsentNavGraph,
                    ) {
                        composable(AddEditAbsentScreenDestination) {
                            AddEditAbsentScreen(
                                absentId = navBackStackEntry.arguments?.getInt("absentId") ?: 0,
                                employeeId = navBackStackEntry.arguments?.getInt("employeeId") ?: 0,
                                navigator = this.destinationsNavigator,
                                onClickAddEmployee = {},
                                resultBackNavigator = resultBackNavigator(),
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun absentScreen_isDisplayed() {
        composeTestRule.apply {
            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(Screens.ABSENT_SCREEN, currentRoute)

            onNodeWithTag(ABSENT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(NAV_SETTING_BTN).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun absentScreen_state_isEmpty() {
        composeTestRule.apply {
            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsDisplayed()

            onNodeWithTag(CREATE_NEW_ABSENT).assertIsDisplayed().assertHasClickAction()
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
            gotoAddEditAbsentScreen()

            onNodeWithText(CREATE_NEW_ABSENT).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            val route = ADD_EDIT_ABSENT_SCREEN
                .plus("?absentId={absentId}&employeeId={employeeId}")

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            assertEquals(route, currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithTag(CREATE_NEW_ABSENT).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    @Test
    fun check_AddEditChargesScreen_hasCorrectFields() {
        composeTestRule.apply {
            gotoAddEditAbsentScreen()

            waitForIdle()

            // Check screen has correct field
            onNodeWithTag(ABSENT_EMPLOYEE_NAME_FIELD).assertIsDisplayed()
            onNodeWithText(ABSENT_EMPLOYEE_NAME_EMPTY).assertIsDisplayed()

            onNodeWithTag(ABSENT_DATE_FIELD).assertIsDisplayed()

            onNodeWithTag(ABSENT_REASON_FIELD).assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsDisplayed().assertIsNotEnabled()
        }
    }

    /*

    @Test
    fun check_addEditChargesScreen_validationErrors() {
        composeTestRule.apply {
            val employee = repository.createTestItem()

            gotoAddEditAbsentScreen()

            waitForIdle()

            // Initial state of the screen
            onNodeWithText(ABSENT_EMPLOYEE_NAME_EMPTY).assertIsDisplayed()
            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsDisplayed().assertIsNotEnabled()

            onNodeWithTag(ABSENT_EMPLOYEE_NAME_FIELD).performClick()
//            onNodeWithTag(employee.employeeName).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("ChooseDate").assertIsDisplayed().performClick()
            onNodeWithText("SELECT DATE").assertIsDisplayed()
            onNodeWithTag("positive").assertIsDisplayed()
            onNodeWithTag("negative").assertIsDisplayed()
            onNodeWithTag("dialog_date_selection_6").assertIsDisplayed().performClick()

            // Perform input on applied switch
            onNodeWithTag(ABSENT_REASON_FIELD).performTextInput("Sick")
            onNodeWithTag(ABSENT_REASON_FIELD).onChild()
                .assertContentDescriptionContains(CLEAR_ICON)
                .assertIsDisplayed()

            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsDisplayed().assertIsEnabled()
        }
    }

    @Test
    fun onCreatedNewItem_shouldBe_addedAndVisibleToUser() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_SCREEN, route)

            onNodeWithTag(ABSENT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()
            onNodeWithTag(ABSENT_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(newAbsent.absentDate.toDateString)
        }
    }

    @Test
    fun onSelectAnItem_editDeleteBtn_willVisible() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertExists().assertIsDisplayed()

            onNodeWithTag(ABSENT_TAG.plus(1))
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
            createNewAbsent(newAbsent)
            composeTestRule.waitForIdle()

            createNewAbsentList(2)

            onNodeWithTag(ABSENT_TAG.plus(1)).performTouchInput { longClick() }

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ABSENT_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ABSENT_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ABSENT_TAG.plus(3)).assertIsNotSelected()

            onNodeWithTag(ABSENT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(DRAWER_ICON).assertIsDisplayed()
        }
    }

    @Test
    fun onClickEditIcon_userShouldNavigateTo_addEditScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

            // Check user has been navigated to AddEditItemScreen
            assertNotNull(currentRoute)
            val screenRoute = ADD_EDIT_ABSENT_SCREEN
                .plus("?absentId={absentId}&employeeId={employeeId}")

            assertEquals(screenRoute, currentRoute)

            // Check screen is visible or not
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed()
            onNodeWithText(EDIT_ABSENT_ITEM).assertIsDisplayed()

            // Check data has been populated or not
//            onNodeWithTag(ABSENT_EMPLOYEE_NAME_FIELD).assertTextContains(newAbsent)

            onNodeWithTag(ABSENT_DATE_FIELD).assertTextContains(newAbsent.absentDate.toPrettyDate())

            onNodeWithTag(ABSENT_REASON_FIELD).assertTextContains(newAbsent.absentReason)

            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsDisplayed().assertIsEnabled().performClick()

            waitForIdle()
            val route = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_SCREEN, route)

            onNodeWithTag(ABSENT_SCREEN_TITLE).assertIsDisplayed()

            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsDisplayed().assertIsNotSelected()
            onNodeWithTag(CLEAR_ICON).assertIsNotDisplayed()
        }
    }

    @Test
    fun onUpdatingAnItem_shouldBe_updatedInScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_EDIT_BTN).assertIsDisplayed().performClick()

//            onNodeWithTag(ABSENT_EMPLOYEE_NAME_FIELD).performTextReplacement(updatedAbsent.chargesName)

            onNodeWithTag(ABSENT_DATE_FIELD).assertTextContains(updatedAbsent.absentDate.toPrettyDate())

            onNodeWithTag(ABSENT_REASON_FIELD).assertTextContains(updatedAbsent.absentReason)

            onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsEnabled().performClick()

            waitForIdle()

            onNodeWithTag(ABSENT_TAG.plus(1))
                .assertIsDisplayed()
                .assertTextContains(updatedAbsent.absentDate.toDateString)
        }
    }

    @Test
    fun onClickDeleteIcon_deleteDialog_shouldVisibleToUser() {
        composeTestRule.apply {
            createAndSelectItem(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertIsDisplayed()
            onNodeWithText(DELETE_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithText(DELETE_ABSENT_MESSAGE).assertIsDisplayed()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed()
            onNodeWithText(DIALOG_DISMISS_TEXT).assertIsDisplayed()

            onNodeWithText(DIALOG_DISMISS_TEXT).performClick()
            waitForIdle()

            onNodeWithTag(STANDARD_DELETE_DIALOG).assertDoesNotExist()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun deleteAnItemAndItShould_removedFromScreen() {
        composeTestRule.apply {
            createAndSelectItem(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_DELETE_BTN).assertIsDisplayed().performClick()
            onNodeWithText(DIALOG_CONFIRM_TEXT).assertIsDisplayed().performClick()

            waitForIdle()

            onNodeWithTag(ABSENT_LIST).assertDoesNotExist()
            onNodeWithTag(ABSENT_TAG.plus("1")).assertDoesNotExist()
            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsDisplayed()
        }
    }

    @Test
    fun onSelectMultipleItems_editIcon_shouldNotVisible() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(3)

            waitForIdle()

            items.take(3).forEach {
                onNodeWithTag(ABSENT_TAG.plus(it.absentId.plus(1)))
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

            items.take(3).forEach {
                onNodeWithTag(ABSENT_TAG.plus(it.absentId.plus(1)))
                    .assertIsDisplayed()
                    .assertIsNotSelected()
            }

            onNodeWithTag(ABSENT_SCREEN_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed()
        }
    }

    @Test
    fun selectAnItem_andPressSystemBack_shouldDeselectItem() {
        composeTestRule.apply {
            createAndSelectItem(newAbsent)
            waitForIdle()

            Espresso.pressBack()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun searchBar_clickOnSearchIcon_shouldVisible() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ABSENT_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun searchBar_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ABSENT_SEARCH_PLACEHOLDER).assertIsDisplayed()

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
            createNewAbsent(newAbsent)
            waitForIdle()

            onNodeWithTag(ABSENT_TAG.plus(1))
                .assertIsDisplayed().performTouchInput { longClick() }
            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun onSearchForItem_returns_emptyResult() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(2)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun onSearchForItem_returns_successResult() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(4)

            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput(newAbsent.absentDate.toDateString)
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()

            val searchResultCount =
                items.take(4).filterAbsent(newAbsent.absentDate.toDateString).count()
            val listSize = onNodeWithTag(ABSENT_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun searchForItems_andPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).performClick()

            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertExists()
        }
    }

    @Test
    fun clickOnSettingsIcon_open_SettingScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(ABSENT_SETTINGS_TITLE).assertIsDisplayed()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()

            onNodeWithTag(IMPORT_ABSENT_TITLE).assertIsDisplayed().assertHasClickAction()
            onNodeWithTag(EXPORT_ABSENT_TITLE).assertIsDisplayed().assertHasClickAction()
        }
    }

    @Test
    fun clickOnImportIcon_shouldNavigateTo_ImportScreen() {
        composeTestRule.apply {
            onNodeWithTag(NAV_SETTING_BTN)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()

            onNodeWithTag(IMPORT_ABSENT_TITLE).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_IMPORT_SCREEN, currentRoute)

            onNodeWithText(IMPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithText(IMPORT_ABSENT_NOTE_TEXT).assertIsDisplayed()
            onNodeWithText(IMPORT_OPN_FILE, ignoreCase = true).assertIsDisplayed()
        }
    }

    @Test
    fun clickOnExportIcon_shouldNavigateTo_ExportScreen() {
        composeTestRule.apply {
            navigateToExportScreen()

            onNodeWithText(EXPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsDisplayed()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_EXPORT_SCREEN, currentRoute)
        }
    }

    @Test
    fun clickOnExportIcon_withSomeData_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsNotDisplayed()

            onNodeWithText(EXPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
            onNodeWithTag(IMPORT_EXPORT_BTN).assertIsDisplayed()

            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_whenItemSelected_shouldVisibleInExportScreen() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithText(EXPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithText("All 1 absent will be exported.").assertIsDisplayed()
            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(CLEAR_ICON).assertIsDisplayed().assertHasClickAction()
            onNodeWithText("1 Selected").assertIsDisplayed()
            onNodeWithText("1 absent will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onClickSelectAll_shouldSelectAndDeselectAllItems() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(2)
            waitForIdle()

            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithText("All 3 absentees will be exported.").assertIsDisplayed()
            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsSelected()
            onNodeWithTag(ABSENT_TAG.plus(2)).assertIsSelected()
            onNodeWithTag(ABSENT_TAG.plus(3)).assertIsSelected()
            onNodeWithText("3 Selected").assertIsDisplayed()
            onNodeWithText("3 absentees will be exported.").assertIsDisplayed()

            onNodeWithTag(NAV_SELECT_ALL_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
            onNodeWithTag(ABSENT_TAG.plus(2)).assertIsNotSelected()
            onNodeWithTag(ABSENT_TAG.plus(3)).assertIsNotSelected()
            onNodeWithText("All 3 absentees will be exported.").assertIsDisplayed()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldDeselectSelectedItems() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsDisplayed().performClick()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsSelected()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(ABSENT_TAG.plus(1)).assertIsNotSelected()
        }
    }

    @Test
    fun exportScreen_onClickSearchIcon_searchBarShouldVisible() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            composeTestRule.waitForIdle()
            navigateToExportScreen()

            waitForIdle()
            onNodeWithText(EXPORT_ABSENT_TITLE).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ABSENT_SEARCH_PLACEHOLDER).assertIsDisplayed()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
        }
    }

    @Test
    fun exportScreen_onInvalidSearch_shouldReturnsEmptyResult() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(2)
            waitForIdle()

            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("extra")
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertDoesNotExist()
        }
    }

    @Test
    fun exportScreen_onValidSearch_shouldReturnsSomeResult() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()

            createNewAbsentList(4)

            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()
            val searchText = newAbsent.absentDate.toDateString

            // Search by name
            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput(searchText)

            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()

            val searchResultCount = items.take(4).filterAbsent(searchText).count()
            val listSize = onNodeWithTag(ABSENT_LIST).fetchSemanticsNode().children.size
            assertEquals(searchResultCount, listSize)
        }
    }

    @Test
    fun exportScreen_whenSearchPressClearIcon_shouldReturnAllItems() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()
            navigateToExportScreen()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).performTextInput("Extra")
            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed()
            waitForIdle()

            onNodeWithText(SEARCH_ITEM_NOT_FOUND).assertIsDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsNotDisplayed()

            onNodeWithTag(SEARCH_BAR_CLEAR_BUTTON).assertIsDisplayed().performClick()

            onNodeWithText(ABSENT_NOT_AVAILABLE).assertIsNotDisplayed()
            onNodeWithTag(ABSENT_LIST).assertIsDisplayed()
            onNodeWithTag(ABSENT_TAG.plus(1)).assertExists()

            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()
            navigateBackToCategoryScreen()
        }
    }

    @Test
    fun exportScreen_onPressSystemBack_shouldCloseSearchBar() {
        composeTestRule.apply {
            createNewAbsent(newAbsent)
            waitForIdle()
            navigateToExportScreen()
            waitForIdle()

            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed().performClick()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsDisplayed()
            onNodeWithText(ABSENT_SEARCH_PLACEHOLDER).assertIsDisplayed()

            // For hiding keyboard
            Espresso.pressBack()

            // For go back
            Espresso.pressBack()

            onNodeWithTag(STANDARD_SEARCH_BAR).assertIsNotDisplayed()
            onNodeWithTag(NAV_SEARCH_BTN).assertIsDisplayed()
        }
    }

     */

    private fun gotoAddEditAbsentScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ABSENT)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
    }

    private fun createNewAbsent(newAbsent: Absent, usingList: Boolean = false) {
        val date = newAbsent.absentDate.toDateString
        val employeeList = EmployeePreviewData.employeeList

        if (usingList) {
            composeTestRule.onNodeWithTag(STANDARD_FAB_BUTTON).assertIsDisplayed().performClick()
        } else {
            composeTestRule
                .onNodeWithTag(CREATE_NEW_ABSENT)
                .assertIsDisplayed()
                .assertHasClickAction()
                .performClick()
        }

        repository.updateEmployeeData(employeeList)
        val employeeName = employeeList.find { it.employeeId == newAbsent.employeeId }?.employeeName

        employeeName?.let {
            chooseEmployee(it)
        }

        chooseDate(date)

        composeTestRule.onNodeWithTag(ABSENT_REASON_FIELD).performTextInput(newAbsent.absentReason)

        composeTestRule.onNodeWithTag(ADD_EDIT_ABSENT_BTN).assertIsEnabled().performClick()

        composeTestRule.waitForIdle()
    }

    private fun createAndSelectItem(newAbsent: Absent) {
        createNewAbsent(newAbsent)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithTag(ABSENT_TAG.plus(1))
            .assertIsDisplayed()
            .performTouchInput { longClick() }
            .assertIsSelected()
    }

    private fun createNewAbsentList(limit: Int) {
        composeTestRule.apply {
            AbsentPreviewData.absents.take(limit).forEach {
                createNewAbsent(it, true)

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

            onNodeWithTag(EXPORT_ABSENT_TITLE).assertIsDisplayed().performClick()

            waitForIdle()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_EXPORT_SCREEN, currentRoute)
        }
    }

    private fun navigateBackToCategoryScreen() {
        composeTestRule.apply {
            onNodeWithTag(STANDARD_BACK_BUTTON).assertIsDisplayed().performClick()

            val currentRoute = appState.navController.currentBackStackEntry?.route()?.route
            assertEquals(Screens.ABSENT_SCREEN, currentRoute)
        }
    }

    private fun chooseDate(date: String) {
        composeTestRule.apply {
            onNodeWithTag(ABSENT_DATE_FIELD).assertIsDisplayed()
            onNodeWithTag("ChooseDate").assertIsDisplayed().performClick()
            onNodeWithText("SELECT DATE").assertIsDisplayed()
            onNodeWithTag("positive").assertIsDisplayed()
            onNodeWithTag("negative").assertIsDisplayed()
            onNodeWithTag("dialog_date_selection_$date").assertIsDisplayed().performClick()
        }
    }

    private fun chooseEmployee(employeeName: String) {
        composeTestRule.apply {
            onNodeWithTag(ABSENT_EMPLOYEE_NAME_FIELD).performClick()
            onNodeWithTag(employeeName).assertIsDisplayed().performClick()
        }
    }
}
