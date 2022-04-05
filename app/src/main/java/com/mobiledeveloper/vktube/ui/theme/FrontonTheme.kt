package com.mobiledeveloper.vktube.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.mobiledeveloper.vktube.ui.theme.colors.FrontonColors
import com.mobiledeveloper.vktube.ui.theme.colors.LocalFrontonColors
import com.mobiledeveloper.vktube.ui.theme.colors.lightPalette
import com.mobiledeveloper.vktube.ui.theme.typography.*
import com.mobiledeveloper.vktube.ui.theme.typography.headings

@Composable
fun FrontonTheme(
    content: @Composable () -> Unit
) {
    val typography = FrontonTypography(
        headings = headings,
        body = Body(
            small = bodySmall,
            medium = bodyMedium,
            large = bodyLarge,
        ),
        minor = minor
    )

    val rippleIndication = rememberRipple()

    CompositionLocalProvider(
        LocalFrontonColors provides lightPalette,
        LocalFrontonTypography provides typography,
        LocalIndication provides rippleIndication,
        content = content
    )
}

object Fronton {
    val color: FrontonColors
        @Composable
        get() = LocalFrontonColors.current

    val typography: FrontonTypography
        @Composable
        get() = LocalFrontonTypography.current
}