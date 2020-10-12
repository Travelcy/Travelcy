package com.travelcy.travelcy.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.travelcy.travelcy.model.Settings
import com.travelcy.travelcy.database.entity.SettingsWithCurrencies

@Dao
interface SettingsDao {
    @Insert(onConflict = REPLACE)
    fun updateSettings(settings: Settings)

    @Transaction
    @Query("SELECT * FROM settings where id = 1")
    fun getSettings(): LiveData<SettingsWithCurrencies>
}