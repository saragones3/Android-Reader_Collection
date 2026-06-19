/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local

import aragones.sergio.readercollection.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BooksLocalDataSource {
    fun getAllBooks(): Flow<List<Book>>
    fun getReadBooks(): Flow<List<Book>>
    suspend fun importDataFrom(books: List<Book>)
    suspend fun getBook(googleId: String): Book?
    suspend fun insertBooks(books: List<Book>)
    suspend fun updateBooks(books: List<Book>)
    suspend fun deleteBooks(books: List<Book>)
    //endregion
}