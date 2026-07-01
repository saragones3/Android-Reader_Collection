/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

actual object AppUiProvider {
    @Composable
    actual fun isDarkThemeApplied(): Boolean = isSystemInDarkTheme()

    @Composable
    actual fun applyBarsStyle(
        isDarkTheme: Boolean,
        colors: ColorScheme,
        statusBarSameAsBackground: Boolean,
        navigationBarSameAsBackground: Boolean,
    ) {}

    @Composable
    actual fun isPortrait(): Boolean {
        val windowInfo = LocalWindowInfo.current
        return windowInfo.containerSize.height > windowInfo.containerSize.width
    }

    @Composable
    actual fun getScreenWidth(): Int {
        val density = LocalDensity.current
        val windowInfo = LocalWindowInfo.current
        return with(density) {
            windowInfo.containerSize.width
                .toDp()
                .value
                .toInt()
        }
    }

    @Composable
    actual fun launchWorker() {}

    @Composable
    actual fun cancelWorker() {}
}