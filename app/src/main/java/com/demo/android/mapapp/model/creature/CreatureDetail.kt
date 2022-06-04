package com.demo.android.mapapp.model.creature

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 生き物詳細情報Entity(マップで記録、表示する生き物情報)
 */
@Entity(tableName = "CreatureDetail")
data class CreatureDetail(
    @PrimaryKey(autoGenerate = true)
    val creatureDetailId: Long,
    val creatureId: Long,
    val creatureNum: Int,
    val detailMemo: String?,
    val recordedAt: String,
    val longitude: Double,
    val latitude: Double
)
