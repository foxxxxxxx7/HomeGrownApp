package com.wit.homegrownapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.wit.homegrownapp.R
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.databinding.CardProductBinding
import com.wit.homegrownapp.utils.customTransformation


interface ProductListener {
    fun onProductClick(product: ProductModel)
}

/* This is the constructor of the ProductAdapter class. */
class ProductAdapter constructor(
    private var products: ArrayList<ProductModel>,
    private val listener: ProductListener,
    private val readOnly: Boolean
) : RecyclerView.Adapter<ProductAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val product = products[holder.adapterPosition]
        holder.bind(product, listener)
    }

    fun removeAt(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = products.size

    /* This is the constructor of the MainHolder class. */
    inner class MainHolder(val binding: CardProductBinding, private val readOnly: Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        fun bind(product: ProductModel, listener: ProductListener) {
            Log.d("ProductAdapter", "Product icon: ${product.icon}")
            Log.d("ProductAdapter", "Producer image URL: ${product.producerimage}")
            binding.root.tag = product
            binding.product = product
            binding.imageIcon.setImageResource(product.icon)

            if (!product.producerimage.isNullOrEmpty()) {
                Picasso.get().load(product.producerimage)
                    .resize(200, 200)
                    .transform(customTransformation())
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(binding.imageProducer, object : Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully")
                        }

                        override fun onError(e: Exception) {
                            Log.e("Picasso", "Error loading image", e)
                        }
                    })
            } else {
                binding.imageProducer.setImageResource(R.mipmap.ic_launcher_round)
            }

            binding.root.setOnClickListener { listener.onProductClick(product) }
            binding.executePendingBindings()
        }


    }
}