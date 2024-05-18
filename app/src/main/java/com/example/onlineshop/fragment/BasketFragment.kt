package com.example.onlineshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.databinding.FragmentBasketBinding
import com.example.onlineshop.manager.BasketManager
import com.example.onlineshop.models.Product

class BasketFragment : Fragment(), ProductAdapter.OnProductDeleteClickListener {

    private lateinit var binding: FragmentBasketBinding
    private lateinit var productAdapter: ProductAdapter
    private val basketProducts = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductAdapter(basketProducts, this)
        binding.recyclerViewBasket.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBasket.adapter = productAdapter

        loadBasketProducts()
    }

    private fun loadBasketProducts() {
        BasketManager.getBasket(
            onSuccess = { products ->
                basketProducts.clear()
                basketProducts.addAll(products)
                productAdapter.notifyDataSetChanged()
            },
            onFailure = { exception ->
                // Handle error
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = BasketFragment()
    }

    override fun onProductClick(product: Product) {
        val fragment = ProductDetailsFragment.newInstance(product)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onProductDeleteClick(product: Product) {
        BasketManager.removeFromBasket(product)
        loadBasketProducts() // Refresh the basket
    }
}
