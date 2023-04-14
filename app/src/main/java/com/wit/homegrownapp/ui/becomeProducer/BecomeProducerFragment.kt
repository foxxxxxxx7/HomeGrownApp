package com.wit.homegrownapp.ui.becomeProducer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentBecomeProducerBinding
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.UserModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel

class BecomeProducerFragment : Fragment() {

    private var _binding: FragmentBecomeProducerBinding? = null
    private val binding get() = _binding!!
    private lateinit var becomeProducerViewModel: BecomeProducerViewModel
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBecomeProducerBinding.inflate(inflater, container, false)
        val root = binding.root
        activity?.title = getString(R.string.action_become_producer)

        becomeProducerViewModel = ViewModelProvider(this).get(BecomeProducerViewModel::class.java)

        setButtonListener(binding)

        return root
    }

    private fun setButtonListener(layout: FragmentBecomeProducerBinding) {
        layout.becomeProducerButton.setOnClickListener {
            val user = UserModel(
                fName = layout.fName.text.toString(),
                sName = layout.sName.text.toString(),
                username = layout.username.text.toString(),
                eircode = layout.eircode.text.toString(),
                bio = layout.bio.text.toString(),
                phoneNumber = layout.phoneNumber.text.toString(),
                businessEmail = layout.businessEmail.text.toString()
            )

            if (becomeProducerViewModel.validateUser(user)) {
                FirebaseDBManager.updateUser(loggedInViewModel.liveFirebaseUser, user)
                Toast.makeText(context, "Congratulations! You are now a producer!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_becomeProducerFragment_to_productListFragment)
            }
        }

        becomeProducerViewModel.validationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                BecomeProducerViewModel.ValidationStatus.InvalidInput -> Toast.makeText(
                    context,
                    R.string.invalid_input_error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
