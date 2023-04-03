package com.wit.homegrownapp.ui.map

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.wit.homegrownapp.R
import com.wit.homegrownapp.ui.productList.ProductListViewModel
import timber.log.Timber

import android.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
import com.wit.homegrownapp.utils.createLoader
import com.wit.homegrownapp.utils.hideLoader
import com.wit.homegrownapp.utils.showLoader

/* This is the code for the map fragment. It is responsible for displaying the map and the markers on
the map. */
class MapsFragment : Fragment() {

    private val productListViewModel: ProductListViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
    lateinit var loader: AlertDialog

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        mapsViewModel.map = googleMap
        mapsViewModel.map.isMyLocationEnabled = true

        mapsViewModel.currentLocation.observe(viewLifecycleOwner) {
            val loc = LatLng(
                mapsViewModel.currentLocation.value!!.latitude,
                mapsViewModel.currentLocation.value!!.longitude
            )
            val ardkeenStores = LatLng(52.260791, -7.105922)


            googleMap.addMarker(
                MarkerOptions().position(ardkeenStores).title("Ardkeen Stores")
            )

            mapsViewModel.map.uiSettings.isZoomControlsEnabled = true
            mapsViewModel.map.uiSettings.isMyLocationButtonEnabled = true
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 9f))

            productListViewModel.observableProductList.observe(
                viewLifecycleOwner,
                Observer { products ->
                    products?.let {
                        render(products as ArrayList<ProductModel>)
                        hideLoader(loader)
                    }
                })

        }
    }

    /**
     * It sets the menu for the activity.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
     * is the state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    /**
     * This function is called when the fragment is created. It creates a loader and returns the layout
     * for the fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle object containing the activity's previously saved state. If
     * the activity has never existed before, the value of the Bundle object is null.
     * @return The view of the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        mapsViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        loader = createLoader(requireActivity())

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    /**
     * A callback function that is called when the map is ready to be used.
     *
     * @param view View - The view returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this
     * is the state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    /**
     * We're inflating the menu, finding the toggle button, setting the checked state, and setting the
     * onCheckedChangeListener
     *
     * @param menu The menu object that you want to inflate.
     * @param inflater MenuInflater
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)

        val item = menu.findItem(R.id.toggleProducts) as MenuItem
        item.setActionView(R.layout.togglebutton_layout)
        val toggleProducts: SwitchCompat = item.actionView!!.findViewById(R.id.toggleButton)
        toggleProducts.isChecked = false

        toggleProducts.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) productListViewModel.loadAll()
            else productListViewModel.load()
        }
    }

    /**
     * The function takes an ArrayList of ProductModel objects as a parameter, clears the map, adds
     * markers for the three depots, then adds a marker for each ProductModel object in the ArrayList
     *
     * @param productsList ArrayList<ProductModel> - This is the list of products that we want to display
     * on the map.
     */
    private fun render(productsList: ArrayList<ProductModel>) {
        if (productsList.isNotEmpty()) {
            mapsViewModel.map.clear()

            productsList.forEach { product ->
                val producerLocation = LatLng(product.latitude, product.longitude)
                val markerTitle = "${product.title} - ${product.price}"
                val markerSnippet = product.description

                mapsViewModel.map.addMarker(
                    MarkerOptions().position(producerLocation)
                        .title(markerTitle)
                        .snippet(markerSnippet)
                )
            }
        }
    }




    /**
     * The function is called when the fragment is resumed. It shows a loader, and then observes the
     * loggedInViewModel.liveFirebaseUser. If the user is logged in, it sets the
     * productListViewModel.liveFirebaseUser to the logged in user, and then calls the
     * productListViewModel.load() function
     */
    override fun onResume() {
        super.onResume()
        showLoader(loader, "Downloading Products")
        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, Observer { firebaseUser ->
            if (firebaseUser != null) {
                productListViewModel.liveFirebaseUser.value = firebaseUser
                productListViewModel.load()
            }
        })
    }
}