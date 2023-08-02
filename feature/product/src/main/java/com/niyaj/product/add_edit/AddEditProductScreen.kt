package com.niyaj.product.add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FeaturedPlayList
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.data.utils.PaymentScreenTags
import com.niyaj.data.utils.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.data.utils.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.data.utils.ProductTestTags.EDIT_PRODUCT
import com.niyaj.data.utils.ProductTestTags.PRODUCT_AVAILABILITY_FIELD
import com.niyaj.data.utils.ProductTestTags.PRODUCT_CATEGORY_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_CATEGORY_FIELD
import com.niyaj.data.utils.ProductTestTags.PRODUCT_DESCRIPTION_FIELD
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NAME_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_NAME_FIELD
import com.niyaj.data.utils.ProductTestTags.PRODUCT_PRICE_ERROR
import com.niyaj.data.utils.ProductTestTags.PRODUCT_PRICE_FIELD
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.SpaceSmallMax
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardButton
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.components.StandardScaffoldWithOutDrawer
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.UiEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination(
    route = Screens.AddEditProductScreen
)
@Composable
fun AddEditProductScreen(
    productId: Int = 0,
    navController: NavController,
    viewModel: AddEditProductViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val lazyListState = rememberLazyListState()

    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val categoryError = viewModel.categoryError.collectAsStateWithLifecycle().value
    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value
    val nameError = viewModel.nameError.collectAsStateWithLifecycle().value

    val enableBtn = listOf(
        categoryError,
        priceError,
        nameError,
    ).all { it == null }

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

    var categoryToggled by remember { mutableStateOf(false) }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val title = if (productId == 0) CREATE_NEW_PRODUCT else EDIT_PRODUCT

    StandardScaffoldWithOutDrawer(
        title = title,
        onBackClick = {
            navController.navigateUp()
        },
        showBottomBar = enableBtn,
        bottomBar = {
            StandardButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PRODUCT_BUTTON)
                    .padding(horizontal = SpaceSmallMax),
                enabled = enableBtn,
                text = title,
                icon = if (productId == 0) Icons.Default.Add else Icons.Default.Edit,
                onClick = {
                    viewModel.onEvent(AddEditProductEvent.AddOrUpdateProduct(productId))
                }
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(PaymentScreenTags.ADD_EDIT_PAYMENT_SCREEN)
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall)
        ) {
            item(PRODUCT_CATEGORY_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = categories.isNotEmpty() && categoryToggled,
                    onExpandedChange = {
                        categoryToggled = !categoryToggled
                    },
                ) {
                    StandardOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                //This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(),
                        value = selectedCategory.categoryName,
                        label = PRODUCT_CATEGORY_FIELD,
                        leadingIcon = Icons.Default.Category,
                        isError = categoryError != null,
                        errorText = categoryError,
                        readOnly = true,
                        errorTextTag = PRODUCT_CATEGORY_ERROR,
                        onValueChange = {},
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryToggled
                            )
                        },
                    )

                    DropdownMenu(
                        expanded = categoryToggled,
                        onDismissRequest = {
                            categoryToggled = false
                        },
                        properties = PopupProperties(
                            focusable = false,
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true,
                            excludeFromSystemGesture = true,
                            clippingEnabled = true,
                        ),
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                    ) {
                        categories.forEachIndexed { index, category ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(category.categoryName)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.onEvent(
                                        AddEditProductEvent.CategoryChanged(category)
                                    )
                                    categoryToggled = false
                                },
                                text = {
                                    Text(text = category.categoryName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = Icons.Default.Category,
                                        doesSelected = false,
                                        size = 30.dp,
                                        showBorder = !category.isAvailable,
                                        text = category.categoryName
                                    )
                                }
                            )

                            if (index != categories.size - 1) {
                                Divider(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 44.dp))
                            }
                        }

                        if (categories.isEmpty()) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally),
                                enabled = false,
                                onClick = {},
                                text = {
                                    Text(
                                        text = "Categories not available",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally)
                                    )
                                },
                            )
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                navController.navigate(Screens.AddEditCategoryScreen)
                            },
                            text = {
                                Text(
                                    text = "Create a new category",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ArrowRightAlt,
                                    contentDescription = "trailing"
                                )
                            }
                        )
                    }
                }
            }

            item(PRODUCT_NAME_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.productName,
                    label = PRODUCT_NAME_FIELD,
                    leadingIcon = Icons.Default.FeaturedPlayList,
                    isError = nameError != null,
                    errorText = nameError,
                    errorTextTag = PRODUCT_NAME_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditProductEvent.ProductNameChanged(it))
                    },
                )
            }

            item(PRODUCT_PRICE_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.productPrice,
                    label = PRODUCT_PRICE_FIELD,
                    leadingIcon = Icons.Default.Money,
                    keyboardType = KeyboardType.Number,
                    isError = priceError != null,
                    errorText = priceError,
                    errorTextTag = PRODUCT_PRICE_ERROR,
                    onValueChange = {
                        viewModel.onEvent(AddEditProductEvent.ProductPriceChanged(it))
                    }
                )
            }

            item(PRODUCT_DESCRIPTION_FIELD) {
                StandardOutlinedTextField(
                    value = viewModel.state.productDesc,
                    label = PRODUCT_DESCRIPTION_FIELD,
                    leadingIcon = Icons.Default.Description,
                    onValueChange = {
                        viewModel.onEvent(AddEditProductEvent.ProductDescChanged(it))
                    }
                )
            }

            item(PRODUCT_AVAILABILITY_FIELD) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(PRODUCT_AVAILABILITY_FIELD),
                        checked = viewModel.state.productAvailability,
                        onCheckedChange = {
                            viewModel.onEvent(AddEditProductEvent.ProductAvailabilityChanged)
                        }
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (viewModel.state.productAvailability)
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
}