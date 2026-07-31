/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.compose.runtime.Immutable

sealed class FriendsUiState {

    data object Loading : FriendsUiState()

    data class Success(
        val tab: FriendsTab = FriendsTab.FRIENDS,
        val friends: UsersUi,
        val requests: UsersUi,
    ) : FriendsUiState()
}

enum class FriendsTab {
    FRIENDS,
    REQUESTS,
}

data class UserUi(
    val id: String,
    val username: String,
    val isPending: Boolean,
)

@Immutable
data class UsersUi(val users: List<UserUi> = emptyList())