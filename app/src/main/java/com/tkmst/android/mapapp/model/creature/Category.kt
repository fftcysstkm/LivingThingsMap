package com.tkmst.android.mapapp.model.creature

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 生物カテゴリーEntity
 */
@Entity(tableName = "Category")
data class Category(
    @PrimaryKey
    val categoryId: Long,
    /** 生物のタイプ（魚、鳥、哺乳類など） */
    val categoryName: String,
    val displayOrder: Long
)
