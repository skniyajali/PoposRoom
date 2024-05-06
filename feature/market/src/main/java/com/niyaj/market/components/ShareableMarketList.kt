/*
 *      Copyright 2024 Sk Niyaj Ali
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *              http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.trace
import androidx.compose.ui.window.DialogProperties
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toSafeString
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.ButtonSize
import com.niyaj.designsystem.theme.SpaceLarge
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.MarketItemAndQuantity
import com.niyaj.ui.components.AnimatedTextDivider
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.TwoGridTexts
import com.niyaj.ui.components.drawRainbowBorder
import com.niyaj.ui.utils.CaptureController
import com.niyaj.ui.utils.ScrollableCapturable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareableMarketList(
    captureController: CaptureController,
    marketDate: Long,
    marketLists: List<MarketItemAndQuantity>,
    onDismiss: () -> Unit,
    onClickShare: () -> Unit,
    onCaptured: (Bitmap?, Throwable?) -> Unit
) = trace("ShareableMarketList") {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxSize(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ScrollableCapturable(
                    controller = captureController,
                    onCaptured = onCaptured,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.5f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceMedium),
                            verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {

                            CircularBox(
                                icon = PoposIcons.ShoppingBag,
                                doesSelected = false,
                                size = 80.dp,
                            )

                            Text(
                                text = "Market Date".uppercase(),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Text(
                                text = marketDate.toPrettyDate(),
                                style = MaterialTheme.typography.titleLarge
                            )

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = SpaceLarge)
                                    .drawRainbowBorder(3.dp, durationMillis = 5000),
                                thickness = SpaceMini
                            )
                            val groupedByType = marketLists.groupBy { it.item.itemType }

                            groupedByType.forEach { (itemType, list) ->
                                AnimatedTextDivider(
                                    text = itemType,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                list.fastForEachIndexed { i, it ->
                                    TwoGridTexts(
                                        textOne = it.item.itemName,
                                        textTwo = it.quantityAndType.itemQuantity.toSafeString() +
                                                " " + it.item.itemMeasureUnit?.unitName,
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        isTitle = true
                                    )
                                    if (i != list.size - 1) {
                                        Spacer(modifier = Modifier.height(SpaceMini))
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
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
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
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
        }
    }
}