package com.example.sgpsiindex.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sgpsiindex.database.converter.ItemTypeConverter
import com.example.sgpsiindex.database.converter.RegionTypeConverter
import com.example.sgpsiindex.database.dao.Dao
import com.example.sgpsiindex.model.Response

@androidx.room.Database(
    entities = arrayOf(Response::class),
    version = 1,
    exportSchema = false
)
@TypeConverters(ItemTypeConverter::class, RegionTypeConverter::class)
abstract class Database : RoomDatabase() {

    companion object {
        fun create(context: Context): Database {
            val databaseBuilder = Room
                .databaseBuilder(context, Database::class.java, "main.db")
            return databaseBuilder
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun dao(): Dao

}