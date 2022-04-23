package com.demo.android.mapapp.application

import android.app.Application
import com.demo.android.mapapp.model.database.CreatureRoomDatabase

/**
 * ViewModelにDatabaseオブジェクトを利用可能にする
 */
class LivingThingsMapApplication : Application() {
    val database: CreatureRoomDatabase by lazy { CreatureRoomDatabase.getDatabase(this) }
}