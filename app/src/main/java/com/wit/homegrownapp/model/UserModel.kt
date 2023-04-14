package com.wit.homegrownapp.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize
@Parcelize
data class UserModel(
    var fName: String? = null,
    var sName: String? = null,
    var username: String? = null,
    var eircode: String? = null,
    var bio: String? = null,
    var phoneNumber: String? = null,
    var businessEmail: String? = null,
    var uid: String = "",
    var email: String? = "",
    var role: String = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fName" to fName,
            "sName" to sName,
            "username" to username,
            "eircode" to eircode,
            "bio" to bio,
            "phoneNumber" to phoneNumber,
            "businessEmail" to businessEmail,
            "uid" to uid,
            "email" to email,
            "role" to role,

            )
    }
}