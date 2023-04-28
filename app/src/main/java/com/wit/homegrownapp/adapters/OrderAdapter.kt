package com.wit.homegrownapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.R
import com.wit.homegrownapp.databinding.CardOrderBinding
import com.wit.homegrownapp.firebase.FirebaseDBManager
import com.wit.homegrownapp.model.OrderModel

interface OrderListener {
    fun onOrderClick(order: OrderModel)
}


class OrderAdapter(private val listener: OrderListener) :
    ListAdapter<OrderModel, OrderAdapter.OrderViewHolder>(OrderModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = CardOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: CardOrderBinding, private val listener: OrderListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tickButton.setOnClickListener {
                getItem(adapterPosition).status = "accepted"
                FirebaseDBManager.saveOrder(getItem(adapterPosition))
                updateStatusIndicator(getItem(adapterPosition).status)
            }

            binding.crossButton.setOnClickListener {
                getItem(adapterPosition).status = "declined"
                FirebaseDBManager.saveOrder(getItem(adapterPosition))
                updateStatusIndicator(getItem(adapterPosition).status)
            }
        }

        fun bind(item: OrderModel) {
            binding.order = item
            updateStatusIndicator(item.status)
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener.onOrderClick(item) }
        }

        private fun updateStatusIndicator(status: String) {
            when (status) {
                "pending" -> {
                    binding.statusIndicator.setImageResource(R.drawable.ic_menu_about)
                    binding.statusIndicator.setBackgroundColor(Color.parseColor("#DA530A"))
                }
                "accepted" -> {
                    binding.statusIndicator.setImageResource(R.drawable.ic_check_mark)
                    binding.statusIndicator.setBackgroundColor(Color.parseColor("#008450"))
                }
                "declined" -> {
                    binding.statusIndicator.setImageResource(R.drawable.ic_cross_mark)
                    binding.statusIndicator.setBackgroundColor(Color.parseColor("#B81D13"))
                }
            }
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
