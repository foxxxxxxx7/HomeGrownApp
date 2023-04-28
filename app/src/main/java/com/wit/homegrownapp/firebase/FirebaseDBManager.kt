package com.wit.homegrownapp.firebase

import android.os.LocaleList
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.wit.homegrownapp.model.*
import timber.log.Timber

/* This is the database reference to the Firebase Realtime Database. */
var database: DatabaseReference =
    FirebaseDatabase.getInstance("https://homegrown-c0ca9-default-rtdb.firebaseio.com/").reference


object FirebaseDBManager : ProductStore, UserStore {


    override fun findAll(productList: MutableLiveData<List<ProductModel>>) {
        val localList = mutableListOf<ProductModel>()
        var totallist = ArrayList<ProductModel>()
        database.child("products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase Product error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                val children = snapshot.children
                children.forEach {
                    val product = it.getValue(ProductModel::class.java)
                    localList.add(product!!)
                }
                database.child("products").removeEventListener(this)

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
                        Timber.i("Fetched product: $product")
                        localList.add(product!!)
                    }
                    database.child("user-products").child(userid).removeEventListener(this)
                    productList.value = localList
                }
            })
    }

    override fun findById(
        productid: String, product: MutableLiveData<ProductModel>
    ) {
        Timber.i("FirebaseDBManager.findById() called with productid: $productid")

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
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("products").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        product.uid = uid // Set uid as the user's uid
        product.pid = key // Set pid as the generated key
        val productValues = product.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/products/$key"] = productValues
        childAdd["/user-products/$uid/$key"] = productValues

        database.updateChildren(childAdd).addOnCompleteListener {
            if (it.isSuccessful) {
                val imageURL = FirebaseImageManager.imageUri.value.toString()
                saveProducerImageToProduct(key, imageURL)
                saveProducerImageToUserProduct(
                    uid, key, imageURL
                ) // Save image to user-products as well
            } else {
                Timber.e("Failed to save product: ${it.exception}")
            }
        }
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

        userProducts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    //Update Users imageUri
                    it.ref.child("profilePics").setValue(imageUri)
                    //Update all products that match 'it'
                    val product = it.getValue(ProductModel::class.java)
                    allProducts.child(product!!.uid).child("profilePics").setValue(imageUri)
                }
            }
        })
    }


    fun saveProducerImageToProduct(productID: String, imageURL: String) {
        val productRef = database.child("products").child(productID)
        productRef.child("producerimage").setValue(imageURL)
    }

    fun saveProducerImageToUserProduct(userId: String, productId: String, imageUrl: String) {
        val userProductRef = database.child("user-products").child(userId).child(productId)
        userProductRef.child("producerimage").setValue(imageUrl)
    }

    fun saveProducerImageToUser(userId: String, imageUrl: String) {
        val userRef = database.child("users").child(userId)
        userRef.child("producerimage").setValue(imageUrl)
    }


    override fun createUser(firebaseUser: MutableLiveData<FirebaseUser>, user: UserModel) {
        val uid = firebaseUser.value!!.uid
        val userValues = user.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uid"] = userValues

        database.updateChildren(childUpdates).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("User created successfully")
            } else {
                Timber.e("Failed to create user: ${it.exception}")
            }
        }
    }


    // Add this method to FirebaseDBManager
    override fun updateUser(firebaseUser: MutableLiveData<FirebaseUser>, updatedUser: UserModel) {
        val uid = firebaseUser.value!!.uid
        val updatedUserValues = updatedUser.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/users/$uid"] = updatedUserValues

        database.updateChildren(childUpdates).addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.i("User updated successfully")
                val imageURL = FirebaseImageManager.imageUri.value.toString()
                saveProducerImageToUser(
                    uid, imageURL
                ) // Update the producer image in the user collection
            } else {
                Timber.e("Failed to update user: ${it.exception}")
            }
        }
    }

    fun getUserRole(uid: String, userRole: MutableLiveData<String>) {
        database.child("users").child(uid).child("role")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase User Role error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    userRole.value = snapshot.getValue(String::class.java)
                }
            })
    }

    fun saveBasket(uid: String, basketItems: List<BasketItemModel>) {
        val basketItemsMap =
            basketItems.associateBy { it.biid }.mapValues { (_, item) -> item.toMap() }
        database.child("user-baskets").child(uid).setValue(basketItemsMap)
    }

    fun getBasket(uid: String, basketItemsLiveData: MutableLiveData<List<BasketItemModel>>) {
        database.child("user-baskets").child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val basketItems =
                        snapshot.children.mapNotNull { it.getValue(BasketItemModel::class.java) }
                    basketItemsLiveData.postValue(basketItems)
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e("Failed to get basket: ${error.message}")
                }
            })
    }

    fun saveOrder(order: OrderModel) {
        val orderValues = order.toMap()
        database.child("orders").child(order.oid).setValue(orderValues)
        database.child("user-orders").child(order.uid).child(order.oid).setValue(orderValues)
    }

    fun findRequestedOrders(uid: String, callback: ValueEventListener) {
        database.child("user-orders").child(uid)
            .addListenerForSingleValueEvent(callback)
    }

    fun findReceivedOrders(uid: String, callback: ValueEventListener) {
        database.child("orders")
            .orderByChild("sellerUids/$uid")
            .equalTo(true)
            .addListenerForSingleValueEvent(callback)
    }





}