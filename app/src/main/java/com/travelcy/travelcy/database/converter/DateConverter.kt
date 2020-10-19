package com.travelcy.travelcy.database.converter

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    companion object {
        @TypeConverter
        fun toDate(timestamp: Long): Date {
            return Date(timestamp)
        }

        @TypeConverter
        fun toTimestamp(date: Date): Long {
            return date.time
        }
    }
}