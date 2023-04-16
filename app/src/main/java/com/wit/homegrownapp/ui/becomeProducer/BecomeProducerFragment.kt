package com.wit.homegrownapp.ui.becomeProducer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentBecomeProducerBinding
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.firebase.FirebaseImageManager
import com.wit.homegrownapp.model.UserModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel

class BecomeProducerFragment : Fragment() {

    private var _binding: FragmentBecomeProducerBinding? = null
    private val binding get() = _binding!!
    private lateinit var becomeProducerViewModel: BecomeProducerViewModel
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBecomeProducerBinding.inflate(inflater, container, false)
        val root = binding.root
        activity?.title = getString(R.string.action_become_producer)

        becomeProducerViewModel = ViewModelProvider(this).get(BecomeProducerViewModel::class.java)

        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data
                    if (imageUri != null) {
                        val userId = loggedInViewModel.liveFirebaseUser.value?.uid
                        if (userId != null) {
                            FirebaseImageManager.updateUserImage(
                                userId,
                                imageUri,
                                binding.profilePicture,
                                true
                            )
                        }
                    }
                }
            }

        loadProfileImage()
        initProfileImageClickListener()
        setButtonListener(binding)

        return root
    }

    private fun initProfileImageClickListener() {
        binding.profilePicture.setOnClickListener {
            showImagePicker(intentLauncher)
        }
    }

    private fun showImagePicker(intentLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intentLauncher.launch(intent)
    }

    private fun setButtonListener(layout: FragmentBecomeProducerBinding) {
        layout.becomeProducerButton.setOnClickListener {
            val user = UserModel(
                uid = loggedInViewModel.liveFirebaseUser.value!!.uid, // Add this line
                email = loggedInViewModel.liveFirebaseUser.value!!.email, // Add this line
                fName = layout.fName.text.toString(),
                sName = layout.sName.text.toString(),
                username = layout.username.text.toString(),
                eircode = layout.eircode.text.toString(),
                bio = layout.bio.text.toString(),
                phoneNumber = layout.phoneNumber.text.toString(),
                businessEmail = layout.businessEmail.text.toString(),
                role = "producer"
            )

            if (becomeProducerViewModel.validateUser(user)) {
                FirebaseDBManager.updateUser(loggedInViewModel.liveFirebaseUser, user)
                Toast.makeText(
                    context,
                    "Congratulations! You are now a producer!",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().navigate(R.id.action_becomeProducerFragment_to_productListFragment)
            }
        }

        becomeProducerViewModel.validationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                BecomeProducerViewModel.ValidationStatus.FirstNameEmpty -> Toast.makeText(
                    context,
                    R.string.first_name_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                BecomeProducerViewModel.ValidationStatus.SurnameEmpty -> Toast.makeText(
                    context,
                    R.string.surname_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                BecomeProducerViewModel.ValidationStatus.UsernameEmpty -> Toast.makeText(
                    context,
                    R.string.username_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                BecomeProducerViewModel.ValidationStatus.EircodeEmpty -> Toast.makeText(
                    context,
                    R.string.eircode_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                BecomeProducerViewModel.ValidationStatus.PhoneNumberEmpty -> Toast.makeText(
                    context,
                    R.string.phone_number_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                BecomeProducerViewModel.ValidationStatus.BusinessEmailInvalid -> Toast.makeText(
                    context,
                    R.string.business_email_invalid_error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadProfileImage() {
        val userId = loggedInViewModel.liveFirebaseUser.value?.uid
        if (userId != null) {
            FirebaseImageManager.checkStorageForExistingProfilePic(userId)
            FirebaseImageManager.imageUri.observe(viewLifecycleOwner) { imageUrl ->
                if (imageUrl != Uri.EMPTY && imageUrl != null) {
                    Log.d("BecomeProducerFragment", "Image URL: $imageUrl")
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.apple)
                        .error(R.drawable.cherry)
                        .into(binding.profilePicture)
                } else {
                    Log.d("BecomeProducerFragment", "Image URL is null")
                }
            }
        } else {
            Log.d("BecomeProducerFragment", "User ID is null")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
