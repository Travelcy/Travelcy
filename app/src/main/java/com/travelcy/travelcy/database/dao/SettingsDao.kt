package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.travelcy.travelcy.model.Settings

@Dao
interface SettingsDao {
    @Insert(onConflict = REPLACE)
    fun updateSettings(settings: Settings)

    @Query("UPDATE settings set localCurrencyCode = :localCurrencyCode where id = 1")
    fun updateLocalCurrencyCode(localCurrencyCode: String)

    @Query("UPDATE settings set foreignCurrencyCode = :foreignCurrencyCode where id = 1")
    fun updateForeignCurrencyCode(foreignCurrencyCode: String)

    @Query("UPDATE settings set exchangeRatesLastUpdated = :exchangeRatesLastUpdated where id = 1")
    fun updateExchangeRatesLastUpdated(exchangeRatesLastUpdated: Long)

    @Query("UPDATE settings set autoUpdateExchangeRates = :autoUpdateExchangeRates where id = 1")
    fun updateAutoUpdateExchangeRates(autoUpdateExchangeRates: Boolean)

    @Query("SELECT COUNT(*) FROM settings where id = 1")
    fun hasSettings(): Boolean

    @Query("SELECT * FROM settings where id = 1")
    fun getSettingsRaw(): Settings

    @Query("SELECT * FROM settings where id = 1")
    fun getSettings(): LiveData<Settings>
}