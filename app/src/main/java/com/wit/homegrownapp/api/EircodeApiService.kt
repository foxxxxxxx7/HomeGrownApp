package com.wit.homegrownapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface EircodeApiService {
    @GET("maps/api/geocode/json")
    suspend fun findAddress(
        @Query("key") apiKey: String,
        @Query("address") eircode: String
    ): GeocodingResponse

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"

        fun create(): EircodeApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(EircodeApiService::class.java)
        }
    }
}


data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val geometry: Geometry
)

data class Geometry(
    val location: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)