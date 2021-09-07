package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.DateFormat.Companion.getDateString
import com.example.weatherapp.R
import com.example.weatherapp.model_forecast.Daily
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.weekly_weather_card.view.*

class WeeklyWeatherAdapter : RecyclerView.Adapter<WeeklyWeatherAdapter.ViewHolder>() {

    lateinit var weeklyWeatherList: MutableList<Daily>

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.weekly_weather_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weeklyWeatherList[position]
        holder.itemView.apply {
            weekly_card_date.text = getDateString(item.dt)

            val iconId = item.weather[0].icon
            Picasso.get().load("https://openweathermap.org/img/wn/$iconId@2x.png")
                .into(weekly_card_icon)


            val temp = item.temp.day.toInt()
            if (temp >= 0) {
                val aboveZeroTemperature = "+${temp}°"
                weekly_card_temp.text = aboveZeroTemperature
            } else {
                val subzeroTemperature = "-${temp}°"
                weekly_card_temp.text = subzeroTemperature
            }
        }
    }

    override fun getItemCount(): Int {
        return weeklyWeatherList.size - 1
    }
}