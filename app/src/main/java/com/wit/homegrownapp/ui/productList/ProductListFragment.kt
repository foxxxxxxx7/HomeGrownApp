package com.wit.homegrownapp.ui.productList

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.wit.homegrownapp.R
import com.wit.homegrownapp.adapters.ProductAdapter
import com.wit.homegrownapp.adapters.ProductListener
import com.wit.homegrownapp.databinding.FragmentProductListBinding
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
import com.wit.homegrownapp.utils.*
import timber.log.Timber

/*  */
class ProductListFragment : Fragment(), ProductListener {

    //  lateinit var app: BikeshopApp
    private var _fragBinding: FragmentProductListBinding? = null
    private val fragBinding get() = _fragBinding!!
    lateinit var loader: AlertDialog
    val user = FirebaseAuth.getInstance().currentUser
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    private val productListViewModel: ProductListViewModel by activityViewModels()
    private val userRole: MutableLiveData<String> = MutableLiveData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        FirebaseDBManager.getUserRole(user?.uid!!, userRole)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentProductListBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_productlist)
        loader = createLoader(requireActivity())

        fragBinding.recyclerView.layoutManager = LinearLayoutManager(activity)


//        fragBinding.recyclerView.adapter =
//           ProductAdapter(ProductListViewModel.findAll(), this@ProductListFragment)
        val fab: FloatingActionButton = fragBinding.fab
        fab.setOnClickListener {
            val action = ProductListFragmentDirections.actionProductListToAddProduct()
            findNavController().navigate(action)
        }


        // ProductListViewModel.load()
        productListViewModel.observableProductList.observe(
            viewLifecycleOwner,
            Observer { products ->
                products?.let {
                    render(products as ArrayList<ProductModel>)
                    hideLoader(loader)
                    checkSwipeRefresh()
                }
            })

        setSwipeRefresh()

        val swipeDeleteHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Timber.i("hello456")
                showLoader(loader, "Deleting Product")
                val adapter = fragBinding.recyclerView.adapter as ProductAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                productListViewModel.delete(
                    user?.uid!!,
                    (viewHolder.itemView.tag as ProductModel).pid!!
                )
                Timber.i(productListViewModel.liveFirebaseUser.value.toString())
                Timber.i("hello123")
                hideLoader(loader)

            }
        }
        val itemTouchDeleteHelper = ItemTouchHelper(swipeDeleteHandler)
        itemTouchDeleteHelper.attachToRecyclerView(fragBinding.recyclerView)

        val swipeEditHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                onProductClick(viewHolder.itemView.tag as ProductModel)
            }
        }
        val itemTouchEditHelper = ItemTouchHelper(swipeEditHandler)
        itemTouchEditHelper.attachToRecyclerView(fragBinding.recyclerView)


        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list, menu)

        val item = menu.findItem(R.id.toggleProducts) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleproducts: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)

        userRole.observe(viewLifecycleOwner, Observer { role ->
            when (role) {
                "producer" -> {
                    toggleproducts.isChecked = false
                }
                "user" -> {
                    toggleproducts.isChecked = true
                }
                else -> toggleproducts.isChecked = false
            }
            // Trigger the checked change listener after setting the initial state based on the user role
            toggleproducts.setOnCheckedChangeListener(null)
            toggleproducts.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) productListViewModel.loadAll()
                else productListViewModel.load()
            }
        })

        toggleproducts.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) productListViewModel.loadAll()
            else productListViewModel.load()
        }

        super.onCreateOptionsMenu(menu, inflater)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }


    private fun render(productList: ArrayList<ProductModel>) {
        fragBinding.recyclerView.adapter = ProductAdapter(
            productList, this, productListViewModel.readOnly.value!!
        )

        if (productList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.productsNotFound.visibility = View.VISIBLE
            fragBinding.John.visibility = View.VISIBLE

            userRole.observe(viewLifecycleOwner, Observer { role ->
                if (role == "user") {
                    fragBinding.BecomeProducerBtn.visibility = View.VISIBLE
                    fragBinding.recyclerView.visibility = View.GONE
                    fragBinding.productsNotFound.visibility = View.GONE
                    fragBinding.John.visibility = View.GONE
                    fragBinding.fab.visibility = View.GONE
                } else {
                    fragBinding.BecomeProducerBtn.visibility = View.GONE
                }
            })

        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.productsNotFound.visibility = View.GONE
            fragBinding.John.visibility = View.GONE
            fragBinding.BecomeProducerBtn.visibility = View.GONE
        }
    }



    fun setSwipeRefresh() {
        fragBinding.swiperefresh.setOnRefreshListener {
            fragBinding.swiperefresh.isRefreshing = true
            showLoader(loader, "Downloading Product")
            if (productListViewModel.readOnly.value!!)
                productListViewModel.loadAll()
            else
                productListViewModel.load()

        }
    }


    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }


    override fun onResume() {
        super.onResume()
        productListViewModel.load()
    }

    /* A static method that returns a new instance of the fragment. */
    companion object {
        @JvmStatic
        fun newInstance() =
            ProductListFragment().apply {
                arguments = Bundle().apply { }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }


    override fun onProductClick(product: ProductModel) {
        val action =
            ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(product.pid)
        if (!productListViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }
}