package com.wit.homegrownapp.ui.order

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.homegrownapp.R
import com.wit.homegrownapp.adapters.OrderAdapter
import com.wit.homegrownapp.adapters.OrderListener
import com.wit.homegrownapp.databinding.FragmentOrderBinding
import com.wit.homegrownapp.model.OrderModel


class OrderFragment : Fragment() {

    companion object {
        fun newInstance() = OrderFragment()
    }

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!


    private val orderViewModel: OrderViewModel by activityViewModels()


    private lateinit var orderAdapter: OrderAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        orderViewModel.loadRequestedOrders()
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_order, menu)

        val item = menu.findItem(R.id.toggleOrders) as MenuItem
        val toggleorders: SwitchCompat = item.actionView as SwitchCompat
        toggleorders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                orderViewModel.loadReceivedOrders()
                // show receivedOrders TextView, hide requestedOrders TextView
                binding.receivedOrders.visibility = View.VISIBLE
                binding.requestedOrders.visibility = View.GONE
            } else {
                orderViewModel.loadRequestedOrders()
                // show requestedOrders TextView, hide receivedOrders TextView
                binding.requestedOrders.visibility = View.VISIBLE
                binding.receivedOrders.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.receivedOrders.visibility = View.GONE
        setupRecyclerView()
        observeOrders()
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(object : OrderListener {
            override fun onOrderClick(order: OrderModel) {
                // Handle click event here
            }
        })
        binding.orderRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun observeOrders() {
        orderViewModel.orders.observe(viewLifecycleOwner, { orders: List<OrderModel> ->
            orderAdapter.submitList(orders)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}