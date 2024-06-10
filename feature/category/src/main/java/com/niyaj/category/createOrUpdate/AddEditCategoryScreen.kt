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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.CategoryConstants.ADD_EDIT_CATEGORY_SCREEN
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
    categoryId: Int = 0,
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
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
        modifier = Modifier,
        title = title,
        icon = icon,
        state = viewModel.addEditState,
        nameError = nameError,
        onEvent = viewModel::onEvent,
        onBackClick = navigator::navigateUp,
    )
}

@VisibleForTesting
@Composable
internal fun AddEditCategoryScreenContent(
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_CATEGORY,
    icon: ImageVector = PoposIcons.Add,
    state: AddEditCategoryState,
    nameError: String? = null,
    onEvent: (AddEditCategoryEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_CATEGORY_SCREEN)

    PoposSecondaryScaffold(
        modifier = modifier,
        title = title,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AddressTestTags.ADD_EDIT_ADDRESS_BTN),
                text = title,
                icon = icon,
                enabled = nameError == null,
                onClick = {
                    onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory)
                },
            )
        },
        onBackClick = onBackClick,
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .testTag(ADD_EDIT_CATEGORY_SCREEN)
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(CATEGORY_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.categoryName,
                    label = CATEGORY_NAME_FIELD,
                    leadingIcon = PoposIcons.Category,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = CATEGORY_NAME_ERROR_TAG,
                    showClearIcon = state.categoryName.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(AddEditCategoryEvent.CategoryNameChanged(""))
                    },
                    onValueChange = {
                        onEvent(AddEditCategoryEvent.CategoryNameChanged(it))
                    },
                )
            }

            item(CATEGORY_AVAILABLE_SWITCH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(CATEGORY_AVAILABLE_SWITCH),
                        checked = state.isAvailable,
                        onCheckedChange = {
                            onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
                        },
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (state.isAvailable) {
                            "Marked as available"
                        } else {
                            "Marked as not available"
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

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
            modifier = modifier,
            state = AddEditCategoryState(
                categoryName = "New Category",
                isAvailable = true,
            ),
            nameError = null,
            onEvent = {},
            onBackClick = {}
        )
    }
}