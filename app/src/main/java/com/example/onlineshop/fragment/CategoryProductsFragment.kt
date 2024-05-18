package com.example.onlineshop.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.databinding.FragmentCategoryProductsBinding
import com.example.onlineshop.models.Product
import com.google.firebase.database.FirebaseDatabase

class CategoryProductsFragment : Fragment(), ProductAdapter.ProductClickListener {

    private lateinit var binding: FragmentCategoryProductsBinding
    private lateinit var productAdapter: ProductAdapter
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = arguments?.getString(ARG_CATEGORY)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter(emptyList(), this)
        binding.recyclerViewCategoryProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategoryProducts.adapter = productAdapter

        loadProductsByCategory(category)

        binding.topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadProductsByCategory(category: String?) {
        if (category == null) return

        val database = FirebaseDatabase.getInstance()
        val productsRef = database.getReference("products")

        productsRef.get().addOnSuccessListener { dataSnapshot ->
            val allProducts = dataSnapshot.children.mapNotNull { it.getValue(Product::class.java) }
            val products = allProducts.filter { it.category == category }
            productAdapter.updateProducts(products)
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
        private const val ARG_CATEGORY = "category"

        @JvmStatic
        fun newInstance(category: String) =
            CategoryProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }
}
