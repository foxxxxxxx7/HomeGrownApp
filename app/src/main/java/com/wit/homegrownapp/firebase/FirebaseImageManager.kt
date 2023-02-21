package com.wit.homegrownapp.firebase

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.wit.homegrownapp.utils.customTransformation
import timber.log.Timber
import java.io.ByteArrayOutputStream
import com.squareup.picasso.Target
import com.wit.homegrownapp.firebase.FirebaseImageManager.storage


/* This is a singleton object. */
object FirebaseImageManager {
    var storage = FirebaseStorage.getInstance().reference
    var imageUri = MutableLiveData<Uri>()

    /**
     * If the user has a profile picture, download it from Firebase Storage and set the imageUri to the
     * download URL. If the user doesn't have a profile picture, set the imageUri to an empty Uri
     *
     * @param userid The user's id
     */
    fun checkStorageForExistingProfilePic(userid: String) {
        val imageRef = storage.child("photos").child("${userid}.jpg")
        val defaultImageRef = storage.child("ic_book_nav_header.png")

        imageRef.metadata.addOnSuccessListener { //File Exists
            imageRef.downloadUrl.addOnCompleteListener { task ->
                imageUri.value = task.result!!
            }
            //File Doesn't Exist
        }.addOnFailureListener {
            imageUri.value = Uri.EMPTY
        }
    }

    /**
     * If the image exists, update it. If it doesn't, upload it
     *
     * @param userid The user's id
     * @param bitmap The bitmap of the image you want to upload
     * @param updating Boolean - This is a boolean that tells the function whether or not the user is
     * updating their profile picture.
     */
    fun uploadImageToFirebase(userid: String, bitmap: Bitmap, updating: Boolean) {
        // Get the data from an ImageView as bytes
        val imageRef = storage.child("photos").child("${userid}.jpg")
        //val bitmap = (imageView as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        lateinit var uploadTask: UploadTask

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        imageRef.metadata.addOnSuccessListener { //File Exists
            if (updating) // Update existing Image
            {
                uploadTask = imageRef.putBytes(data)
                uploadTask.addOnSuccessListener { ut ->
                    ut.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                        imageUri.value = task.result!!
                        FirebaseDBManager.updateImageRef(userid, imageUri.value.toString())
                    }
                }
            }
        }.addOnFailureListener { //File Doesn't Exist
            uploadTask = imageRef.putBytes(data)
            uploadTask.addOnSuccessListener { ut ->
                ut.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                    imageUri.value = task.result!!
                }
            }
        }
    }

    /**
     * `updateUserImage` is a function that takes a userid, imageUri, imageView, and a boolean value.
     * It then uses Picasso to load the imageUri into the imageView, and uploads the image to Firebase
     *
     * @param userid The user's id
     * @param imageUri The Uri of the image you want to upload.
     * @param imageView ImageView - The imageView that will be updated with the image
     * @param updating Boolean - This is a flag to determine if the user is updating their profile or
     * not.
     */
    fun updateUserImage(userid: String, imageUri: Uri?, imageView: ImageView, updating: Boolean) {
        Picasso.get().load(imageUri)
            .resize(200, 200)
            .transform(customTransformation())
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(
                    bitmap: Bitmap?,
                    from: Picasso.LoadedFrom?
                ) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userid, bitmap!!, updating)
                    imageView.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(
                    e: java.lang.Exception?,
                    errorDrawable: Drawable?
                ) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }

    /**
     * > This function takes a userid, a resource, and an imageview. It then loads the resource into
     * the imageview, and uploads the resource to firebase
     *
     * @param userid The user's id
     * @param resource The resource ID of the image to be loaded.
     * @param imageView ImageView - the imageView that will be updated with the image
     */
    fun updateDefaultImage(userid: String, resource: Int, imageView: ImageView) {
        Picasso.get().load(resource)
            .resize(200, 200)
            .transform(customTransformation())
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(
                    bitmap: Bitmap?,
                    from: Picasso.LoadedFrom?
                ) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userid, bitmap!!, false)
                    imageView.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(
                    e: java.lang.Exception?,
                    errorDrawable: Drawable?
                ) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            })
    }
}