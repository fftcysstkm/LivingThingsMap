package com.tkmst.android.mapapp.model.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  ユーザーの設定情報Entity（1レコードのみ存在する）
 */
@Entity(tableName = "UserPreferences")
data class UserPreferences(
    /** ユーザー名（主キー。とりあえず"user"で固定） */
    @PrimaryKey
    @ColumnInfo(defaultValue = "user")
    val userName: String,
    /** 生き物一覧画面で最後に選択したタブインデックス。次回開くときにこのタブを選択する。 */
    @ColumnInfo(defaultValue = 0.toString())
    val lastSelectedTabIndex: Int,
    /** 地図画面を衛星写真モードにするか(1:衛星写真モード, 0:絵) */
    @ColumnInfo(defaultValue = 1.toString())
    val isModeSatellite: Int
)
