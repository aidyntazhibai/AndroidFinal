package com.example.onlineshop.fragment

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
import com.example.onlineshop.databinding.FragmentCategoryProductsBinding
import com.example.onlineshop.models.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


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
        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadProductsByCategory(category: String?) {
        if (category == null) return

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val productService = ApiService.retrofit.create(ProductService::class.java)
                val response = productService.getProducts()
                val products = response.products.filter { it.category == category }
                productAdapter.updateProducts(products)
            } catch (e: Exception) {
                e.printStackTrace()
            }
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