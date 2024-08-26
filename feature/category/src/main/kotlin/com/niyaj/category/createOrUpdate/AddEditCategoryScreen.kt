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

package com.niyaj.category.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.CategoryConstants.ADD_EDIT_CATEGORY_BTN
import com.niyaj.common.tags.CategoryConstants.CATEGORY_AVAILABLE_SWITCH
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_ERROR_TAG
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_FIELD
import com.niyaj.common.tags.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryConstants.UPDATE_CATEGORY
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = Screens.ADD_EDIT_CATEGORY_SCREEN)
@Composable
fun AddEditCategoryScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    categoryId: Int = 0,
    viewModel: AddEditCategoryViewModel = hiltViewModel(),
) {
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val title = if (categoryId == 0) CREATE_NEW_CATEGORY else UPDATE_CATEGORY
    val icon = if (categoryId == 0) PoposIcons.Add else PoposIcons.Edit

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when (data) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(data.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(data.successMessage)
                }
            }
        }
    }

    AddEditCategoryScreenContent(
        state = viewModel.state,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
        modifier = modifier,
        nameError = nameError,
        title = title,
        icon = icon,
    )
}

@VisibleForTesting
@Composable
internal fun AddEditCategoryScreenContent(
    state: AddEditCategoryState,
    onEvent: (AddEditCategoryEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    nameError: String? = null,
    title: String = CREATE_NEW_CATEGORY,
    icon: ImageVector = PoposIcons.Add,
) {
    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_CATEGORY_SCREEN)

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_CATEGORY_BTN),
                text = title,
                icon = icon,
                enabled = nameError == null,
                onClick = {
                    onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory)
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(CATEGORY_NAME_FIELD) {
                StandardOutlinedTextField(
                    label = CATEGORY_NAME_FIELD,
                    leadingIcon = PoposIcons.Category,
                    value = state.categoryName,
                    onValueChange = {
                        onEvent(AddEditCategoryEvent.CategoryNameChanged(it))
                    },
                    isError = nameError != null,
                    errorText = nameError,
                    showClearIcon = state.categoryName.isNotEmpty(),
                    errorTextTag = CATEGORY_NAME_ERROR_TAG,
                    onClickClearIcon = {
                        onEvent(AddEditCategoryEvent.CategoryNameChanged(""))
                    },
                )
            }

            item(CATEGORY_AVAILABLE_SWITCH) {
                StandardCheckboxWithText(
                    text = if (state.isAvailable) {
                        "Marked as available"
                    } else {
                        "Marked as not available"
                    },
                    checked = state.isAvailable,
                    onCheckedChange = {
                        onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
                    },
                    modifier = Modifier.testTag(CATEGORY_AVAILABLE_SWITCH),
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun AddEditCategoryScreenContentPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        AddEditCategoryScreenContent(
            state = AddEditCategoryState(
                categoryName = "New Category",
                isAvailable = true,
            ),
            onEvent = {},
            onBackClick = {},
            modifier = modifier,
            nameError = null,
        )
    }
}
