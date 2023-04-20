// BasketItemModel.kt
package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
@Parcelize
data class BasketItemModel(
    val biid: String,
    val pid: String,
    val uid: String,
    var type: String = "",
    var variety: String = "",
    val price: Double,
    var icon: Int = 0,
    var quantity: Int
) : Parcelable {
    constructor() : this("", "", "", "", "", 0.0, 0, 0)

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "biid" to biid,
            "pid" to pid,
            "uid" to uid,
            "type" to type,
            "variety" to variety,
            "price" to price,
            "icon" to icon,
            "quantity" to quantity,
            )
    }
}


