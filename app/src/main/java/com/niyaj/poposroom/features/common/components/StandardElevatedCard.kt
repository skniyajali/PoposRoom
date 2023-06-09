package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.features.common.ui.theme.SpaceMini
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardElevatedCard(
    modifier: Modifier = Modifier,
    doesSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    testTag: String = "testTag",
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit = {},
) {
    val borderStroke = if (doesSelected) border else null

    ElevatedCard(
        modifier = modifier
            .testTag(testTag)
            .padding(SpaceSmall)
            .then(borderStroke?.let {
                Modifier.border(it, CardDefaults.elevatedShape)
            } ?: Modifier)
            .clip(CardDefaults.elevatedShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = SpaceMini
        )
    ) {
        content()
    }
}