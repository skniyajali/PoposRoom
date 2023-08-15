package com.niyaj.profile.add_edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADDRESS_FIELD
import com.niyaj.common.tags.ProfileTestTags.ADD_EDIT_PROFILE_BTN
import com.niyaj.common.tags.ProfileTestTags.ADD_EDIT_PROFILE_SCREEN
import com.niyaj.common.tags.ProfileTestTags.CREATE_NEW_PROFILE
import com.niyaj.common.tags.ProfileTestTags.DESC_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.DESC_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.EMAIL_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.NAME_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.P_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.QR_CODE_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.S_PHONE_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_ERROR_FIELD
import com.niyaj.common.tags.ProfileTestTags.TAG_FIELD
import com.niyaj.common.tags.ProfileTestTags.UPDATE_PROFILE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.components.TextWithTitle
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import kotlinx.coroutines.flow.collectLatest

@Destination
@Composable
fun AddEditProfileScreen(
    restaurantId: Int = 0,
    navController: NavController,
    viewModel: AddEditProfileViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>,
) {
    val lazyListState = rememberLazyListState()

    val state = viewModel.state

    val title = if (restaurantId == 0) CREATE_NEW_PROFILE else UPDATE_PROFILE

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(it.errorMessage)
                }

                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(it.successMessage)
                }
            }
        }
    }

    StandardScaffoldWithOutDrawer(
        title = title,
        showBottomBar = true,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PROFILE_BTN)
                    .padding(horizontal = SpaceSmallMax),
                enabled = true,
                text = title,
                icon = if (restaurantId == 0) Icons.Default.Add else Icons.Default.Edit,
                onClick = {
                    viewModel.onEvent(AddEditProfileEvent.CreateOrUpdateProfileInfo(restaurantId))
                }
            )
        },
        onBackClick = {
            navController.navigateUp()
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(ADD_EDIT_PROFILE_SCREEN)
                .fillMaxSize()
                .padding(SpaceSmall),
        ) {
            item(NAME_FIELD) {
                StandardOutlinedTextField(
                    value = state.name,
                    label = NAME_FIELD,
                    leadingIcon = Icons.Default.Restaurant,
                    isError = state.nameError != null,
                    errorText = state.nameError,
                    errorTextTag = NAME_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.NameChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(EMAIL_FIELD) {
                StandardOutlinedTextField(
                    value = state.email,
                    label = EMAIL_FIELD,
                    leadingIcon = Icons.Default.Mail,
                    isError = state.emailError != null,
                    errorText = state.emailError,
                    errorTextTag = EMAIL_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.EmailChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(P_PHONE_FIELD) {
                StandardOutlinedTextField(
                    value = state.primaryPhone,
                    label = P_PHONE_FIELD,
                    leadingIcon = Icons.Default.PhoneAndroid,
                    isError = state.primaryPhoneError != null,
                    errorText = state.primaryPhoneError,
                    errorTextTag = P_PHONE_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.PrimaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(S_PHONE_FIELD) {
                StandardOutlinedTextField(
                    value = state.secondaryPhone,
                    label = S_PHONE_FIELD,
                    leadingIcon = Icons.Default.Phone,
                    isError = state.secondaryPhoneError != null,
                    errorText = state.secondaryPhoneError,
                    errorTextTag = S_PHONE_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.SecondaryPhoneChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(TAG_FIELD) {
                StandardOutlinedTextField(
                    value = state.tagline,
                    label = TAG_FIELD,
                    leadingIcon = Icons.Default.StarBorder,
                    isError = state.taglineError != null,
                    errorText = state.taglineError,
                    errorTextTag = TAG_ERROR_FIELD,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.TaglineChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(DESC_FIELD) {
                StandardOutlinedTextField(
                    value = state.description,
                    label = DESC_FIELD,
                    leadingIcon = Icons.Default.Notes,
                    isError = state.descriptionError != null,
                    errorText = state.descriptionError,
                    errorTextTag = DESC_ERROR_FIELD,
                    singleLine = false,
                    maxLines = 2,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.DescriptionChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(ADDRESS_FIELD) {
                StandardOutlinedTextField(
                    value = state.address,
                    label = ADDRESS_FIELD,
                    leadingIcon = Icons.Default.LocationOn,
                    isError = state.addressError != null,
                    errorText = state.addressError,
                    errorTextTag = ADDRESS_ERROR_FIELD,
                    singleLine = false,
                    maxLines = 2,
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.AddressChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }

            item(QR_CODE_FIELD) {
                StandardOutlinedTextField(
                    value = state.paymentQrCode,
                    label = QR_CODE_FIELD,
                    leadingIcon = Icons.Default.QrCode,
                    trailingIcon = {
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = "Scan QR Code"
                            )
                        }
                    },
                    onValueChange = {
                        viewModel.onEvent(AddEditProfileEvent.PaymentQrCodeChanged(it))
                    },
                    suffix = {
                        TextWithTitle(
                            text = "Scan Code",
                            icon = Icons.Default.ArrowRightAlt
                        )
                    }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}