package com.niyaj.poposroom.features.addon_item.presentation.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_APPLIED_SWITCH
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_ERROR_TAG
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_NAME_FIELD
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_PRICE_ERROR_TAG
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADDON_PRICE_FIELD
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.ADD_EDIT_ADDON_SCREEN
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.CREATE_NEW_ADD_ON
import com.niyaj.poposroom.features.addon_item.domain.utils.AddOnConstants.EDIT_ADD_ON_ITEM
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.safeString
import timber.log.Timber

@Composable
fun AddEditItemScreen(
    addOnItemId: Int = 0,
    closeSheet: () -> Unit,
    viewModel: AddEditAddOnItemViewModel = hiltViewModel(),
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && priceError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(key1 = Unit) {
        viewModel.resetFields()
    }

    LaunchedEffect(key1 = event) {
        event?.let { data ->
            when(data) {
                is UiEvent.IsLoading -> {}
                is UiEvent.OnError -> {
//                    closeSheet()
                    Timber.d("Error loading ${data.errorMessage}")
                }
                is UiEvent.OnSuccess -> {
                    closeSheet()
                }
            }
        }
    }

    LaunchedEffect(key1 = addOnItemId) {
        viewModel.getAllAddOnItemById(addOnItemId)
    }

    Column(
        modifier = Modifier
            .testTag(ADD_EDIT_ADDON_SCREEN)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        StandardTextField(
            value = viewModel.addEditState.itemName,
            label = ADDON_NAME_FIELD,
            leadingIcon = Icons.Default.Category,
            isError = nameError != null,
            errorText = nameError,
            errorTextTag = ADDON_NAME_ERROR_TAG,
            onValueChange = {
                viewModel.onEvent(AddEditAddOnItemEvent.ItemNameChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardTextField(
            value = viewModel.addEditState.itemPrice.safeString,
            label = ADDON_PRICE_FIELD,
            leadingIcon = Icons.Default.CurrencyRupee,
            isError = priceError != null,
            errorText = priceError,
            keyboardType = KeyboardType.Number,
            errorTextTag = ADDON_PRICE_ERROR_TAG,
            onValueChange = {
                viewModel.onEvent(AddEditAddOnItemEvent.ItemPriceChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.testTag(ADDON_APPLIED_SWITCH),
                checked = viewModel.addEditState.isApplicable,
                onCheckedChange = {
                    viewModel.onEvent(AddEditAddOnItemEvent.ItemApplicableChanged)
                }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = if(viewModel.addEditState.isApplicable)
                    "Marked as applied"
                else
                    "Marked as not applied",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardButton(
            text = if (addOnItemId == 0) CREATE_NEW_ADD_ON else EDIT_ADD_ON_ITEM,
            enabled = enableBtn,
            onClick = {
                viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem(addOnItemId))
            }
        )
    }
}