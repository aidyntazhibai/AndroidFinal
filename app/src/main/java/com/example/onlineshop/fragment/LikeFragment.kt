package com.example.onlineshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.databinding.FragmentLikeBinding
import com.example.onlineshop.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // User not authenticated, handle accordingly
            return
        }

        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("favorites").child(user.uid)

        favoritesRef.get().addOnSuccessListener { dataSnapshot ->
            val favoriteIds = dataSnapshot.children.mapNotNull { it.key }
            val productsRef = database.getReference("products")

            productsRef.get().addOnSuccessListener { productsSnapshot ->
                val allProducts = productsSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
                val userFavoriteProducts = allProducts.filter { it.id.toString() in favoriteIds }
                productAdapter.updateProducts(userFavoriteProducts)
            }.addOnFailureListener { e ->
                e.printStackTrace()
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    private fun addProductToFavorites(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("favorites").child(user.uid).child(product.id.toString())
        favoritesRef.setValue(true)
    }

    private fun removeProductFromFavorites(product: Product) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val database = FirebaseDatabase.getInstance()
        val favoritesRef = database.getReference("favorites").child(user.uid).child(product.id.toString())
        favoritesRef.removeValue()
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
