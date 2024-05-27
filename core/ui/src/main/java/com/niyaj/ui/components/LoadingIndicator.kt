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

const val LOADING_INDICATION = "loadingIndicator"

@Stable
@Composable
fun LoadingIndicator(
    contentDesc: String = "loadingIndicator",
) {
    Box(
        modifier = Modifier
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
    contentDesc: String = "halfLoadingIndicator",
) {
    Box(
        modifier = Modifier
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