package com.example.weatherapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.CurrentWeatherResponse
import com.example.weatherapp.model_forecast.ForecastResponse
import com.example.weatherapp.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ViewModel(val repository: Repository) : ViewModel() {


    val currentWeather: MutableLiveData<Resource<CurrentWeatherResponse>> = MutableLiveData()
    var weatherResponse: CurrentWeatherResponse? = null

    val forecastWeather: MutableLiveData<Resource<ForecastResponse>> = MutableLiveData()
    var forecastResponse: ForecastResponse? = null


    // получить прогноз погоды по координатам на неделю и почасовой прогноз
    fun getForecast(latitude: Double, longitude: Double) = viewModelScope.launch {
        forecastWeather.value = Resource.Loading()
        val response = repository.getForecast(latitude, longitude)
        forecastWeather.value = handleDailyForecastResponse(response)
    }

    // получить данные по текущей погоде по названию города
    fun getCurrentWeather(cityName: String) = viewModelScope.launch {
        currentWeather.value = Resource.Loading()
        val response = repository.getCurrentWeather(cityName)
        currentWeather.value = handleWeatherResponse(response)
    }

    // получить данные по текущей погоде по координатам
    fun getCurrentWeatherByCoord(latitude: Double, longitude: Double) = viewModelScope.launch {
        currentWeather.value = Resource.Loading()
        val response = repository.getCurrentWeatherByCoord(latitude, longitude)
        currentWeather.value = handleWeatherResponse(response)
    }

    //широта и долгота
    var lat: Double = 0.0
    var lon: Double = 0.0

    //получить координаты запрашиваемого города
    private fun getCoord(){
        weatherResponse?.coord?.lat?.also {
            lat = it
        }
        weatherResponse?.coord?.lon?.also {
            lon = it
        }
        getForecast(lat, lon)
    }

    private fun handleWeatherResponse(response: Response<CurrentWeatherResponse>) : Resource<CurrentWeatherResponse>{
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                    weatherResponse = resultResponse
                    getCoord()
                return Resource.Success(weatherResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleDailyForecastResponse(response: Response<ForecastResponse>) : Resource<ForecastResponse>{
        if(response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if(forecastResponse == null) {
                    forecastResponse = resultResponse
                }
                return Resource.Success(forecastResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}