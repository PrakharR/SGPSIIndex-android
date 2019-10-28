package com.example.sgpsiindex.database.converter

import androidx.room.TypeConverter
import com.example.sgpsiindex.model.Region
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegionTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(data: String?): List<Region>? {
        if (data == null) {
            return null
        }

        val listType = object : TypeToken<List<Region>>() {}.type
        return gson.fromJson<List<Region>>(data, listType)
    }

    @TypeConverter
    fun toString(data: List<Region>): String {
        return gson.toJson(data)
    }

}