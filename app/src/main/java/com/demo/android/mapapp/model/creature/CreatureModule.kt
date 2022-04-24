package com.demo.android.mapapp.model.creature

import android.content.Context
import androidx.room.Room
import com.demo.android.mapapp.repository.creature.CreatureRepository
import com.demo.android.mapapp.repository.creature.CreatureRepositoryImpl
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
object CreatureModule {

    /**
     * Databaseインスタンスの作り方をHiltに伝達
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
     * Daoインスタンスの作り方をHiltに伝達
     */
    @Singleton
    @Provides
    fun provideCreatureDao(database: CreatureRoomDatabase): CreatureDao {
        return database.creatureDao()
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