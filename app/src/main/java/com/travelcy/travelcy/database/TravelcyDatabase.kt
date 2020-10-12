package com.travelcy.travelcy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Settings

@Database(entities = [Currency::class, Settings::class], version = 1)
abstract class TravelcyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    abstract fun settingsDao(): SettingsDao
}