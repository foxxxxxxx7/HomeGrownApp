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
import com.google.android.gms.maps.model.BitmapDescriptor
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

    private fun greenMarker(): BitmapDescriptor {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    }

    private fun orangeMarker(): BitmapDescriptor {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
    }


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

            productListViewModel.observableProductList.observe(viewLifecycleOwner,
                Observer { products ->
                    products?.let {
                        render(products as ArrayList<ProductModel>)
                        hideLoader(loader)
                    }
                })

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        mapsViewModel = ViewModelProvider(this).get(MapsViewModel::class.java)
        loader = createLoader(requireActivity())

        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


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


    private fun render(productsList: ArrayList<ProductModel>) {
        if (productsList.isNotEmpty()) {
            mapsViewModel.map.clear()

            val currentUserId = loggedInViewModel.liveFirebaseUser.value?.uid

            productsList.forEach { product ->
                val producerLocation = LatLng(product.latitude, product.longitude)
                val markerTitle = "${product.title} - ${product.price}"
                val markerSnippet = product.description
                val markerIcon = if (product.uid == currentUserId) greenMarker() else orangeMarker()

                mapsViewModel.map.addMarker(
                    MarkerOptions().position(producerLocation)
                        .title(markerTitle)
                        .snippet(markerSnippet)
                        .icon(markerIcon)
                )
            }
        }
    }



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