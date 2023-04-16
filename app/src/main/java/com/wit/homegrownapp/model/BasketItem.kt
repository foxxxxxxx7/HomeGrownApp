// BasketItem.kt
package com.wit.homegrownapp.model

data class BasketItem(
    val id: String,
    val productId: String,
    val productName: String,
    val price: Double,
    var quantity: Int
)
