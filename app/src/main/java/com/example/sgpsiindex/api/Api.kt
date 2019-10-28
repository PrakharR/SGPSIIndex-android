package com.example.sgpsiindex.api

import com.example.sgpsiindex.model.Response
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    companion object {
        const val BASE_URL = "https://api.data.gov.sg/"
        const val BASE_VERSION = "v1/"

        fun create(): Api {
            return Retrofit.Builder()
                .baseUrl(HttpUrl.parse(BASE_URL + BASE_VERSION)!!)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(Api::class.java)
        }
    }

    @GET("environment/psi")
    fun getEnvironmentPsi(@Query("date_time") dateTime: String): Call<Response>

}