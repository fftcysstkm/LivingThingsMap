package com.demo.android.mapapp.model.creature

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * 見つけたことのある生き物データクラス
 */
@Entity(tableName = "T_CREATURE")
data class Creature(
    @PrimaryKey(autoGenerate = true)
    val creatureId: Long,
    val typeId: Long,
    val creatureName: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
