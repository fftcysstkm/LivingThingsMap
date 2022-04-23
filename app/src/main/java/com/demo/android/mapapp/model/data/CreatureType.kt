package com.demo.android.mapapp.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 生物タイプデータクラス
 */
@Entity(tableName = "M_CREATURE_TYPE")
data class CreatureType(
    @PrimaryKey
    val typeId: Long,
    /** 生物のタイプ（魚、鳥、哺乳類など） */
    val typeName: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
