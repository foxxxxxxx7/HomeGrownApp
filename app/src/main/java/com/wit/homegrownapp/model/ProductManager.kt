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

/**
 * It returns a unique ID
 *
 * @return A Long
 */
var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

/* Creating a singleton object. */
object ProductManager : ProductStore {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    val products = ArrayList<ProductModel>()

    /**
     * It returns a list of products.
     *
     * @param productsList MutableLiveData<List<ProductModel>>
     */
    override fun findAll(productsList: MutableLiveData<List<ProductModel>>) {
        //return products
    }

    /**
     * We are getting the userid from the user, then we are getting the products from the database,
     * then we are adding the products to the local list, then we are removing the event listener, then
     * we are setting the value of the products list to the local list
     *
     * @param userid The user id of the user whose products are to be retrieved.
     * @param productsList MutableLiveData<List<ProductModel>>
     */
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

    /**
     * It finds a product by its id.
     *
     * @param userid The user's id
     * @param productid The id of the product to be found
     * @param product MutableLiveData<ProductModel>
     */
    //from ProductManager
    override fun findById(userid: String, productid: String, product: MutableLiveData<ProductModel>) {
        val foundProduct: ProductModel? = products.find { it.uid == userid }
        product.value = foundProduct
    }

//
//    override fun create(uid: String, product: ProductModel) {
//        product.uid = getId().toString()
//        products.add(product)
//        logAll()
//    }

    /**
     * It adds a product to the list of products.
     *
     * @param firebaseUser MutableLiveData<FirebaseUser>
     * @param product ProductModel - This is the product object that you want to create.
     */
    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, product: ProductModel) {
        product.uid = getId().toString()
        products.add(product)
        logAll()
    }

    /**
     * > The function `logAll()` iterates over the `products` list and logs each product
     */
    fun logAll() {
        Timber.v("** Products List **")
        products.forEach { Timber.v("Product ${it}") }
    }

    /**
     * It deletes a product from the database.
     *
     * @param userid The userid of the user who made the product
     * @param productid The id of the product to be deleted
     */
    override fun delete(userid: String, productid: String) {
        //  products.remove(productid)
    }

    /**
     * The function takes in a userid, productid and a product object. It then searches for the product
     * object in the products array and if it finds it, it updates the product object with the new
     * values
     *
     * @param userid The user's ID
     * @param productid The id of the product to update
     * @param product ProductModel - this is the product object that is being updated
     */
    override fun update(userid: String, productid: String, product: ProductModel) {
        val foundProduct: ProductModel? = products.find { p -> p.uid == product.uid }
        if (foundProduct != null) {
            foundProduct.title = product.title
            foundProduct.price = product.price
            foundProduct.category = product.category
            foundProduct.avgWeight = product.avgWeight
            foundProduct.description = product.description
            foundProduct.eircode = product.eircode
            /*foundProduct.image = bike.image
            foundProduct.lat = bike.lat
            foundProduct.lng = bike.lng
            foundProduct.zoom = bike.zoom*/
            logAll()
        }
    }

}