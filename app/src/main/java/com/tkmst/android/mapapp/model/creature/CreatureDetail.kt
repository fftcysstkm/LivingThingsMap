package com.tkmst.android.mapapp.model.creature

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tkmst.android.mapapp.model.date.RecordDateTime

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
    val recordedAt: RecordDateTime,
    val longitude: Double,
    val latitude: Double
)
