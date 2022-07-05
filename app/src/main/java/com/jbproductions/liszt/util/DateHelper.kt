package com.jbproductions.liszt.util

import java.util.*

/**
 * Convenience function to get a human-readable String for a given date. If the supplied date is within the next
 * week, the function will return a value such as "Today", "Tomorrow", or the weekday of the date. Otherwise, it
 * will return a String of "Month, Day", with the year appended if the date supplied is in a different year.
 */
fun getReadableDate(date: Date): String {

    val dueDate = Calendar.getInstance()
    dueDate.time = date

    val daysToDueDate = dueDate[Calendar.DAY_OF_YEAR] - Calendar.getInstance()[Calendar.DAY_OF_YEAR]
    var readableDueDate = ""

    if (daysToDueDate in 2..7) {
        readableDueDate += dueDate.getDisplayName(
            Calendar.DAY_OF_WEEK,
            Calendar.LONG,
            Locale.getDefault()
        )
    } else {
        when (daysToDueDate) {
            0 -> readableDueDate += "Today"
            1 -> readableDueDate += "Tomorrow"
            else -> {
                val monthName =
                    dueDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                readableDueDate += monthName!! + " " + dueDate[Calendar.DATE]
                if (dueDate[Calendar.YEAR] != Calendar.getInstance()[Calendar.YEAR]) {
                    readableDueDate += ", " + dueDate[Calendar.YEAR]
                }
            }
        }
    }

    return readableDueDate
}