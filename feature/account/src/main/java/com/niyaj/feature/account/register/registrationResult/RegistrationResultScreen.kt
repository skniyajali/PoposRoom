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

package com.niyaj.feature.account.register.registrationResult

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.trace
import androidx.navigation.NavController
import com.niyaj.core.ui.R.drawable
import com.niyaj.designsystem.components.PoposButton
import com.niyaj.designsystem.components.PoposOutlinedButton
import com.niyaj.designsystem.icon.PoposIcons
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.account.R
import com.niyaj.ui.components.NoteCard
import com.niyaj.ui.utils.Presets
import com.niyaj.ui.utils.Screens
import com.ramcosta.composedestinations.annotation.Destination
import nl.dionsegijn.konfetti.compose.KonfettiView

@Composable
@Destination
fun RegistrationResultScreen(
    navController: NavController,
    result: RegistrationResult,
    message: String,
) = trace("RegistrationResultScreen") {
    Crossfade(
        targetState = result,
        label = "Submit Result State",
        modifier = Modifier.fillMaxSize(),
    ) {
        when (it) {
            RegistrationResult.Failure -> {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Scaffold(
                        content = { innerPadding ->
                            Box {
                                KonfettiView(
                                    modifier = Modifier.fillMaxSize(),
                                    parties = Presets.parade(),
                                )
                                RegistrationResult(
                                    title = stringResource(R.string.on_failure_register_title),
                                    subtitle = stringResource(R.string.on_failure_register_subtitle),
                                    description = stringResource(R.string.on_failure_register_desc),
                                    image = drawable.emptystatetwo,
                                    modifier = Modifier.padding(innerPadding),
                                )
                            }
                        },
                        bottomBar = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                tonalElevation = 7.dp,
                                shadowElevation = 7.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceMedium),
                                    verticalArrangement = Arrangement.spacedBy(SpaceSmall),
                                ) {
                                    NoteCard(text = message)

                                    PoposOutlinedButton(
                                        text = stringResource(id = R.string.go_back),
                                        icon = PoposIcons.NavigateBefore,
                                        onClick = {
                                            navController.navigateUp()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    )
                                }
                            }
                        },
                    )
                }
            }

            RegistrationResult.Success -> {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    content = { innerPadding ->
                        Box {
                            KonfettiView(
                                modifier = Modifier.fillMaxSize(),
                                parties = Presets.rain(),
                            )
                            RegistrationResult(
                                title = stringResource(R.string.on_success_register_title),
                                subtitle = stringResource(R.string.on_success_register_subtitle),
                                description = stringResource(R.string.on_success_register_desc),
                                image = drawable.emptystate,
                                modifier = Modifier.padding(innerPadding),
                            )
                        }
                    },
                    bottomBar = {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shadowElevation = 7.dp,
                        ) {
                            PoposButton(
                                text = stringResource(id = R.string.done),
                                onClick = {
                                    navController.navigate(Screens.HOME_SCREEN) {
                                        popUpTo(navController.graph.id) {
                                            inclusive = true
                                        }
                                    }
                                },
                                icon = PoposIcons.Done,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(SpaceMedium),
                            )
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun RegistrationResult(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    description: String,
    @DrawableRes
    image: Int,
) = trace("RegistrationResult") {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(SpaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SpaceMedium),
    ) {
        item {
            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(400.dp),
            )
        }

        item {
            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = Color.Gray,
            )
        }
    }
}

enum class RegistrationResult {
    Success,
    Failure,
}
