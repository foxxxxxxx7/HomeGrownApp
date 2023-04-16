package com.wit.homegrownapp.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface UserStore {
    fun createUser(firebaseUser: MutableLiveData<FirebaseUser>, user: UserModel)
    fun updateUser(firebaseUser: MutableLiveData<FirebaseUser>, updatedUser: UserModel)

}