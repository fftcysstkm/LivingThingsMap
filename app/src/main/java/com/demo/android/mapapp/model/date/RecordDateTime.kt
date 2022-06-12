package com.demo.android.mapapp.model.date

import android.annotation.SuppressLint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 生き物の記録日データクラス
 * 画面用に使用する文字列のフォーマットメソッドを持つ
 */
data class RecordDateTime(val dateTime: LocalDateTime) {
    /**
     * ボトムシートに表示する日時の文字列を取得(yyyy/MM/dd)
     */
    @SuppressLint("NewApi")
    fun dateString(): String {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    }

    /**
     * ボトムシートに表示する時刻の文字列を取得
     */
    @SuppressLint("NewApi")
    fun timeString(): String {
        return dateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
    }

}