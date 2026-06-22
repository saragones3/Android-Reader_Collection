/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.local

import aragones.sergio.readercollection.data.remote.model.GENRES
import aragones.sergio.readercollection.data.remote.model.GenreResponse
import aragones.sergio.readercollection.domain.model.Book
import com.aragones.sergio.model.Book as BookLocal
import com.aragones.sergio.util.extensions.toLocalDate
import com.aragones.sergio.util.extensions.toLong

fun Book.toLocalData(): BookLocal = BookLocal(
    id = id,
    title = title,
    subtitle = subtitle,
    authors = authors,
    publisher = publisher,
    publishedDate = publishedDate?.toLong(),
    readingDate = readingDate?.toLong(),
    description = description,
    summary = summary,
    isbn = isbn,
    pageCount = pageCount,
    categories = categories?.map { it.id },
    averageRating = averageRating,
    ratingsCount = ratingsCount,
    rating = rating,
    thumbnail = thumbnail,
    image = image,
    format = format,
    state = state,
    priority = priority,
)

fun BookLocal.toDomain(): Book = Book(
    id = id,
    title = title,
    subtitle = subtitle,
    authors = authors,
    publisher = publisher,
    publishedDate = publishedDate?.toLocalDate(),
    readingDate = readingDate?.toLocalDate(),
    description = description,
    summary = summary,
    isbn = isbn,
    pageCount = pageCount,
    categories = categories?.map { categoryId ->
        GENRES.firstOrNull { it.id == categoryId } ?: GenreResponse(
            categoryId,
            categoryId.lowercase(),
        )
    },
    averageRating = averageRating,
    ratingsCount = ratingsCount,
    rating = rating,
    thumbnail = thumbnail,
    image = image,
    format = format,
    state = state,
    priority = priority,
)