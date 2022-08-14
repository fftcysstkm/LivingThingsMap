package com.demo.android.mapapp.model.creature

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * 生き物Dao
 */
@Dao
interface CreatureDao {

    /**
     * 生き物リストに表示する生き物を取得
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query(
        "SELECT creatureId, categoryId, creatureName " +
                "FROM CREATURE " +
                "ORDER BY categoryId, creatureId"
    )
    fun getCreatures(): Flow<List<Creature>>

    /**
     * 生き物リストに表示する生き物一覧をカテゴリーIDで取得
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query(
        "SELECT creatureId, categoryId, creatureName, memo " +
                "FROM CREATURE " +
                "WHERE categoryId = :categoryId " +
                "ORDER BY categoryId, creatureId"
    )
    fun getCreaturesByCatId(categoryId: Long): Flow<List<Creature>>


    /**
     * 生きもの詳細(creature_detail)を更新する
     */

    /**
     * 生き物をIDで取得
     * @param creatureId 生き物ID
     * @return Creature 生き物クラス
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT creatureId, categoryId, creatureName FROM Creature WHERE creatureId = :creatureId")
    fun getCreatureById(creatureId: Long): Creature

    /**
     * 生き物リストに表示する生き物を登録
     * @param creature 生き物クラス
     */
    @Insert
    suspend fun addCreature(creature: Creature)

    /**
     * 生きものリストに表示する生きものを更新する
     * @param creatureId 生き物Id
     * @param creatureName 生き物名
     * @param memo 備考
     */
    @Query("UPDATE Creature SET creatureName = :creatureName, memo = :memo WHERE creatureId = :creatureId")
    suspend fun updateCreature(creatureId: Long, creatureName: String, memo: String): Int

    /**
     * 生き物リスト表示中の生き物を削除する(creatureテーブルから)
     * @param 生き物ID
     */
    @Query("DELETE FROM Creature WHERE creatureId = :creatureId")
    suspend fun deleteCreatureById(creatureId: Long): Int

    /**
     * 生き物の詳細情報（位置情報）を登録
     * @param creatureDetail 生き物詳細情報クラス
     */
    @Insert
    suspend fun addCreatureDetail(creatureDetail: CreatureDetail)

    /**
     * 生き物の詳細情報（位置情報）を取得
     * @param creatureId 生き物ID
     */
    @Query(
        "SELECT creatureDetailId, creatureId, creatureNum, detailMemo, recordedAt, longitude, latitude " +
                "FROM CreatureDetail " +
                "WHERE creatureId = :creatureId"
    )
    fun getCreatureDetails(creatureId: Long): Flow<List<CreatureDetail>>

    /**
     * 生き物の詳細情報を更新する
     * @param creatureDetail 生き物詳細インスタンス
     */
    @Update
    suspend fun updateCreatureDetail(creatureDetail: CreatureDetail): Int

    /**
     * 生き物の詳細情報を削除する
     * @param creatureDetail 生き物詳細インスタンス
     */
    @Delete
    suspend fun deleteCreatureDetail(creatureDetail: CreatureDetail): Int

    /**
     * 生き物の詳細情報をIDで削除する
     * @param creatureId 生き物ID
     */
    @Query("DELETE FROM CreatureDetail WHERE creatureId = :creatureId")
    suspend fun deleteCreatureDetailById(creatureId: Long): Int
}