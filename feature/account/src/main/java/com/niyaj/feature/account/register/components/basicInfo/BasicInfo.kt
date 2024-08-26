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

package com.niyaj.feature.account.register.components.basicInfo

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.account.R
import com.niyaj.ui.components.ImageCard
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.utils.DevicePreviews

@Composable
fun BasicInfo(
    infoState: BasicInfoState,
    onEvent: (BasicInfoEvent) -> Unit,
    onChangeLogo: () -> Unit,
    modifier: Modifier = Modifier,
    taglineError: String? = null,
    addressError: String? = null,
    descriptionError: String? = null,
    paymentQRCodeError: String? = null,
    scannedBitmap: Bitmap? = null,
    @DrawableRes
    defaultLogo: Int = com.niyaj.core.ui.R.drawable.reslogo,
    lazyListState: LazyListState = rememberLazyListState(),
) = trace("BasicInfo") {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceMedium),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium, Alignment.CenterVertically),
        state = lazyListState,
    ) {
        item("basic_title") {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceMini),
            ) {
                Text(
                    text = stringResource(R.string.basic_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.basic_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }

        item("print_logo") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ImageCard(
                    defaultImage = defaultLogo,
                    onEditClick = onChangeLogo,
                    imageName = infoState.printLogo,
                    contentScale = ContentScale.Inside,
                    size = DpSize(400.dp, 150.dp),
                )

                NoteCard(text = stringResource(R.string.print_logo_note))
            }
        }

        item("tagline_field") {
            StandardOutlinedTextField(
                label = "Restaurant Tagline",
                leadingIcon = PoposIcons.StarHalf,
                value = infoState.tagline,
                onValueChange = {
                    onEvent(BasicInfoEvent.TaglineChanged(it))
                },
                isError = taglineError != null,
                errorText = taglineError,
            )
        }

        item("description_field") {
            StandardOutlinedTextField(
                label = "Restaurant Description",
                leadingIcon = PoposIcons.Note,
                value = infoState.description,
                onValueChange = {
                    onEvent(BasicInfoEvent.DescriptionChanged(it))
                },
                isError = descriptionError != null,
                errorText = descriptionError,
                singleLine = false,
                maxLines = 4,
            )
        }

        item("address_field") {
            StandardOutlinedTextField(
                label = "Restaurant Address",
                leadingIcon = PoposIcons.LocationOn,
                value = infoState.address,
                onValueChange = {
                    onEvent(BasicInfoEvent.AddressChanged(it))
                },
                isError = addressError != null,
                errorText = addressError,
                singleLine = false,
                maxLines = 2,
            )
        }

        item("qrcode_field") {
            StandardOutlinedTextField(
                label = "Restaurant Payment QR Code",
                leadingIcon = PoposIcons.QrCode,
                value = infoState.paymentQrCode,
                onValueChange = {
                    onEvent(BasicInfoEvent.PaymentQRChanged(it))
                },
                modifier = Modifier,
                isError = paymentQRCodeError != null,
                errorText = paymentQRCodeError,
                singleLine = false,
                maxLines = 4,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onEvent(BasicInfoEvent.StartScanning)
                        },
                    ) {
                        Icon(
                            imageVector = PoposIcons.QrCodeScanner,
                            contentDescription = "Scan QR Code",
                        )
                    }
                },
            )
        }

        if (scannedBitmap != null) {
            item("scannedBitmap") {
                Spacer(modifier = Modifier.height(SpaceSmall))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        bitmap = scannedBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                    )
                }

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}

@DevicePreviews
@Composable
private fun BasicInfoPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        BasicInfo(
            infoState = BasicInfoState(),
            onEvent = {},
            onChangeLogo = {},
            modifier = modifier,
        )
    }
}
