package com.example.sgpsiindex.model

import com.google.gson.annotations.SerializedName

data class Item (
    @SerializedName("timestamp") val timestamp : String,
    @SerializedName("update_timestamp") val updateTimestamp : String,
    @SerializedName("readings") val readings : HashMap<String, HashMap<String, Double>>
)