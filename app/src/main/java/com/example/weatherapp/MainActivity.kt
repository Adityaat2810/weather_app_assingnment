package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.api.ApiInterface
import com.example.weatherapp.dataClass.wetherData
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // for using view binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        // Function to fetch weather data by city name
        fetchWeatherData("meerut")    // meerut is default city

        //function to search for particular city  using search bar
        searchCity()

    }

    private fun searchCity() {

        val serchView = binding.searchView

        // passing search view query  to fetchWeatherData function to get query city detail
        serchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
               fetchWeatherData(p0!!)
                return true

            }

            override fun onQueryTextChange(p0: String?): Boolean {
               // fetchWeatherData(p0!!) // commenting this because it make so many request to api and update after data when me make nay change in query
                return true
            }
        })
   }

    // this function fetch weather data
    private fun fetchWeatherData(cityName: String) {
        // Update the displayed city name
        binding.tvName.text = cityName

        // Set up Retrofit for making API requests to OpenWeatherMap
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        // make a api call to get weather data
        val response = retrofit.getWeatherData(cityName, "", "metric")  // plese put you api key in appid
        response.enqueue(object : Callback<wetherData> {
            override fun onResponse(call: Call<wetherData>, response: Response<wetherData>) {

                // Handle the API response
                val responseBody = response.body()
                if (response.isSuccessful) {
                    // Extract relevant weather information from the response
                    val temperature = responseBody?.main?.temp
                    val dayName = dayName(System.currentTimeMillis())
                    val currentDate = date()

                    // Display the temperature and other weather information in the UI
                    Toast.makeText(this@MainActivity, "$temperature", Toast.LENGTH_LONG).show()
                    binding.tvMainDescription.text = "${responseBody?.weather?.firstOrNull()?.main ?: "unknown"} "
                    binding.tvTemp.text = "${temperature} C"
                    binding.tvHumidity.text = "humid ${responseBody?.main?.humidity} %"
                    binding.tvSpeed.text = "${responseBody?.wind?.speed}"
                    binding.tvMin.text = "min ${responseBody?.main?.temp_min}C"
                    binding.tvMax.text = "max ${responseBody?.main?.temp_max}C"
                    binding.tvSunriseTime.text = "${responseBody?.sys?.sunrise}"
                    binding.tvSunsetTime.text = "${responseBody?.sys?.sunset}"
                    binding.country.text = "${responseBody?.sys?.country}"

                    // Change images in the UI based on the weather condition
                    changeImagesAccordingToWeather(responseBody?.weather?.firstOrNull()?.main ?: "unknown")
                }
                else{
                    Toast.makeText(this@MainActivity , "Some error occured" , Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<wetherData>, t: Throwable) {
                // Handle API call failure
                Log.e("WeatherApp", "API call failed: ${t.message}", t)

                // You can display an error message to the user
                Toast.makeText(this@MainActivity, "Failed to fetch weather data. Please try again later.", Toast.LENGTH_SHORT).show()


            }

        })
    }


    //cahgeing ui Images according to weather condition
    private fun changeImagesAccordingToWeather(condition:String) {
        when(condition){
            "Haze" ,"Smoke","Clouds"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)

            }
            "Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            }
            "Rainy","rainy","rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
            }




        }
    }

    // function that get current day
    fun dayName(timestamp: Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    // function to get current time
    fun date():CharSequence?{
        val sdf= SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
}

