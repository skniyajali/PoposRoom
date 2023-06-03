package com.niyaj.poposroom.features.charges.presentation.add_edit

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
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.ADD_EDIT_CHARGES_BUTTON
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.ADD_EDIT_CHARGES_SCREEN
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_AMOUNT_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_AMOUNT_FIELD
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_APPLIED_SWITCH
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_ERROR
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CHARGES_NAME_FIELD
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.CREATE_NEW_CHARGES
import com.niyaj.poposroom.features.charges.domain.utils.ChargesTestTags.EDIT_CHARGES_ITEM
import com.niyaj.poposroom.features.common.components.StandardButton
import com.niyaj.poposroom.features.common.components.StandardTextField
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.utils.UiEvent
import com.niyaj.poposroom.features.common.utils.safeString

@Composable
fun AddEditChargesScreen(
    chargesId: Int = 0,
    closeSheet: () -> Unit,
    viewModel: AddEditChargesViewModel = hiltViewModel(),
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
                    closeSheet()
                }
                is UiEvent.OnSuccess -> {
                    closeSheet()
                }
            }
        }
    }

    LaunchedEffect(key1 = chargesId) {
        viewModel.getChargesById(chargesId)
    }

    Column(
        modifier = Modifier
            .testTag(ADD_EDIT_CHARGES_SCREEN)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        StandardTextField(
            value = viewModel.addEditState.chargesName,
            label = CHARGES_NAME_FIELD,
            leadingIcon = Icons.Default.Category,
            isError = nameError != null,
            errorText = nameError,
            errorTextTag = CHARGES_NAME_ERROR,
            onValueChange = {
                viewModel.onEvent(AddEditChargesEvent.ChargesNameChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardTextField(
            value = viewModel.addEditState.chargesPrice.safeString,
            label = CHARGES_AMOUNT_FIELD,
            leadingIcon = Icons.Default.CurrencyRupee,
            isError = priceError != null,
            errorText = priceError,
            keyboardType = KeyboardType.Number,
            errorTextTag = CHARGES_AMOUNT_ERROR,
            onValueChange = {
                viewModel.onEvent(AddEditChargesEvent.ChargesPriceChanged(it))
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                modifier = Modifier.testTag(CHARGES_APPLIED_SWITCH),
                checked = viewModel.addEditState.chargesApplicable,
                onCheckedChange = {
                    viewModel.onEvent(AddEditChargesEvent.ChargesApplicableChanged)
                }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            Text(
                text = if(viewModel.addEditState.chargesApplicable)
                    "Marked as applied"
                else
                    "Marked as not applied",
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        StandardButton(
            modifier = Modifier.testTag(ADD_EDIT_CHARGES_BUTTON),
            text = if (chargesId == 0) CREATE_NEW_CHARGES else EDIT_CHARGES_ITEM,
            enabled = enableBtn,
            onClick = {
                viewModel.onEvent(AddEditChargesEvent.CreateOrUpdateCharges(chargesId))
            }
        )
    }
}