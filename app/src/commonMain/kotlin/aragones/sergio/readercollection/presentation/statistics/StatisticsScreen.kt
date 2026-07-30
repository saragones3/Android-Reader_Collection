/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 15/1/2026
 */

package aragones.sergio.readercollection.presentation.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import aragones.sergio.readercollection.data.remote.model.FORMATS
import aragones.sergio.readercollection.data.remote.model.GENRES
import aragones.sergio.readercollection.domain.model.Book
import aragones.sergio.readercollection.isAndroid
import aragones.sergio.readercollection.presentation.LocalLanguage
import aragones.sergio.readercollection.presentation.components.BarChart
import aragones.sergio.readercollection.presentation.components.CustomCircularProgressIndicator
import aragones.sergio.readercollection.presentation.components.CustomPreviewLightDarkLong
import aragones.sergio.readercollection.presentation.components.CustomToolbar
import aragones.sergio.readercollection.presentation.components.HorizontalBarChart
import aragones.sergio.readercollection.presentation.components.NoResultsComponent
import aragones.sergio.readercollection.presentation.components.PieChart
import aragones.sergio.readercollection.presentation.components.SecondaryButton
import aragones.sergio.readercollection.presentation.components.SecondaryOutlinedButton
import aragones.sergio.readercollection.presentation.components.VerticalBookItem
import aragones.sergio.readercollection.presentation.components.withDescription
import aragones.sergio.readercollection.presentation.theme.EbonyClay
import aragones.sergio.readercollection.presentation.theme.ReaderCollectionTheme
import aragones.sergio.readercollection.presentation.theme.RoseBud
import aragones.sergio.readercollection.utils.UiDateMapper
import com.aragones.sergio.util.BookState
import org.jetbrains.compose.resources.stringResource
import reader_collection.app.generated.resources.Res
import reader_collection.app.generated.resources.book_pages
import reader_collection.app.generated.resources.books_per_format
import reader_collection.app.generated.resources.books_per_genre
import reader_collection.app.generated.resources.books_per_month
import reader_collection.app.generated.resources.books_per_year
import reader_collection.app.generated.resources.export_data
import reader_collection.app.generated.resources.formats
import reader_collection.app.generated.resources.import_data
import reader_collection.app.generated.resources.longer_book
import reader_collection.app.generated.resources.months
import reader_collection.app.generated.resources.pages_per_year
import reader_collection.app.generated.resources.reading_dashboard
import reader_collection.app.generated.resources.reading_dashboard_description
import reader_collection.app.generated.resources.shorter_book
import reader_collection.app.generated.resources.title_stats
import reader_collection.app.generated.resources.top_authors
import reader_collection.app.generated.resources.total_books_read

@Composable
fun StatisticsScreen(
    state: StatisticsUiState,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize()) {
        StatisticsToolbar(scrollState)
        StatisticsContent(
            state = state,
            scrollState = scrollState,
            onImportClick = onImportClick,
            onExportClick = onExportClick,
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
private fun StatisticsToolbar(scrollState: ScrollState, modifier: Modifier = Modifier) {
    val elevation by remember {
        derivedStateOf {
            when (scrollState.value) {
                0 -> 0.dp
                else -> 4.dp
            }
        }
    }
    CustomToolbar(
        title = stringResource(Res.string.title_stats),
        modifier = modifier.shadow(elevation),
        backgroundColor = MaterialTheme.colorScheme.background,
    )
}

@Composable
private fun StatisticsContent(
    state: StatisticsUiState,
    scrollState: ScrollState,
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        ImportExportHeader(
            onImportClick = onImportClick,
            onExportClick = onExportClick,
            modifier = Modifier.padding(vertical = 16.dp),
        )
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
private fun ImportExportHeader(
    onImportClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.reading_dashboard),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.reading_dashboard_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        if (isAndroid()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SecondaryOutlinedButton(
                    text = stringResource(Res.string.import_data),
                    onClick = onImportClick,
                    modifier = Modifier.weight(1f),
                    painter = rememberVectorPainter(Icons.Default.Upload).withDescription(null),
                )
                SecondaryButton(
                    text = stringResource(Res.string.export_data),
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f),
                    startPainter = rememberVectorPainter(
                        Icons.Default.Download,
                    ).withDescription(null),
                )
            }
        }
    }
}

@Composable
private fun StatisticsComponent(
    state: StatisticsUiState.Success,
    onGroupClick: (Int?, Int?, String?, String?, String?) -> Unit,
    onBookClick: (String) -> Unit,
) {
    SectionContainer(title = stringResource(Res.string.total_books_read)) {
        Text(
            text = state.totalBooksRead.toString(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
    if (state.booksByYearEntries.entries.isNotEmpty()) {
        SectionContainer(title = stringResource(Res.string.books_per_year)) {
            BarChart(
                entries = state.booksByYearEntries,
                onEntrySelected = { onGroupClick(it, null, null, null, null) },
            )
        }
    }
    if (state.pagesByYearEntries.entries.isNotEmpty()) {
        SectionContainer(title = stringResource(Res.string.pages_per_year)) {
            BarChart(
                entries = state.pagesByYearEntries,
                onEntrySelected = { onGroupClick(it, null, null, null, null) },
            )
        }
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
    BookByPages(
        title = stringResource(Res.string.shorter_book),
        book = state.shorterBook,
        onBookClick = onBookClick,
    )
    BookByPages(
        title = stringResource(Res.string.longer_book),
        book = state.longerBook,
        onBookClick = onBookClick,
    )
}

@Composable
private fun SectionContainer(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        ),
        border = BorderStroke(
            width = 0.1.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun BooksByMonth(entries: Entries, onMonthSelected: (Int?) -> Unit) {
    val monthsTitle = stringResource(Res.string.months)
    val language = LocalLanguage.current
    SectionContainer(title = stringResource(Res.string.books_per_month)) {
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
}

@Composable
private fun BooksByAuthor(entries: Entries, onAuthorSelected: (String?) -> Unit) {
    SectionContainer(title = stringResource(Res.string.top_authors)) {
        HorizontalBarChart(
            entries = entries,
            onEntrySelected = onAuthorSelected,
        )
    }
}

@Composable
private fun BookByPages(title: String, book: Book?, onBookClick: (String) -> Unit) {
    book?.let { book ->
        SectionContainer(title) {
            Row {
                Spacer(Modifier.weight(1f))
                VerticalBookItem(
                    book = book,
                    isSwitchLeftIconEnabled = false,
                    isSwitchRightIconEnabled = false,
                    onClick = { onBookClick(book.id) },
                    onSwitchToLeft = {},
                    onSwitchToRight = {},
                    onLongClick = {},
                )
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.book_pages, book.pageCount),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EbonyClay,
                modifier = Modifier
                    .background(RoseBud, MaterialTheme.shapes.small)
                    .padding(4.dp),
            )
        }
    }
}

@Composable
private fun BooksByFormat(entries: Entries, onFormatSelected: (String?) -> Unit) {
    val formatsTitle = stringResource(Res.string.formats)
    SectionContainer(title = stringResource(Res.string.books_per_format)) {
        PieChart(
            entries = entries,
            centerText = formatsTitle,
            usePercent = true,
            onEntrySelected = { label ->
                onFormatSelected(FORMATS.firstOrNull { it.name == label }?.id)
            },
        )
    }
}

@Composable
private fun BooksByGenre(entries: Entries, onGenreSelected: (String?) -> Unit) {
    SectionContainer(title = stringResource(Res.string.books_per_genre)) {
        HorizontalBarChart(
            entries = entries,
            onEntrySelected = { label ->
                onGenreSelected(GENRES.firstOrNull { it.name == label }?.id)
            },
        )
    }
}

@CustomPreviewLightDarkLong
@Composable
private fun StatisticsScreenPreview(
    @PreviewParameter(StatisticsScreenPreviewParameterProvider::class) state: StatisticsUiState,
) {
    ReaderCollectionTheme {
        CompositionLocalProvider(LocalLanguage provides "en") {
            StatisticsScreen(
                state = state,
                onImportClick = {},
                onExportClick = {},
                onGroupClick = { _, _, _, _, _ -> },
                onBookClick = {},
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
            )
        }
    }
}

private class StatisticsScreenPreviewParameterProvider :
    PreviewParameterProvider<StatisticsUiState> {

    private val book = Book("1").copy(
        title = "Shortest read book",
        authors = listOf("Author"),
        rating = 5.0,
        state = BookState.READ,
        pageCount = 123,
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
                pagesByYearEntries = Entries(
                    listOf(
                        Entry("2023", 2523),
                        Entry("2024", 7600),
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
                pagesByYearEntries = Entries(),
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