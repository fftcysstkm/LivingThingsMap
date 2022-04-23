package com.demo.android.mapapp.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.demo.android.mapapp.model.dao.CreatureDao
import com.demo.android.mapapp.model.data.Creature
import com.demo.android.mapapp.model.data.CreatureType
import com.demo.android.mapapp.util.LocalDateTimeTypeConverter

/**
 * 生き物Daoをインスタンス化するデータベースクラス
 */
@Database(entities = [CreatureType::class, Creature::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateTimeTypeConverter::class)
abstract class CreatureRoomDatabase : RoomDatabase() {
    /**
     * 生き物Daoのインスタンスを呼び出し元に返すメソッド
     */
    abstract fun creatureDao(): CreatureDao

    companion object {
        @Volatile
        private var INSTANCE: CreatureRoomDatabase? = null

        fun getDatabase(context: Context): CreatureRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    CreatureRoomDatabase::class.java,
                    "creatures"
                )
                    .createFromAsset("database/creatures.db")
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}