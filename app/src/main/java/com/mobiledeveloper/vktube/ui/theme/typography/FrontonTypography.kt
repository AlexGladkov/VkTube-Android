package com.mobiledeveloper.vktube.ui.theme.typography

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class FrontonTypography(
    val headings: Headings,
    val body: Body,
    val minor: Minor
)

// Headings
// https://zeroheight.com/3278b70cf/p/92331e-typography/t/745b1b
data class Headings(
    val display: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle
)

// Body Template
// https://zeroheight.com/3278b70cf/p/92331e-typography/t/22cf16
data class Body(
    val large: BodyStyle,
    val medium: BodyStyle,
    val small: BodyStyle
)

data class BodyStyle(
    val long: TextStyle,
    val accent: TextStyle,
    val short: TextStyle,
    val subtitle: TextStyle
)

// Minor
// https://zeroheight.com/3278b70cf/p/92331e-typography/t/786213
data class Minor(
    val caption: TextStyle,
    val captionAccent: TextStyle,
    val overline: TextStyle
)

val LocalFrontonTypography = staticCompositionLocalOf<FrontonTypography> {
    error("No font provided")
}