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

package com.niyaj.feature.product.createOrUpdate

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.niyaj.common.tags.PaymentScreenTags
import com.niyaj.common.tags.ProductTestTags.ADD_EDIT_PRODUCT_BUTTON
import com.niyaj.common.tags.ProductTestTags.CREATE_NEW_PRODUCT
import com.niyaj.common.tags.ProductTestTags.EDIT_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_AVAILABILITY_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_CATEGORY_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_DESCRIPTION_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NAME_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_PRICE_FIELD
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAGS
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAGS_ERROR
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAGS_MSG
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG_NAME
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposTonalIconButton
import com.niyaj.designsystem.components.StandardRoundedFilterChip
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.AddOrUpdateProduct
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.CategoryChanged
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.OnSelectTag
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.ProductAvailabilityChanged
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.ProductDescChanged
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.ProductNameChanged
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.ProductPriceChanged
import com.niyaj.feature.product.createOrUpdate.AddEditProductEvent.TagNameChanged
import com.niyaj.model.Category
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.PoposSecondaryScaffold
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.parameterProvider.CategoryPreviewData
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.Screens
import com.niyaj.ui.utils.Screens.ADD_EDIT_CATEGORY_SCREEN
import com.niyaj.ui.utils.TrackScreenViewEvent
import com.niyaj.ui.utils.TrackScrollJank
import com.niyaj.ui.utils.UiEvent
import com.niyaj.ui.utils.navigate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination(route = Screens.ADD_EDIT_PRODUCT_SCREEN)
@Composable
fun AddEditProductScreen(
    navigator: DestinationsNavigator,
    resultBackNavigator: ResultBackNavigator<String>,
    modifier: Modifier = Modifier,
    productId: Int = 0,
    viewModel: AddEditProductViewModel = hiltViewModel(),
) {
    TrackScreenViewEvent(screenName = Screens.ADD_EDIT_PRODUCT_SCREEN)

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categoryError by viewModel.categoryError.collectAsStateWithLifecycle()
    val priceError by viewModel.priceError.collectAsStateWithLifecycle()
    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val tagError by viewModel.tagError.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val event by viewModel.eventFlow.collectAsStateWithLifecycle(initialValue = null)

    val tagList = viewModel.tagList.toList()
    val selectedTags = viewModel.selectedTags.toList()

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

    val title = if (productId == 0) CREATE_NEW_PRODUCT else EDIT_PRODUCT
    val icon = if (productId == 0) PoposIcons.Add else PoposIcons.Edit

    AddEditProductScreenContent(
        modifier = modifier,
        title = title,
        icon = icon,
        state = viewModel.state,
        selectedCategory = selectedCategory,
        categories = categories,
        tagList = tagList,
        selectedTags = selectedTags,
        onEvent = viewModel::onEvent,
        categoryError = categoryError,
        priceError = priceError,
        nameError = nameError,
        tagError = tagError,
        onBackClick = navigator::navigateUp,
        onClickAddCategory = {
            navigator.navigate(ADD_EDIT_CATEGORY_SCREEN)
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@VisibleForTesting
@Composable
@Suppress("LongMethod")
internal fun AddEditProductScreenContent(
    state: AddEditProductState,
    selectedCategory: Category,
    categories: List<Category>,
    tagList: List<String>,
    selectedTags: List<String>,
    onEvent: (AddEditProductEvent) -> Unit,
    categoryError: String?,
    priceError: String?,
    nameError: String?,
    tagError: String?,
    onBackClick: () -> Unit,
    onClickAddCategory: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = CREATE_NEW_PRODUCT,
    icon: ImageVector = PoposIcons.Add,
    lazyListState: LazyListState = rememberLazyListState(),
) {
    TrackScrollJank(scrollableState = lazyListState, stateName = "Add/Edit Product::Fields")

    val enableBtn = listOf(categoryError, priceError, nameError).all { it == null }
    var categoryToggled by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val height = (LocalConfiguration.current.screenHeightDp / 2).dp

    PoposSecondaryScaffold(
        title = title,
        onBackClick = onBackClick,
        modifier = modifier,
        showBackButton = true,
        showBottomBar = true,
        bottomBar = {
            PoposButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ADD_EDIT_PRODUCT_BUTTON),
                enabled = enableBtn,
                text = title,
                icon = icon,
                onClick = {
                    onEvent(AddOrUpdateProduct)
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .testTag(PaymentScreenTags.ADD_EDIT_PAYMENT_SCREEN)
                .fillMaxWidth()
                .padding(paddingValues),
            contentPadding = PaddingValues(SpaceMedium),
            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        ) {
            item(PRODUCT_CATEGORY_FIELD) {
                ExposedDropdownMenuBox(
                    expanded = categories.isNotEmpty() && categoryToggled,
                    onExpandedChange = {
                        categoryToggled = !categoryToggled
                    },
                ) {
                    StandardOutlinedTextField(
                        label = PRODUCT_CATEGORY_FIELD,
                        leadingIcon = PoposIcons.Category,
                        value = selectedCategory.categoryName,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                // This is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        isError = categoryError != null,
                        errorText = categoryError,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = categoryToggled,
                            )
                        },
                        readOnly = true,
                        errorTextTag = PRODUCT_CATEGORY_ERROR,
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
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                            .heightIn(max = height),
                    ) {
                        categories.forEachIndexed { index, category ->
                            DropdownMenuItem(
                                modifier = Modifier
                                    .testTag(category.categoryName)
                                    .fillMaxWidth(),
                                onClick = {
                                    onEvent(
                                        CategoryChanged(category),
                                    )
                                    categoryToggled = false
                                },
                                text = {
                                    Text(text = category.categoryName)
                                },
                                leadingIcon = {
                                    CircularBox(
                                        icon = PoposIcons.Category,
                                        selected = false,
                                        text = category.categoryName,
                                        showBorder = !category.isAvailable,
                                        size = 30.dp,
                                    )
                                },
                            )

                            if (index != categories.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 44.dp),
                                )
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
                                            .align(Alignment.CenterHorizontally),
                                    )
                                },
                            )
                        }

                        HorizontalDivider(modifier = Modifier.fillMaxWidth())

                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = onClickAddCategory,
                            text = {
                                Text(
                                    text = "Create a new category",
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = PoposIcons.Add,
                                    contentDescription = "Create",
                                    tint = MaterialTheme.colorScheme.secondary,
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "trailing",
                                )
                            },
                        )
                    }
                }
            }

            item(PRODUCT_NAME_FIELD) {
                StandardOutlinedTextField(
                    label = PRODUCT_NAME_FIELD,
                    leadingIcon = PoposIcons.Feed,
                    value = state.productName,
                    onValueChange = {
                        onEvent(ProductNameChanged(it))
                    },
                    isError = nameError != null,
                    errorText = nameError,
                    showClearIcon = state.productName.isNotEmpty(),
                    errorTextTag = PRODUCT_NAME_ERROR,
                    onClickClearIcon = {
                        onEvent(ProductNameChanged(""))
                    },
                )
            }

            item(PRODUCT_PRICE_FIELD) {
                StandardOutlinedTextField(
                    label = PRODUCT_PRICE_FIELD,
                    leadingIcon = PoposIcons.Money,
                    value = state.productPrice,
                    onValueChange = {
                        onEvent(ProductPriceChanged(it))
                    },
                    isError = priceError != null,
                    errorText = priceError,
                    keyboardType = KeyboardType.Number,
                    showClearIcon = state.productPrice.isNotEmpty(),
                    errorTextTag = PRODUCT_PRICE_ERROR,
                    onClickClearIcon = {
                        onEvent(ProductPriceChanged(""))
                    },
                )
            }

            item(PRODUCT_DESCRIPTION_FIELD) {
                StandardOutlinedTextField(
                    label = PRODUCT_DESCRIPTION_FIELD,
                    leadingIcon = PoposIcons.Description,
                    value = state.productDesc,
                    onValueChange = {
                        onEvent(ProductDescChanged(it))
                    },
                    showClearIcon = state.productDesc.isNotEmpty(),
                    onClickClearIcon = {
                        onEvent(ProductDescChanged(""))
                    },
                )
            }

            item(PRODUCT_TAGS) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    StandardOutlinedTextField(
                        label = PRODUCT_TAG_NAME,
                        leadingIcon = PoposIcons.ListAlt,
                        value = state.tagName,
                        onValueChange = {
                            onEvent(TagNameChanged(it))
                        },
                        isError = tagError != null,
                        errorText = tagError,
                        trailingIcon = {
                            PoposTonalIconButton(
                                icon = PoposIcons.Add,
                                onClick = {
                                    onEvent(OnSelectTag(state.tagName))
                                },
                                enabled = state.tagName.isNotEmpty() && tagError == null,
                            )
                        },
                        errorTextTag = PRODUCT_TAGS_ERROR,
                        message = PRODUCT_TAGS_MSG,
                        suffix = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(SpaceMini),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = "Click here")
                                Icon(
                                    imageVector = PoposIcons.ArrowRightAlt,
                                    contentDescription = "Create",
                                )
                            }
                        },
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            SpaceMini,
                            Alignment.CenterHorizontally,
                        ),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        tagList.forEach {
                            StandardRoundedFilterChip(
                                text = it,
                                selected = selectedTags.contains(it),
                                onClick = {
                                    onEvent(OnSelectTag(it))
                                },
                            )
                        }
                    }
                }
            }

            item(PRODUCT_AVAILABILITY_FIELD) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        modifier = Modifier.testTag(PRODUCT_AVAILABILITY_FIELD),
                        checked = state.productAvailability,
                        onCheckedChange = {
                            onEvent(ProductAvailabilityChanged)
                        },
                    )
                    Spacer(modifier = Modifier.width(SpaceSmall))
                    Text(
                        text = if (state.productAvailability) {
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
private fun AddEditProductScreenContentPreview(
    modifier: Modifier = Modifier,
    categories: List<Category> = CategoryPreviewData.categories,
    selectedCategory: Category = categories.first(),
) {
    PoposRoomTheme {
        AddEditProductScreenContent(
            modifier = modifier,
            state = AddEditProductState(
                productName = "New Product",
                productPrice = "120",
                productDesc = "Product description",
                productAvailability = false,
            ),
            selectedCategory = selectedCategory,
            categories = categories,
            tagList = defaultTagList,
            selectedTags = emptyList(),
            onEvent = {},
            categoryError = null,
            priceError = null,
            nameError = null,
            tagError = null,
            onBackClick = {},
            onClickAddCategory = {},
        )
    }
}
