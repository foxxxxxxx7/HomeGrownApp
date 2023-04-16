package com.wit.homegrownapp.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentProductDetailBinding
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
import com.wit.homegrownapp.ui.detail.ProductDetailViewModel
import com.wit.homegrownapp.ui.productList.ProductListViewModel
import timber.log.Timber

/* This is the code for the ProductDetailFragment. It is a fragment that is used to display the details
of a product. */
class ProductDetailFragment : Fragment() {

    companion object {
        fun newInstance() = ProductDetailFragment()
    }

    private val args by navArgs<ProductDetailFragmentArgs>()
    private lateinit var detailViewModel: ProductDetailViewModel
    private var _fragBinding: FragmentProductDetailBinding? = null
    private val fragBinding get() = _fragBinding!!
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val productListViewModel: ProductListViewModel by activityViewModels()
    val user = FirebaseAuth.getInstance().currentUser



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentProductDetailBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_details)

        detailViewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)
        detailViewModel.observableProduct.observe(viewLifecycleOwner, Observer { render() })

        fragBinding.editProductButton.setOnClickListener {
            Timber.i("RFOX")
            Timber.i(detailViewModel.observableProduct.value?.uid!!)
            Timber.i(fragBinding.productvm?.observableProduct!!.value!!.toString())
            detailViewModel.updateProduct(
                user?.uid!!,
                detailViewModel.observableProduct.value?.pid!!,
                fragBinding.productvm?.observableProduct!!.value!!
            )

            productListViewModel.load()
            findNavController().navigateUp()

        }

        fragBinding.deleteProductButton.setOnClickListener {
            productListViewModel.delete(
                user?.uid!!, detailViewModel.observableProduct.value?.pid!!
            )
            findNavController().navigateUp()
        }
        return root
    }

    private fun render() {
        fragBinding.productvm = detailViewModel
        fragBinding.typeIcon.setImageResource(detailViewModel.observableProduct.value?.icon ?: 0)

        val productUid = detailViewModel.observableProduct.value?.uid
        val isEditable = productUid != null && productUid == user?.uid

        fragBinding.editTitle.isEnabled = isEditable
        fragBinding.editPrice.isEnabled = isEditable
        fragBinding.editAvgWeight.isEnabled = isEditable
        fragBinding.editDescription.isEnabled = isEditable
        fragBinding.editEirCode.isEnabled = isEditable

        fragBinding.editProductButton.visibility = if (isEditable) View.VISIBLE else View.GONE
        fragBinding.deleteProductButton.visibility = if (isEditable) View.VISIBLE else View.GONE
        fragBinding.addToBasketButton.visibility = if (isEditable) View.GONE else View.VISIBLE
    }



    override fun onResume() {
        super.onResume()
        val pid = args.productid
        Timber.i("ProductDetailFragment: Received productid: $pid")

        detailViewModel.getProduct(user?.uid!!, pid)
        detailViewModel.observableProduct.observe(viewLifecycleOwner, Observer { fetchedProduct ->
            if (fetchedProduct != null) {
                Timber.i("ProductDetailFragment: Fetched product: $fetchedProduct")
            } else {
                Timber.i("ProductDetailFragment: No product found with productid: $pid")
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

}