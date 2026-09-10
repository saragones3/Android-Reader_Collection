/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import aragones.sergio.readercollection.presentation.components.ConfirmationAlertDialog
import aragones.sergio.readercollection.presentation.components.InformationAlertDialog
import aragones.sergio.readercollection.presentation.components.LaunchedEffectOnce
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionApp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.user_remove_confirmation

@Composable
fun FriendsView(
    onBookClick: (String, String) -> Unit,
    onShowAll: (String, String?, Boolean, String) -> Unit,
    viewModel: FriendsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val error by viewModel.error.collectAsState()
    val infoDialogMessageId by viewModel.infoDialogMessageId.collectAsState()
    val userDeletionMessage by viewModel.userDeletionMessage.collectAsState()

    ReaderCollectionApp(navigationBarColor = MaterialTheme.colorScheme.primary) {
        FriendsScreen(
            state = state,
            onSearch = viewModel::searchFriends,
            onSelectTab = viewModel::selectTab,
            onViewFriendLibrary = {
                viewModel.toggleLibrary(it)
            },
            onBookClick = onBookClick,
            onShowAll = onShowAll,
            onAcceptFriend = viewModel::acceptFriendRequest,
            onRejectFriend = viewModel::rejectFriendRequest,
            onDeleteFriend = {
                viewModel.showConfirmationDialog(Res.string.user_remove_confirmation, it)
            },
            onRequestFriend = viewModel::requestFriendship,
        )
    }

    ConfirmationAlertDialog(
        textId = userDeletionMessage?.first,
        onCancel = {
            viewModel.closeDialogs()
        },
        onAccept = {
            viewModel.closeDialogs()
            userDeletionMessage?.second?.let { viewModel.deleteFriend(it) }
        },
    )

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

    LaunchedEffectOnce {
        viewModel.fetchFriends()
    }
}