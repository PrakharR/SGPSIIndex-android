package com.example.sgpsiindex.database.converter

import androidx.room.TypeConverter
import com.example.sgpsiindex.model.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromString(data: String?): List<Item>? {
        if (data == null) {
            return null
        }

        val listType = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson<List<Item>>(data, listType)
    }

    @TypeConverter
    fun toString(data: List<Item>): String {
        return gson.toJson(data)
    }

}