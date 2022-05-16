package com.app.weather.data

import com.google.gson.annotations.SerializedName

data class ForecastResponse(

	@field:SerializedName("cnt")
	val cnt: Int? = null,

	@field:SerializedName("cod")
	val cod: String? = null,

	@field:SerializedName("message")
	val message: Int? = null,

	@field:SerializedName("list")
	val list: List<ListItem?>? = null
)



data class ListItem(

	@field:SerializedName("dt_txt")
	val dtTxt: String? = null,

	@field:SerializedName("weather")
	val weather: List<WeatherItem?>? = null,

	@field:SerializedName("main")
	val main: Main? = null,
)

//data class WeatherItem(
//
//	@field:SerializedName("icon")
//	val icon: String? = null,
//
//	@field:SerializedName("description")
//	val description: String? = null,
//
//	@field:SerializedName("main")
//	val main: String? = null,
//
//	@field:SerializedName("id")
//	val id: Int? = null
//)

