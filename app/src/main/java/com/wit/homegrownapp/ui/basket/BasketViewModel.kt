// BasketViewModel.kt
package com.wit.homegrownapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.BasketItemModel
import com.wit.homegrownapp.model.OrderModel
import com.wit.homegrownapp.model.ProductModel
import timber.log.Timber
import java.util.UUID

class BasketViewModel : ViewModel() {

    private val _basketItems = MutableLiveData<List<BasketItemModel>>()
    val basketItems: LiveData<List<BasketItemModel>> get() = _basketItems
    val user = FirebaseAuth.getInstance().currentUser

    init {
        _basketItems.value = mutableListOf()
        user?.let { FirebaseDBManager.getBasket(it.uid, _basketItems) }
    }

    fun addToBasket(product: ProductModel): Boolean {
        if (user?.uid == product.uid) {
            return false
        }
        val biid = UUID.randomUUID().toString()
        val basketItem = BasketItemModel(
            biid,
            product.pid,
            product.uid,
            product.type,
            product.variety,
            product.price,
            product.icon,
            product.quantity
        )
        _basketItems.value = _basketItems.value?.toMutableList()?.apply { add(basketItem) }
        user?.let { FirebaseDBManager.saveBasket(it.uid, _basketItems.value ?: emptyList()) }
        return true

    }

    fun updateBasketItemQuantity(biid: String, quantity: Int) {
        _basketItems.value = _basketItems.value?.map {
            if (it.biid == biid) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }
        user?.let { FirebaseDBManager.saveBasket(it.uid, _basketItems.value ?: emptyList()) }

    }

    fun removeBasketItem(biid: String) {
        _basketItems.value = _basketItems.value?.filter { it.biid != biid }
        user?.let { FirebaseDBManager.saveBasket(it.uid, _basketItems.value ?: emptyList()) }
    }

    fun placeOrder() {
        val oid = UUID.randomUUID().toString()
        val uid = user?.uid ?: return
        val status = "pending"
        val basketItems = _basketItems.value ?: emptyList()
        val totalPrice = basketItems.sumOf { it.price * it.quantity }
        val sellerUids = basketItems.map { it.uid }.distinct()

        val order = OrderModel(oid, uid, status, basketItems, totalPrice, sellerUids)

        FirebaseDBManager.saveOrder(order)

        // Empty the basket
        _basketItems.value = emptyList()
        user?.let { FirebaseDBManager.saveBasket(it.uid, emptyList()) }
    }

}
