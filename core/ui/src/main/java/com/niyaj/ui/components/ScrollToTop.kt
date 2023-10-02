package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ScrollToTop(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
) {
    FilledTonalIconButton(
        modifier = modifier,
        onClick = onClick,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = containerColor
        )
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Scroll To Top",
        )
    }
}

@Composable
fun ScrollToTop(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { fullHeight ->
                fullHeight / 4
            }
        ),
        exit = fadeOut() + slideOutVertically(
            targetOffsetY = { fullHeight ->
                fullHeight / 4
            }
        ),
        label = "FloatingActionButton"
    ) {
        ScrollToTop(
            onClick = onClick
        )
    }
}