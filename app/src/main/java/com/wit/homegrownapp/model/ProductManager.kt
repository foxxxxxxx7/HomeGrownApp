package com.wit.homegrownapp.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.firebase.database
import timber.log.Timber
import java.lang.Exception

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

object ProductManager : ProductStore {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    val products = ArrayList<ProductModel>()

    override fun findAll(productsList: MutableLiveData<List<ProductModel>>) {
        //return products
    }


    override fun findAll(userid: String, productsList: MutableLiveData<List<ProductModel>>) {

        database.child("user-products").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Product error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ProductModel>()
                    val children = snapshot.children
                    children.forEach {
                        val product = it.getValue(ProductModel::class.java)
                        localList.add(product!!)
                    }
                    database.child("user-products").child(userid)
                        .removeEventListener(this)

                    productsList.value = localList
                }
            })
    }

    override fun findById(
        productid: String, product: MutableLiveData<ProductModel>
    ) {
        Timber.i("ProductManager.findById() called with productid: $productid")

        database.child("products").child(productid).get().addOnSuccessListener {
            val fetchedProduct = it.getValue(ProductModel::class.java)
            if (fetchedProduct != null) {
                product.postValue(fetchedProduct)
                Timber.i("firebase Got value ${it.value}")
            } else {
                product.postValue(null)
                Timber.i("firebase Product not found")
            }
        }.addOnFailureListener {
            Timber.e("firebase Error getting data $it")
            product.postValue(null)
        }
    }



    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, product: ProductModel) {
        product.uid = getId().toString()
        products.add(product)
        logAll()
    }


    fun logAll() {
        Timber.v("** Products List **")
        products.forEach { Timber.v("Product ${it}") }
    }


    override fun delete(userid: String, productid: String) {
        //  products.remove(productid)
    }


    override fun update(userid: String, productid: String, product: ProductModel) {
        val foundProduct: ProductModel? = products.find { p -> p.uid == product.uid }
        if (foundProduct != null) {
            foundProduct.title = product.title
            foundProduct.price = product.price
            foundProduct.category = product.category
            foundProduct.avgWeight = product.avgWeight
            foundProduct.description = product.description
            foundProduct.eircode = product.eircode
            logAll()
        }
    }

}