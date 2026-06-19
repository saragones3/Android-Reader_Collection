/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package aragones.sergio.readercollection.data.local

actual class AppInfoProvider {
    actual fun getVersion(): String? = WEB_APP_VERSION
    actual fun getCurrentLanguage(): String =
        getBrowserLanguage().split("-", "_").first().lowercase()
    actual fun changeLocale(language: String) {}
    actual fun applyTheme(themeMode: Int) {}
}

@JsFun("() => window.navigator.language")
external fun getBrowserLanguage(): String
