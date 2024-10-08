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

package com.niyaj.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.niyaj.designsystem.theme.PoposRoomTheme
import com.niyaj.ui.utils.DevicePreviews

const val LOADING_INDICATION = "loadingIndicator"

@Stable
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    contentDesc: String = "loadingIndicator",
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .testTag(LOADING_INDICATION)
                .align(Alignment.Center),
        )
    }
}

@Stable
@Composable
fun LoadingIndicatorHalf(
    modifier: Modifier = Modifier,
    contentDesc: String = "halfLoadingIndicator",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDesc },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .testTag(LOADING_INDICATION)
                .align(Alignment.Center),
        )
    }
}

@DevicePreviews
@Composable
private fun LoadingIndicatorPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        LoadingIndicator(modifier)
    }
}

@DevicePreviews
@Composable
private fun LoadingIndicatorHalfPreview(
    modifier: Modifier = Modifier,
) {
    PoposRoomTheme {
        LoadingIndicatorHalf(modifier)
    }
}
