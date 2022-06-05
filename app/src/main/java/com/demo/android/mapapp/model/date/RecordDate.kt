package com.demo.android.mapapp.model.date

import java.util.*

/**
 * 生き物の記録日
 * コンストラクタのデフォルト値は現在日時のカレンダーインスタンス
 * DatePicker/TimePickerで扱いやすくするため年月日時分プロパティを保持
 */
class RecordDate(val calendar: Calendar) {
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)
    val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
    val minute: Int = calendar.get(Calendar.MINUTE)

    /**
     * 生き物の記録日テキスト。マップのボトムシートの日付テキストに使用
     */
    fun recordDateText(): String {
        return "${year}/${month + 1}/${dayOfMonth}"
    }

    /**
     * 生き物の記録時間のテキスト。マップのボトムシートの日付テキストに使用
     */
    fun recordTimeText(): String {
        return "${hour}:${minute}"
    }
}