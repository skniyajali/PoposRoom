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

package com.niyaj.printerInfo.createOrUpdate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.ADDRESS_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.CUSTOMER_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_DPI_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_NBR_LINES_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_PRODUCT_NAME_LENGTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRINTER_WIDTH_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_LOGO_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_QR_CODE_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRINT_WELCOME_TEXT_IN_BILL
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_FIELD
import com.niyaj.common.tags.PrinterInfoTestTags.PRODUCT_REPORT_LIMIT_MESSAGE
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_BUTTON
import com.niyaj.common.tags.PrinterInfoTestTags.UPDATE_PRINTER_INFO
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardCheckboxWithText
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = Screens.UPDATE_PRINTER_INFO_SCREEN)
@Composable
fun UpdatePrinterInfoScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    viewModel: UpdatePrinterInfoViewModel = hiltViewModel(),
) {
    val state = rememberLazyListState()

    val dpiError = viewModel.dpiError.collectAsStateWithLifecycle().value
    val widthError = viewModel.widthError.collectAsStateWithLifecycle().value
    val nbrError = viewModel.nbrError.collectAsStateWithLifecycle().value
    val nameLengthError = viewModel.nameLengthError.collectAsStateWithLifecycle().value
    val productLimitError = viewModel.productLimitError.collectAsStateWithLifecycle().value
    val addressLimitError = viewModel.addressLimitError.collectAsStateWithLifecycle().value
    val customerLimitError = viewModel.customerLimitError.collectAsStateWithLifecycle().value

    val hasError = viewModel.hasError.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    TrackScreenViewEvent(screenName = Screens.UPDATE_PRINTER_INFO_SCREEN)
    TrackScrollJank(scrollableState = state, stateName = "Update::PrinterInfo")

    PoposSecondaryScaffold(
        title = UPDATE_PRINTER_INFO,
        showBackButton = true,
        onBackClick = navigator::navigateUp,
        showBottomBar = !hasError,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(UPDATE_PRINTER_BUTTON)
                    .padding(SpaceSmallMax),
                enabled = !hasError,
                text = UPDATE_PRINTER_INFO,
                icon = PoposIcons.Edit,
                onClick = {
                    viewModel.onEvent(UpdatePrinterInfoEvent.UpdatePrinterInfo)
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            state = state,
        ) {
            item(PRINTER_DPI_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.printerDpi.toString(),
                    leadingIcon = PoposIcons.DensityMedium,
                    label = PRINTER_DPI_FIELD,
                    isError = dpiError != null,
                    errorText = dpiError,
                    message = PRINTER_DPI_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterDpiChanged(it))
                    },
                )
            }

            item(PRINTER_WIDTH_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.printerWidth.toString(),
                    label = PRINTER_WIDTH_FIELD,
                    leadingIcon = PoposIcons.WidthNormal,
                    isError = widthError != null,
                    errorText = widthError,
                    message = PRINTER_WIDTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterWidthChanged(it))
                    },
                )
            }

            item(PRINTER_NBR_LINES_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.printerNbrLines.toString(),
                    label = PRINTER_NBR_LINES_FIELD,
                    leadingIcon = PoposIcons.ViewHeadline,
                    isError = nbrError != null,
                    errorText = nbrError,
                    message = PRINTER_NBR_LINES_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrinterNbrLinesChanged(it))
                    },
                )
            }

            item(PRINTER_PRODUCT_NAME_LENGTH_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.productNameLength.toString(),
                    label = PRINTER_PRODUCT_NAME_LENGTH_FIELD,
                    leadingIcon = PoposIcons.Margin,
                    isError = nameLengthError != null,
                    errorText = nameLengthError,
                    message = PRINTER_PRODUCT_NAME_LENGTH_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductNameLengthChanged(it))
                    },
                )
            }

            item(PRODUCT_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.productWiseReportLimit.toString(),
                    label = PRODUCT_REPORT_LIMIT_FIELD,
                    leadingIcon = PoposIcons.ReceiptLong,
                    isError = productLimitError != null,
                    errorText = productLimitError,
                    message = PRODUCT_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.ProductReportLimitChanged(it))
                    },
                )
            }

            item(ADDRESS_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.addressWiseReportLimit.toString(),
                    label = ADDRESS_REPORT_LIMIT_FIELD,
                    leadingIcon = PoposIcons.Receipt,
                    isError = addressLimitError != null,
                    errorText = addressLimitError,
                    message = ADDRESS_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.AddressReportLimitChanged(it))
                    },
                )
            }

            item(CUSTOMER_REPORT_LIMIT_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.customerWiseReportLimit.toString(),
                    label = CUSTOMER_REPORT_LIMIT_FIELD,
                    leadingIcon = PoposIcons.Receipt,
                    isError = customerLimitError != null,
                    errorText = customerLimitError,
                    message = CUSTOMER_REPORT_LIMIT_MESSAGE,
                    onValueChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.CustomerReportLimitChanged(it))
                    },
                )
            }

            item(PRINT_QR_CODE_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_QR_CODE_IN_BILL,
                    checked = viewModel.state.printQRCode,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintQrCodeChanged)
                    },
                )
            }

            item(PRINT_LOGO_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_LOGO_IN_BILL,
                    checked = viewModel.state.printResLogo,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintResLogoChanged)
                    },
                )
            }

            item(PRINT_WELCOME_TEXT_IN_BILL) {
                StandardCheckboxWithText(
                    text = PRINT_WELCOME_TEXT_IN_BILL,
                    checked = viewModel.state.printWelcomeText,
                    onCheckedChange = {
                        viewModel.onEvent(UpdatePrinterInfoEvent.PrintWelcomeTextChanged)
                    },
                )
            }
        }
    }
}
