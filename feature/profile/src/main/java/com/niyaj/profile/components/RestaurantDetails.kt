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

package com.niyaj.profile.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.niyaj.core.ui.R
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Profile
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.components.StandardButton
import java.io.File


@Composable
fun RestaurantDetails(
    modifier: Modifier = Modifier,
    info: Profile,
    showPrintLogo: Boolean = false,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
) {
    val context = LocalContext.current

    val printLogoRequest = ImageRequest
        .Builder(context)
        .data(File(context.filesDir, info.printLogo))
        .crossfade(enable = true)
        .placeholder(R.drawable.reslogo)
        .error(R.drawable.reslogo)
        .build()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = info.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(SpaceMini))

        Text(
            text = info.tagline,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.description,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(SpaceSmall))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Text(
            text = info.address,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = SpaceMini),
        )
        Spacer(modifier = Modifier.height(SpaceSmall))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = info.primaryPhone,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            if (info.secondaryPhone.isNotEmpty()) {
                Text(text = " / ")
                Text(
                    text = info.secondaryPhone,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceSmall))
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceSmall))

        if (info.printLogo.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NoteText(
                    text = "You have not set your print logo, Click below to set.",
                    onClick = onClickChangePrintLogo,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                StandardButton(
                    text = "Set Image",
                    icon = PoposIcons.AddAPhoto,
                    onClick = onClickChangePrintLogo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                    ),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(SpaceSmall),
            ) {
                NoteText(
                    text = "Restaurant print logo has been set, Click below to change",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onClickChangePrintLogo,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PoposOutlinedButton(
                        text = "Change",
                        icon = PoposIcons.AddToPhotos,
                        onClick = onClickChangePrintLogo,
                    )

                    Spacer(modifier = Modifier.width(SpaceSmall))

                    StandardButton(
                        text = if (!showPrintLogo) "View Image" else "Hide Image",
                        icon = PoposIcons.ImageSearch,
                        onClick = onClickViewPrintLogo,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showPrintLogo && info.printLogo.isNotEmpty(),
        ) {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(SpaceSmall),
                colors = CardDefaults.cardColors(
                    containerColor = LightColor6,
                ),
            ) {
                SubcomposeAsyncImage(
                    model = printLogoRequest,
                    contentDescription = "Print Logo",
                    loading = { CircularProgressIndicator() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall)
                        .align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

