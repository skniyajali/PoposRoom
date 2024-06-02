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
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.model.MarketListAndType
import com.niyaj.ui.components.CountBox
import com.niyaj.ui.components.IconWithText
import com.niyaj.ui.components.TwoGridTexts
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.ScrollableCapturable
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableMarketList(
    captureController: CaptureController,
    marketDate: Long,
    marketDetail: MarketListAndType? = null,
    marketLists: List<MarketItemAndQuantity>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit,
) = trace("ShareableMarketList") {
    val groupByType = remember(marketLists) {
        marketLists.groupBy { it.typeName }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
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
fun ListTypeHeader(
    modifier: Modifier = Modifier,
    itemType: String,
    listType: String,
    listCount: Int,
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
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge,
            )

            Row {
                CountBox(
                    count = listType,
                    style = TextStyle(
                        fontFamily = FontFamily.Cursive,
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

@Preview
@Composable
fun ShareableItemHeader(
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
                    borderColor = MaterialTheme.colorScheme.primary,
                    icon = PoposIcons.Category,
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

@Composable
fun ShareableListBottomBar(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
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
            modifier = Modifier
                .heightIn(ButtonSize)
                .weight(1.4f),
            icon = PoposIcons.Close,
            text = "Close",
            onClick = onDismiss,
            shape = RoundedCornerShape(SpaceMini),
            color = MaterialTheme.colorScheme.error,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
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
fun ShareableItemBody(
    modifier: Modifier = Modifier,
    groupByType: Map<String, List<MarketItemAndQuantity>>,
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

                groupedByList.fastForEachIndexed { i, it ->
                    TwoGridTexts(
                        modifier = Modifier.padding(SpaceSmall),
                        textOne = it.itemName,
                        textTwo = it.itemQuantity?.toSafeString() +
                            " " + it.unitName,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        isTitle = true,
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
