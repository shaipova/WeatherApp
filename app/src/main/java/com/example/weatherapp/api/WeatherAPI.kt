package com.example.weatherapp.api

import com.example.weatherapp.Constants.Companion.API_KEY
import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model_forecast.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q")
        cityName: String,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = "metric",
        @Query("lang")
        language: String = "ru"
    ) : Response<CurrentWeatherResponse>

    @GET("data/2.5/weather")
    suspend fun getCurrentWeatherByCoord(
        @Query("lat")
        latitude: Double,
        @Query("lon")
        longitude: Double,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = "metric",
        @Query("lang")
        language: String = "ru"
    ) : Response<CurrentWeatherResponse>

    @GET("data/2.5/onecall")
    suspend fun getForecast(
        @Query("lat")
        latitude: Double,
        @Query("lon")
        longitude: Double,
        @Query("exclude")
        exclude: String = "minutely,alerts",
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = "metric",
        @Query("lang")
        language: String = "ru"
    ) : Response<ForecastResponse>


}