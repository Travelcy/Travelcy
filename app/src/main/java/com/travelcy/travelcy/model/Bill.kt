package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
class Bill(
    @PrimaryKey(autoGenerate = false) var id: Int = 1
)