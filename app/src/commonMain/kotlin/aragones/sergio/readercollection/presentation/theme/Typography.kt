/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 30/8/2024
 */

package aragones.sergio.readercollection.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.roboto_serif_bold
import reader_collection.app.generated.resources.roboto_serif_regular
import reader_collection.app.generated.resources.roboto_serif_thin

val Typography: Typography
    @Composable get() = Typography(
        displayLarge = TextStyle.DisplayLarge,
        displayMedium = TextStyle.DisplayMedium,
        displaySmall = TextStyle.DisplaySmall,
        headlineLarge = TextStyle.HeadlineLarge,
        headlineMedium = TextStyle.HeadlineMedium,
        headlineSmall = TextStyle.HeadlineSmall,
        titleLarge = TextStyle.TitleLarge,
        titleMedium = TextStyle.TitleMedium,
        titleSmall = TextStyle.TitleSmall,
        bodyLarge = TextStyle.BodyLarge,
        bodyMedium = TextStyle.BodyMedium,
        bodySmall = TextStyle.BodySmall,
        labelLarge = TextStyle.LabelLarge,
        labelMedium = TextStyle.LabelMedium,
        labelSmall = TextStyle.LabelSmall,
    )

private val FontFamily.Companion.Roboto: FontFamily
    @Composable get() = FontFamily(
        Font(Res.font.roboto_serif_regular, FontWeight.Normal),
        Font(Res.font.roboto_serif_thin, FontWeight.Thin),
        Font(Res.font.roboto_serif_bold, FontWeight.Bold),
    )

private val TextStyle.Companion.DisplayLarge: TextStyle
    @Composable get() = MaterialTheme.typography.displayLarge.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.DisplayMedium: TextStyle
    @Composable get() = MaterialTheme.typography.displayMedium.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.DisplaySmall: TextStyle
    @Composable get() = MaterialTheme.typography.displaySmall.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.HeadlineLarge: TextStyle
    @Composable get() = MaterialTheme.typography.headlineLarge.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.HeadlineMedium: TextStyle
    @Composable get() = MaterialTheme.typography.headlineMedium.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.HeadlineSmall: TextStyle
    @Composable get() = MaterialTheme.typography.headlineSmall.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.TitleLarge: TextStyle
    @Composable get() = MaterialTheme.typography.titleLarge.copy(
        fontSize = 20.sp,
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.TitleMedium: TextStyle
    @Composable get() = MaterialTheme.typography.titleMedium.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.TitleSmall: TextStyle
    @Composable get() = MaterialTheme.typography.titleSmall.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.BodyLarge: TextStyle
    @Composable get() = MaterialTheme.typography.bodyLarge.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.BodyMedium: TextStyle
    @Composable get() = MaterialTheme.typography.bodyMedium.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.BodySmall: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.LabelLarge: TextStyle
    @Composable get() = MaterialTheme.typography.labelLarge.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.LabelMedium: TextStyle
    @Composable get() = MaterialTheme.typography.labelMedium.copy(
        fontFamily = FontFamily.Roboto,
    )

private val TextStyle.Companion.LabelSmall: TextStyle
    @Composable get() = MaterialTheme.typography.labelSmall.copy(
        fontFamily = FontFamily.Roboto,
    )

val Typography.displayLargeEmphasized: TextStyle get() = displayLarge.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.displayMediumEmphasized: TextStyle get() = displayMedium.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.displaySmallEmphasized: TextStyle get() = displaySmall.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.headlineLargeEmphasized: TextStyle get() = headlineLarge.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.headlineMediumEmphasized: TextStyle get() = headlineMedium.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.headlineSmallEmphasized: TextStyle get() = headlineSmall.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.titleLargeEmphasized: TextStyle get() = titleLarge.copy(fontWeight = FontWeight.Bold)
val Typography.titleMediumEmphasized: TextStyle get() = titleMedium.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.titleSmallEmphasized: TextStyle get() = titleSmall.copy(fontWeight = FontWeight.Bold)
val Typography.bodyLargeEmphasized: TextStyle get() = bodyLarge.copy(fontWeight = FontWeight.Bold)
val Typography.bodyMediumEmphasized: TextStyle get() = bodyMedium.copy(fontWeight = FontWeight.Bold)
val Typography.bodySmallEmphasized: TextStyle get() = bodySmall.copy(fontWeight = FontWeight.Bold)
val Typography.labelLargeEmphasized: TextStyle get() = labelLarge.copy(fontWeight = FontWeight.Bold)
val Typography.labelMediumEmphasized: TextStyle get() = labelMedium.copy(
    fontWeight = FontWeight.Bold,
)
val Typography.labelSmallEmphasized: TextStyle get() = labelSmall.copy(fontWeight = FontWeight.Bold)