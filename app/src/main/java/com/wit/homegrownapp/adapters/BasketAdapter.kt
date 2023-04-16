package com.wit.homegrownapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter


import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.BasketItemBinding
import com.wit.homegrownapp.model.BasketItemModel

class BasketAdapter(private val onRemoveClick: (String) -> Unit) :
    ListAdapter<BasketItemModel, BasketAdapter.BasketViewHolder>(BasketItemModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val binding = BasketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(getItem(position), onRemoveClick)
    }

    class BasketViewHolder(private val binding: BasketItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BasketItemModel, onRemoveClick: (String) -> Unit) {
            binding.itemNameTextView.text = item.productName
            binding.itemPriceTextView.text = binding.root.context.getString(R.string.item_price, item.price)
            binding.itemQuantityTextView.text = binding.root.context.getString(R.string.item_quantity, item.quantity)
            binding.removeButton.setOnClickListener { onRemoveClick(item.biid) }
        }
    }

    class BasketItemModelDiffCallback : DiffUtil.ItemCallback<BasketItemModel>() {
        override fun areItemsTheSame(oldItem: BasketItemModel, newItem: BasketItemModel): Boolean {
            return oldItem.biid == newItem.biid
        }

        override fun areContentsTheSame(oldItem: BasketItemModel, newItem: BasketItemModel): Boolean {
            return oldItem == newItem
        }
    }
}
