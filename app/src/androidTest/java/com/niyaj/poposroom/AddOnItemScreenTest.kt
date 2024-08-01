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
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.filters.SmallTest
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.niyaj.addonitem.AddonitemNavGraph
import com.niyaj.common.tags.AddOnTestTags.ADDON_APPLIED_SWITCH
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_NOT_AVAILABLE
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_SCREEN_TITLE
import com.niyaj.common.tags.AddOnTestTags.ADD_EDIT_ADDON_SCREEN
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.utils.Constants.DRAWER_ICON
import com.niyaj.common.utils.Constants.STANDARD_BACK_BUTTON
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.poposroom.ui.PoposApp
import com.niyaj.poposroom.ui.PoposAppState
import com.niyaj.poposroom.ui.rememberPoposAppState
import com.niyaj.poposroom.uitesthiltmanifest.HiltComponentActivity
import com.niyaj.ui.components.NAV_SETTING_BTN
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
class AddOnItemScreenTest {

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
    fun a_check_addOnItem_screen_is_displayed() {
        composeTestRule
            .onNodeWithTag(ADDON_SCREEN_TITLE)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(DRAWER_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(NAV_SETTING_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun b_emptyState_is_displayed() {
        composeTestRule
            .onNodeWithText(ADDON_NOT_AVAILABLE)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADD_ON)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(NAV_SETTING_BTN)
            .assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule
            .onNodeWithTag(DRAWER_ICON)
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun click_on_createNewAddOn_and_check_it_navigate_to_AddEditItemScreen() {
        composeTestRule
            .onNodeWithTag(CREATE_NEW_ADD_ON)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeTestRule
            .onNodeWithText(CREATE_NEW_ADD_ON)
            .assertIsDisplayed()

        val currentRoute = appState.navController.currentBackStackEntry?.route()?.route

        assertNotNull(currentRoute)
        assertEquals(Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN.plus("?itemId={itemId}"), currentRoute)

        composeTestRule
            .onNodeWithTag(STANDARD_BACK_BUTTON)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ADD_EDIT_ADDON_SCREEN)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ADDON_NAME_FIELD)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ADDON_NAME_ERROR_TAG)
            .assertExists(ADDON_NAME_EMPTY_ERROR)

        composeTestRule
            .onNodeWithTag(ADDON_PRICE_FIELD)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(ADDON_PRICE_ERROR_TAG)
            .assertExists(ADDON_PRICE_EMPTY_ERROR)

        composeTestRule
            .onNodeWithTag(ADDON_APPLIED_SWITCH)
            .assertIsDisplayed()
            .assertIsSelected()
    }
}
