package com.example.sgpsiindex.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "response")
data class Response (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @SerializedName("region_metadata") val regions : List<Region>,
    @SerializedName("items") val items : List<Item>
)