package com.wit.homegrownapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.wit.homegrownapp.R
import com.wit.homegrownapp.model.ProductModel
import com.wit.homegrownapp.databinding.CardProductBinding
import com.wit.homegrownapp.utils.customTransformation

/* This is an interface that has a single method, onProductClick, which takes a ProductModel as a
parameter. */
interface ProductListener {
    //    fun onDeleteProduct(product: ProductModel)
//    fun onUpdateProduct(product: ProductModel)
    fun onProductClick(product: ProductModel)
}

/* This is the constructor of the ProductAdapter class. */
class ProductAdapter constructor(
    private var products: ArrayList<ProductModel>,
    private val listener: ProductListener,
    private val readOnly: Boolean
) : RecyclerView.Adapter<ProductAdapter.MainHolder>() {

    /**
     * This function inflates the layout for the view holder and returns a new view holder with the
     * view
     *
     * @param parent ViewGroup - The ViewGroup into which the new View will be added after it is bound
     * to an adapter position.
     * @param viewType Int
     * @return A MainHolder object
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardProductBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding, readOnly)
    }

    /**
     * The function takes a MainHolder and an Int as parameters, and returns Unit
     *
     * @param holder MainHolder - this is the view holder that will be used to display the data.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val product = products[holder.adapterPosition]
        holder.bind(product, listener)
    }

    /**
     * Removes the item at the given position from the list and notifies the adapter that the item has
     * been removed.
     *
     * @param position The position of the item in the list.
     */
    fun removeAt(position: Int) {
        products.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * `override fun getItemCount(): Int = products.size`
     *
     * This function returns the number of items in the `products` list
     */
    override fun getItemCount(): Int = products.size

    /* This is the constructor of the MainHolder class. */
    inner class MainHolder(val binding: CardProductBinding, private val readOnly: Boolean) :
        RecyclerView.ViewHolder(binding.root) {

        val readOnlyRow = readOnly

        /**
         * It binds the data to the view.
         *
         * @param product ProductModel - This is the data that we want to bind to the view.
         * @param listener ProductListener - This is the interface that we created earlier.
         */
        fun bind(product: ProductModel, listener: ProductListener) {
            binding.root.tag = product
            binding.product = product
            binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)
            Picasso.get().load(product.profilepic.toUri())
                .resize(200, 200)
                .transform(customTransformation())
                .centerCrop()
                .into(binding.imageIcon)
//
//            binding.name.text = product.name
//            binding.phoneNumber.text = product.phoneNumber
//            binding.date.text = product.date
            binding.root.setOnClickListener { listener.onProductClick(product) }
//            binding.buttonDelete.setOnClickListener { listener.onDeleteProduct(product) }
//            binding.buttonUpdate.setOnClickListener { listener.onUpdateProduct(product) }

            binding.executePendingBindings()
            /* binding.imageIcon.setImageResource(R.mipmap.ic_launcher_round)*/
        }


    }
}