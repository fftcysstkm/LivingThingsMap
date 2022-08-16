package com.tkmst.android.mapapp.model.hilt

import android.content.Context
import androidx.room.Room
import com.tkmst.android.mapapp.application.LivingThingsMapApplication
import com.tkmst.android.mapapp.model.creature.CreatureDao
import com.tkmst.android.mapapp.model.creature.CreatureRoomDatabase
import com.tkmst.android.mapapp.model.user.UserPreferencesDao
import com.tkmst.android.mapapp.model.user.UserPreferencesRoomDatabase
import com.tkmst.android.mapapp.repository.creature.CreatureRepository
import com.tkmst.android.mapapp.repository.creature.CreatureRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * HiltがInjectする（Injectするのに必要な）インスタンスの作り方を定義
 * DatabaseクラスやDao、Repository
 */
@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    /**
     * CreatureRoomDatabaseインスタンスの作り方をHiltに伝達
     * 事前定義のDBあり
     */
    @Singleton
    @Provides
    fun provideCreatureRoomDatabase(
        @ApplicationContext context: Context
    ): CreatureRoomDatabase {
        return Room.databaseBuilder(
            context,
            CreatureRoomDatabase::class.java,
            "creatures"
        )
            .createFromAsset("database/creatures.db")
            .build()
    }

    /**
     * CreatureDaoインスタンスの作り方をHiltに伝達
     */
    @Singleton
    @Provides
    fun provideCreatureDao(database: CreatureRoomDatabase): CreatureDao {
        return database.creatureDao()
    }

    /**
     * UserPreferencesRoomDatabaseインスタンスの作り方をHiltに伝達
     * 事前定義のDBあり
     */
    @Singleton
    @Provides
    fun provideUserPreferencesRoomDatabase(
        @ApplicationContext context: Context
    ): UserPreferencesRoomDatabase {
        return Room.databaseBuilder(
            context,
            UserPreferencesRoomDatabase::class.java,
            "creatures"
        )
            .createFromAsset("database/creatures.db")
            .build()
    }

    /**
     * UserPreferencesDaoインスタンスの作り方をHiltに伝達
     */
    @Singleton
    @Provides
    fun provideUserPreferencesDao(database: UserPreferencesRoomDatabase): UserPreferencesDao {
        return database.userPreferencesDao()
    }

    /**
     * Applicationインスタンスの作り方をHiltに伝達
     */
    @Singleton
    @Provides
    fun provideApplication(): LivingThingsMapApplication {
        return LivingThingsMapApplication()
    }
}

/**
 * Repositoryインスタンスの作り方をHiltに伝達
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CreatureRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindCreatureRepository(
        repositoryImpl: CreatureRepositoryImpl
    ): CreatureRepository
}