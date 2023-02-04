package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(var title:String = "",
                        var price:Double = 0.0,
                        var category: String = "",
                        var avgWeight: Double = 0.0,
                        var description: String = "",
                        var eircode: String = "",
                        var uid: String = ""): Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {

        return  mapOf(
            "title" to title,
            "price" to price,
            "category" to category,
            "avgWeight" to avgWeight,
            "description" to description,
            "eircode" to eircode,
            "uid" to uid
        )
    }

}
