package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class Currency(
    @PrimaryKey var id: String,
    var name: String,
    var exchangeRate: Double,
    var enabled: Boolean,
    var sort: Int
)