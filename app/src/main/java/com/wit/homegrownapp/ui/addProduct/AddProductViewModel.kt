package com.wit.homegrownapp.ui.addProduct

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import com.wit.homegrownapp.api.EircodeApiService
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductModel
import com.google.android.gms.maps.model.LatLng as GmsLatLng // Add this import at the top

class AddProductViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    private val eircodeApiService = EircodeApiService.create()

    suspend fun getCoordinatesFromEircode(eircode: String): GmsLatLng? {
        return try {
            val API_KEY = "AIzaSyDKf7OJJ4dJgaNFN5cBm1sWIj6RnGk_a4s"
            val response = eircodeApiService.findAddress(API_KEY, eircode)

            if (response.status == "OK" && response.results.isNotEmpty()) {
                val apiLatLng = response.results[0].geometry.location
                Log.d(
                    "EircodeDebug", "Coordinates fetched: ${apiLatLng.lat}, ${apiLatLng.lng}"
                ) // Log coordinates
                GmsLatLng(apiLatLng.lat, apiLatLng.lng) // Convert to GmsLatLng
            } else {
                Log.e("EircodeError", "Error fetching coordinates: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("EircodeError", "Error fetching coordinates: ${e.message}")
            null
        }
    }


    suspend fun addProduct(firebaseUser: MutableLiveData<FirebaseUser>, product: ProductModel) {
        try {
            val coordinates = getCoordinatesFromEircode(product.eircode)
            if (coordinates != null) {
                product.latitude = coordinates.latitude
                product.longitude = coordinates.longitude
                FirebaseDBManager.create(firebaseUser, product)
                status.postValue(true)
            } else {
                status.postValue(false)
            }
        } catch (e: Exception) {
            Log.e("EircodeError", "Error adding product: ${e.message}")
            status.postValue(false)
        }
    }

}