package com.niyaj.poposroom.features.common.components.color_buttons

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import com.niyaj.poposroom.R

@Stable
data class WiggleButtonItem(
    @DrawableRes val backgroundIcon: Int,
    @DrawableRes val icon: Int,
    var isSelected: Boolean,
    @StringRes val description: Int,
    val animationType: ColorButtonAnimation = BellColorButton(
        tween(500),
        background = ButtonBackground(R.drawable.plus)
    ),
)

@Stable
data class Item(
    @DrawableRes val icon: Int,
    var isSelected: Boolean,
    @StringRes val description: Int,
    val animationType: ColorButtonAnimation = BellColorButton(
        tween(500),
        background = ButtonBackground(R.drawable.plus)
    ),
)