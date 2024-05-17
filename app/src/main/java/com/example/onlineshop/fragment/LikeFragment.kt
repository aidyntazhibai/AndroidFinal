package com.example.onlineshop.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.api.ApiService
import com.example.onlineshop.api.ProductService
import com.example.onlineshop.databinding.FragmentLikeBinding
import com.example.onlineshop.models.Product
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LikeFragment : Fragment(), ProductAdapter.ProductClickListener {

    private lateinit var binding: FragmentLikeBinding
    private lateinit var productAdapter: ProductAdapter
    private val favoriteProducts = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter(favoriteProducts, this)
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFavorites.adapter = productAdapter

        loadFavoriteProducts()
    }

    private fun loadFavoriteProducts() {
        val sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favoriteIds = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()

        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.get().addOnSuccessListener { dataSnapshot ->
            val allProducts = dataSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
            val allFavoriteProducts = allProducts.filter { it.id.toString() in favoriteIds }
            productAdapter.updateProducts(allFavoriteProducts)
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }




    override fun onProductClick(product: Product) {
        val fragment = ProductDetailsFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LikeFragment()
    }
}
