package com.example.weatherapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.example.weatherapp.DateFormat.Companion.getDateString
import com.example.weatherapp.adapters.HourlyWeatherAdapter
import com.example.weatherapp.adapters.WeeklyWeatherAdapter
import com.example.weatherapp.repository.Repository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_weather_screen.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*

class WeatherScreenFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: ViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestLocationPermissions()


        val repository = Repository()
        val viewModelProviderFactory = ViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(ViewModel::class.java)

        val weeklyWeatherAdapter = WeeklyWeatherAdapter()
        val hourlyWeatherAdapter = HourlyWeatherAdapter()

        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { currentWeatherResponse ->
                        val currentTemp = currentWeatherResponse.main.temp.toInt()
                        if (currentTemp >= 0) {
                            val aboveZeroTemperature = "+${currentTemp}°"
                            weather_screen_temp.text = aboveZeroTemperature
                        } else {
                            val subzeroTemperature = "-${currentTemp}°"
                            weather_screen_temp.text = subzeroTemperature
                        }
                        val date = getDateString(currentWeatherResponse.dt)
                        weather_screen_date.text = date
                        weather_screen_city.text = currentWeatherResponse.name
                        weather_screen_description.text =
                            currentWeatherResponse.weather[0].description
                        val iconId = currentWeatherResponse.weather[0].icon
                        Picasso.get().load("https://openweathermap.org/img/wn/$iconId@2x.png")
                            .into(weather_screen_icon)
                    }
                }
                is Resource.Error -> {
                    showProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })

        viewModel.forecastWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { forecastResponse ->
                        val dailyList = forecastResponse.daily.toMutableList()
                        val hourlyList = forecastResponse.hourly.toMutableList()


                        weather_screen_weekly_forecast_recycler_view.adapter = weeklyWeatherAdapter
                        weather_screen_hourly_forecast_recycler_view.adapter = hourlyWeatherAdapter

                        weeklyWeatherAdapter.weeklyWeatherList = dailyList
                        weeklyWeatherAdapter.notifyDataSetChanged()

                        hourlyWeatherAdapter.hourlyWeatherList = hourlyList
                        hourlyWeatherAdapter.notifyDataSetChanged()
                    }
                }
                is Resource.Error -> {
                    showProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })

        weather_screen_search_btn.setOnClickListener {
            if (weather_screen_city_search_field.text?.isNotEmpty() == true) {

                val newCity = weather_screen_city_search_field.text.toString()
                viewModel.getCurrentWeather(newCity)

                // скрыть клавиатуру
                val imm =
                    context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }


    }

    private fun showProgressBar() {
        weather_screen_progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        weather_screen_progress_bar.visibility = View.GONE
    }


    private fun requestLocationPermissions() {
        EasyPermissions.requestPermissions(
            this,
            "Для прогноза погоды в вашем городе введите город в поле сверху",
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_DENIED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val lat = location?.latitude.toString()
                    val lon = location?.longitude.toString()

                    val coordLength = lat.length - 1
                    val shortLatitude = lat.removeRange(5..coordLength).toDouble()
                    val shortLongitude = lon.removeRange(5..coordLength).toDouble()


                    viewModel.getForecast(shortLatitude, shortLongitude)
                    viewModel.getCurrentWeatherByCoord(shortLatitude, shortLongitude)

                }
        }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
            viewModel.getCurrentWeather("Moscow")
        } else {
            requestLocationPermissions()
        }
    }


}

class DateFormat {
    companion object {
        private val simpleDateFormat = SimpleDateFormat("E, dd MMMM", Locale("ru"))
        private val shortDateFormat = SimpleDateFormat("dd MMM", Locale("ru"))
        private val simpleTimeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
        fun getTimeString(time: Int): String = simpleTimeFormat.format(time * 1000L)
        fun getDateString(time: Int): String {
            val date = simpleDateFormat.format(time * 1000L)
            var result = ""
            if (date[4].equals('0')) {
                result = date.removeRange(4..4)
            } else return date
            return result
        }

        fun getShortDateString(time: Int): String {
            val date = shortDateFormat.format(time * 1000L)
            var result = ""
            if (date[0].equals('0')) {
                result = date.removeRange(0..0)
            } else return date
            return result
        }
    }
}