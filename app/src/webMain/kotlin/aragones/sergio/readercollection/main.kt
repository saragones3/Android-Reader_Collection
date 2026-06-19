/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

@file:Suppress("ktlint:standard:filename")

package aragones.sergio.readercollection

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import aragones.sergio.readercollection.presentation.MainView
import aragones.sergio.readercollection.presentation.MainViewModel
import aragones.sergio.readercollection.presentation.di.initKoin
import aragones.sergio.readercollection.presentation.landing.LandingView
import aragones.sergio.readercollection.presentation.landing.LandingViewModel
import aragones.sergio.readercollection.presentation.navigation.Navigator
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    ComposeViewport {
        App()
    }
}

@Composable
private fun App() {
    val coroutineScope = rememberCoroutineScope()
    var view by remember { mutableStateOf<View?>(View.LANDING) }
    val navigator = object : Navigator {
        override fun goToLanding() {
            view = View.LANDING
        }
        override fun goToMain(withOptions: Boolean) {
            if (withOptions) {
                view = View.MAIN
            } else {
                view = null
                coroutineScope.launch {
                    delay(1.seconds)
                    view = View.MAIN
                }
            }
        }
    }

    val movement = when (view) {
        View.LANDING -> -1
        View.MAIN -> 1
        null -> -1
    }
    AnimatedContent(
        targetState = view,
        transitionSpec = {
            slideInHorizontally { fullWidth -> fullWidth * movement } + fadeIn() togetherWith
                slideOutHorizontally { fullWidth -> -fullWidth * movement } + fadeOut()
        },
    ) {
        when (it) {
            View.LANDING -> {
                Landing(navigator)
            }
            View.MAIN -> {
                Main(navigator)
            }
            null -> {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun Landing(navigator: Navigator) {
    val viewModel = remember { getKoin().get<LandingViewModel>() }
    LandingView(
        navigator = navigator,
        viewModel = viewModel,
        skipAnimation = true,
        isAppUpdated = true,
    )
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteConfigValues()
    }
}

@Composable
private fun Main(navigator: Navigator, viewModel: MainViewModel = koinViewModel()) {
    MainView(
        navigator = navigator,
        viewModel = viewModel,
    )
}

private enum class View {
    LANDING,
    MAIN,
}
