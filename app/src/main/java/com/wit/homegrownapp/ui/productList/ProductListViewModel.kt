package com.wit.homegrownapp.ui.productList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
import timber.log.Timber
import java.lang.Exception

/* This is the ViewModel for the ProductListActivity. It is responsible for loading the products from
the database and displaying them in the RecyclerView. */
class ProductListViewModel : ViewModel() {

    private var productsList = MutableLiveData<List<ProductModel>>()

    val observableProductList: LiveData<List<ProductModel>>
        get() = productsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    val user = FirebaseAuth.getInstance().currentUser
    var readOnly = MutableLiveData(false)
    //var  ussser = LoggedInViewModel.liveFirebaseUser


    /* Calling the load function when the view model is created. */
    init {
        load()
    }

    // fun findAll(): List<ProductModel> {
    //     return BookManager.products
    // }


    fun load() {
        try {
            readOnly.value = false
            //DonationManager.findAll(liveFirebaseUser.value?.email!!, donationsList)
            Timber.i("Product List 1")
            //Timber.i(liveFirebaseUser.value?.uid!!)
            Timber.i(user?.uid!!)
            //Timber.i(productsList.toString())
            FirebaseDBManager.findAll(
//                liveFirebaseUser.value?.uid!!,
                user?.uid!!,
                // "3kl1HSOCtVa7gLexgdnDgmzhRun1",
                productsList
            )
            Timber.i("productsList List Load Success : ${productsList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Product List Load Error : $e.message")
        }
    }


    fun delete(userid: String, id: String) {
        try {
            //DonationManager.delete(userid,id)
            FirebaseDBManager.delete(userid, id)
            Timber.i("Product Deleted ")
        } catch (e: Exception) {
            Timber.i("Product Delete Error : $e.message")
        }
    }

    /**
     * It deletes a product from the database.
     *
     * @param userid The user's ID.
     * @param uid The unique id of the product list
     */
    fun del(userid: String, uid: String) {
        try {
            //DonationManager.delete(userid,id)
            FirebaseDBManager.delete(userid, uid)
            Timber.i("Product List Delete Success")
        } catch (e: Exception) {
            Timber.i("Product List Delete Error : $e.message")
        }
    }


    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(productsList)
            Timber.i("Product List LoadAll Success : ${productsList.value.toString()}")
        } catch (e: Exception) {
            Timber.i("Product List LoadAll Error : $e.message")
        }
    }
}