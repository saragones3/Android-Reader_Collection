/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 5/1/2025
 */

package aragones.sergio.readercollection.presentation.statistics

import androidx.compose.runtime.Immutable
import aragones.sergio.readercollection.domain.model.Book

sealed class StatisticsUiState {

    data object Empty : StatisticsUiState()

    data class Success(
        val totalBooksRead: Int,
        val booksByYearEntries: Entries,
        val pagesByYearEntries: Entries,
        val booksByMonthEntries: Entries,
        val booksByAuthorStats: Entries,
        val shorterBook: Book?,
        val longerBook: Book?,
        val booksByFormatEntries: Entries,
        val booksByGenreEntries: Entries,
        val isLoading: Boolean,
    ) : StatisticsUiState() {
        companion object {
            fun empty(): Success = Success(
                totalBooksRead = 0,
                booksByYearEntries = Entries(),
                pagesByYearEntries = Entries(),
                booksByMonthEntries = Entries(),
                booksByAuthorStats = Entries(),
                shorterBook = null,
                longerBook = null,
                booksByFormatEntries = Entries(),
                booksByGenreEntries = Entries(),
                isLoading = false,
            )
        }
    }
}

data class Entry(val key: String, val size: Int)

@Immutable
data class Entries(val entries: List<Entry> = listOf())