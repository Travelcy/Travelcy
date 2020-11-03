package com.travelcy.travelcy.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings (
    val localCurrencyCode: String?,
    val foreignCurrencyCode: String?,
    val exchangeRatesLastUpdated: Int = 0,
    val autoUpdateExchangeRates: Boolean = true
){
    // We only want one settings row since we can't have multiple users
    // By setting the id always as 1 we make sure there will never be more than 1 line
    @PrimaryKey(autoGenerate = false) var id: Int = 1
}