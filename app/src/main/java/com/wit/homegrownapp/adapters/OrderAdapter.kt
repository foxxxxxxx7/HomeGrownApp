package com.wit.homegrownapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.databinding.CardOrderBinding
import com.wit.homegrownapp.model.OrderModel

interface OrderListener {
    fun onOrderClick(order: OrderModel)
}


class OrderAdapter(private val listener: OrderListener) :
    ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(OrderModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding =
            CardOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(private val binding: CardOrderBinding, private val listener: OrderListener) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderModel) {
            binding.order = item
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener.onOrderClick(item) }
        }
    }


    class OrderModelDiffCallback : DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.oid == newItem.oid
        }

        override fun areContentsTheSame(
            oldItem: OrderModel, newItem: OrderModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
