/*
 * Copyright (c) 2024 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 10/4/2024
 */

package aragones.sergio.readercollection.presentation.search

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.domain.model.Books
import aragones.sergio.readercollection.domain.model.ErrorModel
import aragones.sergio.readercollection.presentation.components.CustomFilterChip
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDark
import aragones.sergio.readercollection.presentation.components.CustomSearchBar
import aragones.sergio.readercollection.presentation.components.EmptyStateCard
import aragones.sergio.readercollection.presentation.components.ErrorStateCard
import aragones.sergio.readercollection.presentation.components.ListButton
import aragones.sergio.readercollection.presentation.components.NoResultsStateCard
import aragones.sergio.readercollection.presentation.components.VerticalBookItem
import aragones.sergio.readercollection.presentation.components.getBoldTextFor
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.enter_author
import reader_collection.app.generated.resources.enter_title
import reader_collection.app.generated.resources.error_server
import reader_collection.app.generated.resources.filters
import reader_collection.app.generated.resources.go_to_end
import reader_collection.app.generated.resources.go_to_start
import reader_collection.app.generated.resources.load_more
import reader_collection.app.generated.resources.no_search_yet_text
import reader_collection.app.generated.resources.results_for
import reader_collection.app.generated.resources.search_results_count
import reader_collection.app.generated.resources.title_search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onSearch: (String) -> Unit,
    onFilter: (SearchParam) -> Unit,
    onBookClick: (String) -> Unit,
    onLoadMoreClick: () -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()
    val showTopButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0
        }
    }
    val showBottomButton by remember {
        derivedStateOf {
            !listState.reachedBottom()
        }
    }
    val coroutineScope = rememberCoroutineScope()

    val (isLoading, query) = when (state) {
        SearchUiState.Empty -> false to null
        is SearchUiState.Success -> state.isLoading to state.query
        is SearchUiState.Error -> state.isLoading to state.query
    }

    val pullRefreshState = rememberPullToRefreshState()

    val elevation = if (showTopButton && !isLoading) 4.dp else 0.dp

    val searchPlaceholder = when (state.param) {
        SearchParam.TITLE -> stringResource(Res.string.enter_title)
        SearchParam.AUTHOR -> stringResource(Res.string.enter_author)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        CustomSearchBar(
            title = stringResource(Res.string.title_search),
            placeholder = searchPlaceholder,
            query = query ?: "",
            onSearch = onSearch,
            modifier = Modifier.shadow(elevation),
            backgroundColor = MaterialTheme.colorScheme.background,
            onBack = onBack,
        )

        Filters(
            selectedParam = state.param,
            onSelectParam = {
                onFilter(it)
            },
        )

        val modifier = if (query != null) {
            Modifier.pullToRefresh(
                isRefreshing = isLoading,
                state = pullRefreshState,
                onRefresh = onRefresh,
            )
        } else {
            Modifier
        }

        Box(
            modifier = modifier.fillMaxSize(),
        ) {
            when (state) {
                is SearchUiState.Empty -> {
                    NoResultsContent(query = "")
                }
                is SearchUiState.Success -> {
                    if (state.books.books.isEmpty() && !state.isLoading) {
                        NoResultsContent(query = state.query ?: "")
                    } else {
                        SearchContent(
                            books = state.books,
                            query = query ?: "",
                            listState = listState,
                            showTopButton = showTopButton && !isLoading,
                            showBottomButton = showBottomButton && !isLoading,
                            onTopButtonClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(index = 0)
                                }
                            },
                            onBottomButtonClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(
                                        index = listState.layoutInfo.totalItemsCount - 1,
                                    )
                                }
                            },
                            onBookClick = onBookClick,
                            onLoadMoreClick = onLoadMoreClick,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                is SearchUiState.Error -> {
                    ErrorContent(onRetry = onRefresh)
                }
            }
            PullToRefreshBox(
                isRefreshing = isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullRefreshState,
                contentAlignment = Alignment.TopCenter,
                indicator = {
                    Indicator(
                        state = pullRefreshState,
                        isRefreshing = isLoading,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = MaterialTheme.colorScheme.primary,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                },
                content = {},
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Filters(
    selectedParam: SearchParam,
    onSelectParam: (SearchParam) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.filters),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
        )
        for (entry in SearchParam.entries) {
            CustomFilterChip(
                title = stringResource(entry.value),
                selected = selectedParam == entry,
                onClick = { onSelectParam(entry) },
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                ),
                selectedIcon = rememberVectorPainter(entry.icon)
                    .withDescription(null),
            )
        }
    }
}

@Composable
private fun NoResultsContent(query: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            if (query.isNotEmpty()) {
                NoResultsStateCard(query)
            } else {
                EmptyStateCard(subtitle = stringResource(Res.string.no_search_yet_text))
            }
        }
    }
}

@Composable
private fun ErrorContent(onRetry: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        item {
            ErrorStateCard(onRetry = onRetry)
        }
    }
}

@Composable
private fun SearchContent(
    books: Books,
    query: String,
    listState: LazyGridState,
    showTopButton: Boolean,
    showBottomButton: Boolean,
    onTopButtonClick: () -> Unit,
    onBottomButtonClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onLoadMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bookItems = books.books.filter { it.id.isNotBlank() }
    Box(modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            stickyHeader {
                ResultsTitle(
                    query = query,
                    resultsCount = bookItems.size,
                )
            }
            itemsIndexed(
                items = bookItems,
                key = { index, book -> "$index-${book.id}" },
            ) { _, book ->
                VerticalBookItem(
                    book = book,
                    isSwitchLeftIconEnabled = false,
                    isSwitchRightIconEnabled = false,
                    onClick = { onBookClick(book.id) },
                    onSwitchToLeft = {},
                    onSwitchToRight = {},
                    onLongClick = {},
                )
            }
            books.books.firstOrNull { it.id.isBlank() }?.let {
                item(span = { GridItemSpan(2) }) {
                    LoadMoreButton(onLoadMoreClick)
                }
            }
            item(span = { GridItemSpan(2) }) {
                Spacer(Modifier.height(8.dp))
            }
        }

        val topOffset by animateFloatAsState(
            targetValue = if (showTopButton) 0f else 200f,
            label = "",
        )
        val bottomOffset by animateFloatAsState(
            targetValue = if (showBottomButton) 0f else 200f,
            label = "",
        )

        ListButton(
            painter = rememberVectorPainter(Icons.Default.KeyboardDoubleArrowUp)
                .withDescription(stringResource(Res.string.go_to_start)),
            onClick = onTopButtonClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(topOffset.toInt(), 0) },
        )

        ListButton(
            painter = rememberVectorPainter(Icons.Default.KeyboardDoubleArrowDown)
                .withDescription(stringResource(Res.string.go_to_end)),
            onClick = onBottomButtonClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset { IntOffset(bottomOffset.toInt(), 0) },
        )
    }
}

@Composable
private fun ResultsTitle(query: String, resultsCount: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = getBoldTextFor(
                    text = stringResource(Res.string.results_for, query),
                    placeholder = "\"" + query + "\"",
                ),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = getBoldTextFor(
                    text = pluralStringResource(
                        Res.plurals.search_results_count,
                        resultsCount,
                        resultsCount,
                    ),
                    placeholder = resultsCount.toString(),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
            )
        }
        Spacer(Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun LoadMoreButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(12.dp).widthIn(max = 320.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = rememberVectorPainter(Icons.Default.AddCircle),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(Res.string.load_more),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
            )
        }
    }
}

private fun LazyGridState.reachedBottom(buffer: Int = 1): Boolean {
    val lastVisibleItem = this.layoutInfo.visibleItemsInfo.lastOrNull()
    return lastVisibleItem?.index != 0 &&
        lastVisibleItem?.index == this.layoutInfo.totalItemsCount - buffer
}

@CustomPreviewLightDark
@Composable
private fun SearchScreenPreview(
    @PreviewParameter(SearchScreenPreviewParameterProvider::class) state: SearchUiState,
) {
    ReaderCollectionTheme {
        SearchScreen(
            state = state,
            onSearch = {},
            onFilter = {},
            onBookClick = {},
            onLoadMoreClick = {},
            onRefresh = {},
            onBack = {},
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
        )
    }
}

private class SearchScreenPreviewParameterProvider :
    PreviewParameterProvider<SearchUiState> {

    override val values: Sequence<SearchUiState>
        get() = sequenceOf(
            SearchUiState.Success(
                books = Books(
                    listOf(
                        Book("1").copy(
                            title = "Book 1",
                            authors = listOf("Author"),
                            rating = 6.0,
                        ),
                        Book("2").copy(
                            title = "Book 2",
                            authors = listOf("Author"),
                            rating = 5.0,
                        ),
                        Book("3").copy(
                            title = "Book 3",
                        ),
                        Book("").copy(
                            title = "",
                        ),
                    ),
                ),
                isLoading = true,
                query = "Title to search books",
                param = SearchParam.TITLE,
            ),
            SearchUiState.Success(
                books = Books(),
                isLoading = false,
                query = null,
                param = SearchParam.AUTHOR,
            ),
            SearchUiState.Empty,
            SearchUiState.Error(
                isLoading = false,
                query = null,
                value = ErrorModel("", Res.string.error_server),
                param = SearchParam.TITLE,
            ),
        )
}