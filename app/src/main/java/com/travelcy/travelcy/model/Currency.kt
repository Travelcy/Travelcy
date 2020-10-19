package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey val id: String,
    val name: String,
    val exchangeRate: Double
)