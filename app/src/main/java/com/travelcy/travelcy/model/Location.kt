package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class Location (
    // We only want one location row since the old location should be replaced with the new one
    // By setting the id always as 1 we make sure there will never be more than 1 line
    @PrimaryKey(autoGenerate = false) var id:Int=1,
    val country:String,
    val currencyCode:String
)
