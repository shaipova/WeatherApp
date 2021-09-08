package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.DateFormat.Companion.getShortDateString
import com.example.weatherapp.DateFormat.Companion.getTimeString
import com.example.weatherapp.R
import com.example.weatherapp.model_forecast.Hourly
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.hourly_weather_card.view.*


class HourlyWeatherAdapter : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    lateinit var hourlyWeatherList: MutableList<Hourly>

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.hourly_weather_card, parent, false)
        return HourlyViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = hourlyWeatherList[position]
        holder.itemView.apply {

            val temp = item.temp.toInt()
            if (temp >= 0) {
                val aboveZeroTemperature = "+${temp}°"
                hourly_card_temp.text = aboveZeroTemperature
            } else {
                val subzeroTemperature = "-${temp}°"
                hourly_card_temp.text = subzeroTemperature
            }

            hourly_card_time.text = getTimeString(item.dt)
            hourly_card_date.text = getShortDateString(item.dt)

            val iconId = item.weather[0].icon
            Picasso.get().load("https://openweathermap.org/img/wn/$iconId@2x.png")
                .into(hourly_card_icon)


        }
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }

}