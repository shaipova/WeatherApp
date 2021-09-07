package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitInstance

class Repository {
    suspend fun getCurrentWeather(cityName: String) = RetrofitInstance.api.getCurrentWeather(cityName)
    suspend fun getForecast(latitude: Double, longitude: Double) = RetrofitInstance.api.getForecast(latitude, longitude)
}