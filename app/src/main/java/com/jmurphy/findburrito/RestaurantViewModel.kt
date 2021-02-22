package com.jmurphy.findburrito

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.jmurphy.findburrito.data.BurritoRepository
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application) : AndroidViewModel(application) {

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private var latitude = 0.0
    private var longitude = 0.0

    private val selectedRestuarant = MutableLiveData<GetRestaurantsQuery.Business>()

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
        selectedRestuarant.value = restaurant
    }

    fun getSelectedRestaurant(): LiveData<GetRestaurantsQuery.Business> {
        return selectedRestuarant
    }

}