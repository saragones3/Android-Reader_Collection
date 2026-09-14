/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/1/2026
 */

package aragones.sergio.readercollection.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

expect object AppUiProvider {
    @Composable
    fun isDarkThemeApplied(): Boolean

    @Composable
    fun applyBarsStyle(statusBarColor: Color, navigationBarColor: Color)

    @Composable
    fun isPortrait(): Boolean

    @Composable
    fun getScreenWidth(): Int

    @Composable
    fun launchWorker()

    @Composable
    fun cancelWorker()
}