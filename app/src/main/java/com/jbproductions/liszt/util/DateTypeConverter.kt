package com.jbproductions.liszt.util

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * TypeConverters to manage storing Java Date objects as strings in Android's internal SQLite database.
 */
class DateTypeConverter {
    var sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    /**
     * Parser to convert strings to Java Date objects as they are queried from internal db.
     * @param value String representation of a date
     * @return Java Date object parsed from string
     */
    @TypeConverter
    fun toDate(value: String?): Date? {
        try {
            return if (value == null) null else sdf.parse(value)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    @TypeConverter
    fun toString(date: Date?): String? {
        return if (date == null) null else sdf.format(date)
    }
}