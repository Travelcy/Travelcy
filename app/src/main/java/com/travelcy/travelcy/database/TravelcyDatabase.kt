package com.travelcy.travelcy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.LocationDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Settings
import com.travelcy.travelcy.model.Location

@Database(entities = [Currency::class, Location::class, Settings::class], version = 1)
abstract class TravelcyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    abstract fun locationDao(): LocationDao

    abstract fun settingsDao(): SettingsDao

    companion object {
        private val DB_NAME = "travelcy_database"

        @Volatile
        private var INSTANCE: TravelcyDatabase? = null

        fun getInstance(context: Context): TravelcyDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TravelcyDatabase::class.java,
                        DB_NAME
                    )
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}