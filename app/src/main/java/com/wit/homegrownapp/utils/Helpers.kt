package com.wit.homegrownapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.wit.homegrownapp.R
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.io.IOException

/**
 * It creates a dialog with a loading icon and returns it
 *
 * @param activity The activity in which the loader is to be displayed.
 * @return A function that takes an activity and returns an AlertDialog.
 */
fun createLoader(activity: FragmentActivity): AlertDialog {
    val loaderBuilder = AlertDialog.Builder(activity)
        .setCancelable(true) // 'false' if you want user to wait
        .setView(R.layout.loading)
    var loader = loaderBuilder.create()
    loader.setTitle(R.string.app_name)
    loader.setIcon(R.mipmap.ic_launcher_round)

    return loader
}

/**
 * If the loader is not showing, set the title to the message and show the loader.
 *
 * @param loader The loader to show.
 * @param message The message to be displayed in the loader.
 */
fun showLoader(loader: AlertDialog, message: String) {
    if (!loader.isShowing) {
        loader.setTitle(message)
        loader.show()
    }
}

/**
 * It hides the loader.
 *
 * @param loader AlertDialog
 */
fun hideLoader(loader: AlertDialog) {
    if (loader.isShowing)
        loader.dismiss()
}

/**
 * It takes an activity as a parameter and displays a toast message to the user.
 *
 * @param activity The activity that is calling the function.
 */
fun serviceUnavailableMessage(activity: FragmentActivity) {
    Toast.makeText(
        activity,
        "Booking Service Unavailable. Try again later",
        Toast.LENGTH_LONG
    ).show()
}

/**
 * It shows a toast message.
 *
 * @param activity The activity that is calling the function.
 */
fun serviceAvailableMessage(activity: FragmentActivity) {
    Toast.makeText(
        activity,
        "Bookinig Contacted Successfully",
        Toast.LENGTH_LONG
    ).show()
}

/**
 * `showImagePicker` is a function that takes an `ActivityResultLauncher<Intent>` as a parameter and
 * returns nothing.
 *
 * @param intentLauncher This is the ActivityResultLauncher that we get from the
 * registerForActivityResult() method.
 */
fun showImagePicker(intentLauncher: ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_profile_image.toString())
    intentLauncher.launch(chooseFile)
}

/**
 * If the resultCode is Activity.RESULT_OK and the data is not null, then return the data.data
 *
 * @param resultCode The result code of the activity.
 * @param data Intent? - The intent that was used to start the activity.
 * @return The URI of the image that was selected.
 */
fun readImageUri(resultCode: Int, data: Intent?): Uri? {
    var uri: Uri? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            uri = data.data
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return uri
}

/**
 * Create a rounded transformation with a white border and a corner radius of 35dp.
 */
fun customTransformation(): Transformation =
    RoundedTransformationBuilder()
        .borderColor(Color.WHITE)
        .borderWidthDp(2F)
        .cornerRadiusDp(35F)
        .oval(false)
        .build()

