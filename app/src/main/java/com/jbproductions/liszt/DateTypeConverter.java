package com.jbproductions.liszt;

import android.util.Log;
import androidx.room.TypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TypeConverters to manage storing Java Date objects as strings in Android's internal SQLite database.
 */
public class DateTypeConverter {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    /**
     * Parser to convert strings to Java Date objects as they are queried from internal db.
     * @param value String representation of a date
     * @return Java Date object parsed from string
     */
    @TypeConverter
    public Date toDate(String value) {
        try {
            return value == null ? null : sdf.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @TypeConverter
    public String toString(Date date) {
        return date == null ? null : sdf.format(date);
    }

}