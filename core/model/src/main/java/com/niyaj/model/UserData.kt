package com.niyaj.model

import androidx.compose.runtime.Stable

/**
 * Class summarizing user interest data
 */
@Stable
data class UserData(
    val themeBrand: ThemeBrand = ThemeBrand.DEFAULT,
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val useDynamicColor: Boolean = false,
    val shouldHideOnboarding: Boolean = false,
)
