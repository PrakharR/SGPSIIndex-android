package com.example.sgpsiindex.model

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("region_metadata") val regions : List<Region>,
    @SerializedName("items") val items : List<Item>
)