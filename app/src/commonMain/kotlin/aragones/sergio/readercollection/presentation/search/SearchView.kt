/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 12/1/2025
 */

package aragones.sergio.readercollection.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionApp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchView(
    onBookClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    ReaderCollectionApp {
        SearchScreen(
            state = state,
            onSearch = {
                viewModel.searchBooks(reload = true, query = it)
            },
            onFilter = viewModel::changeFilter,
            onBookClick = onBookClick,
            onLoadMoreClick = viewModel::searchBooks,
            onRefresh = {
                viewModel.searchBooks(reload = true)
            },
            onBack = onBack,
        )
    }
}