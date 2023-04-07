package com.wit.homegrownapp.ui.map

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.wit.homegrownapp.model.ProductModel
import timber.log.Timber

/* This is the ViewModel for the MapFragment. It is responsible for getting the current location of the
user. */
@SuppressLint("MissingPermission")
class MapsViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var map: GoogleMap
    var currentLocation = MutableLiveData<Location>()
    var locationClient: FusedLocationProviderClient
    val locationRequest = LocationRequest.create().apply {
        interval = (2 * 60 * 1000)
        fastestInterval = (45 * 1000)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            currentLocation.value = locationResult.locations.last()
        }
    }

    val selectedProduct = MutableLiveData<ProductModel?>()

    /* This is the initializer block of the ViewModel. It is responsible for getting the current
    location of the user. */
    init {
        locationClient = LocationServices.getFusedLocationProviderClient(application)
        locationClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * > If the last location is successful, then add a success listener to the last location and set
     * the current location to the location
     */
    fun updateCurrentLocation() {
        if (locationClient.lastLocation.isSuccessful)
            locationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation.value = location!!
                }
        Timber.i("LOC : %s", currentLocation.value)
    }
}