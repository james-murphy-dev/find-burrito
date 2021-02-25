package com.jmurphy.findburrito

import android.app.Application
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.model.CameraPosition
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.jmurphy.findburrito.data.BurritoRepository
import com.jmurphy.findburrito.data.RestaurantContent
import kotlinx.coroutines.launch
import java.io.IOException

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private var latitude = 0.0
    private var longitude = 0.0

    private val selectedRestuarant = MutableLiveData<RestaurantContent.RestaurantItem>()
    private val coordinates = MutableLiveData<Result<RestaurantContent.Location>>()

    val burritoRepository = BurritoRepository(getApplication())

    val nearbyRestaurants = burritoRepository.burritoLiveData

    fun initLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication())
    }

    fun getLatitude(): Double{
        return latitude;
    }

    fun getLongitude(): Double{
        return longitude;
    }

    fun getNearbyRestaurants() : LiveData<List<GetRestaurantsQuery.Business?>> {

        try{
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->


                latitude = location.latitude
                longitude = location.longitude
                viewModelScope.launch {
                    val radius = getApplication<Application>().resources.getInteger(R.integer.search_radius_miles)
                    burritoRepository.getNearbyRestaurants(location.latitude, location.longitude, radius)
                }
            }

        }catch (e: SecurityException){

        }

        return nearbyRestaurants
    }

    fun setSelectedRestaurant(restaurant: GetRestaurantsQuery.Business){
        val item = RestaurantContent.RestaurantItem(
                name = restaurant.name!!,
                address = restaurant.location?.formatted_address!!,
                price = restaurant.price!!,
                phone = restaurant.phone!!
        )
        selectedRestuarant.value = item
    }

    fun getSelectedRestaurant(): LiveData<RestaurantContent.RestaurantItem> {

        return Transformations.map(selectedRestuarant) { restaurant ->

            try {
                val geocoder = Geocoder(getApplication())
                val addresses = geocoder.getFromLocationName(restaurant.address, 1)

                if (addresses.size > 0) {
                    val latitude = addresses.get(0).latitude
                    val longitude = addresses.get(0).longitude
                    val location = RestaurantContent.Location(latitude, longitude)
                    restaurant.coordinates = location
                }

            } catch (e: IOException) {
                //show error message
                Log.e("LocationError", e.toString())
            }

            restaurant
        }
    }

}