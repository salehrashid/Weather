package com.app.weather.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.weather.data.WeatherResponse
import com.app.weather.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    val weatherByCity = MutableLiveData<WeatherResponse>()

    fun weatherByCity(city: String){
        ApiConfig.getApiServices().weatherByCity(city).enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) weatherByCity.postValue(response.body())
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Failure", t.message.toString())
            }
        })
    }

    fun getWeatherByCity(): LiveData<WeatherResponse> = weatherByCity
}