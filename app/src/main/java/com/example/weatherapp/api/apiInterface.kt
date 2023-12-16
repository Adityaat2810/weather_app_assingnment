package com.example.weatherapp.api

import com.example.weatherapp.dataClass.wetherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    // creating api interface to get weather information
    @GET("weather")
    fun getWeatherData(
        @Query("q") city: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Call<wetherData>

}
