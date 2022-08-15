package com.tkmst.android.mapapp.model.creature

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 生き物Entity(トップ画面のリストに表示)
 */
@Entity(tableName = "Creature")
data class Creature(
    @PrimaryKey(autoGenerate = true)
    val creatureId: Long,
    val categoryId: Long,
    val creatureName: String,
    val scientificName: String?,
    val memo: String?
)
