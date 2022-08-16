package com.tkmst.android.mapapp.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  ユーザーの設定情報Entity（1レコードのみ存在する）
 */
@Entity(tableName = "UserPreferences")
data class UserPreferences(
    /** ユーザー名（主キー。とりあえず"user"で固定） */
    @PrimaryKey
    val userName: String,
    /** 生き物一覧画面で最後に選択したタブインデックス。次回開くときにこのタブを選択する。 */
    val lastSelectedTabIndex: Int,
    /** 地図画面を衛星写真モードにするか(true:衛星写真モード) */
    val isModeSatellite: Boolean
)
