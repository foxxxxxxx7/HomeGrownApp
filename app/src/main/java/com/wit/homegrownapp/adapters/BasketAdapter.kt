package com.wit.homegrownapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.wit.homegrownapp.databinding.CardBasketItemBinding
import com.wit.homegrownapp.model.BasketItemModel


class BasketAdapter :
    ListAdapter<BasketItemModel, BasketAdapter.BasketViewHolder>(BasketItemModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val binding =
            CardBasketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BasketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BasketViewHolder(private val binding: CardBasketItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BasketItemModel) {
            binding.basketItem = item
            binding.imageIcon.setImageResource(item.icon)
            binding.executePendingBindings()
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
