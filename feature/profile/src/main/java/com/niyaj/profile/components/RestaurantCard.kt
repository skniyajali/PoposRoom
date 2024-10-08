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

package com.niyaj.profile.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Profile
import com.niyaj.ui.components.ImageCard
import com.niyaj.ui.components.NoteText
import com.niyaj.ui.utils.DevicePreviews
import java.io.File

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun RestaurantCard(
    info: Profile,
    onClickChangeResLogo: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
    modifier: Modifier = Modifier,
    showPrintLogo: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary),
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = SpaceSmall,
            bottomEnd = SpaceSmall,
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )

                ImageCard(
                    defaultImage = com.niyaj.core.ui.R.drawable.popos,
                    onEditClick = onClickChangeResLogo,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 30.dp),
                    imageName = info.logo,
                )
            }

            RestaurantDetails(
                modifier = Modifier
                    .padding(top = 30.dp),
                info = info,
                showPrintLogo = showPrintLogo,
                onClickChangePrintLogo = onClickChangePrintLogo,
                onClickViewPrintLogo = onClickViewPrintLogo,
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun UpdatedRestaurantCard(
    info: Profile,
    onClickChangeResLogo: () -> Unit,
    onClickChangePrintLogo: () -> Unit,
    onClickViewPrintLogo: () -> Unit,
    modifier: Modifier = Modifier,
    showPrintLogo: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.background,
) {
    val context = LocalContext.current
    val sidePadding = (-8).dp

    val printLogoRequest = ImageRequest
        .Builder(context)
        .data(File(context.filesDir, info.printLogo))
        .crossfade(enable = true)
        .placeholder(com.niyaj.core.ui.R.drawable.popos)
        .error(com.niyaj.core.ui.R.drawable.popos)
        .build()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .layout { measurable, constraints ->
                // Measure the composable adding the side padding*2 (left+right)
                val placeable =
                    measurable.measure(
                        constraints.offset(
                            horizontal = -sidePadding.roundToPx() * 2,
                            vertical = -sidePadding.roundToPx(),
                        ),
                    )

                // increase the width adding the side padding*2
                layout(
                    placeable.width + sidePadding.roundToPx() * 2,
                    placeable.height,
                ) {
                    // Where the composable gets placed
                    placeable.place(+sidePadding.roundToPx(), +sidePadding.roundToPx())
                }
            },
        shape = RoundedCornerShape(
            bottomStart = SpaceSmall,
            bottomEnd = SpaceSmall,
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(MaterialTheme.colorScheme.primary),
                )

                ImageCard(
                    defaultImage = com.niyaj.core.ui.R.drawable.popos,
                    onEditClick = onClickChangeResLogo,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 30.dp),
                    imageName = info.logo,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

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

            Crossfade(
                targetState = info.printLogo.isEmpty(),
                label = "isPrintLogoEmpty",
            ) {
                if (it) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceMedium),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                    ) {
                        NoteText(
                            text = "You have not set your print logo, Click below to set.",
                            onClick = onClickChangePrintLogo,
                        )

                        PoposButton(
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
                            .padding(SpaceMedium),
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
                                onClick = onClickChangePrintLogo,
                                icon = PoposIcons.AddToPhotos,
                            )

                            Spacer(modifier = Modifier.width(SpaceSmall))

                            PoposButton(
                                text = if (!showPrintLogo) "View Image" else "Hide Image",
                                icon = PoposIcons.ImageSearch,
                                onClick = onClickViewPrintLogo,
                            )
                        }
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
                    colors = CardDefaults.elevatedCardColors(
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
}

@DevicePreviews
@Composable
private fun RestaurantCardPreview(
    modifier: Modifier = Modifier,
    profile: Profile = Profile.defaultProfileInfo,
) {
    RestaurantCard(
        modifier = modifier,
        info = profile,
        showPrintLogo = false,
        onClickChangeResLogo = {},
        onClickChangePrintLogo = {},
        onClickViewPrintLogo = {},
    )
}

@DevicePreviews
@Composable
private fun UpdatedRestaurantCardPreview(
    modifier: Modifier = Modifier,
) {
    UpdatedRestaurantCard(
        modifier = modifier,
        info = Profile(
            restaurantId = 9551,
            name = "Virgil Guthrie",
            email = "bennie.frazier@example.com",
            primaryPhone = "(109) 112-3895",
            secondaryPhone = "(863) 902-6761",
            tagline = "esse",
            description = "petentium",
            address = "egestas",
            logo = "quas",
            printLogo = "habitasse",
            paymentQrCode = "dictas",
            createdAt = "duo",
            updatedAt = null,
        ),
        showPrintLogo = false,
        onClickChangeResLogo = {},
        onClickChangePrintLogo = {},
        onClickViewPrintLogo = {},
    )
}
