package com.tkmst.android.mapapp.model.user

import androidx.room.Dao
import androidx.room.Query

/**
 * ユーザ設定情報Dao
 */
@Dao
interface UserPreferencesDao {

    /**
     * ユーザ情報を取得（レコードは1件のみ存在）
     * @return UserPreferences ユーザ設定情報クラス
     */
    @Query("SELECT * FROM UserPreferences WHERE userName = 'user'")
    fun getUserPreferences(): UserPreferences

    /**
     * 最期に閲覧したタブインデックスを更新
     * @param lastSelectedTabIndex 最期に閲覧したタブインデックス
     * @return Int 更新した件数
     */
    @Query("UPDATE UserPreferences SET lastSelectedTabIndex = :lastSelectedTabIndex WHERE userName = 'user'")
    suspend fun updateLastSelectedTabIndex(lastSelectedTabIndex: Long): Int

    /**
     * 最期に設定した地図表示モードを更新（衛星写真か絵か）
     * @param isModeSatellite 最期に設定した地図閲覧モード
     * @return Int 更新した件数
     */
    @Query("UPDATE UserPreferences SET isModeSatellite = :isModeSatellite WHERE userName = 'user'")
    suspend fun updateIsModeSatellite(isModeSatellite: Boolean): Int
}