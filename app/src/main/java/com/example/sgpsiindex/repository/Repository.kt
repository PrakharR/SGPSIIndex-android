package com.example.sgpsiindex.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sgpsiindex.api.Api
import com.example.sgpsiindex.database.Database
import com.example.sgpsiindex.model.Response
import com.example.sgpsiindex.model.State
import retrofit2.Call
import retrofit2.Callback
import java.util.concurrent.Executor
import org.json.JSONObject
import java.lang.Exception


class Repository(
    private val api: Api,
    private val database: Database,
    private val executor: Executor) {

    fun getData(): LiveData<Response> {
        return database.dao().load()
    }

    fun refresh(dateTime: String): LiveData<State> {
        val state = MutableLiveData<State>()
        state.value = State.LOADING

        api.getEnvironmentPsi(dateTime).enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful) {
                    executor.execute {
                        database.dao().clear()
                        database.dao().save(response.body()!!)

                        state.postValue(State.LOADED)
                    }
                } else {
                    try {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        state.value = State.error(jsonObject.getString("message"))
                    } catch (ignore: Exception) {
                        state.value = State.error("unknown")
                    }
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                state.value = State.error(t.message)
            }
        })

        return state
    }

}