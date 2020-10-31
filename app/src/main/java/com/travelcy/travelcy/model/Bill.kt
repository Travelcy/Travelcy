package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
class Bill(
    @PrimaryKey(autoGenerate = false) var id: Int = 1,
    var tipAmount: Double? = null,
    var tipPercentage: Double? = null,
    var taxPercentage: Double = 0.0
)