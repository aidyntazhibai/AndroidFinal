package com.example.onlineshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.R
import com.example.onlineshop.models.Category

class CategoryAdapter(private var categories: List<Category>, private val listener: CategoryClickListener) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    fun updateCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewCategoryName)
        private val imageView: ImageView = itemView.findViewById(R.id.imageViewCategory)

        fun bind(category: Category) {
            titleTextView.text = category.name

            Glide.with(itemView.context)
                .load(category.image)
                .into(imageView)

            itemView.setOnClickListener {
                listener.onCategoryClick(category)
            }
        }
    }

    interface CategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}
