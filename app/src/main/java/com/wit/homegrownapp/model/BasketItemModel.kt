// BasketItemModel.kt
package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
@Parcelize
data class BasketItemModel(
    val biid: String,
    val pid: String,
    val productName: String,
    val price: Double,
    var quantity: Int
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "biid" to biid,
            "pid" to pid,
            "productName" to productName,
            "price" to price,
            "quantity" to quantity,
            )
    }
}


