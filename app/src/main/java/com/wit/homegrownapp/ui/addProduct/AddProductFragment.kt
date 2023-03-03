package com.wit.homegrownapp.ui.addProduct

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentAddProductBinding
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
//import com.wit.homegrownapp.ui.map.MapsViewModel
import java.text.SimpleDateFormat
import java.util.*

/* This is the code for the BookFragment. It is the fragment that is used to book a bike. */
class AddProductFragment : Fragment() {

    //  lateinit var app: BikeshopApp
    var product = ProductModel()
    var edit = false
    private var _fragBinding: FragmentAddProductBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var addProductViewModel: AddProductViewModel
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
//    private val mapsViewModel: MapsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //app = activity?.application as BikeshopApp
        setHasOptionsMenu(true)


    }

    /**
     * The function sets the button listener for the book button, and sets the date change listener for
     * the calendar view
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container The parent that this fragment's UI should be attached to.
     * @param savedInstanceState Bundle?
     * @return The root view is being returned.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAddProductBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_add_product)

        addProductViewModel = ViewModelProvider(this).get(addProductViewModel::class.java)
        addProductViewModel.observableStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let { render(status) }
        })


//        setButtonListener(fragBinding)
//        val selectDate = fragBinding.bookDate
//        //https://stackoverflow.com/questions/16031314/how-can-i-get-selected-date-in-calenderview-in-android#:~:text=Set%20listener%20to%20set%20selected,date%20to%20get%20selected%20date.
//        //I found this solution on StackOverflow after the date kept appearing as today's date
//        // Set date change listener on calenderView.
//        // Callback notified when user select a date from CalenderView on UI.
//        selectDate.setOnDateChangeListener { calView: CalendarView, year: Int, month: Int, dayOfMonth: Int ->
//            // Create calender object with which will have system date time.
//            val calender: Calendar = Calendar.getInstance()
//            // Set attributes in calender object as per selected date.
//            calender.set(year, month, dayOfMonth)
//            // Now set calenderView with this calender object to highlight selected date on UI.
//            calView.setDate(calender.timeInMillis, true, true)
//            Log.d("SelectedDate", "$dayOfMonth/${month + 1}/$year")
//
//        }
        return root
    }

    /**
     * It renders the result of the book request.
     *
     * @param status Boolean - This is the status of the book. If the book is successfully added, it
     * will return true. If it fails, it will return false.
     */
    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {
                    //Uncomment this if you want to immediately return to Report
                    //findNavController().popBackStack()
                }
            }
            false -> Toast.makeText(context, getString(R.string.error_product), Toast.LENGTH_LONG)
                .show()
        }
    }

    /* A static method that is used to create a new instance of the fragment. */
    companion object {
        @JvmStatic
        fun newInstance() =
            AddProductFragment().apply {
                arguments = Bundle().apply {}
            }
    }


    /**
     * It sets the binding variable to null.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    /**
     * This function is called when the user clicks the "Book" button. It takes the data from the user
     * input fields and creates a new BookModel object, which is then passed to the addProductViewModel to be
     * added to the database
     *
     * @param layout FragmentBookBinding - This is the binding object that is created when the fragment
     * is created.
     */
    fun setButtonListener(layout: FragmentAddProductBinding) {


        layout.addProductButton.setOnClickListener {

//            val readableDate = SimpleDateFormat("dd/MM/yy").format(Date(layout.bookDate.date))
//            product.date = readableDate
            product.title = layout.addTitle.text.toString()
            product.price = layout.addPrice.text.toString()
            product.category = layout.addCategory.text.toString()
            product.description = layout.addDescription.text.toString()
            product.eircode = layout.addEircode.text.toString()
            //var selInt = R.array.bikeArray.
            product.bike = layout.addProduct.selectedItemPosition
            if (product.title.isEmpty() || product.price.toString().isEmpty() || product.category.isEmpty() || product.description.isEmpty()) {
                Toast.makeText(context, "Please complete ALL fields", Toast.LENGTH_LONG).show()
//            } else {
//                if (edit) {
//                   // addProductViewModel.updateBook(product.copy())
            } else {
                layout.addTitle.setText("")
                layout.addPrice.setText("")
                layout.addCategory.setText("")
                layout.addDescription.setText("")
                layout.addEircode.setText("")
                Toast.makeText(context, "Product Added!", Toast.LENGTH_LONG).show()
//                    addProductViewModel.addBook(product.copy())
                addProductViewModel.addProduct(
                    loggedInViewModel.liveFirebaseUser,
                    ProductModel(
                        title = product.title,
                        price = product.price,
                        category = product.category,
                        description = product.description,
                        eircode = product.eircode,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!,
//                        latitude = mapsViewModel.currentLocation.value!!.latitude,
//                        longitude = mapsViewModel.currentLocation.value!!.longitude
                    )
                )
                print("Add Button Pressed: $layout.addTitle, $layout.addPrice, $layout.addDes")
                /*setResult(AppCompatActivity.RESULT_OK)*/
            }
        }
    }


    /**
     * > This function inflates the menu_product.xml file into the menu object
     *
     * @param menu The menu object that you want to inflate.
     * @param inflater The MenuInflater that you use to inflate your menu resource into the Menu
     * object.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_product, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * If the user clicks on an item in the menu, and that item has an associated action, then perform
     * that action
     *
     * @param item The menu item that was selected.
     * @return The return value is a boolean.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}