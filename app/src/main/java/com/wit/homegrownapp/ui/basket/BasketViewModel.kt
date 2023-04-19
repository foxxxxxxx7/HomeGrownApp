// BasketViewModel.kt
package com.wit.homegrownapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wit.homegrownapp.model.BasketItemModel
import com.wit.homegrownapp.model.ProductModel
import java.util.UUID

class BasketViewModel : ViewModel() {

    private val _basketItems = MutableLiveData<List<BasketItemModel>>()
    val basketItems: LiveData<List<BasketItemModel>> get() = _basketItems

    init {
        _basketItems.value = mutableListOf()
    }

    fun addToBasket(product: ProductModel) {
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
    }

    fun updateBasketItemQuantity(biid: String, quantity: Int) {
        _basketItems.value = _basketItems.value?.map {
            if (it.biid == biid) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }
    }

    fun removeBasketItem(biid: String) {
        _basketItems.value = _basketItems.value?.filter { it.biid != biid }
    }
}
