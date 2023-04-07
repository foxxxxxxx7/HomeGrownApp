package com.wit.homegrownapp.ui.addProduct

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.FragmentAddProductBinding
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.ui.auth.LoggedInViewModel
import kotlinx.coroutines.launch
//import com.wit.homegrownapp.ui.map.MapsViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddProductFragment : Fragment() {

    var product = ProductModel()
    var edit = false
    private var _fragBinding: FragmentAddProductBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var addProductViewModel: AddProductViewModel
    private val loggedInViewModel: LoggedInViewModel by activityViewModels()
//    private val mapsViewModel: MapsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentAddProductBinding.inflate(inflater, container, false)
        val root = fragBinding.root
        activity?.title = getString(R.string.action_add_product)

        addProductViewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)
        addProductViewModel.observableStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let { render(status) }
        })

        setButtonListener(fragBinding)

        fragBinding.addCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                updateTypeDropdown(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                updateTypeDropdown("")
            }
        }

        return root
    }
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

    private fun updateTypeDropdown(category: String) {
        val typeArray = when (category) {
            "Fruit" -> R.array.fruitArray
            "Vegetable" -> R.array.vegArray
            else -> 0
        }

        if (typeArray != 0) {
            val typeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                typeArray,
                android.R.layout.simple_spinner_item
            )
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fragBinding.addType.adapter = typeAdapter

            fragBinding.addType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedType = parent?.getItemAtPosition(position).toString()
                    Log.d("onItemSelected", "Selected type: $selectedType")
                    updateVarietyDropdown(selectedType)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.d("onNothingSelected", "No type selected")
                    updateVarietyDropdown("")
                }
            }

        } else {
            fragBinding.addType.adapter = null
        }
    }
    fun getTypeArrayName(type: String): String {
        val camelCase = type.split(" ").joinToString(separator = "") { it.capitalize() }
        val arrayName = camelCase.decapitalize() + "Array"
        Log.d("getTypeArrayName", "Array name: $arrayName")
        return arrayName
    }


    fun updateVarietyDropdown(selectedType: String) {
        val arrayName = getTypeArrayName(selectedType)
        val resourceId = resources.getIdentifier(arrayName, "array", requireContext().packageName)

        if (resourceId != 0) {
            val varietyAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                resourceId,
                android.R.layout.simple_spinner_item
            )
            varietyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            fragBinding.addVariety.adapter = varietyAdapter
            Log.d("updateVarietyDropdown", "Updated variety dropdown with resource ID: $resourceId")
        } else {
            fragBinding.addVariety.adapter = null
            Log.d("updateVarietyDropdown", "No resource found for array name: $arrayName")
        }
    }

    private fun getIconResource(type: String): Int {
        val resourceName = type.replace(" ", "").toLowerCase(Locale.ROOT)
        return resources.getIdentifier(resourceName, "drawable", requireContext().packageName)
    }



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



    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }


    fun setButtonListener(layout: FragmentAddProductBinding) {
        layout.addProductButton.setOnClickListener {
            product.title = layout.addTitle.text.toString()
            product.price = layout.addPrice.text.toString().toDoubleOrNull() ?: 0.0
            product.avgWeight = layout.addAvgWeight.text.toString().toDoubleOrNull() ?: 0.0
            product.description = layout.addDescription.text.toString()
            product.eircode = layout.addEircode.text.toString()
            product.category = layout.addCategory.selectedItem.toString()
            // Add the selected type to the product model
            product.type = layout.addType.selectedItem?.toString() ?: ""
            product.variety = layout.addVariety.selectedItem?.toString() ?: ""
            // Set icon based on category and type
            product.icon = getIconResource(product.type)

            if (addProductViewModel.validateProduct(product)) {
                lifecycleScope.launch {
                    addProductViewModel.addProduct(loggedInViewModel.liveFirebaseUser, product)
                    layout.addTitle.setText("")
                    layout.addPrice.setText("")
                    layout.addAvgWeight.setText("")
                    layout.addDescription.setText("")
                    layout.addEircode.setText("")
                    Toast.makeText(context, "Product Added!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_addProduct_to_productList)
                }
            }
        }

        addProductViewModel.validationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                AddProductViewModel.ValidationStatus.TitleEmpty -> Toast.makeText(
                    context,
                    R.string.title_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                AddProductViewModel.ValidationStatus.PriceInvalid -> Toast.makeText(
                    context,
                    R.string.price_invalid_error,
                    Toast.LENGTH_LONG
                ).show()
                AddProductViewModel.ValidationStatus.AvgWeightInvalid -> Toast.makeText(
                    context,
                    R.string.average_weight_invalid_error,
                    Toast.LENGTH_LONG
                ).show()
                AddProductViewModel.ValidationStatus.DescriptionEmpty -> Toast.makeText(
                    context,
                    R.string.description_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                AddProductViewModel.ValidationStatus.EircodeEmpty -> Toast.makeText(
                    context,
                    R.string.eircode_empty_error,
                    Toast.LENGTH_LONG
                ).show()
                AddProductViewModel.ValidationStatus.CategoryEmpty -> Toast.makeText(
                    context,
                    R.string.category_empty_error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_addproduct, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }
}