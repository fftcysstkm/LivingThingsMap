package com.tkmst.android.mapapp.model.user

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * ユーザー設定情報Daoをインスタンス化するデータベースクラス
 */
@Database(
    entities = [UserPreferences::class],
    version = 1,
    exportSchema = false
)
abstract class UserPreferencesRoomDatabase : RoomDatabase() {
    /**
     * ユーザ設定Daoのインスタンスを呼び出し元に返すメソッド
     */
    abstract fun userPreferencesDao() : UserPreferencesDao
}