package com.tkmst.android.mapapp.repository.user

import com.tkmst.android.mapapp.model.creature.CreatureDetail
import com.tkmst.android.mapapp.model.user.UserPreferences

/**
 * ユーザ設定情報のリポジトリインタフェース
 */
interface UserPreferencesRepository {

    /**
     * ユーザ情報を取得（レコードは1件のみ存在）
     * @return UserPreferences ユーザ設定情報クラス
     */
    fun getUserPreferences(): UserPreferences

    /**
     * 最期に閲覧したタブインデックスを更新
     * @param lastSelectedTabIndex 最期に閲覧したタブインデックス
     * @return Int 更新した件数
     */
    suspend fun updateLastSelectedTabIndex(lastSelectedTabIndex: Long): Int

    /**
     * 最期に設定した地図表示モードを更新（衛星写真か絵か）
     * @param isModeSatellite 最期に設定した地図閲覧モード
     * @return Int 更新した件数
     */
    suspend fun updateIsModeSatellite(isModeSatellite: Boolean): Int
}