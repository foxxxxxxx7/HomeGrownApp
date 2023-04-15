package com.wit.homegrownapp.model

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.firebase.database
import com.wit.homegrownapp.model.ProductManager.logAll
import timber.log.Timber
import java.lang.Exception

var lastUserId = 0L

internal fun getUserId(): Long {
    return lastUserId++
}

/* Creating a singleton object. */
object UserManager : UserStore {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    val users = ArrayList<UserModel>()

    override fun createUser(firebaseUser: MutableLiveData<FirebaseUser>, user: UserModel) {
        user.uid = getUserId().toString()
        users.add(user)
        logAll()
    }

    override fun updateUser(firebaseUser: MutableLiveData<FirebaseUser>, updatedUser: UserModel) {
        val index = users.indexOfFirst { it.uid == updatedUser.uid }
        if (index != -1) {
            users[index] = updatedUser
            logAll()
            firebaseUser.value?.let {
                FirebaseDBManager.updateUser(firebaseUser, updatedUser)
            } ?: run {
                Timber.e("FirebaseUser is null, cannot update user in Firebase")
            }
        } else {
            Timber.e("User not found with uid: ${updatedUser.uid}")
        }
    }
}
