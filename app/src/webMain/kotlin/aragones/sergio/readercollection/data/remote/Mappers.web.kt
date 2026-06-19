/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.data.remote

import com.aragones.sergio.util.extensions.toLocalDate
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

actual fun Any?.fromNativeDate(): LocalDate? = when (this) {
    is String -> {
        if (this.isBlank()) {
            null
        } else {
            this.toLocalDate("dd/MM/yyyy")
                ?: this.toLocalDate("yyyy-MM-dd")
                ?: this.toLocalDate()
                ?: try {
                    Instant
                        .parse(this)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                } catch (_: Exception) {
                    try {
                        LocalDate.parse(this)
                    } catch (_: Exception) {
                        null
                    }
                }
        }
    }
    is Instant -> {
        this.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    is Number -> {
        try {
            Instant
                .fromEpochSeconds(this.toLong())
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        } catch (_: Exception) {
            try {
                Instant
                    .fromEpochMilliseconds(this.toLong())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            } catch (_: Exception) {
                null
            }
        }
    }
    is Map<*, *> -> {
        val seconds = (this["seconds"] ?: this["_seconds"]) as? Number
        if (seconds != null) {
            try {
                Instant
                    .fromEpochSeconds(seconds.toLong())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            } catch (_: Exception) {
                null
            }
        } else {
            val millis = (this["milliseconds"] ?: this["_milliseconds"]) as? Number
            millis?.toLong()?.let {
                try {
                    Instant
                        .fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .date
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
    else -> {
        null
    }
}

actual fun LocalDate?.toNativeDate(): Any? = this?.atStartOfDayIn(TimeZone.currentSystemDefault())
