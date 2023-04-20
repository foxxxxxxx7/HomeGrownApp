package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderModel(
    val oid: String,
    val uid: String,
    val basketItems: List<BasketItemModel>,
    val totalPrice: Double,
    val sellerUids: List<String>
) : Parcelable {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "oid" to oid,
            "uid" to uid,
            "basketItems" to basketItems.map { it.toMap() },
            "totalPrice" to totalPrice,
            "sellerUids" to sellerUids
        )
    }
}
