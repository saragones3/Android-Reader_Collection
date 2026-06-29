/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.presentation.statistics

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.data.remote.model.FORMATS
import aragones.sergio.readercollection.data.remote.model.GENRES
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.presentation.LocalLanguage
import aragones.sergio.readercollection.presentation.components.BarChart
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDark
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.HorizontalBarChart
import aragones.sergio.readercollection.presentation.components.NoResultsComponent
import aragones.sergio.readercollection.presentation.components.PieChart
import aragones.sergio.readercollection.presentation.components.VerticalBookItem
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.utils.UiDateMapper
import com.aragones.sergio.util.BookState
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.formats
import reader_collection.app.generated.resources.longer_book
import reader_collection.app.generated.resources.months
import reader_collection.app.generated.resources.shorter_book
import reader_collection.app.generated.resources.title_books_count
import reader_collection.app.generated.resources.title_stats

@Composable
actual fun StatisticsScreen(
    state: StatisticsUiState,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize()) {
        StatisticsToolbar(
            state = state,
            scrollState = scrollState,
            onImportClick = onImportClick,
            onExportClick = onExportClick,
        )
        StatisticsContent(
            state = state,
            scrollState = scrollState,
            onGroupClick = onGroupClick,
            onBookClick = onBookClick,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
    if (state is StatisticsUiState.Success && state.isLoading) {
        CustomCircularProgressIndicator()
    }
}

@Composable
private fun StatisticsToolbar(
    state: StatisticsUiState,
    scrollState: ScrollState,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val booksRead = when (state) {
        is StatisticsUiState.Empty -> 0
        is StatisticsUiState.Success -> state.totalBooksRead
    }

    val elevation = when (scrollState.value) {
        0 -> 0.dp
        else -> 4.dp
    }
    CustomToolbar(
        title = stringResource(Res.string.title_stats),
        modifier = modifier.shadow(elevation),
        subtitle = pluralStringResource(Res.plurals.title_books_count, booksRead, booksRead),
        backgroundColor = MaterialTheme.colorScheme.background,
    )
}

@Composable
private fun StatisticsContent(
    state: StatisticsUiState,
    scrollState: ScrollState,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.height(16.dp))
        when (state) {
            is StatisticsUiState.Empty -> NoResultsComponent()
            is StatisticsUiState.Success -> StatisticsComponent(
                state = state,
                onGroupClick = onGroupClick,
                onBookClick = onBookClick,
            )
        }
    }
}

@Composable
private fun StatisticsComponent(
    state: StatisticsUiState.Success,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
) {
    if (state.booksByYearEntries.entries.isNotEmpty()) {
        BarChart(
            entries = state.booksByYearEntries,
            onEntrySelected = { onGroupClick(it, null, null, null, null) },
        )
    }
    if (state.booksByMonthEntries.entries.isNotEmpty()) {
        BooksByMonth(
            entries = state.booksByMonthEntries,
            onMonthSelected = { onGroupClick(null, it, null, null, null) },
        )
    }
    if (state.booksByAuthorStats.entries.isNotEmpty()) {
        BooksByAuthor(
            entries = state.booksByAuthorStats,
            onAuthorSelected = { onGroupClick(null, null, it, null, null) },
        )
    }
    BooksByPages(
        shorterBook = state.shorterBook,
        longerBook = state.longerBook,
        onBookClick = onBookClick,
    )
    if (state.booksByFormatEntries.entries.isNotEmpty()) {
        BooksByFormat(
            entries = state.booksByFormatEntries,
            onFormatSelected = { onGroupClick(null, null, null, it, null) },
        )
    }
    if (state.booksByGenreEntries.entries.isNotEmpty()) {
        BooksByGenre(
            entries = state.booksByGenreEntries,
            onGenreSelected = { onGroupClick(null, null, null, null, it) },
        )
    }
}

@Composable
private fun BooksByMonth(entries: Entries, onMonthSelected: (Int?) -> Unit) {
    val monthsTitle = stringResource(Res.string.months)
    val language = LocalLanguage.current
    Spacer(Modifier.height(24.dp))
    PieChart(
        entries = entries,
        centerText = monthsTitle,
        usePercent = false,
        onEntrySelected = { label ->
            val month = UiDateMapper.getMonthNumberFromName(label, language)
            onMonthSelected(month)
        },
    )
}

@Composable
private fun BooksByAuthor(entries: Entries, onAuthorSelected: (String?) -> Unit) {
    Spacer(Modifier.height(24.dp))
    HorizontalBarChart(
        entries = entries,
        onEntrySelected = onAuthorSelected,
    )
}

@Composable
private fun BooksByPages(shorterBook: Book?, longerBook: Book?, onBookClick: (String) -> Unit) {
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        shorterBook?.let { book ->
            Column {
                Text(
                    text = stringResource(Res.string.shorter_book),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(8.dp))
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
        }
        longerBook?.let { book ->
            Column {
                Text(
                    text = stringResource(Res.string.longer_book),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Spacer(Modifier.height(8.dp))
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
        }
    }
}

@Composable
private fun BooksByFormat(entries: Entries, onFormatSelected: (String?) -> Unit) {
    val formatsTitle = stringResource(Res.string.formats)
    Spacer(Modifier.height(24.dp))
    PieChart(
        entries = entries,
        centerText = formatsTitle,
        usePercent = true,
        onEntrySelected = { label ->
            onFormatSelected(FORMATS.firstOrNull { it.name == label }?.id)
        },
    )
}

@Composable
private fun BooksByGenre(entries: Entries, onGenreSelected: (String?) -> Unit) {
    Spacer(Modifier.height(24.dp))
    HorizontalBarChart(
        entries = entries,
        onEntrySelected = { label ->
            onGenreSelected(GENRES.firstOrNull { it.name == label }?.id)
        },
    )
}

@CustomPreviewLightDark
@Composable
private fun StatisticsScreenPreview(
    @PreviewParameter(StatisticsScreenPreviewParameterProvider::class) state: StatisticsUiState,
) {
    ReaderCollectionTheme {
        StatisticsScreen(
            state = state,
            onImportClick = {},
            onExportClick = {},
            onGroupClick = { _, _, _, _, _ -> },
            onBookClick = {},
        )
    }
}

private class StatisticsScreenPreviewParameterProvider :
    PreviewParameterProvider<StatisticsUiState> {

    private val book = Book("1").copy(
        title = "Shortest read book",
        authors = listOf("Author"),
        rating = 5.0,
        state = BookState.READ,
    )

    override val values: Sequence<StatisticsUiState>
        get() = sequenceOf(
            StatisticsUiState.Success(
                totalBooksRead = 12345,
                booksByYearEntries = Entries(
                    listOf(
                        Entry("2023", 10),
                        Entry("2024", 20),
                    ),
                ),
                booksByMonthEntries = Entries(
                    listOf(
                        Entry("FEB", 10),
                        Entry("AGO", 20),
                    ),
                ),
                booksByAuthorStats = Entries(
                    listOf(
                        Entry("Author 1", 1),
                        Entry("Author 1", 2),
                    ),
                ),
                shorterBook = book.copy(title = "Shortest read book"),
                longerBook = book.copy(title = "Longest read book"),
                booksByFormatEntries = Entries(
                    listOf(
                        Entry("Physical", 10),
                        Entry("Digital", 20),
                    ),
                ),
                booksByGenreEntries = Entries(
                    listOf(
                        Entry("Fiction", 5),
                        Entry("Thriller", 8),
                    ),
                ),
                isLoading = false,
            ),
            StatisticsUiState.Success(
                totalBooksRead = 12345,
                booksByYearEntries = Entries(),
                booksByMonthEntries = Entries(),
                booksByAuthorStats = Entries(),
                shorterBook = book.copy(title = "Shortest read book"),
                longerBook = book.copy(title = "Longest read book"),
                booksByFormatEntries = Entries(),
                booksByGenreEntries = Entries(),
                isLoading = true,
            ),
            StatisticsUiState.Empty,
        )
}