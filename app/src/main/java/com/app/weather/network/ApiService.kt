package com.app.weather.network

import com.app.weather.BuildConfig.API_KEY
import com.app.weather.data.ForecastResponse
import com.app.weather.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun weatherByCity(
        @Query("q") location: String,
        @Query("appid") api_key: String = API_KEY
    ): Call<WeatherResponse>

    @GET("forecast")
    fun forecastByCity(
        @Query("q") location: String,
        @Query("appid") api_key: String = API_KEY
    ): Call<ForecastResponse>

    @GET("weather")
    fun weatherByCurrentLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") api_key: String = API_KEY
    ): Call<WeatherResponse>

    @GET("forecast")
    fun forecastByCurrentLocation(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") api_key: String = API_KEY
    ): Call<ForecastResponse>
}