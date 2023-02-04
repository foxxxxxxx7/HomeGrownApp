package com.wit.homegrownapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(var title:String = "",
                        var price:Double = 0.0,
                        var category: String = "",
                        var avgWeight: Double = 0.0,
                        var description: String = "",
                        var eircode: String = ""): Parcelable
