package com.tkmst.android.mapapp.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.tkmst.android.mapapp.model.date.RecordDateTime
import java.time.LocalDateTime

/**
 * 型を変換するクラス
 * RoomはLocalDateTime未対応のためStringに変換する場合に使用
 */
class LocalDateTimeTypeConverter {

    @SuppressLint("NewApi")
    @TypeConverter
    fun stringToLocalDateTime(dateTimeString: String?): RecordDateTime {
        val localDateTime = LocalDateTime.parse(dateTimeString)
        return RecordDateTime(localDateTime)
    }

    @SuppressLint("NewApi")
    @TypeConverter
    fun localDateTimeToString(recordDateTime: RecordDateTime): String {
        return recordDateTime.dateTime.toString()
    }

}