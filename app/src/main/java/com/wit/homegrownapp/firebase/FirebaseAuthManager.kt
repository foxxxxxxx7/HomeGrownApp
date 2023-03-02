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


    /**
     * > The function takes in two parameters, email and password, and then uses Firebase's
     * signInWithEmailAndPassword function to sign in the user. If the sign in is successful, the
     * current user is posted to the liveFirebaseUser MutableLiveData object, and the errorStatus
     * MutableLiveData object is set to false. If the sign in is unsuccessful, the errorStatus
     * MutableLiveData object is set to true
     *
     * @param email The email address of the user.
     * @param password String?
     */
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

    /**
     * We're creating a new user with the email and password provided, and if the task is successful,
     * we're posting the current user to the liveFirebaseUser MutableLiveData object, and if it's not
     * successful, we're posting an error to the errorStatus MutableLiveData object
     *
     * @param email The email address of the user.
     * @param password String? - The password that the user will use to login.
     */
    fun register(email: String?, password: String?) {
        firebaseAuth!!.createUserWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(application!!.mainExecutor, { task ->
                if (task.isSuccessful) {
                    liveFirebaseUser.postValue(firebaseAuth!!.currentUser)
                    errorStatus.postValue(false)
                } else {
                    Timber.i("Registration Failure: $task.exception!!.message")
                    errorStatus.postValue(true)
                }
            })
    }

    /**
     * It logs out the user from the app.
     */
    fun logOut() {
        firebaseAuth!!.signOut()
        Timber.i("firebaseAuth Signed out")
        googleSignInClient.value!!.signOut()
        Timber.i("googleSignInClient Signed out")
        loggedOut.postValue(true)
        errorStatus.postValue(false)
    }

    /**
     * `configureGoogleSignIn()` is a function that configures the GoogleSignInOptions object with the
     * default web client id and requests the user's email
     */
    private fun configureGoogleSignIn() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application!!.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient.value = GoogleSignIn.getClient(application!!.applicationContext, gso)
    }

    /**
     * The function takes a GoogleSignInAccount object as a parameter, creates a GoogleAuthProvider
     * object, and then uses the GoogleAuthProvider object to sign in to Firebase
     *
     * @param acct GoogleSignInAccount
     */
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