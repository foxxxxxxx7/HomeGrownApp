package com.wit.homegrownapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.databinding.CardBasketItemBinding
import com.wit.homegrownapp.model.BasketItemModel

interface BasketListener {
    fun onBasketItemClick(basketItem: BasketItemModel)
    fun onIncreaseQuantityClick(basketItem: BasketItemModel)
    fun onDecreaseQuantityClick(basketItem: BasketItemModel)
}


class BasketAdapter(private val listener: BasketListener) :
    ListAdapter<BasketItemModel, BasketAdapter.BasketViewHolder>(BasketItemModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val binding =
            CardBasketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BasketViewHolder(private val binding: CardBasketItemBinding, private val listener: BasketListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BasketItemModel) {
            binding.basketItem = item
            binding.imageIcon.setImageResource(item.icon)
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener.onBasketItemClick(item) }


            binding.plusButton.setOnClickListener { listener.onIncreaseQuantityClick(item) }
            binding.minusButton.setOnClickListener { listener.onDecreaseQuantityClick(item) }
        }
    }


    class BasketItemModelDiffCallback : DiffUtil.ItemCallback<BasketItemModel>() {
        override fun areItemsTheSame(oldItem: BasketItemModel, newItem: BasketItemModel): Boolean {
            return oldItem.biid == newItem.biid
        }

        override fun areContentsTheSame(
            oldItem: BasketItemModel, newItem: BasketItemModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
