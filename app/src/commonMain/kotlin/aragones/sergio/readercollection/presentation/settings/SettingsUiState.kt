/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 25/1/2026
 */

package aragones.sergio.readercollection.presentation.settings

data class SettingsUiState(
    val isProfilePublic: Boolean,
    val isAutomaticSyncEnabled: Boolean,
    val lastSynced: String,
    val language: String,
    val sortParam: String?,
    val isSortDescending: Boolean,
    val themeMode: Int,
    val version: String,
    val isLoading: Boolean,
) {
    companion object {
        fun empty(): SettingsUiState = SettingsUiState(
            isProfilePublic = true,
            isAutomaticSyncEnabled = true,
            lastSynced = "",
            language = "",
            sortParam = null,
            isSortDescending = false,
            themeMode = 0,
            version = "",
            isLoading = false,
        )
    }
}