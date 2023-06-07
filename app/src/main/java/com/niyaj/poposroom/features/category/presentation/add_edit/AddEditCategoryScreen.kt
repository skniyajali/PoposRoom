package com.niyaj.poposroom.features.category.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.ADD_EDIT_CATEGORY_SCREEN
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_AVAILABLE_SWITCH
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_NAME_ERROR_TAG
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CATEGORY_NAME_FIELD
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.CREATE_NEW_CATEGORY
import com.niyaj.poposroom.features.category.domain.utils.CategoryConstants.UPDATE_CATEGORY
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent

@Composable
fun AddEditCategoryScreen(
    categoryId: Int = 0,
    closeSheet: () -> Unit,
    viewModel: AddEditCategoryViewModel = hiltViewModel(),
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.IsLoading -> {}
                is UiEvent.OnError -> {
                    closeSheet()
                }
                is UiEvent.OnSuccess -> {
                    closeSheet()
                }
            }
        }
    }

    LaunchedEffect(key1 = categoryId) {
        viewModel.getCategoryById(categoryId)
    }

    Column(
        modifier = Modifier
            .testTag(ADD_EDIT_CATEGORY_SCREEN)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        StandardTextField(
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

        Spacer(modifier = Modifier.height(SpaceSmall))

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
                text = if(viewModel.addEditState.isAvailable)
                    "Marked as available"
                else
                    "Marked as not available",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardButton(
            text = if (categoryId == 0) CREATE_NEW_CATEGORY else UPDATE_CATEGORY,
            icon = if (categoryId == 0) Icons.Default.Add else Icons.Default.Edit,
            enabled = enableBtn,
            onClick = {
                viewModel.onEvent(AddEditCategoryEvent.CreateUpdateAddEditCategory(categoryId))
            }
        )
    }
}