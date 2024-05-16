package com.example.onlineshop.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.widget.SearchView
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.api.ApiService
import com.example.onlineshop.api.ProductService
import com.example.onlineshop.models.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopFragment : Fragment(), ProductAdapter.ProductClickListener {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private var allProducts: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProducts)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        searchView = view.findViewById(R.id.searchView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(emptyList(), this)
        recyclerView.adapter = productAdapter

        swipeRefreshLayout.setOnRefreshListener {
            loadProducts()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterProducts(newText)
                return true
            }
        })

        loadProducts()

        return view
    }

    private fun loadProducts() {
        swipeRefreshLayout.isRefreshing = true
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val productService = ApiService.retrofit.create(ProductService::class.java)
                val response = productService.getProducts()
                allProducts = response.products
                Log.d("ShopFragment", "Loaded products: $allProducts")
                productAdapter.updateProducts(allProducts)
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ShopFragment", "Error loading products", e)
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun filterProducts(query: String?) {
        val filteredProducts = if (query.isNullOrEmpty()) {
            allProducts
        } else {
            allProducts.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
        productAdapter.updateProducts(filteredProducts)
    }

    override fun onProductClick(product: Product) {
        val fragment = ProductDetailsFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
