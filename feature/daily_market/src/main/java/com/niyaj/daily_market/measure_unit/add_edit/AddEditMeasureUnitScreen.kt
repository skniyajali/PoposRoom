package com.niyaj.daily_market.measure_unit.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LineWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.MeasureUnitTestTags.ADD_EDIT_UNIT_BUTTON
import com.niyaj.common.tags.MeasureUnitTestTags.CREATE_NEW_UNIT
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_ERROR_TAG
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_NAME_FIELD
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_ERROR_TAG
import com.niyaj.common.tags.MeasureUnitTestTags.UNIT_VALUE_FIELD
import com.niyaj.common.tags.MeasureUnitTestTags.UPDATE_UNIT
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun AddEditMeasureUnitScreen(
    unitId: Int = 0,
    navController: NavController,
    viewModel: AddEditMeasureUnitViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value
    val valueError = viewModel.valueError.collectAsStateWithLifecycle().value

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

    val enableBtn = kotlin.collections.listOf(nameError, valueError).all { it == null }

    val title = if (unitId == 0) CREATE_NEW_UNIT else UPDATE_UNIT

    StandardScaffoldNew(
        navController = navController,
        title = title,
        showBottomBar = true,
        showBackButton = true,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_UNIT_BUTTON)
                    .padding(SpaceMedium),
                text = title,
                icon = if (unitId == 0) Icons.Default.Add else Icons.Default.Edit,
                enabled = enableBtn,
                onClick = {
                    viewModel.onEvent(AddEditMeasureUnitEvent.SaveOrUpdateMeasureUnit)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmallMax),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {

            StandardOutlinedTextField(
                value = viewModel.state.unitName,
                label = UNIT_NAME_FIELD,
                leadingIcon = Icons.Default.LineWeight,
                errorText = nameError,
                isError = nameError != null,
                errorTextTag = UNIT_NAME_ERROR_TAG,
                onValueChange = {
                    viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitNameChanged(it))
                }
            )

            StandardOutlinedTextField(
                value = viewModel.state.unitValue,
                label = UNIT_VALUE_FIELD,
                leadingIcon = Icons.Default.Api,
                errorText = valueError,
                isError = valueError != null,
                errorTextTag = UNIT_VALUE_ERROR_TAG,
                onValueChange = {
                    viewModel.onEvent(AddEditMeasureUnitEvent.MeasureUnitValueChanged(it))
                },
                keyboardType = KeyboardType.Number,
            )
        }
    }
}