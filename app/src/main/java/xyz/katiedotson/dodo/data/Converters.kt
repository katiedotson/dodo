package xyz.katiedotson.dodo.data

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class Converters {

    @SuppressLint("NewApi")
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return if (value !== null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(value), TimeZone.getDefault().toZoneId())
        } else null
    }

    @SuppressLint("NewApi")
    @TypeConverter
    fun localDateTimeToTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.atZone(TimeZone.getDefault().toZoneId())?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun fromTimestampToLocalDate(value: Long?): LocalDate? {
        if (value == null) return null
        return LocalDate.ofEpochDay(value)
    }

    @TypeConverter
    fun localDateToTimestamp(localDate: LocalDate?): Long? {
        if (localDate == null) return null
        return localDate.toEpochDay()
    }

}