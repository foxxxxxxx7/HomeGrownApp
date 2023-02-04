package com.wit.homegrownapp.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ProductStore {
    fun findAll(productList: MutableLiveData<List<ProductModel>>)
    fun findAll(userid: String, productList: MutableLiveData<List<ProductModel>>)
    fun findById(userid: String, productid: String, product: MutableLiveData<ProductModel> )
    fun create(uid: String, product: ProductModel)
    fun update(userid: String, productid: String, product: ProductModel)
    fun delete(userid: String, productid: String)
}