package com.example.onlineshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.models.Product

class ProductAdapter(
    private var products: List<Product>,
    private val listener: ProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface ProductClickListener {
        fun onProductClick(product: Product)
    }

    interface OnProductDeleteClickListener : ProductClickListener {
        fun onProductDeleteClick(product: Product)
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, listener)
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        private val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
        private val buttonDelete: ImageButton = itemView.findViewById(R.id.buttonDelete)

        fun bind(product: Product, listener: ProductClickListener) {
            textViewTitle.text = product.title
            textViewPrice.text = itemView.context.getString(R.string.product_price, product.price)
            textViewRating.text = product.rating.toString()
            Glide.with(itemView.context)
                .load(product.images.firstOrNull())
                .into(imageViewProduct)

            itemView.setOnClickListener {
                listener.onProductClick(product)
            }

            if (listener is OnProductDeleteClickListener) {
                buttonDelete.visibility = View.VISIBLE
                buttonDelete.setOnClickListener {
                    listener.onProductDeleteClick(product)
                }
            } else {
                buttonDelete.visibility = View.GONE
            }
        }
    }
}
