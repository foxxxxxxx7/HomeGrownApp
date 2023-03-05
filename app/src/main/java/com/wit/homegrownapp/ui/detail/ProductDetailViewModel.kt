package com.wit.homegrownapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductManager
import com.wit.homegrownapp.model.ProductModel
import timber.log.Timber
import java.lang.Exception

/* The ProductDetailViewModel class is a ViewModel class that holds a MutableLiveData object of type
ProductModel */
class ProductDetailViewModel : ViewModel() {
    private val product = MutableLiveData<ProductModel>()

    var observableProduct: LiveData<ProductModel>
        get() = product
        set(value) {
            product.value = value.value
        }

    /**
     * It gets the product details from the database.
     *
     * @param userid The user's id
     * @param id The id of the product you want to retrieve
     */
    fun getProduct(userid: String, id: String) {
        try {
            //ProductManager.findById(email, id, donation)
            FirebaseDBManager.findById(userid, id, product)
            Timber.i(
                "Detail getProduct() Success : ${
                    product.value.toString()
                }"
            )
        } catch (e: Exception) {
            Timber.i("Detail getProduct() Error : $e.message")
        }
    }

    /**
     * It deletes a product from the database.
     *
     * @param userid The userid of the user who created the product.
     * @param id The id of the product you want to delete.
     */
    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid, id)
            Timber.i("Product Deleted ")
        } catch (e: Exception) {
            Timber.i("Product Delete Error : $e.message")
        }
    }

    /**
     * It updates the product with the given id.
     *
     * @param userid The user's ID.
     * @param id The id of the product you want to update.
     * @param product ProductModel
     */
    fun updateProduct(userid: String, id: String, product: ProductModel) {
        try {
            FirebaseDBManager.update(userid, id, product)
            Timber.i("Detail update() Success : $product")
        } catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}