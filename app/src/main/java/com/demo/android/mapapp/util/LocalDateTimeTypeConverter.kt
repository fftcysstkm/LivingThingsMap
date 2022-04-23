package com.demo.android.mapapp.util

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 * 型を変換するクラス
 * RoomはLocalDateTime未対応のためStringに変換する場合などに使用
 */
class LocalDateTimeTypeConverter {

    @SuppressLint("NewApi")
    @TypeConverter
    fun toDate(dateString: String?): LocalDateTime? {
        return LocalDateTime.parse(dateString)
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): String? {
        return date?.toString()
    }

}