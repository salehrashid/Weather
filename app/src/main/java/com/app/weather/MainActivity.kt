package com.app.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weather.data.ForecastResponse
import com.app.weather.data.WeatherResponse
import com.app.weather.databinding.ActivityMainBinding
import com.app.weather.ui.MainViewModel
import com.app.weather.ui.WeatherAdapter
import com.app.weather.utils.HelperFunction.formatterDegree
import com.app.weather.utils.sizeIconWeather4x
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private val mAdapter by lazy { WeatherAdapter() }
    private var isLoading: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        searchCity()
        getWeatherByCity()
        getWeatherByCurrentLocation()
        loadingStateView()
    }

    private fun getWeatherByCity() {
        viewModel.getWeatherByCity().observe(this) { setupView(it, null) }
        viewModel.getForecastByCity().observe(this) { setupView(null, it) }
    }

    private fun setupView(weather: WeatherResponse?, forecast: ForecastResponse?) {
        binding.apply {
            weather?.let {
                tvCity.text = it.name
                tvDegree.text = formatterDegree(it.main?.temp)

                val iconId = it.weather?.get(0)?.icon
                val iconUrl = BuildConfig.ICON_URL + iconId + sizeIconWeather4x
                Glide.with(this@MainActivity).load(iconUrl)
                    .into(imgIcWeather)

                setupBackgroundImage(it.weather?.get(0)?.id, iconId)
            }
        }
        mAdapter.setData(forecast?.list)
        binding.rvWeather.apply {
            layoutManager =
                LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }
    }

    private fun setupBackgroundImage(idWeather: Int?, icon: String?) {
        idWeather?.let {
            when (idWeather) {
                in resources.getIntArray(R.array.thunderstorm_id_list) ->
                    setImageBackground(R.drawable.thunderstorm)
                in resources.getIntArray(R.array.drizzle_id_list) ->
                    setImageBackground(R.drawable.drizzle)
                in resources.getIntArray(R.array.rain_id_list) ->
                    setImageBackground(R.drawable.rain)
                in resources.getIntArray(R.array.freezing_rain_id_list) ->
                    setImageBackground(R.drawable.freezing_rain)
                in resources.getIntArray(R.array.sleet_id_list) ->
                    setImageBackground(R.drawable.sleet)

                in resources.getIntArray(R.array.clear_id_list) -> {
                    when (icon) {
                        "01d" -> setImageBackground(R.drawable.clear)
                        "01n" -> setImageBackground(R.drawable.clear_night)
                    }
                }

                in resources.getIntArray(R.array.clouds_id_list) ->
                    setImageBackground(R.drawable.lightcloud)
                in resources.getIntArray(R.array.heavy_clouds_id_list) ->
                    setImageBackground(R.drawable.heavycloud)
                in resources.getIntArray(R.array.fog_id_list) ->
                    setImageBackground(R.drawable.fog)
                in resources.getIntArray(R.array.sand_id_list) ->
                    setImageBackground(R.drawable.sand)
                in resources.getIntArray(R.array.dust_id_list) ->
                    setImageBackground(R.drawable.dust)
                in resources.getIntArray(R.array.volcanic_ash_id_list) ->
                    setImageBackground(R.drawable.volcanic)
                in resources.getIntArray(R.array.squalls_id_list) ->
                    setImageBackground(R.drawable.squalls)
                in resources.getIntArray(R.array.tornado_id_list) ->
                    setImageBackground(R.drawable.tornado)

            }
        }
    }

    private fun setImageBackground(image: Int) {
        Glide.with(this).load(image).into(binding.imgBgWeather)
    }

    private fun getWeatherByCurrentLocation() {
        isLoading = true
        loadingStateView()
        val fusedLocationLocation: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1000
            )
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationLocation.lastLocation
            .addOnSuccessListener {
                try {
                    val lat = it.latitude
                    val lon = it.longitude

//                    viewModel.weatherByCurrentLocation(lat, lon)
//                    viewModel.forecastByCurrentLocation(lat, lon)

                } catch (e: Throwable) {
                    Log.e("MainActivity", "last location coordinate $it")
                    Log.e("MainActivity", "Couldn't get lat & lon")
                }
            }
            .addOnFailureListener {
                Log.e("MainActivity", "failed getting current location")
            }

        viewModel.weatherByCurrentLocation(1.9, 9.9)
        viewModel.forecastByCurrentLocation(1.9, 9.9)
        viewModel.getWeatherByCurrentLocation().observe(this) {
            setupView(it, null)
        }
        viewModel.getforecastByCurrentLocation().observe(this) {
            setupView(null, it)
            isLoading = false
            loadingStateView()
        }
    }

    private fun searchCity() {
        binding.edtSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        isLoading = true
                        loadingStateView()
                        try {
                            val inputMethodManager =
                                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                        }catch (e: Throwable){
                            Log.e("MainActvity", e.toString())
                        }
                        viewModel.weatherByCity(it)
                        viewModel.forecastByCity(it)
                        isLoading = false
                        loadingStateView()
                    }
                    return true
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    return false
                }
            }
        )
    }

    private fun loadingStateView() {
        binding.apply{
            when(isLoading){
                true -> {
                    layoutWeather.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                }
                false -> {
                    layoutWeather.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                }
                else -> {
                    layoutWeather.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
}