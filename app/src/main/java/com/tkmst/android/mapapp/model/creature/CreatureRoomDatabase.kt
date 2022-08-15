package com.tkmst.android.mapapp.model.creature

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tkmst.android.mapapp.util.LocalDateTimeTypeConverter

/**
 * 生き物Daoをインスタンス化するデータベースクラス
 */
@Database(
    entities = [Category::class, Creature::class, CreatureDetail::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class CreatureRoomDatabase : RoomDatabase() {
    /**
     * 生き物Daoのインスタンスを呼び出し元に返すメソッド
     */
    abstract fun creatureDao(): CreatureDao

}