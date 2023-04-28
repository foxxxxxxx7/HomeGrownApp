package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderModel(
    val oid: String = "",
    val uid: String = "",
    val status: String = "pending",
    val basketItems: List<BasketItemModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val sellerUids: List<String> = emptyList()
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "oid" to oid,
            "uid" to uid,
            "status" to status,
            "basketItems" to basketItems.map { it.toMap() },
            "totalPrice" to totalPrice,
            "sellerUids" to sellerUids
        )
    }
}
