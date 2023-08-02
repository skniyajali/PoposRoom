package com.niyaj.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.niyaj.common.utils.Constants
import com.niyaj.designsystem.theme.SpaceSmall

@Composable
fun StandardFAB(
    fabVisible: Boolean,
    showScrollToTop: Boolean = false,
    fabText: String = Constants.FAB_TEXT,
    fabIcon: ImageVector = Icons.Filled.Add,
    containerColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    onFabClick: () -> Unit,
    onClickScroll: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            ScrollToTop(onClick = onClickScroll, containerColor = containerColor)
        }

        Spacer(modifier = Modifier.height(SpaceSmall))

        AnimatedVisibility(
            visible = fabVisible,
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
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onFabClick,
                expanded = !showScrollToTop,
                icon = { Icon(fabIcon, fabText) },
                text = { Text(text = fabText.uppercase()) },
            )
        }
    }
}