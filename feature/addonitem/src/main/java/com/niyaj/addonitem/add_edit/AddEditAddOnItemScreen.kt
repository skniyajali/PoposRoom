package com.niyaj.addonitem.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AddOnTestTags
import com.niyaj.common.tags.AddOnTestTags.ADDON_APPLIED_SWITCH
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_NAME_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_ERROR_TAG
import com.niyaj.common.tags.AddOnTestTags.ADDON_PRICE_FIELD
import com.niyaj.common.tags.AddOnTestTags.ADD_EDIT_ADDON_SCREEN
import com.niyaj.common.tags.AddOnTestTags.CREATE_NEW_ADD_ON
import com.niyaj.common.tags.AddOnTestTags.EDIT_ADD_ON_ITEM
import com.niyaj.common.utils.safeString
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.StandardBottomSheet
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

@Destination(
    route = Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun AddEditAddOnItemScreen(
    itemId: Int = 0,
    navController: NavController,
    viewModel: AddEditAddOnItemViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    val enableBtn = nameError == null && priceError == null

    val event = viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null).value

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

    val title = if (itemId == 0) CREATE_NEW_ADD_ON else EDIT_ADD_ON_ITEM

    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_ADD_ON_ITEM_SCREEN)

    StandardBottomSheet(
        modifier = Modifier,
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
    ) {
        Column(
            modifier = Modifier
                .testTag(ADD_EDIT_ADDON_SCREEN)
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall, Alignment.CenterVertically),
        ) {
            StandardOutlinedTextField(
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

            StandardOutlinedTextField(
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
                    text = if (viewModel.addEditState.isApplicable)
                        "Marked as applied"
                    else
                        "Marked as not applied",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            StandardButton(
                modifier = Modifier
                    .testTag(AddOnTestTags.ADD_EDIT_ADDON_BUTTON),
                text = title,
                enabled = enableBtn,
                icon = if (itemId == 0) Icons.Default.Add else Icons.Default.Edit,
                onClick = {
                    viewModel.onEvent(AddEditAddOnItemEvent.CreateUpdateAddOnItem(itemId))
                }
            )
        }
    }
}