/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.compose.runtime.Immutable
import aragones.sergio.readercollection.domain.model.Book

sealed class FriendsUiState {

    data object Loading : FriendsUiState()

    data class Success(
        val tab: FriendsTab = FriendsTab.FRIENDS,
        val searchQuery: String = "",
        val friends: UsersUi,
        val requests: UsersUi,
        val isSearching: Boolean = false,
        val searchResults: UsersUi = UsersUi(),
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
    val isLoading: Boolean = false,
    val isExpanded: Boolean = false,
    val hasBooks: Boolean? = null,
    val readingBooks: List<Book> = emptyList(),
    val pendingBooks: List<Book> = emptyList(),
    val readBooks: List<Book> = emptyList(),
)

@Immutable
data class UsersUi(val users: List<UserUi> = emptyList())