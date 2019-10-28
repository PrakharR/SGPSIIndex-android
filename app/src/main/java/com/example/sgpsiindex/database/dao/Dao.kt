package com.example.sgpsiindex.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.sgpsiindex.model.Response

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(response: Response)

    @Query("SELECT * FROM response")
    fun load(): LiveData<Response>

    @Query("DELETE FROM response")
    fun clear()

}