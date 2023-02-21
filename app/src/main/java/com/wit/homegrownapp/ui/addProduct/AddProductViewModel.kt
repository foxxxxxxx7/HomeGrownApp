package com.wit.homegrownapp.ui.addProduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductModel

class AddProductViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addProduct(uid: String, product: ProductModel) {
        status.value = try {
            FirebaseDBManager.create(uid,product)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}