package com.example.onlineshop.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineshop.R
import com.example.onlineshop.adapter.ProductAdapter
import com.example.onlineshop.adapter.ProductImagesAdapter
import com.example.onlineshop.api.ApiService
import com.example.onlineshop.api.ProductService
import com.example.onlineshop.databinding.FragmentProductDetailsBinding
import com.example.onlineshop.manager.BasketManager
import com.example.onlineshop.models.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ProductDetailsFragment : Fragment(), ProductAdapter.ProductClickListener {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var productsRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var productsAdapter: ProductAdapter
    private var product: Product? = null
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getParcelable(ARG_PRODUCT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Инициализация Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        productsRef = database.getReference("products")

        binding.buttonBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        product?.let { product ->
            bindProductDetails(product)
            setupFavoriteButton(product)
            binding.buttonAddToCart.setOnClickListener {
                addToBasket(product)
            }
        }

        loadProductsFromFirebase()
    }

    private fun loadProductsFromFirebase() {
        val productId = product?.id.toString()
        productsRef.child(productId).get().addOnSuccessListener { dataSnapshot ->
            val product = dataSnapshot.getValue(Product::class.java)
            product?.let {
                bindProductDetails(it)
                setupFavoriteButton(it)
            }
        }.addOnFailureListener { e ->
            Log.e("ProductDetailsFragment", "Error loading product details from Firebase", e)
            Toast.makeText(context, "Failed to load product details", Toast.LENGTH_SHORT).show()
        }
    }


    private fun bindProductDetails(product: Product) {
        binding.textViewTitle.text = product.title
        binding.textViewPrice.text = getString(R.string.product_price, product.price)
        binding.textViewDescription.text = product.description
        binding.textViewCategory.text = product.category
        binding.textViewDiscount.text = getString(R.string.product_discount, product.discountPercentage)
        binding.textViewRating.text = getString(R.string.product_rating, product.rating)
        binding.textViewStock.text = getString(R.string.product_stock, product.stock)
        binding.textViewBrand.text = product.brand

        val adapter = ProductImagesAdapter(product.images)
        binding.viewPagerProductImages.adapter = adapter
    }

    private fun setupFavoriteButton(product: Product) {
        val sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favoriteIds = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
        isFavorite = favoriteIds.contains(product.id.toString())
        updateFavoriteIcon()

        binding.buttonFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()
            handleFavorite(product, isFavorite)
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun handleFavorite(product: Product, isFavorite: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val favorites = sharedPreferences.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()

        if (isFavorite) {
            favorites.add(product.id.toString())
        } else {
            favorites.remove(product.id.toString())
        }

        editor.putStringSet("favorites", favorites)
        editor.apply()
    }

    private fun addToBasket(product: Product) {
        BasketManager.addToBasket(product)
        Toast.makeText(requireContext(), "Added to a cart", Toast.LENGTH_SHORT).show()
    }

    override fun onProductClick(product: Product) {
        // Handle product click if needed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PRODUCT, product)
                }
            }
    }
}
