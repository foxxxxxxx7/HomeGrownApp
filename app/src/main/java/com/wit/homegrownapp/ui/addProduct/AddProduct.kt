package com.wit.homegrownapp.ui.addProduct

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentAddProductBinding
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel

class AddProduct : Fragment() {

    companion object {
        fun newInstance() = AddProduct()
    }

    private var _fragBinding:   FragmentAddProductBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val viewModel: AddProductViewModel by activityViewModels()
    var state: Parcelable? = null
    var day = 0
    var searching: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragBinding = FragmentAddProductBinding.inflate(inflater, container, false)
        val root = fragBinding.root



        fragBinding.addProductButton.setOnClickListener {
            val title = fragBinding.editTitle.text.toString()
            val prod = ProductModel()
            prod.title = title
            viewModel.addProduct(loggedInViewModel.liveFirebaseUser.value!!.uid, prod )

        }
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}