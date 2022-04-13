package com.demo.android.mapapp.data

import java.time.LocalDateTime


data class Creature(
    val id: Int,
    val type: String,
    val name: String,
    val createdAt: LocalDateTime
)
