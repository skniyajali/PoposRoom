package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.niyaj.poposroom.features.common.utils.Constants.LOADING_INDICATION

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(LOADING_INDICATION),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        CircularProgressIndicator()
    }
}