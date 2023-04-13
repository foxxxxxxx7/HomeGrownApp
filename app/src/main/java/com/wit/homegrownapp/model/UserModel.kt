package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
@Parcelize
data class UserModel(
//    var fName: String = "",
//    var sName: String = "",
    var uid: String = "",
    var email: String = "",
    var role: String = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
//            "fName" to fName,
//            "sName" to sName,
            "uid" to uid,
            "email" to email,
            "role" to role,

            )
    }
}