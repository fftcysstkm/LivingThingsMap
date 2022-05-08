package com.demo.android.mapapp.model.creature

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.android.mapapp.util.LocalDateTimeTypeConverter

/**
 * 生き物Daoをインスタンス化するデータベースクラス
 */
@Database(entities = [Category::class, Creature::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class CreatureRoomDatabase : RoomDatabase() {
    /**
     * 生き物Daoのインスタンスを呼び出し元に返すメソッド
     */
    abstract fun creatureDao(): CreatureDao

}