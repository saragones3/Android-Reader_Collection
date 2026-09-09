/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.presentation.components.BooksSection
import aragones.sergio.readercollection.presentation.components.CustomCard
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDarkLong
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.MainIconButton
import aragones.sergio.readercollection.presentation.components.SearchBar
import aragones.sergio.readercollection.presentation.components.SecondaryButton
import aragones.sergio.readercollection.presentation.components.SecondaryIconButton
import aragones.sergio.readercollection.presentation.components.getBoldTextFor
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.bodyLargeEmphasized
import aragones.sergio.readercollection.presentation.theme.labelSmallEmphasized
import aragones.sergio.readercollection.presentation.theme.titleLargeEmphasized
import aragones.sergio.readercollection.presentation.theme.titleSmallEmphasized
import com.aragones.sergio.util.BookState
import com.aragones.sergio.util.Constants
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.add_friend_action
import reader_collection.app.generated.resources.community
import reader_collection.app.generated.resources.community_description
import reader_collection.app.generated.resources.delete
import reader_collection.app.generated.resources.friends_tab_title
import reader_collection.app.generated.resources.friends_title
import reader_collection.app.generated.resources.hide_library_action
import reader_collection.app.generated.resources.image_no_friends
import reader_collection.app.generated.resources.no_friends_found
import reader_collection.app.generated.resources.no_friends_yet_subtitle
import reader_collection.app.generated.resources.no_friends_yet_title
import reader_collection.app.generated.resources.pending
import reader_collection.app.generated.resources.pending_status
import reader_collection.app.generated.resources.read
import reader_collection.app.generated.resources.reading
import reader_collection.app.generated.resources.reject_friend_action
import reader_collection.app.generated.resources.rejected_status
import reader_collection.app.generated.resources.requests_tab_title
import reader_collection.app.generated.resources.results_for
import reader_collection.app.generated.resources.send_request
import reader_collection.app.generated.resources.view_library_action

@Composable
fun FriendsScreen(
    state: FriendsUiState,
    onSearch: (String) -> Unit,
    onSelectTab: (FriendsTab) -> Unit,
    onViewFriendLibrary: (String) -> Unit,
    onBookClick: (String, String) -> Unit,
    onShowAll: (String, String?, Boolean, String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
    onRequestFriend: (UserUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        FriendsScreenToolbar()
        when (state) {
            FriendsUiState.Loading -> {
                CustomCircularProgressIndicator()
            }
            is FriendsUiState.Success -> {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                ) {
                    item {
                        Header(
                            query = state.searchQuery,
                            onSearch = onSearch,
                            modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }
                    if (state.searchQuery.isNotEmpty()) {
                        if (state.isSearching) {
                            item {
                                CustomCircularProgressIndicator()
                            }
                        } else {
                            SearchResultsContent(
                                query = state.searchQuery,
                                results = state.searchResults,
                                onRequestFriend = onRequestFriend,
                            )
                        }
                    } else if (state.friends.users.isEmpty() && state.requests.users.isEmpty()) {
                        NoFriendsContent()
                    } else {
                        FriendsScreenContent(
                            selectedTab = state.tab,
                            friends = state.friends,
                            requests = state.requests,
                            onSelectTab = onSelectTab,
                            onViewFriendLibrary = onViewFriendLibrary,
                            onBookClick = onBookClick,
                            onShowAll = onShowAll,
                            onAcceptFriend = onAcceptFriend,
                            onRejectFriend = onRejectFriend,
                            onDeleteFriend = onDeleteFriend,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendsScreenToolbar() {
    CustomToolbar(
        title = stringResource(Res.string.friends_title),
        backgroundColor = MaterialTheme.colorScheme.background,
    )
}

@Composable
private fun Header(query: String, onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.community),
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.community_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        SearchBar(
            text = query,
            onSearch = onSearch,
            modifier = Modifier.fillMaxWidth(),
            showLeadingIcon = true,
            requestFocusByDefault = false,
            searchOnClear = true,
        )
    }
}

private fun LazyListScope.NoFriendsContent() {
    item {
        Image(
            painter = painterResource(Res.drawable.image_no_friends),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
    item {
        Spacer(Modifier.height(24.dp))
    }
    item {
        Text(
            text = stringResource(Res.string.no_friends_yet_title),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleLargeEmphasized,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
    }
    item {
        Spacer(Modifier.height(12.dp))
    }
    item {
        Text(
            text = stringResource(Res.string.no_friends_yet_subtitle),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
    }
}

private fun LazyListScope.FriendsScreenContent(
    selectedTab: FriendsTab,
    friends: UsersUi,
    requests: UsersUi,
    onSelectTab: (FriendsTab) -> Unit,
    onViewFriendLibrary: (String) -> Unit,
    onBookClick: (String, String) -> Unit,
    onShowAll: (String, String?, Boolean, String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
) {
    if (friends.users.isNotEmpty() && requests.users.isNotEmpty()) {
        item {
            FriendsTabRow(
                selectedTab = selectedTab,
                requestsCount = requests.users.size,
                onTabSelected = onSelectTab,
            )
        }
    }
    when (selectedTab) {
        FriendsTab.FRIENDS -> FriendsTabContent(
            friends = friends,
            onViewFriendLibrary = onViewFriendLibrary,
            onBookClick = onBookClick,
            onShowAll = onShowAll,
            onAcceptFriend = onAcceptFriend,
            onRejectFriend = onRejectFriend,
            onDeleteFriend = onDeleteFriend,
        )
        FriendsTab.REQUESTS -> RequestsTabContent(
            requests = requests,
            onDeleteFriend = onDeleteFriend,
        )
    }
}

@Composable
private fun FriendsTabRow(
    selectedTab: FriendsTab,
    requestsCount: Int,
    onTabSelected: (FriendsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    SecondaryTabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTab.ordinal),
                height = 2.dp,
                color = MaterialTheme.colorScheme.primary,
            )
        },
        tabs = {
            Tab(
                selected = selectedTab == FriendsTab.FRIENDS,
                onClick = { onTabSelected(FriendsTab.FRIENDS) },
                text = {
                    Text(
                        text = stringResource(Res.string.friends_tab_title),
                        style = MaterialTheme.typography.titleSmallEmphasized,
                    )
                },
            )
            Tab(
                selected = selectedTab == FriendsTab.REQUESTS,
                onClick = { onTabSelected(FriendsTab.REQUESTS) },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.requests_tab_title),
                            style = MaterialTheme.typography.titleSmallEmphasized,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ) {
                            Text(
                                text = requestsCount.toString(),
                                style = MaterialTheme.typography.labelSmallEmphasized,
                            )
                        }
                    }
                },
            )
        },
    )
}

private fun LazyListScope.FriendsTabContent(
    friends: UsersUi,
    onViewFriendLibrary: (String) -> Unit,
    onBookClick: (String, String) -> Unit,
    onShowAll: (String, String?, Boolean, String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
) {
    if (friends.users.any { it.isPending }) {
        item {
            Text(
                text = stringResource(Res.string.pending).uppercase(),
                style = MaterialTheme.typography.bodyLargeEmphasized,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
        }
        items(friends.users.filter { it.isPending }, key = { it.id }) { friend ->
            FriendContainer {
                FriendItem(
                    friend = friend,
                    onViewLibrary = {},
                    onBookClick = { _, _ -> },
                    onShowAll = { _, _, _, _ -> },
                    onAcceptFriend = { onAcceptFriend(friend.id) },
                    onRejectFriend = { onRejectFriend(friend.id) },
                    onDeleteFriend = {},
                )
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
    }
    items(friends.users.filter { !it.isPending }, key = { it.id }) { friend ->
        FriendContainer {
            FriendItem(
                friend = friend,
                onViewLibrary = { onViewFriendLibrary(friend.id) },
                onBookClick = onBookClick,
                onShowAll = onShowAll,
                onAcceptFriend = {},
                onRejectFriend = {},
                onDeleteFriend = { onDeleteFriend(friend.id) },
            )
        }
    }
}

private fun LazyListScope.RequestsTabContent(requests: UsersUi, onDeleteFriend: (String) -> Unit) {
    items(requests.users, key = { it.id }) { friend ->
        FriendContainer {
            RequestItem(
                friend = friend,
                onDeleteFriend = { onDeleteFriend(friend.id) },
            )
        }
    }
}

private fun LazyListScope.SearchResultsContent(
    query: String,
    results: UsersUi,
    onRequestFriend: (UserUi) -> Unit,
) {
    if (results.users.isEmpty()) {
        item {
            Text(
                text = getBoldTextFor(
                    text = stringResource(Res.string.no_friends_found, query),
                    placeholder = "\"" + query + "\"",
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    } else {
        item {
            Text(
                text = stringResource(Res.string.results_for, query),
                style = MaterialTheme.typography.titleSmallEmphasized,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        item {
            Spacer(Modifier.height(12.dp))
        }
        items(results.users, key = { it.id }) { result ->
            FriendContainer {
                SearchResultItem(
                    user = result,
                    onRequestFriend = { onRequestFriend(result) },
                )
            }
        }
    }
}

@Composable
private fun FriendContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CustomCard(
        modifier = modifier,
        contentModifier = Modifier.fillMaxWidth(),
        content = content,
    )
}

@Composable
private fun FriendItem(
    friend: UserUi,
    onViewLibrary: () -> Unit,
    onBookClick: (String, String) -> Unit,
    onShowAll: (String, String?, Boolean, String) -> Unit,
    onAcceptFriend: () -> Unit,
    onRejectFriend: () -> Unit,
    onDeleteFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MainUserInfo(
        username = friend.username,
        modifier = modifier,
        endContent = {
            if (friend.isPending) {
                SecondaryIconButton(
                    rememberVectorPainter(Icons.Default.Close)
                        .withDescription(stringResource(Res.string.reject_friend_action)),
                    onClick = onRejectFriend,
                )
                MainIconButton(
                    rememberVectorPainter(Icons.Default.Check)
                        .withDescription(stringResource(Res.string.add_friend_action)),
                    onClick = onAcceptFriend,
                )
            } else {
                IconButton(onClick = onDeleteFriend) {
                    Icon(
                        imageVector = Icons.Default.PersonRemove,
                        contentDescription = stringResource(Res.string.delete),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        },
    )
    val showLibraryButton = friend.hasBooks != false
    if (!friend.isPending && friend.hasBooks != false) {
        Spacer(modifier = Modifier.height(8.dp))
        SecondaryButton(
            text = stringResource(
                if (friend.isExpanded) {
                    Res.string.hide_library_action
                } else {
                    Res.string.view_library_action
                },
            ),
            onClick = onViewLibrary.takeIf { showLibraryButton } ?: {},
            modifier = Modifier.fillMaxWidth(),
            startPainter = rememberVectorPainter(
                Icons.AutoMirrored.Filled.LibraryBooks,
            ).withDescription(null),
            endPainter = rememberVectorPainter(
                if (friend.isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
            ).withDescription(null).takeIf { showLibraryButton },
        )
        AnimatedVisibility(
            visible = friend.isExpanded && showLibraryButton,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            Column {
                Spacer(Modifier.height(16.dp))
                BooksSection(
                    title = stringResource(Res.string.reading),
                    books = Books(friend.readingBooks),
                    onShowAll = {
                        onShowAll(BookState.READING, null, false, friend.id)
                    }.takeIf { friend.readingBooks.size > Constants.BOOKS_TO_SHOW },
                    onBookClick = {
                        onBookClick(it, friend.id)
                    },
                )
                BooksSection(
                    title = stringResource(Res.string.pending),
                    books = Books(friend.pendingBooks),
                    onShowAll = {
                        onShowAll(BookState.PENDING, null, false, friend.id)
                    }.takeIf { friend.pendingBooks.size > Constants.BOOKS_TO_SHOW },
                    onBookClick = {
                        onBookClick(it, friend.id)
                    },
                )
                BooksSection(
                    title = stringResource(Res.string.read),
                    books = Books(friend.readBooks),
                    onShowAll = {
                        onShowAll(BookState.READ, "readingDate", true, friend.id)
                    }.takeIf { friend.readBooks.size > Constants.BOOKS_TO_SHOW },
                    onBookClick = {
                        onBookClick(it, friend.id)
                    },
                    showDivider = false,
                )
            }
        }
    }
}

@Composable
private fun RequestItem(
    friend: UserUi,
    onDeleteFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MainUserInfo(
        username = friend.username,
        modifier = modifier,
        subtitleContent = {
            Text(
                text =
                    if (friend.isPending) {
                        stringResource(Res.string.pending_status)
                    } else {
                        stringResource(Res.string.rejected_status)
                    },
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (friend.isPending) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                lineHeight = 24.sp,
            )
        },
        endContent = {
            if (!friend.isPending) {
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryIconButton(
                    rememberVectorPainter(Icons.Default.Delete)
                        .withDescription(stringResource(Res.string.delete)),
                    onClick = onDeleteFriend,
                )
            }
        },
    )
}

@Composable
private fun SearchResultItem(
    user: UserUi,
    onRequestFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MainUserInfo(
        username = user.username,
        modifier = modifier,
        endContent = {
            if (user.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Button(
                    onClick = onRequestFriend,
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.5f,
                        ),
                    ),
                ) {
                    Text(
                        text = stringResource(Res.string.send_request),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
        },
    )
}

@Composable
private fun MainUserInfo(
    username: String,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = username,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 24.sp,
            )
            if (subtitleContent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                subtitleContent.invoke()
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        endContent?.invoke()
    }
}

@CustomPreviewLightDarkLong
@Composable
private fun FriendsScreenPreview(
    @PreviewParameter(FriendsScreenPreviewParameterProvider::class) state: FriendsUiState,
) {
    ReaderCollectionTheme {
        FriendsScreen(
            state = state,
            onSearch = {},
            onSelectTab = {},
            onViewFriendLibrary = {},
            onBookClick = { _, _ -> },
            onShowAll = { _, _, _, _ -> },
            onAcceptFriend = {},
            onRejectFriend = {},
            onDeleteFriend = {},
            onRequestFriend = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}

private class FriendsScreenPreviewParameterProvider : PreviewParameterProvider<FriendsUiState> {

    override val values: Sequence<FriendsUiState>
        get() = sequenceOf(
            FriendsUiState.Success(
                tab = FriendsTab.FRIENDS,
                searchQuery = "",
                friends = UsersUi(
                    listOf(
                        UserUi(
                            id = "1",
                            username = "User 1",
                            isPending = false,
                        ),
                        UserUi(
                            id = "2",
                            username = "user with books",
                            isPending = false,
                            isExpanded = true,
                            hasBooks = true,
                            readingBooks = listOf(
                                Book("1").copy(
                                    title = "Reading book",
                                    state = BookState.READING,
                                ),
                            ),
                            pendingBooks = listOf(
                                Book("2").copy(
                                    title = "Pending book",
                                    state = BookState.PENDING,
                                ),
                            ),
                            readBooks = listOf(
                                Book("3").copy(
                                    title = "Read book",
                                    state = BookState.READ,
                                ),
                            ),
                        ),
                        UserUi(
                            id = "3",
                            username = "user without books",
                            isPending = false,
                            hasBooks = false,
                        ),
                        UserUi(
                            id = "4",
                            username = "User with a very long name",
                            isPending = true,
                        ),
                    ),
                ),
                requests = UsersUi(
                    listOf(
                        UserUi(
                            id = "4",
                            username = "User",
                            isPending = true,
                        ),
                        UserUi(
                            id = "5",
                            username = "User",
                            isPending = false,
                        ),
                    ),
                ),
            ),
            FriendsUiState.Success(
                tab = FriendsTab.REQUESTS,
                searchQuery = "",
                friends = UsersUi(),
                requests = UsersUi(
                    listOf(
                        UserUi(
                            id = "4",
                            username = "User",
                            isPending = true,
                        ),
                        UserUi(
                            id = "5",
                            username = "User",
                            isPending = false,
                        ),
                    ),
                ),
            ),
            FriendsUiState.Success(
                tab = FriendsTab.FRIENDS,
                searchQuery = "amigo123",
                friends = UsersUi(),
                requests = UsersUi(),
                searchResults = UsersUi(
                    listOf(
                        UserUi(
                            id = "1",
                            username = "amigo123",
                            isPending = false,
                        ),
                    ),
                ),
            ),
            FriendsUiState.Success(
                tab = FriendsTab.FRIENDS,
                searchQuery = "amigo123",
                friends = UsersUi(),
                requests = UsersUi(),
                searchResults = UsersUi(),
            ),
            FriendsUiState.Success(
                friends = UsersUi(),
                requests = UsersUi(),
            ),
            FriendsUiState.Loading,
        )
}