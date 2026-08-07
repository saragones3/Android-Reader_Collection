/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/1/2025
 */

package aragones.sergio.readercollection.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import aragones.sergio.readercollection.presentation.components.ConfirmationAlertDialog
import aragones.sergio.readercollection.presentation.components.InformationAlertDialog
import aragones.sergio.readercollection.presentation.theme.AppUiProvider.cancelWorker
import aragones.sergio.readercollection.presentation.theme.AppUiProvider.launchWorker
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionApp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.profile_logout_confirmation
import reader_collection.app.generated.resources.public_profile_disable_confirmation
import reader_collection.app.generated.resources.sync_confirmation

@Composable
fun SettingsView(
    onClickOption: (SettingsOption) -> Unit,
    onRelaunch: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val infoDialogMessageId by viewModel.infoDialogMessageId.collectAsState()
    val confirmationMessageId by viewModel.confirmationDialogMessageId.collectAsState()
    val error by viewModel.error.collectAsState()

    val relaunch by viewModel.relaunch.collectAsState()
    if (relaunch) {
        onRelaunch()
        return
    }

    val logOut by viewModel.logOut.collectAsState()
    if (logOut) {
        onClickOption(SettingsOption.Logout)
        return
    }

    ReaderCollectionApp(navigationBarSameAsBackground = false) {
        SettingsScreen(
            state = state,
            onClickOption = {
                when (it) {
                    is SettingsOption.Account,
                    is SettingsOption.Friends,
                    is SettingsOption.DataSync,
                    is SettingsOption.DisplaySettings,
                    -> onClickOption(it)
                    is SettingsOption.Logout -> viewModel.showConfirmationDialog(
                        Res.string.profile_logout_confirmation,
                    )
                }
            },
            onChangePublicProfile = { enable ->
                if (enable) {
                    viewModel.setPublicProfile(true)
                } else {
                    viewModel.showConfirmationDialog(Res.string.public_profile_disable_confirmation)
                }
            },
            onAutoSyncChange = viewModel::changeAutomaticSync,
            onSync = {
                viewModel.showConfirmationDialog(Res.string.sync_confirmation)
            },
            onDisplaySettingsChanged = viewModel::displaySettingsChanged,
        )
    }

    if (state.isAutomaticSyncEnabled) {
        launchWorker()
    } else {
        cancelWorker()
    }

    val text = if (error != null) {
        val errorText = StringBuilder()
        if (requireNotNull(error).error.isNotEmpty()) {
            errorText.append(requireNotNull(error).error)
        } else {
            errorText.append(stringResource(requireNotNull(error).errorKey))
        }
        errorText.toString()
    } else if (infoDialogMessageId != null) {
        stringResource(requireNotNull(infoDialogMessageId))
    } else {
        ""
    }
    InformationAlertDialog(show = text.isNotEmpty(), text = text) {
        viewModel.closeDialogs()
    }

    ConfirmationAlertDialog(
        textId = confirmationMessageId,
        onCancel = {
            viewModel.closeDialogs()
        },
        onAccept = {
            when (confirmationMessageId) {
                Res.string.public_profile_disable_confirmation -> {
                    viewModel.setPublicProfile(false)
                }
                Res.string.sync_confirmation -> {
                    viewModel.syncData()
                }
                Res.string.profile_logout_confirmation -> {
                    viewModel.logout()
                }
                null -> {
                    /*no-op*/
                }
            }
            viewModel.closeDialogs()
        },
    )

    LaunchedEffect(Unit) {
        viewModel.onResume()
    }
}