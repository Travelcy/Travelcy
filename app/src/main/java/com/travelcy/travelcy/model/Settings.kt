package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "settings", foreignKeys = [
    ForeignKey(entity = Currency::class, parentColumns = arrayOf("id"), childColumns = arrayOf("localCurrencyId"), onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Currency::class, parentColumns = arrayOf("id"), childColumns = arrayOf("foreignCurrencyId"), onDelete = ForeignKey.CASCADE)
], indices = [Index("localCurrencyId", "foreignCurrencyId")]
)
data class Settings (
    val localCurrencyId: String,
    val foreignCurrencyId: String
){
    // We only want one settings row since we can't have multiple users
    // By setting the id always as 1 we make sure there will never be more than 1 line
    @PrimaryKey(autoGenerate = false) var id: Int = 1
}