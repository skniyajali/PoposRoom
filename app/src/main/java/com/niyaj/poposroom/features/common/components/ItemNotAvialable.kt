package com.niyaj.poposroom.features.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.poposroom.R
import com.niyaj.poposroom.features.common.ui.theme.ButtonSize
import com.niyaj.poposroom.features.common.ui.theme.SpaceMedium
import com.niyaj.poposroom.features.common.ui.theme.SpaceSmall
import com.niyaj.poposroom.features.common.ui.theme.light_outlineVariant

@Composable
fun ItemNotAvailable(
    modifier: Modifier = Modifier,
    btnModifier: Modifier = Modifier,
    text: String = "",
    buttonText: String = "",
    showImage: Boolean = true,
    image: Painter = painterResource(id = R.drawable.emptystate),
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showImage) {
            Image(
                painter = image,
                contentDescription = "No data available"
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
        }

        Text(
            text = text,
            fontWeight = FontWeight.Normal,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = light_outlineVariant
        )

        if(buttonText.isNotEmpty()){
            Spacer(modifier = Modifier.height(SpaceMedium))
            Button(
                onClick = { onClick() },
                shape= CutCornerShape(4.dp),
                modifier = btnModifier.heightIn(ButtonSize)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(id = R.string.add_icon) )
                Spacer(modifier = Modifier.width(SpaceSmall))
                Text(text = buttonText.uppercase())
            }
        }
    }
}