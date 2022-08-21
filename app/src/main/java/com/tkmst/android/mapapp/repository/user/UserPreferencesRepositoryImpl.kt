package com.tkmst.android.mapapp.repository.user

import com.tkmst.android.mapapp.model.user.UserPreferences
import com.tkmst.android.mapapp.model.user.UserPreferencesDao
import javax.inject.Inject

/**
 * ユーザ設定情報のリポジトリ実装クラス
 */
class UserPreferencesRepositoryImpl @Inject constructor(private val dao: UserPreferencesDao) :
    UserPreferencesRepository {

    /**
     * ユーザ情報を取得（レコードは1件のみ存在）
     * @return UserPreferences ユーザ設定情報クラス
     */
    override suspend fun getUserPreferences(): UserPreferences {
        return dao.getUserPreferences()
    }

    /**
     * 最期に閲覧したタブインデックスを更新
     * @param lastSelectedTabIndex 最期に閲覧したタブインデックス
     * @return Int 更新した件数
     */
    override suspend fun updateLastSelectedTabIndex(lastSelectedTabIndex: Long): Int {
        return dao.updateLastSelectedTabIndex(lastSelectedTabIndex)
    }

    /**
     * 最期に設定した地図表示モードを更新（衛星写真か絵か）
     * @param isModeSatellite 最期に設定した地図閲覧モード
     * @return Int 更新した件数
     */
    override suspend fun updateIsModeSatellite(isModeSatellite: Boolean): Int {
        return dao.updateIsModeSatellite(isModeSatellite)
    }
}