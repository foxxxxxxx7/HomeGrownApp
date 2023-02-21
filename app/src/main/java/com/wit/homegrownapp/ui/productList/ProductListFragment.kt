package com.wit.homegrownapp.ui.productList

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

    /**
     * The function is called when the activity is created
     *
     * @param savedInstanceState This is a Bundle object that contains the activity's previously saved
     * state. If the activity has never existed before, the value of the Bundle object is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // app = activity?.application as BikeshopApp
        setHasOptionsMenu(true)
    }

    /**
     * It creates the view for the fragment.
     *
     * @param inflater LayoutInflater
     * @param container The parent that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle object containing the activity's previously saved state.
     * @return The root view of the fragment.
     */
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
                    (viewHolder.itemView.tag as ProductModel).uid!!
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

    /**
     * We're inflating the menu, finding the toggle button, setting the checked state, and setting the
     * listener
     *
     * @param menu The menu object that you want to inflate.
     * @param inflater The MenuInflater that you use to inflate the menu resource into the given menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product_list, menu)

        val item = menu.findItem(R.id.toggleProducts) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleproducts: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
        toggleproducts.isChecked = false

        toggleproducts.setOnCheckedChangeListener { buttonView, isChecked ->
            /**
             * If the user clicks on an item in the menu, and that item has an associated action, then perform
             * that action
             *
             * @param item The menu item that was selected.
             * @return The return value is a boolean.
             */
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

    /**
     * The function renders the recycler view with the list of products
     *
     * @param productList ArrayList<ProductModel> - The list of products to be displayed
     */
    private fun render(productList: ArrayList<ProductModel>) {
        fragBinding.recyclerView.adapter = ProductAdapter(
            productList, this, productListViewModel.readOnly.value!!
        )
        if (productList.isEmpty()) {
            fragBinding.recyclerView.visibility = View.GONE
            fragBinding.productsNotFound.visibility = View.VISIBLE
            fragBinding.John.visibility = View.VISIBLE
        } else {
            fragBinding.recyclerView.visibility = View.VISIBLE
            fragBinding.productsNotFound.visibility = View.GONE
            fragBinding.John.visibility = View.GONE
        }
    }

    /**
     * It sets the swipe refresh listener.
     */
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

    /**
     * It checks if the swipe refresh is refreshing and if it is, it sets it to false.
     */
    private fun checkSwipeRefresh() {
        if (fragBinding.swiperefresh.isRefreshing)
            fragBinding.swiperefresh.isRefreshing = false
    }

    /**
     * The function is called when the activity is resumed
     */
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

    /**
     * It sets the binding variable to null.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

//    private fun showproducts(products: List<ProductModel>) {
//        view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter =
//            ProductAdapter(products, this@ProductListFragment)
//        view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter?.notifyDataSetChanged()
//    }
//
//    override fun onDeleteproduct(product: ProductModel) {
//        //ProductListViewModel.del(product)
//        ProductListViewModel.load()
//     //   showproducts(ProductListViewModel.findAll())
//        Toast.makeText(context, "product Deleted!", Toast.LENGTH_LONG).show()
//
//    }
//
//    override fun onUpdateproduct(product: ProductModel) {
//     //   showproducts(ProductListViewModel.findAll())
//    }

    /**
     * If the user is not in read-only mode, navigate to the product detail fragment.
     *
     * @param product ProductModel - The product object that was clicked
     */
    override fun onProductClick(product: ProductModel) {
        val action =
            ProductListFragmentDirections.actionProductListToAddProduct()
        if (!productListViewModel.readOnly.value!!)
            findNavController().navigate(action)
    }

}