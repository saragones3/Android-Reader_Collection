/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 19/6/2026
 */

package aragones.sergio.readercollection.utils

import com.aragones.sergio.util.Constants
import com.aragones.sergio.util.extensions.toLocalDate
import com.aragones.sergio.util.extensions.toLong
import com.aragones.sergio.util.extensions.toString
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

actual object UiDateMapper {
    actual fun List<LocalDate>.getGroupedBy(
        pattern: String,
        language: String,
    ): Map<String, List<Any>> = this.groupBy {
        when (pattern) {
            "MMM" -> getMonthName(it.month.number, language, short = true)
            "MMMM" -> getMonthName(it.month.number, language, short = false)
            "yyyy" -> it.year.toString()
            else -> it.toString(pattern) ?: ""
        }
    }

    actual fun LocalDate?.getValueToShow(language: String): String? {
        if (this == null) return null
        val monthName = getMonthName(this.month.number, language, short = false)
        val day = this.day.toString()
        return Constants
            .getDateFormatToShow(language)
            .replace("d", day)
            .replace("MMMM", monthName)
            .replace("yyyy", this.year.toString())
    }

    actual fun String.toLong(language: String?): Long? {
        if (this.isBlank() ||
            this == Constants.NO_VALUE ||
            this == Constants.EMPTY_VALUE
        ) {
            return null
        }

        val isoDate = this.toLocalDate()
        if (isoDate != null) return isoDate.toLong()

        val dmyDate = this.toLocalDate("dd/MM/yyyy")
        if (dmyDate != null) return dmyDate.toLong()

        try {
            val clean = this.replace(",", "")
            val parts = clean.split(" ").filter { it.isNotBlank() }
            if (language == "es" && parts.size >= 3) {
                // d MMMM yyyy
                val day = parts[0].toInt()
                val month = getMonthNumberFromName(parts[1], "es")
                val year = parts[2].toInt()
                return LocalDate(year, month, day).toLong()
            } else if (parts.size >= 3) {
                // MMMM d yyyy
                val month = getMonthNumberFromName(parts[0], "en")
                val day = parts[1].toInt()
                val year = parts[2].toInt()
                return LocalDate(year, month, day).toLong()
            }
        } catch (_: Exception) {
        }

        return null
    }

    actual fun Long.toLocalDate(language: String): LocalDate? = this.toLocalDate()

    actual fun Int.toMonthName(language: String): String {
        val name = getMonthName(this, language, short = false)
        return if (name.isNotEmpty()) "$name," else ""
    }

    fun getMonthName(month: Int, language: String, short: Boolean): String {
        val esMonths = listOf(
            "",
            "enero",
            "febrero",
            "marzo",
            "abril",
            "mayo",
            "junio",
            "julio",
            "agosto",
            "septiembre",
            "octubre",
            "noviembre",
            "diciembre",
        )
        val esShort = listOf(
            "",
            "ene.",
            "feb.",
            "mar.",
            "abr.",
            "may.",
            "jun.",
            "jul.",
            "ago.",
            "sep.",
            "oct.",
            "nov.",
            "dic.",
        )
        val enMonths = listOf(
            "",
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )
        val enShort = listOf(
            "",
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec",
        )

        return if (language == "es") {
            if (short) esShort.getOrNull(month) ?: "" else esMonths.getOrNull(month) ?: ""
        } else {
            if (short) enShort.getOrNull(month) ?: "" else enMonths.getOrNull(month) ?: ""
        }
    }

    fun getMonthNumberFromName(name: String, language: String): Int {
        val n = name.lowercase().removeSuffix(".")
        val esMonths = listOf(
            "",
            "enero",
            "febrero",
            "marzo",
            "abril",
            "mayo",
            "junio",
            "julio",
            "agosto",
            "septiembre",
            "octubre",
            "noviembre",
            "diciembre",
        )
        val esShort = listOf(
            "",
            "ene",
            "feb",
            "mar",
            "abr",
            "may",
            "jun",
            "jul",
            "ago",
            "sep",
            "oct",
            "nov",
            "dic",
        )
        val enMonths = listOf(
            "",
            "january",
            "february",
            "march",
            "april",
            "may",
            "june",
            "july",
            "august",
            "september",
            "october",
            "november",
            "december",
        )
        val enShort = listOf(
            "",
            "jan",
            "feb",
            "mar",
            "apr",
            "may",
            "jun",
            "jul",
            "aug",
            "sep",
            "oct",
            "nov",
            "dec",
        )

        val list = if (language == "es") esMonths else enMonths
        val shortList = if (language == "es") esShort else enShort

        var idx = list.indexOf(n)
        if (idx > 0) return idx
        idx = shortList.indexOf(n)
        if (idx > 0) return idx

        val otherList = if (language == "es") enMonths else esMonths
        val otherShortList = if (language == "es") enShort else esShort
        idx = otherList.indexOf(n)
        if (idx > 0) return idx
        idx = otherShortList.indexOf(n)
        if (idx > 0) return idx

        return 1
    }
}
