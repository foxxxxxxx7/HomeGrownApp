package com.wit.homegrownapp.firebase

import android.os.LocaleList
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.model.ProductStore
import timber.log.Timber

/* This is the database reference to the Firebase Realtime Database. */
var database: DatabaseReference =
    FirebaseDatabase.getInstance("https://homegrown-c0ca9-default-rtdb.firebaseio.com/")
        .getReference()


object FirebaseDBManager : ProductStore {

    /**
     * It gets all the products from the database and adds them to a list
     *
     * @param productList MutableLiveData<List<ProductModel>>
     */
    override fun findAll(productList: MutableLiveData<List<ProductModel>>) {
        val localList = mutableListOf<ProductModel>()
        var totallist = ArrayList<ProductModel>()
        database.child("products")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Product error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    val children = snapshot.children
                    children.forEach {
                        val product = it.getValue(ProductModel::class.java)
                        localList.add(product!!)
                    }
                    database.child("products")
                        .removeEventListener(this)

                    totallist.addAll(localList)
                    productList.value = totallist
                }
            })
    }

    /* This is a function that is used to find all the products that are associated with a user. */
    override fun findAll(userid: String, productList: MutableLiveData<List<ProductModel>>) {
        Timber.i(userid)
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
                    productList.value = localList
                }
            })
    }

    /**
     * It gets the product from the database.
     *
     * @param userid The user id of the user who created the product.
     * @param productid The id of the product to be retrieved
     * @param product MutableLiveData<ProductModel>
     */
    override fun findById(userid: String, productid: String, product: MutableLiveData<ProductModel> ) {

        database.child("user-products").child(userid)
            .child(productid).get().addOnSuccessListener {
                product.value = it.getValue(ProductModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener {
                Timber.e("firebase Error getting data $it")
            }
    }

    /**
     * We create a new product, assign it a unique ID, and then add it to the database
     *
     * @param uid The user ID of the user who created the product.
     * @param product ProductModel - The product model that we want to add to the database.
     * @return A Task<Void>
     */
    override fun create(uid: String, product: ProductModel) {
        Timber.i("Firebase DB Reference : $database")

        product.uid = uid
        val key = database.child("products").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        product.uid = key
        val productValues = product.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/products/$key"] = productValues
        childAdd["/user-products/${product.uid}/$key"] = productValues

        database.updateChildren(childAdd)
    }


    override fun delete(userid: String, productid: String) {

        val childDelete: MutableMap<String, Any?> = HashMap()
        childDelete["/products/$productid"] = null
        childDelete["/user-products/$userid/$productid"] = null

        database.updateChildren(childDelete)
    }


    override fun update(userid: String, productid: String, product: ProductModel) {

        val productValue = product.toMap()

        val childUpdate: MutableMap<String, Any?> = HashMap()
        childUpdate["products/$productid"] = productValue
        childUpdate["user-products/$userid/$productid"] = productValue

        database.updateChildren(childUpdate)
    }


    fun updateImageRef(userid: String, imageUri: String) {

        val userProducts = database.child("user-products").child(userid)
        val allProducts = database.child("products")

        userProducts.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all products that match 'it'
                        val product = it.getValue(ProductModel::class.java)
                        allProducts.child(product!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }

}