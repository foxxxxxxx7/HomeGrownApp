package com.wit.homegrownapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wit.homegrownapp.R
import com.wit.homegrownapp.adapter.BasketAdapter
import androidx.fragment.app.viewModels
import com.wit.homegrownapp.databinding.FragmentBasketBinding
import com.wit.homegrownapp.model.BasketItemModel

class BasketFragment : Fragment() {

    private var _binding: FragmentBasketBinding? = null
    private val binding get() = _binding!!

    private val basketViewModel: BasketViewModel by activityViewModels()
    private lateinit var basketAdapter: BasketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeBasketItems()
    }

    private fun setupRecyclerView() {
        basketAdapter = BasketAdapter { itemId -> basketViewModel.removeBasketItem(itemId) }
        binding.basketRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = basketAdapter
        }
    }

    private fun observeBasketItems() {
        basketViewModel.basketItems.observe(viewLifecycleOwner, { basketItems: List<BasketItemModel> ->
            basketAdapter.submitList(basketItems)
            updateTotalPrice(basketItems)
        })
    }


    private fun updateTotalPrice(basketItems: List<BasketItemModel>) {
        val totalPrice = basketItems.sumOf { it.price * it.quantity }
        binding.totalPriceTextView.text = getString(R.string.total_price, totalPrice)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
