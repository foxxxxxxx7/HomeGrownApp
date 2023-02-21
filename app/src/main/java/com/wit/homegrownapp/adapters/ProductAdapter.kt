package com.wit.homegrownapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wit.homegrownapp.databinding.CardProductBinding
import com.wit.homegrownapp.model.ProductModel

interface ProductListner {
    fun onDeleteProduct(product: ProductModel)
    fun onUpdateProduct(product: ProductModel)
    fun onProductClick(product: ProductModel)
}

class ProductAdapter constructor(
    private var products: List<ProductModel>,
    private val listener: ProductListner
) : RecyclerView.Adapter<ProductAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardProductBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val product = products[holder.adapterPosition]
        holder.bind(product, listener)
    }

    override fun getItemCount(): Int = products.size

    inner class MainHolder(val binding: CardProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductModel, listener: ProductListner) {
            binding.product = product
//            binding.name.text = product.name
//            binding.phoneNumber.text = product.phoneNumber
//            binding.date.text = product.date
            binding.root.setOnClickListener { listener.onProductClick(product) }
            binding.buttonDelete.setOnClickListener { listener.onDeleteProduct(product) }
            binding.buttonUpdate.setOnClickListener { listener.onUpdateProduct(product) }

            binding.executePendingBindings()
            /* binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)*/
        }


    }
}