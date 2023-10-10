package com.niyaj.category.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AddressTestTags
import com.niyaj.common.tags.CategoryConstants.ADD_EDIT_CATEGORY_SCREEN
import com.niyaj.common.tags.CategoryConstants.CATEGORY_AVAILABLE_SWITCH
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_ERROR_TAG
import com.niyaj.common.tags.CategoryConstants.CATEGORY_NAME_FIELD
import com.niyaj.common.tags.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryConstants.UPDATE_CATEGORY
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(
    route = Screens.ADD_EDIT_CATEGORY_SCREEN
)
@Composable
fun AddEditCategoryScreen(
    categoryId: Int = 0,
    navController: NavController,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: AddEditCategoryViewModel = hiltViewModel(),
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value
    val title = if (categoryId == 0) CREATE_NEW_CATEGORY else UPDATE_CATEGORY

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

    StandardScaffoldWithOutDrawer(
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .testTag(AddressTestTags.ADD_EDIT_ADDRESS_BTN)
                    .padding(horizontal = SpaceSmallMax),
                text = title,
                icon = if (categoryId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory(categoryId))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .testTag(ADD_EDIT_CATEGORY_SCREEN)
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            StandardOutlinedTextField(
                value = viewModel.addEditState.categoryName,
                label = CATEGORY_NAME_FIELD,
                leadingIcon = Icons.Default.Category,
                isError = nameError != null,
                errorText = nameError,
                errorTextTag = CATEGORY_NAME_ERROR_TAG,
                onValueChange = {
                    viewModel.onEvent(AddEditCategoryEvent.CategoryNameChanged(it))
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    modifier = Modifier.testTag(CATEGORY_AVAILABLE_SWITCH),
                    checked = viewModel.addEditState.isAvailable,
                    onCheckedChange = {
                        viewModel.onEvent(AddEditCategoryEvent.CategoryAvailabilityChanged)
                    }
                )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(
                    text = if (viewModel.addEditState.isAvailable)
                        "Marked as available"
                    else
                        "Marked as not available",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}