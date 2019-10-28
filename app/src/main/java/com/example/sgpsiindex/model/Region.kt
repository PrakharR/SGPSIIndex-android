package com.example.sgpsiindex.model

import com.google.gson.annotations.SerializedName

data class Region (
    @SerializedName("name") val name : String,
    @SerializedName("label_location") val labelLocation : HashMap<String, Double>
)