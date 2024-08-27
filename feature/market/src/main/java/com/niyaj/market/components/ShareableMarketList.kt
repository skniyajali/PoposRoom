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

package com.niyaj.market.components

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toFormattedDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedAssistChip
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.TwoGridText
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.marketItemsAndQuantities
import com.niyaj.ui.parameterProvider.MarketItemAndQuantityData.marketItemsAndQuantity
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.DevicePreviews
import com.niyaj.ui.utils.ScrollableCapturable
import com.niyaj.ui.utils.rememberCaptureController
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShareableMarketList(
    captureController: CaptureController,
    marketDate: Long,
    marketLists: List<MarketItemAndQuantity>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
    modifier: Modifier = Modifier,
    marketDetail: MarketListAndType? = null,
) = trace("ShareableMarketList") {
    val groupByType = remember(marketLists) {
        marketLists.groupBy { it.typeName }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
            .fillMaxSize(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                ScrollableCapturable(
                    controller = captureController,
                    onCaptured = onCaptured,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.5f),
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        ),
                    ) {
                        ShareableItemHeader(marketDate = marketDate, marketDetail = marketDetail)

                        ShareableItemBody(groupByType = groupByType)
                    }
                }

                ShareableListBottomBar(
                    onDismiss = onDismiss,
                    onClickShare = onClickShare,
                )
            }
        }
    }
}

@Composable
internal fun ListTypeHeader(
    itemType: String,
    listType: String,
    listCount: Int,
    modifier: Modifier = Modifier,
) = trace("ListTypeHeader") {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconWithText(
                text = itemType.uppercase(),
                icon = PoposIcons.Category,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )

            Row {
                CountBox(
                    count = listType,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontStyle = FontStyle.Italic,
                    ),
                )

                Spacer(modifier = Modifier.width(SpaceMini))

                CountBox(
                    count = listCount.toString(),
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

@Composable
private fun ShareableListBottomBar(
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    modifier: Modifier = Modifier,
) = trace("ShareableListBottomBar") {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(SpaceSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PoposOutlinedButton(
            text = "Close",
            onClick = onDismiss,
            modifier = Modifier
                .heightIn(ButtonSize)
                .weight(1.4f),
            icon = PoposIcons.Close,
            shape = RoundedCornerShape(SpaceMini),
            color = MaterialTheme.colorScheme.error,
        )

        Spacer(modifier = Modifier.width(SpaceMedium))

        PoposButton(
            text = "Share",
            icon = PoposIcons.Share,
            onClick = onClickShare,
            modifier = Modifier
                .heightIn(ButtonSize)
                .weight(1.4f),
            shape = RoundedCornerShape(SpaceMini),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}

@Composable
private fun ShareableItemBody(
    groupByType: Map<String, List<MarketItemAndQuantity>>,
    modifier: Modifier = Modifier,
) = trace("ShareableItemBody") {
    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        groupByType.forEach { (itemType, groupedByType) ->
            val groupByListType = remember(groupedByType) {
                groupedByType.groupBy { it.listType }
            }

            groupByListType.forEach { (listType, groupedByList) ->
                ListTypeHeader(
                    itemType = itemType,
                    listType = listType,
                    listCount = groupedByList.size,
                )

                groupedByList.fastForEachIndexed { i, item ->
                    TwoGridText(
                        textOne = item.itemName,
                        textTwo = item.itemQuantity?.toSafeString() +
                            " " + item.unitName,
                        modifier = Modifier.padding(SpaceSmall),
                        isTitle = true,
                        textStyle = MaterialTheme.typography.bodyLarge,
                    )
                    if (i != groupedByList.size - 1) {
                        Spacer(modifier = Modifier.height(SpaceMini))
                        HorizontalDivider(color = Color.LightGray)
                    }
                }
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ShareableItemHeader(
    modifier: Modifier = Modifier,
    marketDate: Long = Clock.System.now().toEpochMilliseconds(),
    marketDetail: MarketListAndType? = null,
) = trace("ShareableItemHeader") {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconWithText(
            text = marketDate.toFormattedDate,
            icon = PoposIcons.CalenderMonth,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        marketDetail?.let {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                PoposOutlinedAssistChip(
                    text = marketDetail.typeName.uppercase(),
                    icon = PoposIcons.Category,
                    borderColor = MaterialTheme.colorScheme.primary,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                    ),
                )

                PoposOutlinedAssistChip(
                    text = marketDetail.listType,
                    icon = PoposIcons.ListAlt,
                    borderColor = MaterialTheme.colorScheme.secondary,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Cursive,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                    ),
                )
            }
        }
    }
}

@DevicePreviews
@Composable
private fun ListTypeHeaderPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ListTypeHeader(
            modifier = modifier,
            itemType = "Vegetable",
            listType = "OUT_OF_STOCK",
            listCount = 4,
        )
    }
}

@DevicePreviews
@Composable
private fun ShareableMarketListWithGroupedPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ShareableMarketList(
            captureController = rememberCaptureController(),
            marketDate = System.currentTimeMillis(),
            marketLists = marketItemsAndQuantities,
            modifier = modifier,
            onDismiss = {},
            onClickShare = {},
            onCaptured = { _, _ -> },
            marketDetail = null,
        )
    }
}

@DevicePreviews
@Composable
private fun ShareableMarketListWithPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        ShareableMarketList(
            captureController = rememberCaptureController(),
            marketDate = System.currentTimeMillis(),
            marketLists = marketItemsAndQuantity,
            modifier = modifier,
            onDismiss = {},
            onClickShare = {},
            onCaptured = { _, _ -> },
            marketDetail = null,
        )
    }
}
