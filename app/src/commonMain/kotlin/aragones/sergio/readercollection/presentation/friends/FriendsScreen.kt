/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/7/2025
 */

package aragones.sergio.readercollection.presentation.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDark
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.ListButton
import aragones.sergio.readercollection.presentation.components.MainActionButton
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.add_friend_action
import reader_collection.app.generated.resources.delete
import reader_collection.app.generated.resources.find_friends_action
import reader_collection.app.generated.resources.friends_tab_title
import reader_collection.app.generated.resources.friends_title
import reader_collection.app.generated.resources.go_to_add_new_friend
import reader_collection.app.generated.resources.image_no_friends
import reader_collection.app.generated.resources.no_friends_yet_subtitle
import reader_collection.app.generated.resources.no_friends_yet_title
import reader_collection.app.generated.resources.pending
import reader_collection.app.generated.resources.pending_status
import reader_collection.app.generated.resources.reject_friend_action
import reader_collection.app.generated.resources.rejected_status
import reader_collection.app.generated.resources.requests_tab_title

@Composable
fun FriendsScreen(
    state: FriendsUiState,
    onBack: () -> Unit,
    onSelectTab: (FriendsTab) -> Unit,
    onSelectFriend: (String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
    onAddFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        FriendsScreenToolbar(onBack = onBack)
        when (state) {
            FriendsUiState.Loading -> {
                CustomCircularProgressIndicator()
            }
            is FriendsUiState.Success -> {
                if (state.friends.users.isEmpty() && state.requests.users.isEmpty()) {
                    NoFriendsContent(onAddFriend)
                } else {
                    FriendsScreenContent(
                        selectedTab = state.tab,
                        friends = state.friends,
                        requests = state.requests,
                        onSelectTab = onSelectTab,
                        onSelectFriend = onSelectFriend,
                        onAcceptFriend = onAcceptFriend,
                        onRejectFriend = onRejectFriend,
                        onDeleteFriend = onDeleteFriend,
                        onAddFriend = onAddFriend,
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendsScreenToolbar(onBack: (() -> Unit)) {
    CustomToolbar(
        title = stringResource(Res.string.friends_title),
        backgroundColor = MaterialTheme.colorScheme.background,
        onBack = onBack,
    )
}

@Composable
private fun NoFriendsContent(onAddFriend: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.image_no_friends),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.no_friends_yet_title),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.no_friends_yet_subtitle),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        MainActionButton(
            text = stringResource(Res.string.find_friends_action),
            enabled = true,
            onClick = onAddFriend,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(24.dp),
        )
    }
}

@Composable
private fun FriendsScreenContent(
    selectedTab: FriendsTab,
    friends: UsersUi,
    requests: UsersUi,
    onSelectTab: (FriendsTab) -> Unit,
    onSelectFriend: (String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
    onAddFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
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
                    onSelectFriend = onSelectFriend,
                    onAcceptFriend = onAcceptFriend,
                    onRejectFriend = onRejectFriend,
                )
                FriendsTab.REQUESTS -> RequestsTabContent(
                    requests = requests,
                    onDeleteFriend = onDeleteFriend,
                )
            }
        }
        ListButton(
            painter = rememberVectorPainter(Icons.Default.PersonAddAlt1)
                .withDescription(stringResource(Res.string.go_to_add_new_friend)),
            onClick = onAddFriend,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
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
                        style = MaterialTheme.typography.displaySmall,
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
                            style = MaterialTheme.typography.displaySmall,
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
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
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
    onSelectFriend: (String) -> Unit,
    onAcceptFriend: (String) -> Unit,
    onRejectFriend: (String) -> Unit,
) {
    if (friends.users.any { it.isPending }) {
        item {
            Text(
                text = stringResource(Res.string.pending).uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
        }
        items(friends.users.filter { it.isPending }, key = { it.id }) { friend ->
            FriendItem(
                friend = friend,
                onSelectFriend = { onSelectFriend(friend.id) },
                onAcceptFriend = { onAcceptFriend(friend.id) },
                onRejectFriend = { onRejectFriend(friend.id) },
            )
        }
        item {
            Spacer(Modifier.height(16.dp))
        }
    }
    items(friends.users.filter { !it.isPending }, key = { it.id }) { friend ->
        FriendItem(
            friend = friend,
            onSelectFriend = { onSelectFriend(friend.id) },
            onAcceptFriend = { onAcceptFriend(friend.id) },
            onRejectFriend = { onRejectFriend(friend.id) },
        )
    }
}

private fun LazyListScope.RequestsTabContent(requests: UsersUi, onDeleteFriend: (String) -> Unit) {
    items(requests.users, key = { it.id }) { friend ->
        RequestItem(
            friend = friend,
            onDeleteFriend = { onDeleteFriend(friend.id) },
        )
    }
}

@Composable
private fun FriendItem(
    friend: UserUi,
    onSelectFriend: () -> Unit,
    onAcceptFriend: () -> Unit,
    onRejectFriend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        MainUserInfo(
            username = friend.username,
            modifier = Modifier.clickable {
                if (!friend.isPending) {
                    onSelectFriend()
                }
            },
            subtitleContent = {
                if (friend.isPending) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.pending_status),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        lineHeight = 24.sp,
                    )
                }
            },
        )
        if (friend.isPending) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = onAcceptFriend,
                    modifier = Modifier.widthIn(max = 320.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Text(
                        text = stringResource(Res.string.add_friend_action),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 1,
                    )
                }
                Button(
                    onClick = onRejectFriend,
                    modifier = Modifier.widthIn(max = 320.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(
                        text = stringResource(Res.string.reject_friend_action),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onError,
                        maxLines = 1,
                    )
                }
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        MainUserInfo(
            username = friend.username,
            subtitleContent = {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        if (friend.isPending) {
                            stringResource(Res.string.pending_status)
                        } else {
                            stringResource(Res.string.rejected_status)
                        },
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 14.sp,
                    color =
                        if (friend.isPending) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    lineHeight = 24.sp,
                )
            },
            extraContent = {
                if (!friend.isPending) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = onDeleteFriend,
                        modifier = Modifier.widthIn(max = 320.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Text(
                            text = stringResource(Res.string.delete),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            maxLines = 2,
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun MainUserInfo(
    username: String,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null,
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
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 24.sp,
            )
            subtitleContent?.invoke()
        }
        Spacer(modifier = Modifier.width(4.dp))
        extraContent?.invoke()
    }
}

@CustomPreviewLightDark
@Composable
private fun FriendsScreenPreview(
    @PreviewParameter(FriendsScreenPreviewParameterProvider::class) state: FriendsUiState,
) {
    ReaderCollectionTheme {
        FriendsScreen(
            state = state,
            onBack = {},
            onSelectTab = {},
            onSelectFriend = {},
            onAcceptFriend = {},
            onRejectFriend = {},
            onDeleteFriend = {},
            onAddFriend = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}

private class FriendsScreenPreviewParameterProvider : PreviewParameterProvider<FriendsUiState> {

    override val values: Sequence<FriendsUiState>
        get() = sequenceOf(
            FriendsUiState.Success(
                tab = FriendsTab.FRIENDS,
                friends = UsersUi(
                    listOf(
                        UserUi(
                            id = "1",
                            username = "User 1",
                            isPending = false,
                        ),
                        UserUi(
                            id = "2",
                            username = "user with a long name",
                            isPending = false,
                        ),
                        UserUi(
                            id = "3",
                            username =
                                """
                                User with a very long name
                                that will have to be fitted in two lines
                                """.trimIndent(),
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
            FriendsUiState.Loading,
        )
}