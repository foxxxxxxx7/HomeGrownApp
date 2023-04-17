package com.wit.homegrownapp.ui.detail

import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductManager
import com.wit.homegrownapp.model.ProductModel
import timber.log.Timber
import java.lang.Exception

/* The ProductDetailViewModel class is a ViewModel class that holds a MutableLiveData object of type
ProductModel */
class ProductDetailViewModel : ViewModel() {
    private val product = MutableLiveData<ProductModel>()
    val isCurrentUserProduct = MutableLiveData<Boolean>(true)
    val user = FirebaseAuth.getInstance().currentUser


    var observableProduct: LiveData<ProductModel>
        get() = product
        set(value) {
            product.value = value.value
        }


    fun getProduct(id: String) {
        try {
            product.observeForever { updatedProduct ->
                Timber.i("Detail getProduct() Updated product: $updatedProduct")
                isCurrentUserProduct.value = (user?.uid == updatedProduct?.uid)
            }
            // Remove the userid parameter when calling findById
            ProductManager.findById(id, product)
        } catch (e: Exception) {
            Timber.i("Detail getProduct() Error : $e.message")
        }
    }


    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid, id)
            Timber.i("Product Deleted ")
        } catch (e: Exception) {
            Timber.i("Product Delete Error : $e.message")
        }
    }


    fun updateProduct(userid: String, id: String, product: ProductModel) {
        try {
            FirebaseDBManager.update(userid, id, product)
            Timber.i("Detail update() Success : $product")
        } catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }

    @InverseMethod("stringToDouble")
    fun doubleToString(value: Double): String {
        return value.toString()
    }

    fun stringToDouble(value: String): Double {
        return try {
            value.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }
}
