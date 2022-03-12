package com.mobiledeveloper.vktube.ui.theme.typography

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val headings = Headings(
    display = TextStyle(
        fontSize = 34.sp,
        fontWeight = FontWeight.W700,
    ),
    h1 = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.W700,
    ),
    h2 = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.W500,
    ),
    h3 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W500,
    )
)

internal val bodyLarge = BodyStyle(
    long = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
    ),
    accent = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
    ),
    short = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
    ),
    subtitle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400,
    )
)

internal val bodyMedium = BodyStyle(
    long = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
    ),
    accent = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
    ),
    short = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
    ),
    subtitle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
    )
)

internal val bodySmall = BodyStyle(
    long = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
    ),
    accent = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
    ),
    short = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W400,
    ),
    subtitle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
    )
)

internal val minor = Minor(
    caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W400,
    ),
    captionAccent = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
    ),
    overline = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
    )
)