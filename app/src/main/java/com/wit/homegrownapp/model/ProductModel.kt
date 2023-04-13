package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
@Parcelize
data class ProductModel(
    var title: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var type: String = "",
    var variety: String = "",
    var avgWeight: Double = 0.0,
    var description: String = "",
    var eircode: String = "",
    var producerimage: String = "" ,
    var productimage: String = "",
    var icon: Int = 0,
    var pid: String = "",
    var uid: String = "",
    var latitude: Double = 0.0, // Add this
    var longitude: Double = 0.0 // Add this
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "price" to price,
            "category" to category,
            "type" to type,
            "variety" to variety,
            "avgWeight" to avgWeight,
            "description" to description,
            "eircode" to eircode,
            "producerimage" to producerimage,
            "productimage" to productimage,
            "icon" to icon,
            "pid" to pid,
            "uid" to uid,
            "latitude" to latitude, // Add this
            "longitude" to longitude // Add this
        )
    }
}