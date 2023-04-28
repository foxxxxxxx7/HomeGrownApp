package com.wit.homegrownapp.ui.order

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.OrderModel
import timber.log.Timber
import java.lang.Exception

class OrderViewModel : ViewModel() {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    val user = FirebaseAuth.getInstance().currentUser
    var readOnly = MutableLiveData(false)

    val orders = MutableLiveData<List<OrderModel>>()

    fun loadRequestedOrders() {
        try {
            FirebaseDBManager.findRequestedOrders(
                user!!.uid, object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tempList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            if (order?.uid == user.uid) {
                                order?.let { tempList.add(it) }
                            }
                        }
                        orders.value = tempList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.i("Requested orders Load Error : ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Timber.i("Requested orders Load Error : $e.message")
        }
    }

    fun loadReceivedOrders() {
        try {
            FirebaseDBManager.findReceivedOrders(
                user!!.uid, object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tempList = mutableListOf<OrderModel>()
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            if (order?.sellerUids?.contains(user.uid) == true) {
                                order?.let { tempList.add(it) }
                            }
                        }
                        orders.value = tempList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.i("Received orders Load Error : ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Timber.i("Received orders Load Error : $e.message")
        }
    }


}