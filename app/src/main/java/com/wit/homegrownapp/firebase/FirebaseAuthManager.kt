package com.wit.homegrownapp.firebase

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.wit.homegrownapp.R
import com.wit.homegrownapp.firebase.FirebaseDBManager.createUser
import com.wit.homegrownapp.model.UserModel
import timber.log.Timber


/* This is the class that is used to manage the authentication of the user. */
class FirebaseAuthManager(application: Application) {

    private var application: Application? = null

    var firebaseAuth: FirebaseAuth? = null
    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    var loggedOut = MutableLiveData<Boolean>()
    var errorStatus = MutableLiveData<Boolean>()
    var googleSignInClient = MutableLiveData<GoogleSignInClient>()

    /* This is the init block of the class. It is called when the class is instantiated. */
    init {
        this.application = application
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth!!.currentUser != null) {
            liveFirebaseUser.postValue(firebaseAuth!!.currentUser)
            loggedOut.postValue(false)
            errorStatus.postValue(false)
            FirebaseImageManager.checkStorageForExistingProfilePic(
                firebaseAuth!!.currentUser!!.uid
            )
        }
        configureGoogleSignIn()
    }



    fun login(email: String?, password: String?) {
        firebaseAuth!!.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(application!!.mainExecutor, { task ->
                if (task.isSuccessful) {
                    liveFirebaseUser.postValue(firebaseAuth!!.currentUser)
                    errorStatus.postValue(false)
                } else {
                    Timber.i("Login Failure: $task.exception!!.message")
                    errorStatus.postValue(true)
                }
            })
    }


    fun register(email: String?, password: String?) {
        firebaseAuth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnSuccessListener(application!!.mainExecutor) { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    liveFirebaseUser.postValue(firebaseUser!!)
                    val uid = firebaseUser.uid
                    errorStatus.postValue(false)
                    createUser(MutableLiveData(firebaseUser), UserModel(uid = uid, email = email, role = "user"))
                } else {
                    Timber.i("Registration Failure: FirebaseUser is null")
                    errorStatus.postValue(true)
                }
            }.addOnFailureListener { exception ->
                Timber.i("Registration Failure: $exception.message")
                errorStatus.postValue(true)
            }
    }




    fun logOut() {
        firebaseAuth!!.signOut()
        Timber.i("firebaseAuth Signed out")
        googleSignInClient.value!!.signOut()
        Timber.i("googleSignInClient Signed out")
        loggedOut.postValue(true)
        errorStatus.postValue(false)
    }


    private fun configureGoogleSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application!!.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient.value = GoogleSignIn.getClient(application!!.applicationContext, gso)
    }


    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Timber.i("DonationX firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(application!!.mainExecutor) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update with the signed-in user's information
                    Timber.i("signInWithCredential:success")
                    liveFirebaseUser.postValue(firebaseAuth!!.currentUser)

                } else {
                    // If sign in fails, display a message to the user.
                    Timber.i("signInWithCredential:failure $task.exception")
                    errorStatus.postValue(true)
                }
            }
    }
}