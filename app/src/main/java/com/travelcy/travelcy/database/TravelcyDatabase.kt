package com.travelcy.travelcy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.travelcy.travelcy.database.dao.BillDao
import com.travelcy.travelcy.model.Currency
import com.travelcy.travelcy.database.dao.CurrencyDao
import com.travelcy.travelcy.database.dao.SettingsDao
import com.travelcy.travelcy.model.Bill
import com.travelcy.travelcy.model.BillItem
import com.travelcy.travelcy.model.Settings

@Database(entities = [Currency::class, Settings::class, Bill::class, BillItem::class], version = 1)
abstract class TravelcyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao

    abstract fun settingsDao(): SettingsDao

    abstract fun billDao(): BillDao

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