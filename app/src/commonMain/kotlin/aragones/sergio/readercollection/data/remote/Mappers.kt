/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 5/1/2026
 */

package aragones.sergio.readercollection.data.remote

import aragones.sergio.readercollection.data.remote.model.BookResponse
import kotlinx.datetime.LocalDate

fun Map<String, Any?>.toBook(id: String): BookResponse? {
    if (isEmpty()) return null
    return try {
        BookResponse(
            id = id,
            title = getValueOrNull<String>("title"),
            subtitle = getValueOrNull<String>("subtitle"),
            authors = getValueOrNull<List<String>>("authors"),
            publisher = getValueOrNull<String>("publisher"),
            publishedDate = getValueOrNull<Any>("publishedDate")?.fromNativeDate(),
            readingDate = getValueOrNull<Any>("readingDate")?.fromNativeDate(),
            description = getValueOrNull<String>("description"),
            summary = getValueOrNull<String>("summary"),
            isbn = getValueOrNull<String>("isbn"),
            pageCount = getValueOrNull<Number>("pageCount")?.toInt() ?: 0,
            categories = getValueOrNull<List<String>>("categories"),
            averageRating = getValueOrNull<Double>("averageRating") ?: 0.0,
            ratingsCount = getValueOrNull<Number>("ratingsCount")?.toInt() ?: 0,
            rating = getValueOrNull<Double>("rating") ?: 0.0,
            thumbnail = getValueOrNull<String>("thumbnail"),
            image = getValueOrNull<String>("image"),
            format = getValueOrNull<String>("format"),
            state = getValueOrNull("state"),
            priority = getValueOrNull<Number>("priority")?.toInt() ?: -1,
        )
    } catch (_: Exception) {
        null
    }
}

fun BookResponse.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "title" to title,
    "subtitle" to subtitle,
    "authors" to authors,
    "publisher" to publisher,
    "publishedDate" to publishedDate?.toNativeDate(),
    "readingDate" to readingDate?.toNativeDate(),
    "description" to description,
    "summary" to summary,
    "isbn" to isbn,
    "pageCount" to pageCount,
    "categories" to categories,
    "averageRating" to averageRating,
    "ratingsCount" to ratingsCount,
    "rating" to rating,
    "thumbnail" to thumbnail,
    "image" to image,
    "format" to format,
    "state" to state,
    "priority" to priority,
)

private fun <V> Map<String, Any?>.getValueOrNull(key: String): V? {
    if (!keys.contains(key)) return null
    return (getValue(key) as? V).takeIf { it != "null" }
}

expect fun Any?.fromNativeDate(): LocalDate?

expect fun LocalDate?.toNativeDate(): Any?