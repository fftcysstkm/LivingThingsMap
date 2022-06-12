package com.demo.android.mapapp.model.date

import android.annotation.SuppressLint
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 生き物の記録日データクラス
 * DatePickerを扱いやすくするLocalDateクラス
 * TimePickerを扱いやすくするLocalTimeクラスを持つ
 */
data class RecordDateTime(val recordDate: LocalDate, val recordTime: LocalTime) {
    /**
     * ボトムシートに表示する日時の文字列を取得
     */
    @SuppressLint("NewApi")
    fun dateString(): String {
        return recordDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    }

    /**
     * ボトムシートに表示する時刻の文字列を取得
     */
    @SuppressLint("NewApi")
    fun timeString(): String {
        return recordTime.format(DateTimeFormatter.ofPattern("hh:mm"))
    }

}